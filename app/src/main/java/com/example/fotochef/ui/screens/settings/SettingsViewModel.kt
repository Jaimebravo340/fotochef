package com.example.fotochef.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.preferences.PreferencesManager
import com.example.fotochef.data.repository.ReceptaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la pantalla de Ajustes.
 * Gestiona:
 * - Preferencias dietéticas (vegano, sin gluten)
 * - Tema (modo oscuro)
 * - Idioma
 * - Configuración de privacidad
 */
class SettingsViewModel(
    private val preferencesManager: PreferencesManager,
    private val receptaRepository: ReceptaRepository
) : ViewModel() {

    // === Dietary Preferences ===

    val isVeganFlow: Flow<Boolean> = preferencesManager.isVeganFlow()
    val isGlutenFreeFlow: Flow<Boolean> = preferencesManager.isGlutenFreeFlow()

    fun setIsVegan(value: Boolean) {
        viewModelScope.launch {
            preferencesManager.setIsVegan(value)
        }
    }

    fun setIsGlutenFree(value: Boolean) {
        viewModelScope.launch {
            preferencesManager.setIsGlutenFree(value)
        }
    }

    // === Theme ===

    val isDarkModeFlow: Flow<Boolean> = preferencesManager.isDarkModeFlow()

    fun setDarkMode(value: Boolean) {
        viewModelScope.launch {
            preferencesManager.setDarkMode(value)
        }
    }

    // === Language ===

    val languageFlow: Flow<String> = preferencesManager.languageFlow()

    fun setLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
        }
    }

    // === Privacy ===

    val isPrivacyAcceptedFlow: Flow<Boolean> = preferencesManager.isPrivacyAcceptedFlow()

    fun setPrivacyAccepted(value: Boolean) {
        viewModelScope.launch {
            preferencesManager.setPrivacyAccepted(value)
        }
    }

    // === Clear All Data ===

    private val _isClearingData = MutableStateFlow(false)
    val isClearingData: StateFlow<Boolean> = _isClearingData.asStateFlow()

    fun clearAllData() {
        viewModelScope.launch {
            _isClearingData.value = true
            try {
                // Clear recipes
                receptaRepository.deleteAllRecipes()
                // Clear preferences
                preferencesManager.clearAllPreferences()
            } finally {
                _isClearingData.value = false
            }
        }
    }
}
