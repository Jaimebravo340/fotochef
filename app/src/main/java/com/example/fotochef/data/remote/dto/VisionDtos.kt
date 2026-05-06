package com.example.fotochef.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs (Data Transfer Objects) para la comunicación con Google Vision API.
 * Estos modelos representan el formato JSON que envía y recibe la API.
 *
 * Documentación: https://cloud.google.com/vision/docs/reference/rest/v1/images/annotate
 */

// === REQUEST ===

/**
 * Petición principal a Vision API. Contiene una lista de requests de imagen.
 */
data class VisionRequest(
    @SerializedName("requests")
    val requests: List<AnnotateImageRequest>
)

/**
 * Cada request contiene una imagen y los tipos de detección deseados.
 */
data class AnnotateImageRequest(
    @SerializedName("image")
    val image: ImageSource,

    @SerializedName("features")
    val features: List<Feature>
)

/**
 * La imagen se envía codificada en base64.
 */
data class ImageSource(
    @SerializedName("content")
    val content: String // Imagen en base64
)

/**
 * Tipo de detección que queremos (LABEL_DETECTION para ingredientes).
 */
data class Feature(
    @SerializedName("type")
    val type: String = "LABEL_DETECTION",

    @SerializedName("maxResults")
    val maxResults: Int = 20
)

// === RESPONSE ===

/**
 * Respuesta principal de Vision API.
 */
data class VisionResponse(
    @SerializedName("responses")
    val responses: List<AnnotateImageResponse>
)

/**
 * Cada respuesta contiene las etiquetas detectadas en la imagen.
 */
data class AnnotateImageResponse(
    @SerializedName("labelAnnotations")
    val labelAnnotations: List<LabelAnnotation>? = null,

    @SerializedName("error")
    val error: VisionError? = null
)

/**
 * Una etiqueta detectada con su descripción y puntuación de confianza.
 */
data class LabelAnnotation(
    @SerializedName("mid")
    val mid: String = "",

    @SerializedName("description")
    val description: String = "",

    @SerializedName("score")
    val score: Float = 0f,

    @SerializedName("topicality")
    val topicality: Float = 0f
)

/**
 * Error devuelto por la API si algo falla.
 */
data class VisionError(
    @SerializedName("code")
    val code: Int = 0,

    @SerializedName("message")
    val message: String = ""
)
