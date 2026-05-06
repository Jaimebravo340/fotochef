package com.example.fotochef.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Tabla intermedia que relaciona recetas con ingredientes (relación muchos a muchos).
 * Incluye la cantidad necesaria del ingrediente para esa receta.
 */
@Entity(
    tableName = "recepta_ingredient",
    primaryKeys = ["receptaId", "ingredientId"],
    foreignKeys = [
        ForeignKey(
            entity = Recepta::class,
            parentColumns = ["id"],
            childColumns = ["receptaId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Ingredient::class,
            parentColumns = ["id"],
            childColumns = ["ingredientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["receptaId"]),
        Index(value = ["ingredientId"])
    ]
)
data class ReceptaIngredient(
    // ID de la receta
    val receptaId: Long,

    // ID del ingrediente
    val ingredientId: Long,

    // Cantidad del ingrediente (ej: "200g", "2 unidades", "1 cucharada")
    val quantitat: String = ""
)
