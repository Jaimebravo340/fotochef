package com.example.fotochef.data.repository

import android.content.Context
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

class VisionRepository(
    private val context: Context
) {
    suspend fun detectIngredients(
        imagePath: String,
        maxResults: Int = 15
    ): List<DetectedIngredientResult> {
        // 1) Comprobar que la foto existe en disco
        val imageFile = File(imagePath)
        if (!imageFile.exists()) {
            throw IllegalArgumentException("No se encontro la imagen capturada.")
        }

        // 2) Convertir el archivo en un InputImage (formato que ML Kit entiende)
        val image = InputImage.fromFilePath(context, Uri.fromFile(imageFile))

        // 3) Configurar el “labeler” (etiquetador) con un umbral mínimo de confianza
        val options = ImageLabelerOptions.Builder()
            .setConfidenceThreshold(0.5f)
            .build()
        val labeler = ImageLabeling.getClient(options)

        // 4) Ejecutar ML Kit (API por callbacks) y convertirlo a suspensión (coroutines)
        val labels = suspendCancellableCoroutine { continuation ->
            labeler.process(image)
                .addOnSuccessListener { result -> continuation.resume(result) }
                .addOnFailureListener { error -> continuation.resumeWithException(error) }
        }

        // 5) Cerrar el cliente para liberar recursos
        labeler.close()

        // 6) Transformar etiquetas genéricas en nuestra estructura “ingrediente detectado”
        return labels
            .take(maxResults)
            .map { label ->
                DetectedIngredientResult(
                    name = label.text.trim(),
                    confidence = label.confidence
                )
            }
            .filter { it.name.isNotEmpty() }
    }
}

data class DetectedIngredientResult(
    val name: String,
    val confidence: Float
)
