package com.example.fotochef.data.repository

import com.example.fotochef.data.local.entity.Ingredient
import com.example.fotochef.data.local.entity.IngredientDetectat
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.local.entity.ReceptaIngredient

/**
 * Repositorio con datos mock (hardcodeados) para pruebas.
 * Se usa antes de tener datos reales de la API o de la base de datos.
 */
object MockDataRepository {

    // === INGREDIENTES DE PRUEBA ===
    fun getIngredients(): List<Ingredient> = listOf(
        Ingredient(id = 1, nom = "Tomate", categoria = "Verdura", icona = "🍅"),
        Ingredient(id = 2, nom = "Cebolla", categoria = "Verdura", icona = "🧅"),
        Ingredient(id = 3, nom = "Ajo", categoria = "Verdura", icona = "🧄"),
        Ingredient(id = 4, nom = "Pimiento", categoria = "Verdura", icona = "🫑"),
        Ingredient(id = 5, nom = "Zanahoria", categoria = "Verdura", icona = "🥕"),
        Ingredient(id = 6, nom = "Patata", categoria = "Verdura", icona = "🥔"),
        Ingredient(id = 7, nom = "Pollo", categoria = "Carne", icona = "🍗"),
        Ingredient(id = 8, nom = "Huevo", categoria = "Lácteo", icona = "🥚"),
        Ingredient(id = 9, nom = "Arroz", categoria = "Cereal", icona = "🍚"),
        Ingredient(id = 10, nom = "Pasta", categoria = "Cereal", icona = "🍝"),
        Ingredient(id = 11, nom = "Aceite de oliva", categoria = "Condimento", icona = "🫒"),
        Ingredient(id = 12, nom = "Sal", categoria = "Condimento", icona = "🧂"),
        Ingredient(id = 13, nom = "Queso", categoria = "Lácteo", icona = "🧀"),
        Ingredient(id = 14, nom = "Leche", categoria = "Lácteo", icona = "🥛"),
        Ingredient(id = 15, nom = "Harina", categoria = "Cereal", icona = "🌾")
    )

    // === RECETAS DE PRUEBA ===
    fun getReceptes(): List<Recepta> = listOf(
        Recepta(
            id = 1,
            nom = "Tortilla de Patatas",
            descripcio = "La clásica tortilla española con patatas, cebolla y huevo.",
            tempsPreparacio = 30,
            dificultat = "Fàcil",
            imatgePath = "",
            esFavorita = true,
            passos = """["Pelar y cortar las patatas en láminas finas","Freír las patatas en aceite a fuego medio","Batir los huevos con sal","Mezclar patatas con huevo batido","Cuajar la tortilla a fuego lento","Dar la vuelta con un plato y terminar de cuajar"]""",
            categoria = "Segon",
            esVegana = false,
            esSenseGluten = true
        ),
        Recepta(
            id = 2,
            nom = "Pasta al Pesto",
            descripcio = "Pasta con salsa pesto fresca de albahaca, piñones y parmesano.",
            tempsPreparacio = 20,
            dificultat = "Fàcil",
            imatgePath = "",
            esFavorita = false,
            passos = """["Cocer la pasta en agua con sal","Triturar albahaca, ajo, piñones y aceite","Añadir queso parmesano rallado","Mezclar la pasta con la salsa pesto","Servir con un chorrito de aceite de oliva"]""",
            categoria = "Primer",
            esVegana = false,
            esSenseGluten = false
        ),
        Recepta(
            id = 3,
            nom = "Pollo al Horno con Verduras",
            descripcio = "Pollo jugoso asado con patatas, zanahorias y pimientos.",
            tempsPreparacio = 60,
            dificultat = "Mitjà",
            imatgePath = "",
            esFavorita = true,
            passos = """["Precalentar el horno a 200°C","Salpimentar el pollo","Cortar las verduras en trozos grandes","Colocar todo en una bandeja de horno","Rociar con aceite de oliva y especias","Hornear 45 minutos","Servir caliente"]""",
            categoria = "Segon",
            esVegana = false,
            esSenseGluten = true
        ),
        Recepta(
            id = 4,
            nom = "Gazpacho Andaluz",
            descripcio = "Sopa fría de tomate, pepino, pimiento y ajo. Perfecta para el verano.",
            tempsPreparacio = 15,
            dificultat = "Fàcil",
            imatgePath = "",
            esFavorita = false,
            passos = """["Lavar y trocear tomates, pepino y pimiento","Añadir ajo, aceite, vinagre y sal","Triturar todo hasta obtener una crema fina","Pasar por un colador si se desea","Refrigerar al menos 2 horas","Servir frío con picadillo de verduras"]""",
            categoria = "Primer",
            esVegana = true,
            esSenseGluten = true
        ),
        Recepta(
            id = 5,
            nom = "Risotto de Champiñones",
            descripcio = "Arroz cremoso con champiñones, cebolla, vino blanco y parmesano.",
            tempsPreparacio = 40,
            dificultat = "Mitjà",
            imatgePath = "",
            esFavorita = false,
            passos = """["Calentar caldo en una cacerola aparte","Sofreír cebolla en mantequilla","Añadir el arroz y tostar 2 minutos","Añadir vino blanco y remover","Ir añadiendo caldo poco a poco removiendo","Saltear champiñones aparte","Mezclar champiñones con el arroz","Añadir parmesano y mantequilla al final"]""",
            categoria = "Primer",
            esVegana = false,
            esSenseGluten = true
        ),
        Recepta(
            id = 6,
            nom = "Crema de Calabaza",
            descripcio = "Crema suave y sedosa de calabaza asada con un toque de nuez moscada.",
            tempsPreparacio = 35,
            dificultat = "Fàcil",
            imatgePath = "",
            esFavorita = true,
            passos = """["Pelar y trocear la calabaza","Sofreír cebolla y ajo","Añadir la calabaza y cubrir con caldo","Cocer 20 minutos hasta que esté tierna","Triturar hasta obtener una crema","Añadir nata, sal y nuez moscada","Servir caliente con semillas por encima"]""",
            categoria = "Primer",
            esVegana = false,
            esSenseGluten = true
        )
    )

