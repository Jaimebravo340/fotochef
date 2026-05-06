package com.example.fotochef.ui.screens.recipes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fotochef.ui.components.EmptyStateView
import androidx.compose.ui.res.stringResource
import com.example.fotochef.R

@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel,
    detectedIngredients: List<String> = emptyList(),
    onRecipeClick: (Long) -> Unit
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedDifficulty by viewModel.selectedDifficulty.collectAsState()
    val isVegan by viewModel.isVegan.collectAsState()
    val isGlutenFree by viewModel.isGlutenFree.collectAsState()
    val matchedRecipes by viewModel.matchedRecipes.collectAsState()
    val hasPantryIngredients by viewModel.hasPantryIngredients.collectAsState()

    // IA states
    val aiRecipes by viewModel.aiRecipes.collectAsState()
    val isGeneratingAi by viewModel.isGeneratingAi.collectAsState()
    val aiError by viewModel.aiError.collectAsState()
    val aiDietFilters by viewModel.aiDietFilters.collectAsState()
    val aiPanelExpanded by viewModel.aiPanelExpanded.collectAsState()
    val aiUsingFallback by viewModel.aiUsingFallback.collectAsState()

    LaunchedEffect(detectedIngredients) {
        viewModel.setDetectedIngredients(detectedIngredients)
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.recipes_action),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.searchQuery.value = it },
                    placeholder = { Text("Buscar por nombre o ingrediente...") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                Icon(Icons.Default.Close, contentDescription = null)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Filtros locales
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    DifficultyChip(
                        label = "Fácil",
                        isSelected = selectedDifficulty == "Fácil",
                        onClick = { viewModel.selectedDifficulty.value = if (selectedDifficulty == "Fácil") null else "Fácil" }
                    )
                    DifficultyChip(
                        label = "Medio",
                        isSelected = selectedDifficulty == "Media",
                        onClick = { viewModel.selectedDifficulty.value = if (selectedDifficulty == "Media") null else "Media" }
                    )
                    DifficultyChip(
                        label = "Difícil",
                        isSelected = selectedDifficulty == "Difícil",
                        onClick = { viewModel.selectedDifficulty.value = if (selectedDifficulty == "Difícil") null else "Difícil" }
                    )

                    VerticalDivider(modifier = Modifier.height(32.dp).padding(horizontal = 4.dp))

                    FilterChip(
                        selected = isVegan,
                        onClick = { viewModel.isVegan.value = !isVegan },
                        label = { Text("Vegano 🌱") },
                        leadingIcon = if (isVegan) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) } } else null
                    )
                    FilterChip(
                        selected = isGlutenFree,
                        onClick = { viewModel.isGlutenFree.value = !isGlutenFree },
                        label = { Text("Sin Gluten 🌾") },
                        leadingIcon = if (isGlutenFree) { { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) } } else null
                    )
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {

            // ─── Banner ingredientes detectados/despensa ───────────────────────────────
            if (detectedIngredients.isNotEmpty() || hasPantryIngredients) {
                item {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (detectedIngredients.isNotEmpty()) "Usando ingredientes detectados y tu despensa" else "Usando ingredientes de tu despensa",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // ─── Panel de generación IA ───────────────────────────────────────
            item {
                AiGenerationPanel(
                    ingredients = detectedIngredients,
                    aiDietFilters = aiDietFilters,
                    isGenerating = isGeneratingAi,
                    aiError = aiError,
                    hasAiRecipes = aiRecipes.isNotEmpty(),
                    onGenerate = { viewModel.generateAiRecipes() },
                    onClear = { viewModel.clearAiRecipes() },
                    onVeganToggle = { viewModel.setAiDietFilter(vegan = !aiDietFilters.vegan) },
                    onGlutenFreeToggle = { viewModel.setAiDietFilter(glutenFree = !aiDietFilters.glutenFree) },
                    onLactoseFreeToggle = { viewModel.setAiDietFilter(lactoseFree = !aiDietFilters.lactoseFree) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // ─── Recetas IA generadas ─────────────────────────────────────────
            if (aiRecipes.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.tertiary,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Recetas generadas por IA (${aiRecipes.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        // Aviso discreto si se usan recetas de demo
                        if (aiUsingFallback) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "⚡ Ejemplos de demostración (API no disponible temporalmente)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                items(aiRecipes, key = { it.nombre + it.hashCode() }) { recipe ->
                    AiRecipeCard(
                        recipe = recipe,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        onFavoriteClick = { viewModel.saveAiRecipe(recipe) },
                        onClick = { 
                            viewModel.viewAiRecipe(recipe) { recipeId ->
                                onRecipeClick(recipeId)
                            }
                        }
                    )
                }

                // Botón cargar más
                item {
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedButton(
                        onClick = { viewModel.loadMoreAiRecipes() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isGeneratingAi
                    ) {
                        if (isGeneratingAi) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Generar más recetas")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // ─── Separador recetas locales ────────────────────────────────────
            item {
                if (aiRecipes.isNotEmpty()) {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
                }
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Recetas del recetario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // ─── Recetas locales ──────────────────────────────────────────────
            if (matchedRecipes.isEmpty()) {
                item { 
                    EmptyStateView(
                        title = stringResource(R.string.empty_recipes_title),
                        description = stringResource(R.string.empty_recipes_desc),
                        icon = Icons.Default.Search
                    )
                }
            } else {
                items(matchedRecipes, key = { it.recipe.id }) { match ->
                    RecipeCard(
                        recipe = match.recipe,
                        matchCount = match.matchCount,
                        onClick = { onRecipeClick(match.recipe.id) },
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AiGenerationPanel(
    ingredients: List<String>,
    aiDietFilters: AiDietFilters,
    isGenerating: Boolean,
    aiError: String?,
    hasAiRecipes: Boolean,
    onGenerate: () -> Unit,
    onClear: () -> Unit,
    onVeganToggle: () -> Unit,
    onGlutenFreeToggle: () -> Unit,
    onLactoseFreeToggle: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header del panel
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.tertiary,
                                    MaterialTheme.colorScheme.primary
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Column {
                    Text(
                        text = "Chef IA",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (ingredients.isEmpty())
                            "Escanea ingredientes para empezar"
                        else
                            "${ingredients.size} ingrediente${if (ingredients.size != 1) "s" else ""} disponible${if (ingredients.size != 1) "s" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filtros de dieta IA
            Text(
                text = "Filtros de dieta",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = aiDietFilters.vegan,
                    onClick = onVeganToggle,
                    label = { Text("🌱 Vegano") },
                    leadingIcon = if (aiDietFilters.vegan) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                FilterChip(
                    selected = aiDietFilters.glutenFree,
                    onClick = onGlutenFreeToggle,
                    label = { Text("🌾 Sin Gluten") },
                    leadingIcon = if (aiDietFilters.glutenFree) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
                FilterChip(
                    selected = aiDietFilters.lactoseFree,
                    onClick = onLactoseFreeToggle,
                    label = { Text("🥛 Sin Lactosa") },
                    leadingIcon = if (aiDietFilters.lactoseFree) {
                        { Icon(Icons.Default.Check, null, modifier = Modifier.size(16.dp)) }
                    } else null
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Barra de progreso de generación
            AnimatedVisibility(visible = isGenerating) {
                Column {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "🍳 Cocinando ideas con IA...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }


            // Mensaje de error
            AnimatedVisibility(visible = aiError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.ErrorOutline,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = aiError ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
            if (aiError != null) Spacer(modifier = Modifier.height(10.dp))

            // Botones de acción
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onGenerate,
                    modifier = Modifier.weight(1f),
                    enabled = ingredients.isNotEmpty() && !isGenerating,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (hasAiRecipes) "Regenerar" else "Generar recetas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                if (hasAiRecipes) {
                    IconButton(
                        onClick = onClear,
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Limpiar recetas IA",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    )
}
