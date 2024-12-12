package com.ochess.edict.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    primary = Purple200,
    primaryContainer = Purple700,
    secondary = Grey300,
    background = blueBGNight,
    surface = cardBGNight,
    onSurface = pinkText,
    onBackground = pinkText,
)

private val LightColorPalette = lightColorScheme(
    primary = Purple500,
    primaryContainer = Purple700,
    secondary = Grey300,
    background = blueBGDay,
    surface = cardBGDay,
    onSurface = blueText,
    onBackground = blueText,

//    surfaceVariant = cardBGDay,
//    onPrimary = Color.White,
//    onSecondary = Color.Black
)

@Composable
fun MTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = Shapes,
        content = content
    )
}