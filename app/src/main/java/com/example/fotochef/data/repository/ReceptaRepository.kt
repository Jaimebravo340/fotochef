package com.example.fotochef.data.repository

import com.example.fotochef.data.local.dao.ReceptaDao

/**
 * Repository module for handling Recepta data operations.
 */
import com.example.fotochef.data.local.dao.IngredientDao
import com.example.fotochef.data.local.entity.Ingredient
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.local.entity.ReceptaIngredient
import com.example.fotochef.data.remote.dto.AiGeneratedRecipe
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await

/**
 * Repository module for handling Recepta data operations.
 */
class ReceptaRepository(
    private val receptaDao: ReceptaDao,
    private val ingredientDao: IngredientDao
) {
    private val gson = Gson()
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val userId: String?
        get() = auth.currentUser?.uid
    // Obtener recetas con filtros
    fun getFilteredRecipes(
        query: String? = null,
        dificultat: String? = null,
        maxMinuts: Int? = null,
        esVegana: Boolean? = null,
        esSenseGluten: Boolean? = null
    ) = receptaDao.getFilteredRecipes(query, dificultat, maxMinuts, esVegana, esSenseGluten)

    // Obtener receta por ID
    fun getById(id: Long) = receptaDao.getById(id)

    // Obtener favoritas desde Firestore
    fun getFavorites(): Flow<List<Recepta>> {
        val uid = userId ?: return flowOf(emptyList())
        
        return callbackFlow {
            val listener = firestore.collection("users").document(uid)
                .collection("favorites")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error)
                        return@addSnapshotListener
                    }
                    
                    val recipes = snapshot?.documents?.mapNotNull { doc ->
                        doc.toObject(Recepta::class.java)?.copy(id = doc.id.toLongOrNull() ?: 0L)
                    } ?: emptyList()
                    
                    trySend(recipes)
                }
            awaitClose { listener.remove() }
        }
    }

    // Obtener receta favorita individual desde Firestore (por si no existe localmente)
    suspend fun getFavoriteFromFirestoreSync(id: Long): Recepta? {
        val uid = userId ?: return null
        return try {
            val doc = firestore.collection("users").document(uid)
                .collection("favorites").document(id.toString())
                .get().await()
            doc.toObject(Recepta::class.java)?.copy(id = id)
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar favorito en Firestore
    suspend fun updateFavorita(id: Long, esFavorita: Boolean) {
        val uid = userId ?: return
        val recipe = receptaDao.getByIdSync(id) ?: return // Necesitamos obtener la receta local primero
        
        if (esFavorita) {
            firestore.collection("users").document(uid)
                .collection("favorites").document(id.toString())
                .set(recipe.copy(esFavorita = true)).await()
        } else {
            firestore.collection("users").document(uid)
                .collection("favorites").document(id.toString())
                .delete().await()
        }
        
        // También actualizamos local por si acaso
        receptaDao.updateFavorita(id, esFavorita)
    }

    // Eliminar todas las recetas
    suspend fun deleteAllRecipes() = receptaDao.deleteAll()

    // Obtener una receta aleatoria (S7-Final)
    fun getRandomRecipe() = receptaDao.getRandomRecipe()

    // Guardar una receta generada por IA en favoritos
    suspend fun saveAiRecipeAsFavorite(aiRecipe: AiGeneratedRecipe) {
        val recepta = Recepta(
            nom = aiRecipe.nombre,
            descripcio = aiRecipe.descripcion,
            tempsPreparacio = aiRecipe.tiempoMinutos,
            dificultat = aiRecipe.dificultad,
            esFavorita = true,
            passos = gson.toJson(aiRecipe.pasos),
            esVegana = aiRecipe.esVegana,
            esSenseGluten = aiRecipe.esSinGluten,
            categoria = "Principal" // Default category
        )
        
        val receptaId = receptaDao.insert(recepta)
        val finalRecepta = recepta.copy(id = receptaId)
        
        // Guardar en Firestore también
        userId?.let { uid ->
            firestore.collection("users").document(uid)
                .collection("favorites").document(receptaId.toString())
                .set(finalRecepta).await()
        }
        
        // Save ingredients
        for (ingredientName in aiRecipe.ingredientesNecesarios) {
            val formattedName = ingredientName.trim().lowercase().replaceFirstChar { it.uppercase() }
            
            // Check if ingredient exists
            var ingredient = ingredientDao.getByName(formattedName)
            if (ingredient == null) {
                // Insert new ingredient
                val newIngredient = Ingredient(nom = formattedName, categoria = "Otro")
                val ingredientId = ingredientDao.insert(newIngredient)
                ingredient = newIngredient.copy(id = ingredientId)
            }
            
            // Link ingredient to recipe
            ingredientDao.insertReceptaIngredient(
                ReceptaIngredient(
                    receptaId = receptaId,
                    ingredientId = ingredient.id,
                    quantitat = "" // No specific quantity string from AI currently
                )
            )
        }
    }

    // Guardar una receta generada por IA localmente para poder ver sus detalles sin ser favorita
    suspend fun saveAiRecipeLocal(aiRecipe: AiGeneratedRecipe): Long {
        val recepta = Recepta(
            nom = aiRecipe.nombre,
            descripcio = aiRecipe.descripcion,
            tempsPreparacio = aiRecipe.tiempoMinutos,
            dificultat = aiRecipe.dificultad,
            esFavorita = false, // No es favorita por defecto
            passos = gson.toJson(aiRecipe.pasos),
            esVegana = aiRecipe.esVegana,
            esSenseGluten = aiRecipe.esSinGluten,
            categoria = "Principal" // Default category
        )
        
        val receptaId = receptaDao.insert(recepta)
        
        // Save ingredients
        for (ingredientName in aiRecipe.ingredientesNecesarios) {
            val formattedName = ingredientName.trim().lowercase().replaceFirstChar { it.uppercase() }
            
            var ingredient = ingredientDao.getByName(formattedName)
            if (ingredient == null) {
                val newIngredient = Ingredient(nom = formattedName, categoria = "Otro")
                val ingredientId = ingredientDao.insert(newIngredient)
                ingredient = newIngredient.copy(id = ingredientId)
            }
            
            ingredientDao.insertReceptaIngredient(
                ReceptaIngredient(
                    receptaId = receptaId,
                    ingredientId = ingredient.id,
                    quantitat = "" 
                )
            )
        }
        return receptaId
    }
}
