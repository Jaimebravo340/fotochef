package com.example.fotochef.ui.screens.recipes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.preferences.PreferencesManager
import com.example.fotochef.data.remote.dto.AiGeneratedRecipe
import com.example.fotochef.data.repository.AiRecipeRepository
import com.example.fotochef.data.repository.FallbackRecipes
import com.example.fotochef.data.repository.MockDataRepository
import com.example.fotochef.data.repository.RateLimitException
import com.example.fotochef.data.repository.ReceptaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class RecipeMatch(
    val recipe: Recepta,
    val matchCount: Int
)

data class AiDietFilters(
    val vegan: Boolean = false,
    val glutenFree: Boolean = false,
    val lactoseFree: Boolean = false
)

class RecipesViewModel(
    private val receptaRepository: ReceptaRepository,
    private val preferencesManager: PreferencesManager,
    private val aiRecipeRepository: AiRecipeRepository,
    private val ingredientRepository: com.example.fotochef.data.repository.IngredientRepository
) : ViewModel() {

    // ─── Filtros locales ─────────────────────────────────────────────────────

    val searchQuery = MutableStateFlow("")
    val selectedDifficulty = MutableStateFlow<String?>(null)
    val maxTime = MutableStateFlow<Int?>(null)
    val isVegan = MutableStateFlow(false)
    val isGlutenFree = MutableStateFlow(false)
    val detectedIngredients = MutableStateFlow<List<String>>(emptyList())
    private val pantryIngredients = ingredientRepository.getPantryIngredients()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val preferenceVegan = preferencesManager.isVeganFlow()
    private val preferenceGlutenFree = preferencesManager.isGlutenFreeFlow()

    private val roomRecipesFlow = combine(
        searchQuery, selectedDifficulty, maxTime,
        isVegan, isGlutenFree, preferenceVegan, preferenceGlutenFree
    ) { p ->
        FilterParams(
            query = (p[0] as String).takeIf { it.isNotBlank() },
            difficulty = p[1] as String?,
            time = p[2] as Int?,
            vegan = (p[3] as Boolean) || (p[5] as Boolean),
            gluten = (p[4] as Boolean) || (p[6] as Boolean)
        )
    }.flatMapLatest { params ->
        receptaRepository.getFilteredRecipes(
            query = params.query,
            dificultat = params.difficulty,
            maxMinuts = params.time,
            esVegana = params.vegan.takeIf { it },
            esSenseGluten = params.gluten.takeIf { it }
        )
    }

    val matchedRecipes: StateFlow<List<RecipeMatch>> = combine(
        roomRecipesFlow, detectedIngredients, pantryIngredients
    ) { recipes, detected, pantry ->
        val allAvailable = (detected + pantry.map { it.nom }).distinct()
        buildRecipeMatches(recipes, allAvailable)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hasPantryIngredients: StateFlow<Boolean> = pantryIngredients
        .map { it.isNotEmpty() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    // ─── Estados IA ──────────────────────────────────────────────────────────

    val aiRecipes = MutableStateFlow<List<AiGeneratedRecipe>>(emptyList())
    val isGeneratingAi = MutableStateFlow(false)
    val aiError = MutableStateFlow<String?>(null)
    val aiDietFilters = MutableStateFlow(AiDietFilters())
    val aiPanelExpanded = MutableStateFlow(false)

    /**
     * true si las recetas mostradas son de fallback (API no disponible).
     * La UI puede mostrar un aviso discreto al usuario.
     */
    val aiUsingFallback = MutableStateFlow(false)

    // ─── Acciones ────────────────────────────────────────────────────────────

    fun setDetectedIngredients(ingredients: List<String>) {
        detectedIngredients.value = ingredients
    }

    fun generateAiRecipes() {
        if (isGeneratingAi.value) return
        val ingredients = detectedIngredients.value
        if (ingredients.isEmpty()) {
            aiError.value = "Primero escanea o añade ingredientes"
            return
        }
        callAi(ingredients, replaceAll = true)
    }

    fun loadMoreAiRecipes() {
        if (isGeneratingAi.value) return
        val ingredients = detectedIngredients.value
        if (ingredients.isEmpty()) return
        callAi(ingredients, replaceAll = false)
    }

    private fun callAi(ingredients: List<String>, replaceAll: Boolean) {
        viewModelScope.launch {
            isGeneratingAi.value = true
            aiError.value = null
            aiPanelExpanded.value = true

            val filters = aiDietFilters.value
            val excludeNames = if (replaceAll) emptyList() else aiRecipes.value.map { it.nombre }

            val result = aiRecipeRepository.generateRecipes(
                ingredients = ingredients,
                isVegan = filters.vegan,
                isGlutenFree = filters.glutenFree,
                isLactoseFree = filters.lactoseFree,
                count = 3,
                excludeNames = excludeNames
            )

            isGeneratingAi.value = false

            result.fold(
                onSuccess = { recipes ->
                    if (replaceAll) aiRecipes.value = recipes else aiRecipes.value = aiRecipes.value + recipes
                    aiError.value = null
                    aiUsingFallback.value = false
                },
                onFailure = { error ->
                    // Ante cualquier fallo (rate limit, sin conexión, etc.)
                    // mostramos recetas de fallback para que la feature siempre funcione
                    val fallback = FallbackRecipes.get(
                        count = 3,
                        isVegan = filters.vegan,
                        isGlutenFree = filters.glutenFree,
                        isLactoseFree = filters.lactoseFree,
                        excludeNames = excludeNames
                    )

                    if (fallback.isNotEmpty()) {
                        if (replaceAll) aiRecipes.value = fallback else aiRecipes.value = aiRecipes.value + fallback
                        aiUsingFallback.value = true
                        aiError.value = null
                    } else {
                        // No quedan recetas de fallback con esos filtros
                        aiError.value = when (error) {
                            is RateLimitException -> "Límite de API alcanzado y no hay más ejemplos disponibles con estos filtros."
                            else -> error.message ?: "Error desconocido"
                        }
                    }
                }
            )
        }
    }

    fun setAiDietFilter(
        vegan: Boolean? = null,
        glutenFree: Boolean? = null,
        lactoseFree: Boolean? = null
    ) {
        val c = aiDietFilters.value
        aiDietFilters.value = c.copy(
            vegan = vegan ?: c.vegan,
            glutenFree = glutenFree ?: c.glutenFree,
            lactoseFree = lactoseFree ?: c.lactoseFree
        )
    }

    fun clearAiRecipes() {
        aiRecipes.value = emptyList()
        aiError.value = null
        aiUsingFallback.value = false
        aiPanelExpanded.value = false
    }

    fun clearFilters() {
        searchQuery.value = ""
        selectedDifficulty.value = null
        maxTime.value = null
        isVegan.value = false
        isGlutenFree.value = false
    }

    private fun buildRecipeMatches(recipes: List<Recepta>, detected: List<String>): List<RecipeMatch> {
        val ingredientsById = MockDataRepository.getIngredients().associateBy { it.id }
        val recipeIngredients = MockDataRepository.getReceptaIngredients().groupBy { it.receptaId }
        val selected = detected.map { it.trim().lowercase() }.filter { it.isNotEmpty() }.toSet()

        return recipes.mapNotNull { recipe ->
            val names = recipeIngredients[recipe.id]
                .orEmpty()
                .mapNotNull { ingredientsById[it.ingredientId]?.nom?.lowercase() }
                .toSet()
            val matchCount = selected.count { it in names }
            if (selected.isEmpty() || matchCount > 0) RecipeMatch(recipe, matchCount) else null
        }.sortedByDescending { it.matchCount }
    }

    private data class FilterParams(
        val query: String?, val difficulty: String?,
        val time: Int?, val vegan: Boolean, val gluten: Boolean
    )

    fun saveAiRecipe(recipe: AiGeneratedRecipe) {
        viewModelScope.launch {
            // Utilizamos el receptaRepository para guardar la receta y los ingredientes
            receptaRepository.saveAiRecipeAsFavorite(recipe)
        }
    }

    fun viewAiRecipe(recipe: AiGeneratedRecipe, onRecipeSaved: (Long) -> Unit) {
        viewModelScope.launch {
            val recipeId = receptaRepository.saveAiRecipeLocal(recipe)
            onRecipeSaved(recipeId)
        }
    }
}
