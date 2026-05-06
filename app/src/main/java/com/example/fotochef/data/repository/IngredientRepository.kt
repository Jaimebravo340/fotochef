package com.example.fotochef.data.repository

import com.example.fotochef.data.local.dao.IngredientDao
import com.example.fotochef.data.local.dao.IngredientDetectatDao
import com.example.fotochef.data.local.entity.IngredientDetectat

/**
 * Repository module for handling Ingredient and IngredientDetectat data operations.
 */
class IngredientRepository(
    private val ingredientDao: IngredientDao,
    private val ingredientDetectatDao: IngredientDetectatDao
) {
    suspend fun saveDetectedIngredients(
        imagePath: String,
        detections: List<DetectedIngredientResult>,
        minConfidence: Float
    ) {
        ingredientDetectatDao.deleteByImatge(imagePath)
        val entities = detections
            .filter { it.confidence >= minConfidence }
            .distinctBy { it.name.lowercase() }
            .map { detection ->
                IngredientDetectat(
                    nom = detection.name,
                    confianca = detection.confidence,
                    imatgePath = imagePath,
                    confirmat = false
                )
            }
        ingredientDetectatDao.insertAll(entities)
    }

    suspend fun getDetectedIngredientsForImage(imagePath: String): List<IngredientDetectat> {
        return ingredientDetectatDao.getByImatgeOnce(imagePath)
    }

    fun getPantryIngredients() = ingredientDao.getPantryIngredients()

    fun getAllIngredients() = ingredientDao.getAll()

    suspend fun togglePantryStatus(id: Long, isInPantry: Boolean) {
        ingredientDao.updatePantryStatus(id, isInPantry)
    }
    
    fun searchIngredients(query: String) = ingredientDao.search(query)
}
