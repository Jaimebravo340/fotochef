package com.example.fotochef.data.remote

import com.example.fotochef.data.remote.dto.OpenAiRequest
import com.example.fotochef.data.remote.dto.OpenAiResponse
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Servicio Retrofit genérico compatible con el API de OpenAI.
 * Usado para contactar con Groq.
 */
interface AiRecipeService {
    @POST("openai/v1/chat/completions")
    suspend fun generateContent(
        @Body request: OpenAiRequest
    ): OpenAiResponse
    
    companion object {
        const val BASE_URL = "https://api.groq.com/"
    }
}
