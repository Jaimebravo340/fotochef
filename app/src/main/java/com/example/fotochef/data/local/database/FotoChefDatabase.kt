package com.example.fotochef.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fotochef.data.local.dao.IngredientDao
import com.example.fotochef.data.local.dao.IngredientDetectatDao
import com.example.fotochef.data.local.dao.ReceptaDao
import com.example.fotochef.data.local.dao.ShoppingListDao
import com.example.fotochef.data.local.entity.Ingredient
import com.example.fotochef.data.local.entity.IngredientCompra
import com.example.fotochef.data.local.entity.IngredientDetectat
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.local.entity.ReceptaIngredient

/**
 * Base de datos Room principal de FotoChef.
 * Contiene todas las entidades y proporciona acceso a los DAOs.
 * Se usa como Singleton para evitar múltiples instancias.
 */
@Database(
    entities = [
        Recepta::class,
        Ingredient::class,
        ReceptaIngredient::class,
        IngredientDetectat::class,
        IngredientCompra::class
    ],
    version = 5,
    exportSchema = false
)
abstract class FotoChefDatabase : RoomDatabase() {

    // DAOs accesibles desde la base de datos
    abstract fun receptaDao(): ReceptaDao
    abstract fun ingredientDao(): IngredientDao
    abstract fun ingredientDetectatDao(): IngredientDetectatDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        // Volatile asegura que la variable se lee siempre de memoria principal
        @Volatile
        private var INSTANCE: FotoChefDatabase? = null

        /**
         * Obtener la instancia única de la base de datos (patrón Singleton).
         * Si no existe, la crea de forma thread-safe con synchronized.
         */
        fun getDatabase(context: Context): FotoChefDatabase {
            // Si ya existe, la devolvemos. Si no, la creamos una sola vez.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FotoChefDatabase::class.java,
                    "fotochef_database"
                )
                    // En producción, usar migraciones en vez de fallbackToDestructiveMigration
                    // Aquí: si cambia el esquema y no hay migración, Room recrea tablas.
                    .fallbackToDestructiveMigration(dropAllTables = false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
