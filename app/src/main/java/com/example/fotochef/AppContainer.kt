package com.example.fotochef

import android.content.Context
import com.example.fotochef.data.local.dao.IngredientDao
import com.example.fotochef.data.local.dao.IngredientDetectatDao
import com.example.fotochef.data.local.dao.ReceptaDao
import com.example.fotochef.data.local.dao.ShoppingListDao
import com.example.fotochef.data.local.database.FotoChefDatabase
import com.example.fotochef.data.preferences.PreferencesManager
import com.example.fotochef.data.repository.ShoppingListRepository
import com.example.fotochef.data.repository.VisionRepository

/**
 * Contenedor de dependencias manual (reemplaza Hilt que es incompatible con AGP 9.x).
 * Proporciona todas las dependencias de la app como singletons.
 * Se inicializa en FotoChefApplication.onCreate().
 */
class AppContainer(context: Context) {

    // === PREFERENCIAS ===

    val preferencesManager: PreferencesManager by lazy {
        // Se crea cuando se usa por primera vez (lazy) y se reutiliza
        PreferencesManager(context)
    }

    val userPreferencesRepository: com.example.fotochef.data.preferences.UserPreferencesRepository by lazy {
        // Preferencias “de estado”: por ejemplo, si el onboarding está completado
        com.example.fotochef.data.preferences.UserPreferencesRepository(context)
    }

    // === BASE DE DATOS ===

    // Instancia singleton de la base de datos Room
    val database: FotoChefDatabase = FotoChefDatabase.getDatabase(context)

    // DAOs accesibles desde el contenedor
    val receptaDao: ReceptaDao = database.receptaDao()
    val ingredientDao: IngredientDao = database.ingredientDao()
    val ingredientDetectatDao: IngredientDetectatDao = database.ingredientDetectatDao()
    val shoppingListDao: ShoppingListDao = database.shoppingListDao()

    // === REPOSITORIOS ===

    val receptaRepository: com.example.fotochef.data.repository.ReceptaRepository by lazy {
        // Repositorio principal para recetas guardadas en Room
        com.example.fotochef.data.repository.ReceptaRepository(receptaDao, ingredientDao)
    }

    val ingredientRepository: com.example.fotochef.data.repository.IngredientRepository by lazy {
        // Repositorio de ingredientes (manuales + detectados)
        com.example.fotochef.data.repository.IngredientRepository(ingredientDao, ingredientDetectatDao)
    }

    val shoppingListRepository: ShoppingListRepository by lazy {
        // Lista de la compra
        ShoppingListRepository(shoppingListDao)
    }

    val visionRepository: VisionRepository by lazy {
        // Detección de “ingredientes” a partir de una foto (ML Kit)
        VisionRepository(context.applicationContext)
    }

    val aiRecipeRepository: com.example.fotochef.data.repository.AiRecipeRepository by lazy {
        // Generación de recetas mediante IA (red)
        com.example.fotochef.data.repository.AiRecipeRepository()
    }

    val authRepository: com.example.fotochef.data.repository.AuthRepository by lazy {
        // Login/registro/cerrar sesión
        com.example.fotochef.data.repository.AuthRepository(context)
    }
}
