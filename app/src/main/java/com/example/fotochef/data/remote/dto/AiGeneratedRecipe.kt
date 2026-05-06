package com.example.fotochef.data.remote.dto

/**
 * Modelo de datos para una receta generada por IA (Gemini).
 * Este objeto se parsea del JSON que devuelve el modelo.
 */
data class AiGeneratedRecipe(
    val nombre: String,
    val descripcion: String,
    val tiempoMinutos: Int,
    val dificultad: String,
    val esVegana: Boolean,
    val esSinGluten: Boolean,
    val esSinLactosa: Boolean = false,
    /** Todos los ingredientes que necesita la receta */
    val ingredientesNecesarios: List<String>,
    /** Ingredientes que el usuario NO tiene (le faltan) */
    val ingredientesFaltantes: List<String>,
    /** Pasos de preparación */
    val pasos: List<String>
) {
    /** Ingredientes que el usuario SÍ tiene */
    val ingredientesTenidos: List<String>
        get() = ingredientesNecesarios.filterNot { it in ingredientesFaltantes }
}
