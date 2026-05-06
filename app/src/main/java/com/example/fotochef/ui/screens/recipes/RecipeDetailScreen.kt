package com.example.fotochef.ui.screens.recipes

import android.content.Intent
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fotochef.R
import com.example.fotochef.data.local.entity.Recepta
import com.example.fotochef.data.repository.MockDataRepository
import kotlinx.coroutines.launch
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    viewModel: RecipeDetailViewModel,
    onBackClick: () -> Unit,
    onStartCooking: (String, String) -> Unit
) {
    val recipe by viewModel.recipe.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(recipeId) {
        viewModel.loadRecipe(recipeId)
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            // TopBar que se vuelve sólida al hacer scroll (simplificado)
            CenterAlignedTopAppBar(
                title = { 
                    if (scrollState.value > 200) {
                        Text(recipe?.nom ?: "", maxLines = 1)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.background(
                            if (scrollState.value > 200) Color.Transparent else Color.Black.copy(alpha = 0.3f),
                            CircleShape
                        )
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Volver",
                            tint = if (scrollState.value > 200) MaterialTheme.colorScheme.onSurface else Color.White
                        )
                    }
                },
                actions = {
                    recipe?.let { r ->
                        IconButton(
                            onClick = {
                                val shareText = context.getString(R.string.share_text, r.nom)
                                val intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(intent, context.getString(R.string.share_recipe)))
                            },
                            modifier = Modifier.background(
                                if (scrollState.value > 200) Color.Transparent else Color.Black.copy(alpha = 0.3f),
                                CircleShape
                            )
                        ) {
                            Icon(
                                Icons.Default.Share, 
                                contentDescription = "Compartir",
                                tint = if (scrollState.value > 200) MaterialTheme.colorScheme.onSurface else Color.White
                            )
                        }
                        
                        IconButton(
                            onClick = { viewModel.toggleFavorite() },
                            modifier = Modifier.background(
                                if (scrollState.value > 200) Color.Transparent else Color.Black.copy(alpha = 0.3f),
                                CircleShape
                            )
                        ) {
                            Icon(
                                imageVector = if (r.esFavorita) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Favorito",
                                tint = if (r.esFavorita) MaterialTheme.colorScheme.primary else (if (scrollState.value > 200) MaterialTheme.colorScheme.onSurface else Color.White)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = if (scrollState.value > 200) MaterialTheme.colorScheme.surface else Color.Transparent,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    viewModel.addIngredientsToShoppingList()
                    scope.launch {
                        snackbarHostState.showSnackbar("Ingredientes añadidos a la lista")
                    }
                },
                icon = { Icon(Icons.Default.AddShoppingCart, contentDescription = null) },
                text = { Text("Lista de compra") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        }
    ) { padding ->
        if (recipe == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val r = recipe!!
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                // Header Image
                RecipeHeaderImage(recipe = r)

                Column(
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .offset(y = (-24).dp)
                ) {
                    // Título y Categoría
                    RecipeTitleCard(recipe = r)

                    Spacer(modifier = Modifier.height(16.dp))

                    // Info Row (Tiempo, Dificultad)
                    RecipeInfoRow(recipe = r)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Diet Tags
                    DietTagsRow(recipe = r)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Ingredients
                    Text(
                        text = "Ingredientes",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    IngredientsList(recipeId = r.id)

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description
                    Text(
                        text = "Descripción",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = r.descripcio,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Steps
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Preparación",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { onStartCooking(r.nom, r.passos) }) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Modo Cocina")
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    StepsList(stepsJson = r.passos)
                    
                    Spacer(modifier = Modifier.height(100.dp)) // Espacio para el FAB
                }
            }
        }
    }
}

@Composable
fun RecipeHeaderImage(recipe: Recepta) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Placeholder image con gradiente
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                )
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(80.dp).align(Alignment.Center),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
        }
        
        // Gradiente inferior para legibilidad
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f)),
                        startY = 400f
                    )
                )
        )
    }
}

@Composable
fun RecipeTitleCard(recipe: Recepta) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = recipe.categoria.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = recipe.nom,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun RecipeInfoRow(recipe: Recepta) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        InfoItem(icon = Icons.Default.Timer, label = "Tiempo", value = "${recipe.tempsPreparacio} min")
        InfoItem(icon = Icons.Default.BarChart, label = "Dificultad", value = recipe.dificultat)
        InfoItem(icon = Icons.Default.LocalFireDepartment, label = "Calorías", value = "${recipe.calories} kcal")
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MacroItem(label = "Proteínas", value = "${recipe.proteines}g")
        MacroItem(label = "Carbos", value = "${recipe.carbs}g")
        MacroItem(label = "Grasas", value = "${recipe.greixos}g")
    }
}

@Composable
fun MacroItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun InfoItem(icon: ImageVector, label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DietTagsRow(recipe: Recepta) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        if (recipe.esVegana) {
            DietTag(text = "Vegano", color = Color(0xFFE8F5E9), textColor = Color(0xFF2E7D32))
        }
        if (recipe.esSenseGluten) {
            DietTag(text = "Sin Gluten", color = Color(0xFFFFF3E0), textColor = Color(0xFFEF6C00))
        }
    }
}

@Composable
fun DietTag(text: String, color: Color, textColor: Color) {
    Surface(
        color = color,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun IngredientsList(recipeId: Long) {
    val ingredientsList = MockDataRepository.getReceptaIngredients().filter { it.receptaId == recipeId }
    val ingredientsMap = MockDataRepository.getIngredients().associateBy { it.id }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ingredientsList.forEach { rel ->
            val ingredient = ingredientsMap[rel.ingredientId]
            if (ingredient != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CheckCircle, 
                        contentDescription = null, 
                        modifier = Modifier.size(18.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = ingredient.nom, modifier = Modifier.weight(1f))
                    Text(text = rel.quantitat, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StepsList(stepsJson: String) {
    val stepsList = try {
        val array = JSONArray(stepsJson)
        List(array.length()) { i -> array.getString(i) }
    } catch (e: Exception) {
        emptyList<String>()
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        stepsList.forEachIndexed { index, step ->
            Row {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier.size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = (index + 1).toString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 22.sp
                )
            }
        }
    }
}
