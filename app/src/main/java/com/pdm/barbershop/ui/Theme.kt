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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat

private val LightColors = lightColorScheme(
    primary = Color(0xFF006E5F),
    secondary = Color(0xFF4E635C),
    tertiary = Color(0xFF446179),
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF5ADBC9),
    secondary = Color(0xFFB9CCC5),
    tertiary = Color(0xFFA8CBE7),
)

@Composable
fun BarbershopTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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

    val activity = (context as? Activity)
    if (activity != null) {
        SideEffect {
            val window = activity.window
            window.statusBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, window.decorView)
                .isAppearanceLightStatusBars = !useDarkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}