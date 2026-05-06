package com.example.fotochef.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Esquema de colores para el tema claro de FotoChef.
 * Usa tonos cálidos naranjas como color primario (marca culinaria).
 */
private val LightColorScheme = lightColorScheme(
    primary = Orange500,
    onPrimary = Color.White,
    primaryContainer = Orange100,
    onPrimaryContainer = Brown700,
    secondary = Green500,
    onSecondary = Color.White,
    secondaryContainer = Green100,
    onSecondaryContainer = Green500,
    tertiary = Red500,
    onTertiary = Color.White,
    tertiaryContainer = Red200,
    onTertiaryContainer = Red500,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    error = ErrorColor,
    onError = Color.White
)

/**
 * Esquema de colores para el tema oscuro de FotoChef.
 * Mantiene la identidad naranja pero adaptada a fondo oscuro.
 */
private val DarkColorScheme = darkColorScheme(
    primary = Orange300,
    onPrimary = Brown700,
    primaryContainer = Orange500,
    onPrimaryContainer = Orange50,
    secondary = Green300,
    onSecondary = Brown700,
    secondaryContainer = Green500,
    onSecondaryContainer = Green100,
    tertiary = Red400,
    onTertiary = Color.White,
    tertiaryContainer = Red500,
    onTertiaryContainer = Red200,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    error = ErrorColorDark,
    onError = Brown700
)

/**
 * Tema principal de FotoChef.
 * Aplica los colores culinarios, tipografía personalizada y colorea la status bar.
 * 
 * @param darkTheme Si es true, usa colores oscuros. Si es false, usa colores claros.
 * @param content El contenido composable a renderizar
 */
@Composable
fun FotochefTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Colorear la status bar para que combine con el tema
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = FotoChefTypography,
        content = content
    )
}