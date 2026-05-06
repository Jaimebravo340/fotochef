package com.example.fotochef.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * Petición para API compatible con OpenAI (usada para Groq).
 */
data class OpenAiRequest(
    val model: String,
    val messages: List<OpenAiMessage>,
    @SerializedName("response_format") val responseFormat: ResponseFormat? = null,
    val temperature: Double = 0.7
)

data class OpenAiMessage(
    val role: String,
    val content: String
)

data class ResponseFormat(
    val type: String = "json_object"
)

/**
 * Respuesta de API compatible con OpenAI.
 */
data class OpenAiResponse(
    val choices: List<OpenAiChoice>?,
    val error: OpenAiError? = null
)

data class OpenAiChoice(
    val message: OpenAiMessage?
)

data class OpenAiError(
    val message: String,
    val type: String?,
    val code: String?
)
