package com.pdm.barbershop.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Paleta de cores baseadas no XML
private val DarkGreen = Color(0xFF1E3932)
private val MediumGreen = Color(0xFF385D51)
private val LightPeach = Color(0xFFF3D9C9)
private val DarkPeach = Color(0xFFE6A685)
private val AccentOrange = Color(0xFFD97D54)
private val OffWhite = Color(0xFFFAF8F7)
private val LightGray = Color(0xFFD3D3D3)
private val DarkGray = Color(0xFF4A4A4A)
private val Black = Color(0xFF000000)
private val White = Color(0xFFFFFFFF)

// Tema claro
private val LightColors = lightColorScheme(
    primary = MediumGreen,
    onPrimary = White,
    secondary = AccentOrange,
    onSecondary = White,
    background = OffWhite,
    onBackground = DarkGreen,
    surface = White,
    onSurface = DarkGray,
    error = DarkPeach,   // pode ser ajustado conforme necessidade
    onError = White
)

// Tema escuro
private val DarkColors = darkColorScheme(
    primary = DarkPeach,
    onPrimary = DarkGreen,
    secondary = AccentOrange,
    onSecondary = DarkGreen,
    background = DarkGreen,
    onBackground = OffWhite,
    surface = MediumGreen,
    onSurface = White,
    error = LightPeach,  // alternativo para diferenciar bem do Light
    onError = Black
)

@Composable
fun BarbershopTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Mantido desabilitado para preservar identidade visual
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColors
        else -> LightColors
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as Activity
            val window = activity.window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
