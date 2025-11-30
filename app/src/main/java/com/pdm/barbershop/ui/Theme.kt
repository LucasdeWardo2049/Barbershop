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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = OnPrimaryGreen,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = TextPrimary,

    secondary = SecondaryGold,
    onSecondary = OnSecondaryGold,
    secondaryContainer = SecondaryGoldLight,
    onSecondaryContainer = TextPrimary,

    tertiary = PrimaryGreenDark,
    onTertiary = OnPrimaryGreen,

    background = BackgroundLight,
    onBackground = TextPrimary,

    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextSecondary,

    error = ErrorRed,
    onError = OnPrimaryGreen,
    errorContainer = ErrorContainer,
    onErrorContainer = TextPrimary,

    outline = TextTertiary,
    outlineVariant = SurfaceVariant
)

private val DarkColors = darkColorScheme(
    primary = PrimaryGreenLight,
    onPrimary = PrimaryGreenDark,
    primaryContainer = PrimaryGreenDark,
    onPrimaryContainer = OnPrimaryGreen,

    secondary = SecondaryGoldLight,
    onSecondary = OnSecondaryGold,
    secondaryContainer = SecondaryGold,
    onSecondaryContainer = TextPrimary,

    tertiary = PrimaryGreen,
    onTertiary = OnPrimaryGreen,

    background = PrimaryGreenDark,
    onBackground = OnPrimaryGreen,

    surface = PrimaryGreenDark,
    onSurface = OnPrimaryGreen,
    surfaceVariant = PrimaryGreen,
    onSurfaceVariant = OnPrimaryGreen,

    error = ErrorRed,
    onError = OnPrimaryGreen,
    errorContainer = ErrorContainer,
    onErrorContainer = TextPrimary,

    outline = TextTertiary,
    outlineVariant = SurfaceVariant
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
            // Status bar color matches primary (AppBar color)
            window.statusBarColor = colorScheme.primary.toArgb()
            // Dark icons for light theme, light icons for dark theme
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
