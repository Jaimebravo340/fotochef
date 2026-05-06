package com.example.fotochef.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fotochef.data.local.entity.IngredientDetectat
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar ingredientes detectados por la IA (Google Vision API).
 */
@Dao
interface IngredientDetectatDao {

    // Obtener todos los ingredientes detectados, ordenados por timestamp (más recientes primero)
    @Query("SELECT * FROM ingredients_detectats ORDER BY timestamp DESC")
    fun getAll(): Flow<List<IngredientDetectat>>

    // Obtener ingredientes detectados con confianza mínima (filtrar baja calidad)
    @Query("SELECT * FROM ingredients_detectats WHERE confianca >= :minConfianca ORDER BY confianca DESC")
    fun getByConfiancaMinima(minConfianca: Float): Flow<List<IngredientDetectat>>

    // Obtener ingredientes detectados de una imagen específica
    @Query("SELECT * FROM ingredients_detectats WHERE imatgePath = :imatgePath ORDER BY confianca DESC")
    fun getByImatge(imatgePath: String): Flow<List<IngredientDetectat>>

    @Query("SELECT * FROM ingredients_detectats WHERE imatgePath = :imatgePath ORDER BY confianca DESC")
    suspend fun getByImatgeOnce(imatgePath: String): List<IngredientDetectat>

    // Obtener solo los ingredientes confirmados por el usuario
    @Query("SELECT * FROM ingredients_detectats WHERE confirmat = 1 ORDER BY nom ASC")
    fun getConfirmats(): Flow<List<IngredientDetectat>>

    // Insertar un ingrediente detectado
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredientDetectat: IngredientDetectat): Long

    // Insertar varios ingredientes detectados a la vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<IngredientDetectat>)

    // Confirmar un ingrediente detectado manualmente
    @Query("UPDATE ingredients_detectats SET confirmat = :confirmat WHERE id = :id")
    suspend fun updateConfirmat(id: Long, confirmat: Boolean)

    // Eliminar todos los ingredientes detectados
    @Query("DELETE FROM ingredients_detectats")
    suspend fun deleteAll()

    // Eliminar ingredientes detectados de una imagen específica
    @Query("DELETE FROM ingredients_detectats WHERE imatgePath = :imatgePath")
    suspend fun deleteByImatge(imatgePath: String)
}
