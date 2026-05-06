package com.example.fotochef.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fotochef.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateToHelp: () -> Unit = {},
    onNavigateToPrivacy: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val isVegan by viewModel.isVeganFlow.collectAsState(initial = false)
    val isGlutenFree by viewModel.isGlutenFreeFlow.collectAsState(initial = false)
    val isDarkMode by viewModel.isDarkModeFlow.collectAsState(initial = false)
    val language by viewModel.languageFlow.collectAsState(initial = "es")
    val isClearingData by viewModel.isClearingData.collectAsState()

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showClearDataDialog by remember { mutableStateOf(false) }

    // Obtenemos el usuario actual de Firebase para el Header
    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Profile / Branding Header
        SettingsHeader(
            userName = currentUser?.displayName ?: currentUser?.email?.substringBefore("@") ?: "Chef",
            userEmail = currentUser?.email ?: "usuario@fotochef.app"
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Preferencias dietéticas
            SettingsCard(title = stringResource(R.string.dietary_preferences)) {
                SettingsToggleItem(
                    icon = Icons.Default.Favorite,
                    title = stringResource(R.string.vegan),
                    description = "Filtra recetas aptas para veganos",
                    isChecked = isVegan,
                    onToggle = { viewModel.setIsVegan(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                SettingsToggleItem(
                    icon = Icons.Default.RestaurantMenu,
                    title = stringResource(R.string.gluten_free),
                    description = "Filtra recetas sin gluten",
                    isChecked = isGlutenFree,
                    onToggle = { viewModel.setIsGlutenFree(it) }
                )
            }

            // Apariencia
            SettingsCard(title = stringResource(R.string.theme_settings)) {
                SettingsToggleItem(
                    icon = Icons.Default.DarkMode,
                    title = stringResource(R.string.dark_mode),
                    description = "Usa el tema oscuro para descansar la vista",
                    isChecked = isDarkMode,
                    onToggle = { viewModel.setDarkMode(it) }
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                SettingsClickableItem(
                    icon = Icons.Default.Language,
                    title = stringResource(R.string.language),
                    description = when (language) {
                        "ca" -> stringResource(R.string.catalan)
                        else -> stringResource(R.string.spanish)
                    },
                    onClick = { showLanguageDialog = true }
                )
            }

            // Ayuda y Soporte
            SettingsCard(title = "Ayuda y Privacidad") {
                SettingsClickableItem(
                    icon = Icons.Default.Help,
                    title = stringResource(R.string.help_title),
                    description = "Cómo funciona FotoChef",
                    onClick = onNavigateToHelp
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                SettingsClickableItem(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(R.string.privacy_title),
                    description = "Política de privacidad y datos",
                    onClick = onNavigateToPrivacy
                )
            }

            // Datos y Sesión
            SettingsCard(title = "Cuenta y Datos") {
                SettingsClickableItem(
                    icon = Icons.Default.Logout,
                    title = "Cerrar sesión",
                    description = "Salir de tu cuenta actual",
                    onClick = onLogout
                )
                HorizontalDivider(modifier = Modifier.padding(horizontal = 12.dp))
                SettingsClickableItem(
                    icon = Icons.Default.Delete,
                    title = "Borrar todos los datos",
                    description = "Elimina recetas y restablece ajustes",
                    onClick = { showClearDataDialog = true },
                    isDestructive = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "${stringResource(R.string.version)}: 1.0.0",
                modifier = Modifier.align(Alignment.CenterHorizontally),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showLanguageDialog) {
        LanguageSelectionDialog(
            currentLanguage = language,
            onLanguageSelected = { newLanguage ->
                viewModel.setLanguage(newLanguage)
                showLanguageDialog = false
            },
            onDismiss = { showLanguageDialog = false }
        )
    }

    if (showClearDataDialog) {
        ClearDataConfirmationDialog(
            onConfirm = {
                viewModel.clearAllData()
                showClearDataDialog = false
            },
            onDismiss = { showClearDataDialog = false },
            isClearing = isClearingData
        )
    }
}

@Composable
fun SettingsHeader(userName: String, userEmail: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.2f),
                border = androidx.compose.foundation.BorderStroke(2.dp, Color.White)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = userEmail,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "FotoChef Premium",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun SettingsCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(content = content)
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Switch(checked = isChecked, onCheckedChange = onToggle)
    }
}

@Composable
private fun SettingsClickableItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = if (isDestructive) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, modifier = Modifier.size(20.dp), tint = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.secondary)
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = if (isDestructive) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface)
                Text(text = description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun LanguageSelectionDialog(
    currentLanguage: String,
    onLanguageSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.language)) },
        text = {
            Column {
                LanguageOption(label = "Español", isSelected = currentLanguage == "es", onClick = { onLanguageSelected("es") })
                LanguageOption(label = "Català", isSelected = currentLanguage == "ca", onClick = { onLanguageSelected("ca") })
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cerrar") } }
    )
}

@Composable
private fun LanguageOption(label: String, isSelected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        RadioButton(selected = isSelected, onClick = onClick)
        Text(label)
    }
}

@Composable
private fun ClearDataConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit, isClearing: Boolean) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("¿Borrar todos los datos?") },
        text = { Text("Esta acción eliminará todas las recetas guardadas y restablecerá tus preferencias. No se puede deshacer.") },
        confirmButton = {
            Button(onClick = onConfirm, enabled = !isClearing, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                if (isClearing) CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White) else Text("Borrar todo")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isClearing) { Text("Cancelar") } }
    )
}

