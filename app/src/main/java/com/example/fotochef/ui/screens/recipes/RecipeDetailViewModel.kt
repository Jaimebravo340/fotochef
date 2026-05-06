package com.example.fotochef.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.local.entity.IngredientCompra
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.repository.MockDataRepository
import com.example.fotochef.data.repository.ReceptaRepository
import com.example.fotochef.data.repository.ShoppingListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class RecipeDetailViewModel(
    private val receptaRepository: ReceptaRepository,
    private val shoppingListRepository: ShoppingListRepository
) : ViewModel() {

    // Estado interno: la receta cargada (puede ser null si aún no ha llegado)
    private val _recipe = MutableStateFlow<Recepta?>(null)
    // Estado público (solo lectura) para la UI
    val recipe: StateFlow<Recepta?> = _recipe.asStateFlow()

    fun loadRecipe(id: Long) {
        viewModelScope.launch {
            // 1) Intentamos cargar la receta desde Room (BD local)
            receptaRepository.getById(id).collect { localRecipe ->
                if (localRecipe != null) {
                    _recipe.value = localRecipe
                } else {
                    // Si no existe localmente (ej: tras reinstalar app o limpiar BD), la buscamos en Firestore
                    // 2) Plan B: buscar en remoto (favoritos en Firestore) y mostrarla igualmente
                    val firestoreRecipe = receptaRepository.getFavoriteFromFirestoreSync(id)
                    if (firestoreRecipe != null) {
                        _recipe.value = firestoreRecipe
                    }
                }
            }
        }
    }

    fun toggleFavorite() {
        // Cambia el flag de favorita en la BD (y el repo se encarga de persistirlo)
        val currentRecipe = _recipe.value ?: return
        viewModelScope.launch {
            receptaRepository.updateFavorita(currentRecipe.id, !currentRecipe.esFavorita)
        }
    }

    fun addIngredientsToShoppingList() {
        // Convierte los ingredientes de la receta en items de compra y los guarda
        val currentRecipe = _recipe.value ?: return
        viewModelScope.launch {
            // Obtenemos los ingredientes de la receta desde el MockDataRepository
            // (En un futuro esto vendría de un IngredientRepository con DAOs relacionales)
            val allIngredients = MockDataRepository.getIngredients().associateBy { it.id }
            val relations = MockDataRepository.getReceptaIngredients()
                .filter { it.receptaId == currentRecipe.id }
            
            val items = relations.mapNotNull { rel ->
                val ingredient = allIngredients[rel.ingredientId]
                if (ingredient != null) {
                    // Creamos el objeto que usa la lista de compra
                    IngredientCompra(
                        nom = ingredient.nom,
                        quantitat = rel.quantitat,
                        unitat = "", // La unidad ya viene en la cantidad del mock
                        receptaId = currentRecipe.id,
                        // Si no asignamos fecha, el filtro de Shopping por día no lo muestra.
                        scheduledDate = getStartOfDayTimestamp()
                    )
                } else null
            }
            
            if (items.isNotEmpty()) {
                // Insert masivo en la lista de compra
                shoppingListRepository.addItems(items)
            }
        }
    }

    private fun getStartOfDayTimestamp(timeInMillis: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}
