package com.example.fotochef.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.fotochef.ui.ViewModelFactory
import com.example.fotochef.ui.screens.favorites.FavoritesScreen
import com.example.fotochef.ui.screens.favorites.FavoritesViewModel
import com.example.fotochef.ui.screens.help.HelpScreen
import com.example.fotochef.ui.screens.help.PrivacyScreen
import com.example.fotochef.ui.screens.home.HomeScreen
import com.example.fotochef.ui.screens.home.HomeViewModel
import com.example.fotochef.ui.screens.ingredients.IngredientsScreen
import com.example.fotochef.ui.screens.recipes.RecipeDetailScreen
import com.example.fotochef.ui.screens.recipes.RecipeDetailViewModel
import com.example.fotochef.ui.screens.recipes.RecipesScreen
import com.example.fotochef.ui.screens.recipes.RecipesViewModel
import com.example.fotochef.ui.screens.scan.CameraScreen
import com.example.fotochef.ui.screens.scan.DetectionScreen
import com.example.fotochef.ui.screens.scan.ScanFlowViewModel
import com.example.fotochef.ui.screens.settings.SettingsScreen
import com.example.fotochef.ui.screens.settings.SettingsViewModel
import com.example.fotochef.ui.screens.shopping.ShoppingListScreen
import com.example.fotochef.ui.screens.shopping.ShoppingListViewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.fotochef.ui.screens.auth.AuthViewModel
import com.example.fotochef.ui.screens.auth.LoginScreen
import com.example.fotochef.ui.screens.auth.RegisterScreen
import com.example.fotochef.ui.screens.onboarding.OnboardingScreen

