package com.example.fotochef.ui.screens.ingredients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.local.entity.Ingredient
import com.example.fotochef.data.repository.IngredientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class IngredientsViewModel(
    private val ingredientRepository: IngredientRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val pantryIngredients: StateFlow<List<Ingredient>> = ingredientRepository.getPantryIngredients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val searchResults: StateFlow<List<Ingredient>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) {
                ingredientRepository.getAllIngredients()
            } else {
                ingredientRepository.searchIngredients(query)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun togglePantryStatus(ingredient: Ingredient) {
        viewModelScope.launch {
            ingredientRepository.togglePantryStatus(ingredient.id, !ingredient.isInPantry)
        }
    }
}
