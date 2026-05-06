package com.example.fotochef.data.repository

import android.util.Log
import com.example.fotochef.BuildConfig
import com.example.fotochef.data.remote.AiRecipeService
import com.example.fotochef.data.remote.dto.AiGeneratedRecipe
import com.example.fotochef.data.remote.dto.OpenAiMessage
import com.example.fotochef.data.remote.dto.OpenAiRequest
import com.example.fotochef.data.remote.dto.ResponseFormat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Repositorio responsable de generar recetas usando Groq API (Llama 3).
 * Hace UNA sola petición. Si recibe 429, lanza RateLimitException.
 */
class AiRecipeRepository {

    companion object {
        private const val TAG = "AiRecipeRepo"
    }

    private val apiKey = BuildConfig.GROQ_API_KEY

    private val service: AiRecipeService by lazy {
        // Logs básicos de red (útil en debug para ver si la petición sale)
        val logging = HttpLoggingInterceptor { msg -> Log.d(TAG, msg) }.apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        // Interceptor que añade el token a TODAS las peticiones
        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .header("Content-Type", "application/json")
            chain.proceed(requestBuilder.build())
        }

        // Cliente HTTP con timeouts razonables para una llamada a IA
        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build()

        // Retrofit: convierte JSON ↔ objetos Kotlin usando Gson
        Retrofit.Builder()
            .baseUrl(AiRecipeService.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AiRecipeService::class.java)
    }

    private val gson = Gson()

    suspend fun generateRecipes(
        ingredients: List<String>,
        isVegan: Boolean = false,
        isGlutenFree: Boolean = false,
        isLactoseFree: Boolean = false,
        count: Int = 3,
        excludeNames: List<String> = emptyList()
    ): Result<List<AiGeneratedRecipe>> {
        // Si no hay API key configurada, no podemos llamar a la IA
        if (apiKey.isBlank()) {
            return Result.failure(Exception("API key de Groq no configurada"))
        }

        // 1) Construimos el prompt (texto que le mandamos a la IA)
        val prompt = buildPrompt(ingredients, isVegan, isGlutenFree, isLactoseFree, count, excludeNames)

        // 2) Construimos el request estilo “chat” (system + user)
        val request = OpenAiRequest(
            model = "llama-3.3-70b-versatile", // Groq's high-quality fast model
            messages = listOf(
                OpenAiMessage(role = "system", content = "Eres un experto chef culinario. Devuelves únicamente JSON sin formato Markdown."),
                OpenAiMessage(role = "user", content = prompt)
            ),
            responseFormat = ResponseFormat(type = "json_object"),
            temperature = 0.6
        )

        return try {
            Log.d(TAG, "Enviando petición a Groq...")
            // 3) Llamada de red (suspend) a la API
            val response = service.generateContent(request = request)

            // 4) Si la API devuelve error en el body, lo tratamos como fallo
            if (response.error != null) {
                val msg = "Error Groq: ${response.error.message}"
                Log.e(TAG, msg)
                return Result.failure(Exception(msg))
            }

            // 5) Sacamos el texto “crudo” generado por el modelo
            val rawText = response.choices?.firstOrNull()?.message?.content
                ?: return Result.failure(Exception("La IA no devolvió ningún resultado"))

            Log.d(TAG, "Respuesta recibida (${rawText.length} chars)")

            // 6) Limpiamos el texto por si viene con basura alrededor y nos quedamos con el JSON
            val cleanJson = extractJson(rawText)
            
            // Si el objeto JSON raíz tiene una clave que contiene el array de recetas, 
            // extraeremos esa clave. El prompt pide un objeto con una clave 'recetas'.
            val type = object : TypeToken<Map<String, List<AiGeneratedRecipe>>>() {}.type
            val resultWrapper: Map<String, List<AiGeneratedRecipe>> = gson.fromJson(cleanJson, type)
            
            // 7) Nos quedamos con la lista (por ejemplo, la del campo "recetas")
            val recipes = resultWrapper.values.firstOrNull() ?: emptyList()

            if (recipes.isEmpty()) {
                return Result.failure(Exception("No se pudieron extraer recetas del JSON"))
            }

            Log.d(TAG, "✅ ${recipes.size} recetas generadas")
            Result.success(recipes)

        } catch (e: HttpException) {
            // Error HTTP (código 4xx/5xx). 429 = demasiadas peticiones (rate limit)
            val errorBody = runCatching { e.response()?.errorBody()?.string() }.getOrNull() ?: ""
            Log.e(TAG, "HTTP ${e.code()}: $errorBody", e)
            if (e.code() == 429) {
                Result.failure(RateLimitException())
            } else {
                Result.failure(Exception(friendlyHttpError(e.code())))
            }
        } catch (e: Exception) {
            // Cualquier otro error (red, parseo JSON, timeout, etc.)
            Log.e(TAG, "Error de red", e)
            Result.failure(Exception(friendlyNetworkError(e)))
        }
    }

    private fun friendlyHttpError(code: Int): String = when (code) {
        400 -> "Solicitud inválida (400)"
        401 -> "API Key de Groq inválida (401)."
        403 -> "Acceso denegado (403)."
        404 -> "Modelo no encontrado (404)."
        500, 503 -> "Error del servidor de Groq ($code)."
        else -> "Error HTTP $code."
    }

    private fun friendlyNetworkError(e: Exception): String {
        val name = e.javaClass.simpleName
        val msg = e.message ?: ""
        return when {
            "UnknownHostException" in name || "Unable to resolve host" in msg -> "Sin conexión a internet"
            "SocketTimeoutException" in name || "timeout" in msg.lowercase() -> "Tiempo de espera agotado"
            "JsonSyntaxException" in name -> "Error al interpretar JSON de la IA"
            else -> "Error: $msg"
        }
    }

    private fun buildPrompt(
        ingredients: List<String>,
        isVegan: Boolean,
        isGlutenFree: Boolean,
        isLactoseFree: Boolean,
        count: Int,
        excludeNames: List<String>
    ): String {
        val ingredientList = ingredients.joinToString(", ")
        val excludeClause = if (excludeNames.isNotEmpty())
            "No incluyas estas recetas: ${excludeNames.joinToString(", ")}. " else ""

        val dietConstraints = buildList {
            if (isVegan) add("VEGANA (sin ningun producto animal)")
            if (isGlutenFree) add("SIN GLUTEN")
            if (isLactoseFree) add("SIN LACTOSA")
        }
        val dietClause = if (dietConstraints.isNotEmpty())
            "Todas deben ser ${dietConstraints.joinToString(" y ")}. " else ""

        return """
Tengo estos ingredientes: $ingredientList.

${dietClause}${excludeClause}Genera $count recetas en espanol usando algunos de estos ingredientes.
Indica que ingredientes extra NO tengo en "ingredientesFaltantes".
Dificultad: "Facil", "Media" o "Dificil". 
La guia de preparacion ("pasos") debe ser muy especifica, detallada y exhaustiva (idealmente entre 6 y 10 pasos explicativos).

Responde SOLO con un objeto JSON valido con la clave "recetas" que contenga la lista de recetas:
{
  "recetas": [
    {
      "nombre": "Nombre",
      "descripcion": "Descripcion breve",
      "tiempoMinutos": 30,
      "dificultad": "Facil",
      "esVegana": false,
      "esSinGluten": false,
      "esSinLactosa": false,
      "ingredientesNecesarios": ["ingrediente1"],
      "ingredientesFaltantes": ["extra1"],
      "pasos": ["Paso 1", "Paso 2"]
    }
  ]
}
        """.trimIndent()
    }

    private fun extractJson(text: String): String {
        // A veces la IA devuelve el JSON dentro de ``` ```; este regex lo extrae si existe
        val codeBlock = Regex("```(?:json)?\\s*([\\s\\S]*?)\\s*```").find(text)
        val raw = codeBlock?.groupValues?.get(1)?.trim() ?: text.trim()
        val start = raw.indexOf('{')
        val end = raw.lastIndexOf('}')
        // Si encontramos llaves, recortamos para quedarnos con un JSON “limpio”
        return if (start != -1 && end > start) raw.substring(start, end + 1) else raw
    }

    suspend fun askAboutRecipe(question: String, recipeContext: String): String {
        // Chat “simple”: pregunta + contexto de receta para que la IA responda mejor
        if (apiKey.isBlank()) return "API Key no configurada"

        val request = OpenAiRequest(
            model = "llama-3.3-70b-versatile",
            messages = listOf(
                OpenAiMessage(role = "system", content = "Eres un asistente de cocina experto. Estás ayudando a un usuario con esta receta: $recipeContext. Responde de forma concisa y útil."),
                OpenAiMessage(role = "user", content = question)
            ),
            temperature = 0.7
        )

        return try {
            val response = service.generateContent(request = request)
            response.choices?.firstOrNull()?.message?.content ?: "No pude obtener respuesta"
        } catch (e: Exception) {
            "Error al consultar al asistente: ${e.message}"
        }
    }
}

/** Excepción específica para rate limit (ej. HTTP 429) */
class RateLimitException : Exception("rate_limit_429")
