package com.newbet.app.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = UnibetGreenAccent,
    onPrimary = TextWhite,
    primaryContainer = UnibetGreenHeader,
    onPrimaryContainer = TextWhite,
    secondary = UnibetGold,
    onSecondary = BgCanvas,
    tertiary = UnibetGreenHover,
    background = BgCanvas,
    surface = CardSurface,
    surfaceVariant = CardSurfaceHover,
    onBackground = TextWhite,
    onSurface = TextWhite,
    onSurfaceVariant = TextSecondary,
    outline = BorderSubtle
)

@Composable
fun NewbetTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = UnibetGreenHeader.toArgb()
            window.navigationBarColor = BgCanvas.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
