package com.example.fotochef.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fotochef.data.local.entity.IngredientCompra
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Query("SELECT * FROM ingredients_compra ORDER BY createdAt DESC")
    fun getAll(): Flow<List<IngredientCompra>>

    @Query("SELECT * FROM ingredients_compra WHERE isComprat = :isComprat ORDER BY createdAt DESC")
    fun getByStatus(isComprat: Boolean): Flow<List<IngredientCompra>>

    @Query("SELECT * FROM ingredients_compra WHERE isComprat = :isComprat AND (scheduledDate = :date OR scheduledDate IS NULL) ORDER BY createdAt DESC")
    fun getByStatusAndDate(isComprat: Boolean, date: Long): Flow<List<IngredientCompra>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: IngredientCompra): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<IngredientCompra>)

    @Update
    suspend fun update(item: IngredientCompra)

    @Query("UPDATE ingredients_compra SET isComprat = :isComprat WHERE id = :id")
    suspend fun updateStatus(id: Long, isComprat: Boolean)

    @Delete
    suspend fun delete(item: IngredientCompra)

    @Query("DELETE FROM ingredients_compra WHERE id = :id")
    suspend fun deleteById(id: Long)
    
    @Query("DELETE FROM ingredients_compra WHERE isComprat = 1")
    suspend fun clearPurchased()
}
