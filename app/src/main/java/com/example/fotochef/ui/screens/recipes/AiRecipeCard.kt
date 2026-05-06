package com.example.fotochef.ui.screens.recipes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fotochef.data.remote.dto.AiGeneratedRecipe

/**
 * Tarjeta de receta generada por IA con:
 * - Badge "IA" destacado
 * - Info de tiempo y dificultad
 * - Chips de dieta (Vegano, Sin Gluten, Sin Lactosa)
 * - Ingredientes que el usuario TIENE (verde) y los que le FALTAN (rojo)
 * - Pasos de preparación expandibles
 */
@Composable
fun AiRecipeCard(
    recipe: AiGeneratedRecipe,
    modifier: Modifier = Modifier,
    onFavoriteClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    var expanded by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        onClick = onClick,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // ─── Cabecera con gradiente + Badge IA ──────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.10f)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge IA + Badges de dieta
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Badge "IA"
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.tertiary
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    Icons.Default.AutoAwesome,
                                    contentDescription = null,
                                    modifier = Modifier.size(12.dp),
                                    tint = MaterialTheme.colorScheme.onTertiary
                                )
                                Text(
                                    text = "IA",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onTertiary
                                )
                            }
                        }

                        // Badges de dieta
                        if (recipe.esVegana) {
                            DietBadge("🌱 Vegano", Color(0xFF2E7D32), Color(0xFFE8F5E9))
                        }
                        if (recipe.esSinGluten) {
                            DietBadge("🌾 Sin Gluten", Color(0xFFBF360C), Color(0xFFFBE9E7))
                        }
                        if (recipe.esSinLactosa) {
                            DietBadge("🥛 Sin Lactosa", Color(0xFF1565C0), Color(0xFFE3F2FD))
                        }
                        
                        // Botón de Favorito
                        IconButton(
                            onClick = { 
                                isSaved = true
                                onFavoriteClick() 
                            },
                            enabled = !isSaved,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Guardar en Favoritos",
                                tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } // close outer Row
                    } // actually close the outer Row this time

                    Spacer(modifier = Modifier.height(8.dp))

                    // Nombre
                    Text(
                        text = recipe.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Descripción
                    Text(
                        text = recipe.descripcion,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tiempo y Dificultad
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RecipeStatChip(
                            icon = "⏱",
                            label = "${recipe.tiempoMinutos} min"
                        )
                        RecipeStatChip(
                            icon = when (recipe.dificultad) {
                                "Fácil" -> "🟢"
                                "Media" -> "🟡"
                                else -> "🔴"
                            },
                            label = recipe.dificultad
                        )
                    }
                }
            }

            // ─── Ingredientes ────────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {

                // Ingredientes que TIENE el usuario
                if (recipe.ingredientesTenidos.isNotEmpty()) {
                    IngredientSection(
                        title = "✅ Ingredientes que tienes",
                        ingredients = recipe.ingredientesTenidos,
                        chipColor = Color(0xFF2E7D32),
                        chipBackground = Color(0xFFE8F5E9)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Ingredientes que FALTAN
                if (recipe.ingredientesFaltantes.isNotEmpty()) {
                    IngredientSection(
                        title = "🛒 Te faltan",
                        ingredients = recipe.ingredientesFaltantes,
                        chipColor = Color(0xFFBF360C),
                        chipBackground = Color(0xFFFBE9E7)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Surface(
                        color = Color(0xFFE8F5E9),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🎉", fontSize = 18.sp)
                            Text(
                                text = "¡Tienes todos los ingredientes!",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // ─── Botón expandir pasos ────────────────────────────────────
                OutlinedButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (expanded) "Ocultar preparación" else "Ver preparación (${recipe.pasos.size} pasos)",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // ─── Pasos expandibles ────────────────────────────────────────
                AnimatedVisibility(
                    visible = expanded,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(modifier = Modifier.padding(top = 12.dp)) {
                        HorizontalDivider(modifier = Modifier.padding(bottom = 12.dp))
                        Text(
                            text = "Preparación",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        recipe.pasos.forEachIndexed { index, paso ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Número de paso
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = "${index + 1}",
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = paso,
                                    style = MaterialTheme.typography.bodyMedium,
                                    lineHeight = 22.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DietBadge(text: String, textColor: Color, bgColor: Color) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = bgColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = textColor
        )
    }
}

@Composable
private fun RecipeStatChip(icon: String, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(icon, fontSize = 14.sp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun IngredientSection(
    title: String,
    ingredients: List<String>,
    chipColor: Color,
    chipBackground: Color
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(modifier = Modifier.height(6.dp))
    // Wrap chips in rows of 3
    val rows = ingredients.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                row.forEach { ingredient ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = chipBackground
                    ) {
                        Text(
                            text = ingredient.replaceFirstChar { it.uppercase() },
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = chipColor
                        )
                    }
                }
            }
        }
    }
}
