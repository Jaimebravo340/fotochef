package com.example.fotochef.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore for managing user preferences using Preferences Proto DataStore.
 * Handles:
 * - Dietary preferences (vegan, gluten-free)
 * - Theme (dark mode)
 * - Language (ES/CA)
 * - App settings
 */
private const val PREFERENCES_NAME = "fotochef_preferences"

private val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = PREFERENCES_NAME
)

class PreferencesManager(private val context: Context) {

    // === Preference Keys ===
    private companion object {
        // Dietary preferences
        val IS_VEGAN_KEY = booleanPreferencesKey("is_vegan")
        val IS_GLUTEN_FREE_KEY = booleanPreferencesKey("is_gluten_free")
        
        // Theme
        val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        
        // Language
        val LANGUAGE_KEY = stringPreferencesKey("language")
        
        // Help/Privacy
        val SHOW_PRIVACY_ACCEPTED_KEY = booleanPreferencesKey("privacy_accepted")
    }

    // === DIETARY PREFERENCES ===

    /**
     * Flow of vegan preference
     */
    fun isVeganFlow(): Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[IS_VEGAN_KEY] ?: false
    }

    /**
     * Flow of gluten-free preference
     */
    fun isGlutenFreeFlow(): Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[IS_GLUTEN_FREE_KEY] ?: false
    }

    /**
     * Set vegan preference
     */
    suspend fun setIsVegan(value: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[IS_VEGAN_KEY] = value
        }
    }

    /**
     * Set gluten-free preference
     */
    suspend fun setIsGlutenFree(value: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[IS_GLUTEN_FREE_KEY] = value
        }
    }

    // === THEME ===

    /**
     * Flow of dark mode preference
     */
    fun isDarkModeFlow(): Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[DARK_MODE_KEY] ?: false
    }

    /**
     * Set dark mode preference
     */
    suspend fun setDarkMode(value: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = value
        }
    }

    // === LANGUAGE ===

    /**
     * Flow of current language preference (default: "es")
     * Supported: "es" (Spanish), "ca" (Catalan)
     */
    fun languageFlow(): Flow<String> = context.settingsDataStore.data.map { preferences ->
        preferences[LANGUAGE_KEY] ?: "es"
    }

    /**
     * Set language preference
     */
    suspend fun setLanguage(language: String) {
        context.settingsDataStore.edit { preferences ->
            preferences[LANGUAGE_KEY] = language
        }
    }

    // === PRIVACY & HELP ===

    /**
     * Flow of privacy acceptance status
     */
    fun isPrivacyAcceptedFlow(): Flow<Boolean> = context.settingsDataStore.data.map { preferences ->
        preferences[SHOW_PRIVACY_ACCEPTED_KEY] ?: false
    }

    /**
     * Mark privacy policy as accepted
     */
    suspend fun setPrivacyAccepted(value: Boolean) {
        context.settingsDataStore.edit { preferences ->
            preferences[SHOW_PRIVACY_ACCEPTED_KEY] = value
        }
    }

    /**
     * Clear all preferences (for testing or reset)
     */
    suspend fun clearAllPreferences() {
        context.settingsDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
