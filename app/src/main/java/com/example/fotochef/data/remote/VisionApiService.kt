package com.example.fotochef.data.remote

import com.example.fotochef.data.remote.dto.VisionRequest
import com.example.fotochef.data.remote.dto.VisionResponse
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Interfaz Retrofit para comunicarse con Google Cloud Vision API.
 * En este sprint solo se define la estructura; la lógica real se implementará en Sprint 4.
 *
 * Endpoint: https://vision.googleapis.com/v1/images:annotate
 */
interface VisionApiService {

    /**
     * Envía una imagen a Google Vision API para detectar objetos/ingredientes.
     * @param apiKey - API Key de Google Cloud (se pasa como query parameter)
     * @param request - Cuerpo de la petición con la imagen en base64
     * @return VisionResponse con las detecciones encontradas
     */
    @POST("v1/images:annotate")
    suspend fun annotateImage(
        @Query("key") apiKey: String,
        @Body request: VisionRequest
    ): VisionResponse

    companion object {
        // URL base de Google Vision API
        const val BASE_URL = "https://vision.googleapis.com/"
    }
}
