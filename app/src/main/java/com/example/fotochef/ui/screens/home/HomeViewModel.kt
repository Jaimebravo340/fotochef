package com.example.fotochef.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.repository.ReceptaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    private val receptaRepository: ReceptaRepository
) : ViewModel() {
    
    // Receta destacada (aleatoria)
    val featuredRecipe = receptaRepository.getRandomRecipe()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Últimas recetas marcadas como favoritas
    val recentFavorites = receptaRepository.getFavorites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    // Nombre del usuario actual
    val userName: String
        get() {
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            return user?.displayName ?: user?.email?.substringBefore("@") ?: "Chef"
        }
}
