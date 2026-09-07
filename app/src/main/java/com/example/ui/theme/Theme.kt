package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val ObsidianFluxColorScheme = darkColorScheme(
    primary = Zinc100,
    onPrimary = Obsidian950,
    primaryContainer = Obsidian800,
    onPrimaryContainer = Zinc100,
    secondary = Emerald400,
    onSecondary = Obsidian950,
    secondaryContainer = Emerald950,
    onSecondaryContainer = Emerald400,
    tertiary = Cyan400,
    onTertiary = Obsidian950,
    background = Obsidian950,
    onBackground = Zinc100,
    surface = Obsidian900,
    onSurface = Zinc100,
    surfaceVariant = Obsidian800,
    onSurfaceVariant = Zinc300,
    outline = Obsidian700,
    outlineVariant = Obsidian600,
    error = Rose500,
    onError = Zinc50,
    errorContainer = Rose950,
    onErrorContainer = Rose400
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force Obsidian Flux Pro Dark
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = Obsidian950.toArgb()
                it.navigationBarColor = Obsidian950.toArgb()
                WindowCompat.getInsetsController(it, view).apply {
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    MaterialTheme(
        colorScheme = ObsidianFluxColorScheme,
        typography = Typography,
        content = content
    )
}

