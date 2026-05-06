package com.example.fotochef.ui

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fotochef.FotoChefApplication
import com.example.fotochef.ui.screens.favorites.FavoritesViewModel
import com.example.fotochef.ui.screens.home.HomeViewModel
import com.example.fotochef.ui.screens.recipes.RecipeDetailViewModel
import com.example.fotochef.ui.screens.recipes.RecipesViewModel
import com.example.fotochef.ui.screens.scan.ScanFlowViewModel
import com.example.fotochef.ui.screens.settings.SettingsViewModel
import com.example.fotochef.ui.screens.shopping.ShoppingListViewModel

/**
 * Extension to provide the [FotoChefApplication] from the CreationExtras.
 */
fun CreationExtras.fotoChefApplication(): FotoChefApplication =
    // Recupera la instancia de la Application desde el “contexto” interno del ViewModelProvider
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FotoChefApplication)

/**
 * Centralized ViewModelFactory to inject dependencies manually into ViewModels.
 */
val ViewModelFactory = viewModelFactory {
    initializer {
        // Cada initializer crea un ViewModel e “inyecta” lo que necesita desde AppContainer
        val appContainer = fotoChefApplication().appContainer
        HomeViewModel(
            receptaRepository = appContainer.receptaRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        FavoritesViewModel(
            receptaRepository = appContainer.receptaRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        SettingsViewModel(
            preferencesManager = appContainer.preferencesManager,
            receptaRepository = appContainer.receptaRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        ScanFlowViewModel(
            visionRepository = appContainer.visionRepository,
            ingredientRepository = appContainer.ingredientRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        RecipesViewModel(
            receptaRepository = appContainer.receptaRepository,
            preferencesManager = appContainer.preferencesManager,
            aiRecipeRepository = appContainer.aiRecipeRepository,
            ingredientRepository = appContainer.ingredientRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        RecipeDetailViewModel(
            receptaRepository = appContainer.receptaRepository,
            shoppingListRepository = appContainer.shoppingListRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        ShoppingListViewModel(
            shoppingListRepository = appContainer.shoppingListRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        com.example.fotochef.ui.screens.auth.AuthViewModel(
            authRepository = appContainer.authRepository
        )
    }
    initializer {
        val appContainer = fotoChefApplication().appContainer
        com.example.fotochef.ui.screens.ingredients.IngredientsViewModel(
            ingredientRepository = appContainer.ingredientRepository
        )
    }

    initializer {
        val appContainer = fotoChefApplication().appContainer
        com.example.fotochef.ui.screens.recipes.CookingAssistantViewModel(
            aiRecipeRepository = appContainer.aiRecipeRepository
        )
    }
}
