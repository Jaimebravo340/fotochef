package com.example.fotochef.ui.screens.scan

import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.repository.IngredientRepository
import com.example.fotochef.data.repository.VisionRepository
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch

class ScanFlowViewModel(
    private val visionRepository: VisionRepository,
    private val ingredientRepository: IngredientRepository
) : ViewModel() {
    companion object {
        // Umbral: solo guardamos detecciones con suficiente “seguridad”
        private const val MIN_CONFIDENCE_TO_SAVE = 0.5f
        // Umbral: si la media supera esto, consideramos la detección “fiable”
        private const val MIN_PRECISION_VALID = 0.8f
    }

    // Ruta del archivo de imagen capturado por cámara
    var photoPath by mutableStateOf<String?>(null)
        private set

    // Lista de nombres de ingredientes detectados (los que se enseñan en la UI)
    var detectedIngredients by mutableStateOf<List<String>>(emptyList())
        private set

    // Media de confianza de las detecciones guardadas
    var averageConfidence by mutableStateOf(0f)
        private set

    // Bandera para mostrar “detección válida” en la pantalla
    var isPrecisionValid by mutableStateOf(false)
        private set

    // Bandera para spinner/carga en UI
    var isLoading by mutableStateOf(false)
        private set

    // Mensaje de error para UI (si algo falla)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun onPhotoCaptured(path: String) {
        // Resetea el estado al empezar un nuevo escaneo
        photoPath = path
        isLoading = true
        errorMessage = null
        detectedIngredients = emptyList()
        averageConfidence = 0f
        isPrecisionValid = false

        viewModelScope.launch {
            try {
                // 1) Ejecuta la detección (ML Kit)
                val detections = visionRepository.detectIngredients(
                    imagePath = path
                )
                // 2) Guarda en BD solo las detecciones con confianza suficiente
                ingredientRepository.saveDetectedIngredients(
                    imagePath = path,
                    detections = detections,
                    minConfidence = MIN_CONFIDENCE_TO_SAVE
                )

                // 3) Vuelve a cargar lo guardado (lo que usará la UI)
                val savedDetections = ingredientRepository.getDetectedIngredientsForImage(path)
                detectedIngredients = savedDetections.map { it.nom }.distinct()
                averageConfidence = if (savedDetections.isEmpty()) {
                    0f
                } else {
                    savedDetections.map { it.confianca }.average().toFloat()
                }
                // 4) Validación simple basada en la media de confianza
                isPrecisionValid = averageConfidence >= MIN_PRECISION_VALID
            } catch (e: Exception) {
                // Si falla, mostramos error en pantalla
                errorMessage = e.message ?: "Error desconocido al detectar ingredientes."
            } finally {
                // Siempre paramos el loading (éxito o error)
                isLoading = false
            }
        }
    }

    fun updateDetectedIngredients(newIngredients: List<String>) {
        // La pantalla Detection puede editar manualmente la lista
        detectedIngredients = newIngredients
    }
}