import com.example.fotochef.ui.screens.recipes.CookingModeScreen
import java.net.URLEncoder
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.runtime.rememberCoroutineScope
import com.example.fotochef.FotoChefApplication
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun MainNavGraph(
    navController: NavHostController,
    startDestination: String,
    innerPadding: PaddingValues
) {
    // ViewModel que guarda el estado del flujo de escaneo (foto → detección → ingredientes)
    val scanFlowViewModel: ScanFlowViewModel = viewModel(factory = ViewModelFactory)

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = Modifier.padding(innerPadding)
    ) {
        composable(Screen.Onboarding.route) {
            // Onboarding: se marca como completado y se redirige a Login o Home
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val onboardingRepo = (context.applicationContext as FotoChefApplication).appContainer.userPreferencesRepository
            val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory)
            
            OnboardingScreen(
                onFinished = {
                    scope.launch {
                        // Guardamos que el usuario ya ha visto el onboarding
                        onboardingRepo.setOnboardingCompleted(true)
                        val nextDest = if (authViewModel.isUserLoggedIn()) Screen.Home.route else Screen.Login.route
                        navController.navigate(nextDest) {
                            // Quitamos onboarding del backstack para que no se vuelva atrás
                            popUpTo(Screen.Onboarding.route) { inclusive = true }
                        }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            // Login: si va bien, saltamos a Home y eliminamos Login del backstack
            val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory)
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onLoginSuccess = { 
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Register.route) {
            // Register: si va bien, saltamos a Home y eliminamos Login del backstack
            val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory)
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            // Home: pantalla principal con accesos rápidos
            val viewModel: HomeViewModel = viewModel(factory = ViewModelFactory)
            HomeScreen(
                viewModel = viewModel,
                onNavigateToScan = { navController.navigate(Screen.Camera.route) },
                onNavigateToRecipes = { navController.navigate(Screen.Recipes.route) },
                onNavigateToShopping = { navController.navigate(Screen.ShoppingList.route) },
                onNavigateToFavorites = { navController.navigate(Screen.Favorites.route) },
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }
        composable(Screen.Camera.route) {
            // Cámara: al capturar foto se guarda el path en el ViewModel y se navega a Detection
            CameraScreen(
                onPhotoCaptured = { path ->
                    scanFlowViewModel.onPhotoCaptured(path)
                    navController.navigate(Screen.Detection.route) {
                        // No pop the Camera from stack so user can go back
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Screen.Detection.route) {
            // Detección: muestra ingredientes detectados y permite editar antes de continuar
            DetectionScreen(
                imagePath = scanFlowViewModel.photoPath,
                detectedIngredients = scanFlowViewModel.detectedIngredients,
                averageConfidence = scanFlowViewModel.averageConfidence,
                isPrecisionValid = scanFlowViewModel.isPrecisionValid,
                isLoading = scanFlowViewModel.isLoading,
                errorMessage = scanFlowViewModel.errorMessage,
                onIngredientsChange = { newIngredients ->
                    scanFlowViewModel.updateDetectedIngredients(newIngredients)
                },
                onContinueToRecipes = {
                    navController.navigate(Screen.Recipes.route)
                }
            )
        }
        composable(Screen.Recipes.route) {
            // Recetas: usa los ingredientes detectados (si vienen del escaneo) para sugerir recetas
            val viewModel: RecipesViewModel = viewModel(factory = ViewModelFactory)
            RecipesScreen(
                viewModel = viewModel,
                detectedIngredients = scanFlowViewModel.detectedIngredients,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(navArgument("recipeId") { type = NavType.LongType })
        ) { backStackEntry ->
            // Detalle: lee el id de la receta desde la ruta
            val recipeId = backStackEntry.arguments?.getLong("recipeId") ?: 0L
            val viewModel: RecipeDetailViewModel = viewModel(factory = ViewModelFactory)
            RecipeDetailScreen(
                recipeId = recipeId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() },
                onStartCooking = { name, steps ->
                    // Se codifican los pasos para poder pasarlos por la ruta sin romper caracteres especiales
                    val encodedSteps = URLEncoder.encode(steps, StandardCharsets.UTF_8.toString())
                    navController.navigate(Screen.CookingMode.createRoute(name, encodedSteps))
                }
            )
        }
        composable(
            route = Screen.CookingMode.route,
            arguments = listOf(
                navArgument("recipeName") { type = NavType.StringType },
                navArgument("stepsJson") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            // CookingMode: decodifica los parámetros que venían en la ruta
            val name = backStackEntry.arguments?.getString("recipeName") ?: ""
            val encodedSteps = backStackEntry.arguments?.getString("stepsJson") ?: "[]"
            val steps = URLDecoder.decode(encodedSteps, StandardCharsets.UTF_8.toString())
            
            CookingModeScreen(
                recipeName = name,
                stepsJson = steps,
                onFinished = { navController.popBackStack() },
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Ingredients.route) {
            // Pantalla de ingredientes / nevera
            IngredientsScreen()
        }
        composable(Screen.ShoppingList.route) {
            // Lista de la compra
            val viewModel: ShoppingListViewModel = viewModel(factory = ViewModelFactory)
            ShoppingListScreen(viewModel = viewModel)
        }
        composable(Screen.Favorites.route) {
            // Favoritos
            val viewModel: FavoritesViewModel = viewModel(factory = ViewModelFactory)
            FavoritesScreen(
                viewModel = viewModel,
                onRecipeClick = { recipeId ->
                    navController.navigate(Screen.RecipeDetail.createRoute(recipeId))
                }
            )
        }

        composable(Screen.Help.route) {
            // Ayuda
            HelpScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Privacy.route) {
            // Privacidad
            PrivacyScreen(
                onBackPressed = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Profile.route) {
            // Perfil/Ajustes: aquí se ofrece cerrar sesión y navegar a ayuda/privacidad
            val viewModel: SettingsViewModel = viewModel(factory = ViewModelFactory)
            val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory)
            SettingsScreen(
                viewModel = viewModel,
                onNavigateToHelp = {
                    navController.navigate(Screen.Help.route)
                },
                onNavigateToPrivacy = {
                    navController.navigate(Screen.Privacy.route)
                },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        // Limpia todo el backstack: al salir no se puede volver “atrás” a pantallas privadas
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
