package com.jarvis.ai.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = JarvisColors.BlueGlow,
    onPrimary = JarvisColors.DeepBlack,
    primaryContainer = JarvisColors.BlueGlow.copy(alpha = 0.2f),
    onPrimaryContainer = JarvisColors.ArcReactor,
    secondary = JarvisColors.Red,
    onSecondary = JarvisColors.Black,
    secondaryContainer = JarvisColors.RedDark.copy(alpha = 0.2f),
    onSecondaryContainer = JarvisColors.Red,
    tertiary = JarvisColors.Gold,
    onTertiary = JarvisColors.Black,
    background = JarvisColors.DeepBlack,
    onBackground = JarvisColors.TextPrimary,
    surface = JarvisColors.Gunmetal,
    onSurface = JarvisColors.TextPrimary,
    surfaceVariant = JarvisColors.GunmetalLight,
    onSurfaceVariant = JarvisColors.TextSecondary,
    outline = JarvisColors.GlassBorder,
    error = JarvisColors.Error,
    onError = JarvisColors.Black,
)

@Composable
fun JarvisTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = JarvisColors.DeepBlack.toArgb()
            window.navigationBarColor = JarvisColors.DeepBlack.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = JarvisTypography,
        content = content
    )
}
