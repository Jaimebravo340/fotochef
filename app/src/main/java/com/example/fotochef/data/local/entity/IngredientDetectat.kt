package com.example.fotochef.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que almacena los ingredientes detectados por la IA (Google Vision API).
 * Cada detección incluye un nivel de confianza (score) para filtrar resultados.
 */
@Entity(tableName = "ingredients_detectats")
data class IngredientDetectat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Nombre del ingrediente detectado
    val nom: String,

    // Nivel de confianza de la detección (0.0 a 1.0)
    val confianca: Float,

    // Ruta de la imagen donde se detectó
    val imatgePath: String = "",

    // Timestamp de cuándo se realizó la detección
    val timestamp: Long = System.currentTimeMillis(),

    // Si el usuario ha confirmado/validado manualmente este ingrediente
    val confirmat: Boolean = false
)
