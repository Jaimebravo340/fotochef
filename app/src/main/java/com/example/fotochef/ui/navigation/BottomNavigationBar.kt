package com.example.fotochef.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavigationBar(navController: NavController) {
    // Barra inferior (Material 3). Muestra los accesos rápidos definidos en Screen.bottomNavItems
    NavigationBar {
        // Ruta/pantalla actual (sirve para pintar el item seleccionado)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // Crea un item por cada pantalla del bottom nav
        Screen.bottomNavItems.forEach { screen ->
            // Comprueba si la pantalla actual pertenece a la “jerarquía” de esa ruta (selected)
            val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true

            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = isSelected,
                onClick = {
                    if (screen.route == Screen.Home.route) {
                        // Para Home: siempre volver al inicio limpiando el stack
                        navController.popBackStack(
                            route = Screen.Home.route,
                            inclusive = false
                        ).let { popped ->
                            if (!popped) {
                                // Si Home no estaba en el stack, navegar a él
                                navController.navigate(Screen.Home.route) {
                                    // popUpTo(start) + save/restore: típico patrón para bottom navigation
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    } else {
                        // Para el resto: navegamos a su ruta preservando estado si ya existía
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}
