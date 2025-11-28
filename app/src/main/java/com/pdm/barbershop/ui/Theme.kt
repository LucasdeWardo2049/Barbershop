package com.pdm.barbershop.ui

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

private val LightColors = lightColorScheme(
    primary = DarkGreen, // Verde Escuro
    onPrimary = PureWhite,
    primaryContainer = DarkGreen, // Para o topo da tela (AppBar)
    onPrimaryContainer = PureWhite,
    secondary = GoldAccent,
    onSecondary = TextBlack,
    background = OffWhite,
    onBackground = TextBlack,
    surface = PureWhite,
    onSurface = TextBlack,
    error = ErrorRed,
    onError = PureWhite
)

// O tema escuro pode ser ajustado futuramente, se necessário.
private val DarkColors = darkColorScheme(
    primary = DarkGreen,
    onPrimary = PureWhite,
    primaryContainer = DarkGreen,
    onPrimaryContainer = PureWhite,
    secondary = GoldAccent,
    onSecondary = TextBlack,
    background = Color(0xFF121212),
    onBackground = PureWhite,
    surface = Color(0xFF1E1E1E),
    onSurface = PureWhite,
    error = ErrorRed.copy(alpha = 0.8f),
    onError = PureWhite
)

@Composable
fun BarbershopTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
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
            // Ajuste para usar a cor primária (verde escuro) na status bar, integrando com a AppBar
            window.statusBarColor = colorScheme.primary.toArgb() 
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false // Texto claro na status bar escura
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
