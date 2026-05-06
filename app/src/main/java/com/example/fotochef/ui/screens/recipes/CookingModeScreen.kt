package com.example.fotochef.ui.screens.recipes

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
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
import org.json.JSONArray

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookingModeScreen(
    recipeName: String,
    stepsJson: String,
    onFinished: () -> Unit,
    onBack: () -> Unit
) {
    val steps = remember(stepsJson) {
        try {
            val array = JSONArray(stepsJson)
            List(array.length()) { i -> array.getString(i) }
        } catch (e: Exception) {
            emptyList<String>()
        }
    }

    var currentStep by remember { mutableIntStateOf(0) }
    var showAssistant by remember { mutableStateOf(false) }
    val progress = (currentStep + 1).toFloat() / steps.size

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Modo Cocina", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    IconButton(onClick = { showAssistant = true }) {
                        Icon(
                            Icons.Default.AutoAwesome, 
                            contentDescription = "Asistente IA",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (showAssistant) {
            CookingAssistantSheet(
                recipeContext = "Receta: $recipeName. Paso actual: ${steps[currentStep]}",
                onClose = { showAssistant = false }
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = recipeName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Paso ${currentStep + 1} de ${steps.size}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Step Content
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedContent(
                    targetState = currentStep,
                    transitionSpec = {
                        if (targetState > initialState) {
                            slideInHorizontally { it } + fadeIn() togetherWith slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut()
                        }.using(SizeTransform(clip = false))
                    },
                    label = "StepTransition"
                ) { stepIndex ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            modifier = Modifier.size(80.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = (stepIndex + 1).toString(),
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(32.dp))
                        Text(
                            text = steps[stepIndex],
                            style = MaterialTheme.typography.headlineSmall,
                            textAlign = TextAlign.Center,
                            lineHeight = 36.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilledTonalIconButton(
                    onClick = { if (currentStep > 0) currentStep-- },
                    enabled = currentStep > 0,
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", modifier = Modifier.size(32.dp))
                }

                if (currentStep == steps.size - 1) {
                    Button(
                        onClick = onFinished,
                        modifier = Modifier
                            .height(64.dp)
                            .weight(1f)
                            .padding(horizontal = 16.dp),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Terminar", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                FilledIconButton(
                    onClick = { if (currentStep < steps.size - 1) currentStep++ },
                    enabled = currentStep < steps.size - 1,
                    modifier = Modifier.size(64.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}
