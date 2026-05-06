package com.example.fotochef.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa una receta en la base de datos.
 * Contiene toda la información necesaria para mostrar y gestionar una receta.
 */
@Entity(tableName = "receptes")
data class Recepta(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Nombre de la receta
    val nom: String = "",

    // Descripción breve de la receta
    val descripcio: String = "",

    // Tiempo de preparación en minutos
    val tempsPreparacio: Int = 0,

    // Nivel de dificultad: "Fàcil", "Mitjà", "Difícil"
    val dificultat: String = "",

    // Ruta o URL de la imagen de la receta
    val imatgePath: String = "",

    // Si la receta está marcada como favorita
    val esFavorita: Boolean = false,

    // Pasos de la receta almacenados como JSON string
    // Ejemplo: ["Paso 1", "Paso 2", "Paso 3"]
    val passos: String = "[]",

    // Categoría de la receta: "Primer", "Segon", "Postres", etc.
    val categoria: String = "",

    // Si es apta para veganos
    val esVegana: Boolean = false,

    // Si es sin gluten
    val esSenseGluten: Boolean = false,

    // Información nutricional (por ración)
    val calories: Int = 0,
    val proteines: Float = 0f,
    val carbs: Float = 0f,
    val greixos: Float = 0f
)