    // === RELACIONES RECETA-INGREDIENTE ===
    fun getReceptaIngredients(): List<ReceptaIngredient> = listOf(
        // Tortilla de Patatas (receta 1)
        ReceptaIngredient(receptaId = 1, ingredientId = 6, quantitat = "4 medianas"),
        ReceptaIngredient(receptaId = 1, ingredientId = 2, quantitat = "1 grande"),
        ReceptaIngredient(receptaId = 1, ingredientId = 8, quantitat = "6 unidades"),
        ReceptaIngredient(receptaId = 1, ingredientId = 11, quantitat = "200ml"),
        ReceptaIngredient(receptaId = 1, ingredientId = 12, quantitat = "al gusto"),

        // Pasta al Pesto (receta 2)
        ReceptaIngredient(receptaId = 2, ingredientId = 10, quantitat = "400g"),
        ReceptaIngredient(receptaId = 2, ingredientId = 3, quantitat = "2 dientes"),
        ReceptaIngredient(receptaId = 2, ingredientId = 13, quantitat = "50g parmesano"),
        ReceptaIngredient(receptaId = 2, ingredientId = 11, quantitat = "100ml"),
        ReceptaIngredient(receptaId = 2, ingredientId = 12, quantitat = "al gusto"),

        // Pollo al Horno (receta 3)
        ReceptaIngredient(receptaId = 3, ingredientId = 7, quantitat = "1 entero"),
        ReceptaIngredient(receptaId = 3, ingredientId = 6, quantitat = "3 medianas"),
        ReceptaIngredient(receptaId = 3, ingredientId = 5, quantitat = "2 grandes"),
        ReceptaIngredient(receptaId = 3, ingredientId = 4, quantitat = "2 unidades"),
        ReceptaIngredient(receptaId = 3, ingredientId = 11, quantitat = "50ml"),
        ReceptaIngredient(receptaId = 3, ingredientId = 12, quantitat = "al gusto"),

        // Gazpacho (receta 4)
        ReceptaIngredient(receptaId = 4, ingredientId = 1, quantitat = "1kg"),
        ReceptaIngredient(receptaId = 4, ingredientId = 4, quantitat = "1 verde"),
        ReceptaIngredient(receptaId = 4, ingredientId = 3, quantitat = "1 diente"),
        ReceptaIngredient(receptaId = 4, ingredientId = 11, quantitat = "50ml"),
        ReceptaIngredient(receptaId = 4, ingredientId = 12, quantitat = "al gusto"),

        // Risotto (receta 5)
        ReceptaIngredient(receptaId = 5, ingredientId = 9, quantitat = "300g"),
        ReceptaIngredient(receptaId = 5, ingredientId = 2, quantitat = "1 mediana"),
        ReceptaIngredient(receptaId = 5, ingredientId = 13, quantitat = "80g"),
        ReceptaIngredient(receptaId = 5, ingredientId = 11, quantitat = "30ml"),

        // Crema de Calabaza (receta 6)
        ReceptaIngredient(receptaId = 6, ingredientId = 2, quantitat = "1 mediana"),
        ReceptaIngredient(receptaId = 6, ingredientId = 3, quantitat = "2 dientes"),
        ReceptaIngredient(receptaId = 6, ingredientId = 14, quantitat = "100ml nata"),
        ReceptaIngredient(receptaId = 6, ingredientId = 12, quantitat = "al gusto")
    )

    // === INGREDIENTES DETECTADOS MOCK (simulan resultado de la IA) ===
    fun getIngredientsDetectats(): List<IngredientDetectat> = listOf(
        IngredientDetectat(
            id = 1, nom = "Tomate", confianca = 0.95f,
            imatgePath = "/mock/foto1.jpg", confirmat = true
        ),
        IngredientDetectat(
            id = 2, nom = "Cebolla", confianca = 0.88f,
            imatgePath = "/mock/foto1.jpg", confirmat = true
        ),
        IngredientDetectat(
            id = 3, nom = "Pimiento", confianca = 0.82f,
            imatgePath = "/mock/foto1.jpg", confirmat = false
        ),
        IngredientDetectat(
            id = 4, nom = "Ajo", confianca = 0.75f,
            imatgePath = "/mock/foto1.jpg", confirmat = false
        ),
        IngredientDetectat(
            id = 5, nom = "Manzana", confianca = 0.45f,
            imatgePath = "/mock/foto1.jpg", confirmat = false
        )
    )
}
