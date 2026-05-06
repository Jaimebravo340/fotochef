package com.example.fotochef.data.repository

import com.example.fotochef.data.remote.dto.AiGeneratedRecipe

/**
 * Recetas de ejemplo que se muestran cuando la API de Gemini no está disponible
 * (rate limit, sin conexión, etc.). Permiten que la feature funcione siempre.
 */
object FallbackRecipes {

    private val all = listOf(
        AiGeneratedRecipe(
            nombre = "Tortilla Española Express",
            descripcion = "La clásica tortilla de patatas hecha en tiempo récord, jugosa por dentro y dorada por fuera.",
            tiempoMinutos = 25,
            dificultad = "Facil",
            esVegana = false,
            esSinGluten = true,
            esSinLactosa = true,
            ingredientesNecesarios = listOf("patata", "huevo", "cebolla", "aceite de oliva", "sal"),
            ingredientesFaltantes = listOf(),
            pasos = listOf(
                "Pela y corta las patatas en láminas finas de unos 3mm.",
                "Fríe las patatas con la cebolla en aceite abundante a fuego medio durante 15 min.",
                "Escurre el aceite y mezcla las patatas con 4 huevos batidos y sal.",
                "Cuaja la tortilla en una sartén antiadherente a fuego suave 3 min por cada lado.",
                "Dale la vuelta con un plato y sirve caliente o a temperatura ambiente."
            )
        ),
        AiGeneratedRecipe(
            nombre = "Sofrito de Verduras",
            descripcion = "Base aromática de verduras salteadas, perfecta como guarnición o primer plato vegano.",
            tiempoMinutos = 20,
            dificultad = "Facil",
            esVegana = true,
            esSinGluten = true,
            esSinLactosa = true,
            ingredientesNecesarios = listOf("tomate", "cebolla", "pimiento", "ajo", "aceite de oliva", "sal"),
            ingredientesFaltantes = listOf(),
            pasos = listOf(
                "Pica finamente la cebolla, el ajo y el pimiento.",
                "Sofríe el ajo y la cebolla en aceite de oliva a fuego medio hasta que estén transparentes.",
                "Añade el pimiento y cocina 5 minutos más.",
                "Incorpora el tomate troceado y cocina 10 minutos hasta que reduzca.",
                "Sazona con sal y sirve como guarnición o sobre arroz."
            )
        ),
        AiGeneratedRecipe(
            nombre = "Arroz Salteado con Huevo",
            descripcion = "Arroz frito al estilo asiático, sencillo y lleno de sabor con ingredientes de la nevera.",
            tiempoMinutos = 15,
            dificultad = "Facil",
            esVegana = false,
            esSinGluten = true,
            esSinLactosa = true,
            ingredientesNecesarios = listOf("arroz", "huevo", "cebolla", "aceite de oliva", "sal"),
            ingredientesFaltantes = listOf("salsa de soja"),
            pasos = listOf(
                "Cocina el arroz con el doble de agua y sal durante 12 minutos.",
                "Saltea la cebolla picada en aceite a fuego alto hasta dorar.",
                "Empuja la cebolla al lado y añade los huevos batidos, revuelve rápido.",
                "Incorpora el arroz frío (mejor del día anterior) y mezcla todo.",
                "Añade unas gotas de salsa de soja si tienes y sirve inmediatamente."
            )
        ),
        AiGeneratedRecipe(
            nombre = "Crema de Zanahoria",
            descripcion = "Sopa cremosa de zanahoria con un toque de jengibre, vegana y reconfortante.",
            tiempoMinutos = 30,
            dificultad = "Facil",
            esVegana = true,
            esSinGluten = true,
            esSinLactosa = true,
            ingredientesNecesarios = listOf("zanahoria", "cebolla", "ajo", "aceite de oliva", "sal"),
            ingredientesFaltantes = listOf("caldo de verduras", "jengibre"),
            pasos = listOf(
                "Pela y trocea las zanahorias, la cebolla y el ajo.",
                "Sofríe la cebolla y el ajo en aceite durante 3 minutos.",
                "Añade las zanahorias y cubre con caldo o agua caliente.",
                "Cocina a fuego medio 20 minutos hasta que las zanahorias estén tiernas.",
                "Tritura con una batidora hasta obtener una crema suave.",
                "Ajusta de sal y sirve con un chorrito de aceite de oliva."
            )
        ),
        AiGeneratedRecipe(
            nombre = "Pasta con Tomate y Ajo",
            descripcion = "El clásico italiano aglio e olio adaptado con tomate, listo en 15 minutos.",
            tiempoMinutos = 15,
            dificultad = "Facil",
            esVegana = true,
            esSinGluten = false,
            esSinLactosa = true,
            ingredientesNecesarios = listOf("pasta", "tomate", "ajo", "aceite de oliva", "sal"),
            ingredientesFaltantes = listOf("albahaca fresca"),
            pasos = listOf(
                "Cuece la pasta en agua con sal abundante según el tiempo del paquete.",
                "Mientras, lamina el ajo y dóralo en aceite de oliva a fuego suave.",
                "Añade el tomate troceado y cocina 5 minutos.",
                "Escurre la pasta reservando un poco del agua de cocción.",
                "Mezcla la pasta con la salsa añadiendo un chorrito del agua de cocción.",
                "Sirve con albahaca fresca si tienes y un hilo de aceite crudo."
            )
        ),
        AiGeneratedRecipe(
            nombre = "Revuelto de Champiñones",
            descripcion = "Huevos revueltos cremosos con champiñones salteados, listo en 10 minutos.",
            tiempoMinutos = 10,
            dificultad = "Facil",
            esVegana = false,
            esSinGluten = true,
            esSinLactosa = false,
            ingredientesNecesarios = listOf("huevo", "cebolla", "aceite de oliva", "sal"),
            ingredientesFaltantes = listOf("champiñones", "mantequilla"),
            pasos = listOf(
                "Lamina los champiñones y saltéalos en aceite a fuego alto 3 minutos.",
                "Baja el fuego y añade la cebolla picada, cocina 2 minutos más.",
                "Bate los huevos con una pizca de sal y viértelos sobre las verduras.",
                "Revuelve suavemente con una espátula hasta que cuajen a tu gusto.",
                "Sirve inmediatamente sobre tostadas o acompañado de pan."
            )
        )
    )

    /**
     * Devuelve [count] recetas de fallback que no estén en [excludeNames],
     * aplicando los filtros de dieta solicitados.
     */
    fun get(
        count: Int = 3,
        isVegan: Boolean = false,
        isGlutenFree: Boolean = false,
        isLactoseFree: Boolean = false,
        excludeNames: List<String> = emptyList()
    ): List<AiGeneratedRecipe> {
        return all
            .filter { r ->
                r.nombre !in excludeNames &&
                (!isVegan || r.esVegana) &&
                (!isGlutenFree || r.esSinGluten) &&
                (!isLactoseFree || r.esSinLactosa)
            }
            .shuffled()
            .take(count)
    }
}
