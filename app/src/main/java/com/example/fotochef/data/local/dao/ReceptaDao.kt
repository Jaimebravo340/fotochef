package com.example.fotochef.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fotochef.data.local.entity.Recepta
import kotlinx.coroutines.flow.Flow

/**
 * DAO para acceder y gestionar recetas en la base de datos.
 * Usa Flow para observar cambios reactivamente desde la UI.
 */
@Dao
interface ReceptaDao {

    // Obtener todas las recetas ordenadas por nombre
    @Query("SELECT * FROM receptes ORDER BY nom ASC")
    fun getAll(): Flow<List<Recepta>>

    // Obtener una receta por su ID
    @Query("SELECT * FROM receptes WHERE id = :id")
    fun getById(id: Long): Flow<Recepta?>

    @Query("SELECT * FROM receptes WHERE id = :id")
    suspend fun getByIdSync(id: Long): Recepta?

    // Obtener solo las recetas favoritas
    @Query("SELECT * FROM receptes WHERE esFavorita = 1 ORDER BY nom ASC")
    fun getFavorites(): Flow<List<Recepta>>

    // Buscar recetas por nombre o descripción
    @Query("SELECT * FROM receptes WHERE nom LIKE '%' || :query || '%' OR descripcio LIKE '%' || :query || '%'")
    fun search(query: String): Flow<List<Recepta>>

    // Filtrar recetas por dificultad
    @Query("SELECT * FROM receptes WHERE dificultat = :dificultat ORDER BY nom ASC")
    fun getByDificultat(dificultat: String): Flow<List<Recepta>>

    // Filtrar recetas por tiempo máximo de preparación
    @Query("SELECT * FROM receptes WHERE tempsPreparacio <= :maxMinuts ORDER BY tempsPreparacio ASC")
    fun getByTempsMax(maxMinuts: Int): Flow<List<Recepta>>

    // Filtrar recetas por categoría
    @Query("SELECT * FROM receptes WHERE categoria = :categoria ORDER BY nom ASC")
    fun getByCategoria(categoria: String): Flow<List<Recepta>>

    // Filtro múltiple para recetas (S5-05)
    @Query("""
        SELECT * FROM receptes 
        WHERE (:query IS NULL OR nom LIKE '%' || :query || '%' OR descripcio LIKE '%' || :query || '%')
        AND (:dificultat IS NULL OR dificultat = :dificultat OR 
             (:dificultat = 'Fácil' AND (dificultat = 'Fàcil' OR dificultat = 'Facil')) OR 
             (:dificultat = 'Media' AND (dificultat = 'Mitjà' OR dificultat = 'Medio')) OR 
             (:dificultat = 'Difícil' AND dificultat = 'Dificil'))
        AND (:maxMinuts IS NULL OR tempsPreparacio <= :maxMinuts)
        AND (:esVegana IS NULL OR esVegana = :esVegana)
        AND (:esSenseGluten IS NULL OR esSenseGluten = :esSenseGluten)
        ORDER BY nom ASC
    """)
    fun getFilteredRecipes(
        query: String?,
        dificultat: String?,
        maxMinuts: Int?,
        esVegana: Boolean?,
        esSenseGluten: Boolean?
    ): Flow<List<Recepta>>

    // Insertar una receta (reemplazar si ya existe)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recepta: Recepta): Long

    // Insertar varias recetas a la vez
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(receptes: List<Recepta>)

    // Actualizar una receta existente
    @Update
    suspend fun update(recepta: Recepta)

    // Eliminar una receta
    @Delete
    suspend fun delete(recepta: Recepta)

    // Cambiar el estado de favorito de una receta
    @Query("UPDATE receptes SET esFavorita = :esFavorita WHERE id = :id")
    suspend fun updateFavorita(id: Long, esFavorita: Boolean)

    // Eliminar todas las recetas
    @Query("DELETE FROM receptes")
    suspend fun deleteAll()

    // Obtener una receta aleatoria (S7-Final)
    @Query("SELECT * FROM receptes ORDER BY RANDOM() LIMIT 1")
    fun getRandomRecipe(): Flow<Recepta?>
}
