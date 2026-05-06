package com.example.fotochef.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.fotochef.data.local.entity.Ingredient
import com.example.fotochef.data.local.entity.ReceptaIngredient
import kotlinx.coroutines.flow.Flow

/**
 * DAO para gestionar ingredientes y sus relaciones con recetas.
 */
@Dao
interface IngredientDao {

    // Obtener todos los ingredientes ordenados por nombre
    @Query("SELECT * FROM ingredients ORDER BY nom ASC")
    fun getAll(): Flow<List<Ingredient>>

    // Obtener un ingrediente por su ID
    @Query("SELECT * FROM ingredients WHERE id = :id")
    fun getById(id: Long): Flow<Ingredient?>

    // Obtener ingredientes por categoría
    @Query("SELECT * FROM ingredients WHERE categoria = :categoria ORDER BY nom ASC")
    fun getByCategoria(categoria: String): Flow<List<Ingredient>>

    // Buscar ingredientes por nombre (Flow)
    @Query("SELECT * FROM ingredients WHERE nom LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Ingredient>>

    // Buscar ingrediente exacto por nombre
    @Query("SELECT * FROM ingredients WHERE nom = :nom LIMIT 1")
    suspend fun getByName(nom: String): Ingredient?

    // Obtener los ingredientes de una receta concreta (a través de la tabla intermedia)
    @Query("""
        SELECT i.* FROM ingredients i
        INNER JOIN recepta_ingredient ri ON i.id = ri.ingredientId
        WHERE ri.receptaId = :receptaId
        ORDER BY i.nom ASC
    """)
    fun getByReceptaId(receptaId: Long): Flow<List<Ingredient>>

    // Insertar un ingrediente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(ingredient: Ingredient): Long

    // Insertar varios ingredientes a la vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ingredients: List<Ingredient>)

    // Insertar una relación receta-ingrediente
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReceptaIngredient(receptaIngredient: ReceptaIngredient)

    // Insertar varias relaciones receta-ingrediente a la vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllReceptaIngredients(relations: List<ReceptaIngredient>)

    // Obtener las cantidades de ingredientes para una receta
    @Query("SELECT * FROM recepta_ingredient WHERE receptaId = :receptaId")
    fun getReceptaIngredients(receptaId: Long): Flow<List<ReceptaIngredient>>
    // Obtener los ingredientes que están en la despensa
    @Query("SELECT * FROM ingredients WHERE isInPantry = 1 ORDER BY nom ASC")
    fun getPantryIngredients(): Flow<List<Ingredient>>

    // Actualizar el estado de la despensa de un ingrediente
    @Query("UPDATE ingredients SET isInPantry = :isInPantry WHERE id = :id")
    suspend fun updatePantryStatus(id: Long, isInPantry: Boolean)
}
