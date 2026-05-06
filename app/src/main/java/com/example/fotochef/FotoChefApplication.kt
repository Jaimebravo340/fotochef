package com.example.fotochef

import android.app.Application

/**
 * Clase Application de FotoChef.
 * Punto de entrada de la aplicación a nivel de proceso.
 * Inicializa el contenedor de dependencias para inyección manual.
 */
class FotoChefApplication : Application() {

    // Contenedor de dependencias accesible desde toda la app
    lateinit var appContainer: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        // Se crea una sola vez cuando arranca el proceso de la app.
        // Desde aquí se accede a base de datos, repositorios y preferencias.
        appContainer = AppContainer(this)
    }
}
