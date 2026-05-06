package com.example.fotochef.ui.screens.scan

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetectionScreen(
    imagePath: String?,
    detectedIngredients: List<String>,
    averageConfidence: Float,
    isPrecisionValid: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    onIngredientsChange: (List<String>) -> Unit,
    onContinueToRecipes: () -> Unit
) {
    var ingredientInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        // Header con gradiente
        DetectionHeader(isLoading = isLoading)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Estado de la detección
            DetectionStatusCard(
                isLoading = isLoading,
                confidence = averageConfidence,
                isValid = isPrecisionValid,
                error = errorMessage
            )

            // Input para añadir manualmente
            OutlinedTextField(
                value = ingredientInput,
                onValueChange = { ingredientInput = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("¿Falta algún ingrediente?") },
                placeholder = { Text("Ej: Tomate, Cebolla...") },
                trailingIcon = {
                    IconButton(onClick = {
                        val value = ingredientInput.trim()
                        if (value.isNotEmpty()) {
                            onIngredientsChange((detectedIngredients + value).distinct())
                            ingredientInput = ""
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir")
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true
            )

            // Lista de ingredientes detectados
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Ingredientes detectados",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))
                
                if (detectedIngredients.isEmpty() && !isLoading) {
                    EmptyDetectionState()
                } else {
                    WrappingChips(
                        ingredients = detectedIngredients,
                        onRemove = { ingredient ->
                            onIngredientsChange(detectedIngredients.filterNot { it == ingredient })
                        }
                    )
                }
            }

            // Botón continuar
            Button(
                onClick = onContinueToRecipes,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading && errorMessage == null && detectedIngredients.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("Ver recetas disponibles", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun DetectionHeader(isLoading: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = "Escaneando...",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Identificando ingredientes con IA",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun DetectionStatusCard(isLoading: Boolean, confidence: Float, isValid: Boolean, error: String?) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = when {
                error != null -> MaterialTheme.colorScheme.errorContainer
                !isValid && !isLoading -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                Column {
                    Text("Analizando foto...", fontWeight = FontWeight.Bold)
                    Text("Esto tardará solo un momento", style = MaterialTheme.typography.bodySmall)
                }
            } else if (error != null) {
                Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                Text("Error en el análisis: $error", color = MaterialTheme.colorScheme.onErrorContainer)
            } else {
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { confidence },
                        modifier = Modifier.size(48.dp),
                        strokeWidth = 4.dp,
                        color = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                    Text(
                        "${(confidence * 100).toInt()}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Column {
                    Text(
                        if (isValid) "¡Detección precisa!" else "Detección poco clara",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        if (isValid) "IA confía en los resultados" else "Revisa los ingredientes detectados",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyDetectionState() {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Default.SearchOff,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "No se detectaron ingredientes",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            "Intenta añadir algunos manualmente",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WrappingChips(
    ingredients: List<String>,
    onRemove: (String) -> Unit
) {
    // Layout manual de chips en filas (reemplaza FlowRow incompatible con BOM 2024.09)
    val rows = mutableListOf<List<String>>()
    val currentRow = mutableListOf<String>()
    ingredients.forEachIndexed { index, ingredient ->
        currentRow.add(ingredient)
        if (currentRow.size == 3 || index == ingredients.lastIndex) {
            rows.add(currentRow.toList())
            currentRow.clear()
        }
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        rows.forEach { rowItems ->
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                rowItems.forEach { ingredient ->
                    InputChip(
                        selected = true,
                        onClick = { },
                        label = { Text(ingredient, maxLines = 1) },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Eliminar",
                                modifier = Modifier
                                    .size(16.dp)
                                    .clickable { onRemove(ingredient) }
                            )
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }
        }
    }
}
