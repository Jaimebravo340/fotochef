package com.example.fotochef

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.fotochef.data.preferences.PreferencesManager
import com.example.fotochef.ui.ViewModelFactory
import com.example.fotochef.ui.navigation.BottomNavigationBar
import com.example.fotochef.ui.navigation.MainNavGraph
import com.example.fotochef.ui.navigation.Screen
import com.example.fotochef.ui.screens.auth.AuthViewModel
import com.example.fotochef.ui.theme.FotochefTheme

/**
 * Activity principal de FotoChef.
 * Maneja:
 * - Tema oscuro/claro basado en preferencias
 * - Selección de idioma
 * - Navegación principal
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Deja que el contenido se dibuje detrás de las barras del sistema (status/nav bar)
        enableEdgeToEdge()
        
        // Acceso a preferencias simples (por ejemplo: modo oscuro)
        val preferencesManager = PreferencesManager(this)
        
        setContent {
            // Lee el modo oscuro en tiempo real: si el usuario lo cambia, la UI se recompone
            val isDarkMode by preferencesManager.isDarkModeFlow().collectAsState(initial = false)
            
            // ViewModel de autenticación: nos dice si hay sesión iniciada
            val authViewModel: AuthViewModel = viewModel(factory = ViewModelFactory)

            // Preferencias de onboarding (si ya se vieron las pantallas de introducción)
            val onboardingRepo = (applicationContext as FotoChefApplication).appContainer.userPreferencesRepository
            val onboardingCompleted by onboardingRepo.isOnboardingCompleted.collectAsState(initial = true)
            
            // Decide a qué pantalla entrar primero según el estado de la app
            val startDestination = when {
                !onboardingCompleted -> Screen.Onboarding.route
                authViewModel.isUserLoggedIn() -> Screen.Home.route
                else -> Screen.Login.route
            }

            // Aplica el tema (colores/tipografía) según modo oscuro
            FotochefTheme(darkTheme = isDarkMode) {
                // Controlador de navegación (cambia de pantallas)
                val navController = rememberNavController()

                // Observa la ruta actual para saber en qué pantalla estamos
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                
                // Mostrar BottomNavBar solo en las pantallas principales
                val showBottomBar = when (currentRoute) {
                    Screen.Home.route,
                    Screen.Recipes.route,
                    Screen.ShoppingList.route,
                    Screen.Favorites.route,
                    Screen.Settings.route,
                    Screen.Profile.route -> true
                    else -> false
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // Barra de navegación inferior (solo en pantallas principales)
                    bottomBar = { 
                        if (showBottomBar) {
                            BottomNavigationBar(navController = navController) 
                        }
                    }
                ) { innerPadding ->
                    // Grafo de navegación: define qué composable se abre para cada ruta
                    MainNavGraph(
                        navController = navController, 
                        startDestination = startDestination,
                        innerPadding = innerPadding
                    )
                }
            }
        }
    }
}