package com.example.fotochef.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Kitchen
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    // Pantallas de autenticación / inicio
    object Login : Screen("login", "Login", Icons.Filled.Home)
    object Register : Screen("register", "Register", Icons.Filled.Home)

    // Pantallas principales
    object Home : Screen("home", "Home", Icons.Filled.Home)
    object Camera : Screen("camera", "Camera", Icons.Filled.Home)
    object Detection : Screen("detection", "Detection", Icons.Filled.Home)
    object Recipes : Screen("recipes", "Recipes", Icons.Filled.MenuBook)

    // Pantalla con parámetro (recipeId) en la ruta
    object RecipeDetail : Screen("recipe_detail/{recipeId}", "Recipe Detail", Icons.Filled.MenuBook) {
        fun createRoute(recipeId: Long) = "recipe_detail/$recipeId"
    }
    object Ingredients : Screen("ingredients", "Fridge", Icons.Filled.Kitchen)
    object ShoppingList : Screen("shopping_list", "Compra", Icons.Filled.ShoppingCart)
    object Favorites : Screen("favorites", "Favorites", Icons.Filled.Favorite)
    object Settings : Screen("settings", "Settings", Icons.Filled.Settings)
    object Help : Screen("help", "Help", Icons.Filled.Home)
    object Privacy : Screen("privacy", "Privacy", Icons.Filled.Home)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Filled.Home)
    object Profile : Screen("profile", "Perfil", Icons.Filled.Person)

    // Pantalla “modo cocina” que recibe nombre y pasos codificados en la ruta
    object CookingMode : Screen("cooking_mode/{recipeName}/{stepsJson}", "Cooking Mode", Icons.Filled.Restaurant) {
        fun createRoute(recipeName: String, stepsJson: String) = "cooking_mode/$recipeName/$stepsJson"
    }

    companion object {
        // Elementos que aparecen en la barra inferior (bottom navigation).
        // Se define como getter para evitar problemas de inicialización en sealed class.
        val bottomNavItems: List<Screen> 
            get() = listOf(Home, Recipes, ShoppingList, Favorites, Profile)
    }
}
