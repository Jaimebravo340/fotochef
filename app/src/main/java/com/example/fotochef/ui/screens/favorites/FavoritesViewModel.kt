package com.example.fotochef.ui.screens.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.repository.ReceptaRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FavoritesViewModel(
    private val receptaRepository: ReceptaRepository
) : ViewModel() {

    // Flujo de recetas favoritas directamente desde la base de datos
    val favorites: StateFlow<List<Recepta>> = receptaRepository.getFavorites()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            receptaRepository.updateFavorita(id, isFavorite)
        }
    }
}
