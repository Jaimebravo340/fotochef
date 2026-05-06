package com.example.fotochef.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un ingrediente en la lista de la compra.
 */
@Entity(tableName = "ingredients_compra")
data class IngredientCompra(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // Nombre del ingrediente
    val nom: String = "",
    
    // Cantidad necesaria (ej: "500", "2")
    val quantitat: String = "",
    
    // Unidad (ej: "g", "kg", "ud")
    val unitat: String = "",
    
    // ID de la receta de la que proviene (opcional)
    val receptaId: Long? = null,
    
    // Estado del ítem en la lista
    val isComprat: Boolean = false,
    
    // Fecha de creación
    val createdAt: Long = System.currentTimeMillis(),
    
    // Fecha programada para la compra (timestamp en ms, inicio del día)
    val scheduledDate: Long? = null
)
