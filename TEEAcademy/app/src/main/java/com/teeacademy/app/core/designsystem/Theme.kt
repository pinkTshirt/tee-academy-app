package com.teeacademy.app.core.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = Color.White,
    background = SurfaceLight,
    surface = SurfaceLight
)

private val DarkColors = darkColorScheme(
    primary = BrandPrimaryDark,
    onPrimary = Color.Black,
    background = SurfaceDark,
    surface = SurfaceDark
)

/**
 * App-wide theme. Deliberately does NOT use dynamicColorScheme — see
 * Color.kt header comment and UX spec Section 11.
 */
@Composable
fun TeeAcademyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

/** Returns the correct image-matte color for the current theme (never pure black). */
@Composable
fun imageMatteColor(darkTheme: Boolean = isSystemInDarkTheme()): Color =
    if (darkTheme) ImageMatteDark else ImageMatteLight
