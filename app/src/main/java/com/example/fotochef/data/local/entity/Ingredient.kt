package com.example.fotochef.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un ingrediente en la base de datos.
 * Los ingredientes se asocian a recetas a través de la tabla intermedia ReceptaIngredient.
 */
@Entity(tableName = "ingredients")
data class Ingredient(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Nombre del ingrediente (ej: "Tomate", "Cebolla")
    val nom: String,

    // Categoría del ingrediente: "Verdura", "Fruta", "Carne", "Lácteo", etc.
    val categoria: String = "",

    // Nombre del icono asociado (para mostrar en la UI)
    val icona: String = "",

    // Si el ingrediente está en la despensa virtual del usuario
    val isInPantry: Boolean = false
)
