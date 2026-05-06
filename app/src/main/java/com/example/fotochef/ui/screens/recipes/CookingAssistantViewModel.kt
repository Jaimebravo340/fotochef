package com.example.fotochef.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.repository.AiRecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class CookingAssistantViewModel(
    private val aiRecipeRepository: AiRecipeRepository
) : ViewModel() {

    private val _messages = MutableStateFlow<List<ChatMessage>>(listOf(
        ChatMessage("¡Hola! Soy tu asistente de FotoChef. ¿Tienes alguna duda con la receta?", false)
    ))
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendMessage(text: String, recipeContext: String) {
        if (text.isBlank()) return
        
        val userMsg = ChatMessage(text, true)
        _messages.value = _messages.value + userMsg
        
        _isLoading.value = true
        viewModelScope.launch {
            // Simplified call to Gemini (using the same repository logic but for questions)
            // In a real app, we'd have a specific prompt for assistant
            val response = aiRecipeRepository.askAboutRecipe(text, recipeContext)
            
            _messages.value = _messages.value + ChatMessage(response, false)
            _isLoading.value = false
        }
    }
}
