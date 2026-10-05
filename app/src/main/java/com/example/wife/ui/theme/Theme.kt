package com.example.wife.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

private val LightColorScheme = lightColorScheme(
    primary = LightTerracotta,
    onPrimary = LightPaperRaised,
    secondary = LightInk,
    onSecondary = LightOnInk,
    background = LightPaper,
    onBackground = LightInk,
    surface = LightPaper,
    onSurface = LightInk,
    surfaceVariant = LightPaperRaised,
    onSurfaceVariant = LightInkMuted,
    outline = LightHairline,
    error = LightDanger,
    onError = LightPaperRaised
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkTerracotta,
    onPrimary = DarkPaperRaised,
    secondary = DarkInk,
    onSecondary = DarkOnInk,
    background = DarkPaper,
    onBackground = DarkInk,
    surface = DarkPaper,
    onSurface = DarkInk,
    surfaceVariant = DarkPaperRaised,
    onSurfaceVariant = DarkInkMuted,
    outline = DarkHairline,
    error = DarkDanger,
    onError = DarkPaperRaised
)

@Composable
fun TerasSenjaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkTerasSenjaColors else LightTerasSenjaColors
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(
        LocalTerasSenjaColors provides colors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object TerasSenjaTheme {
    val colors: TerasSenjaColors
        @Composable
        @ReadOnlyComposable
        get() = LocalTerasSenjaColors.current
}

// Alias for backwards compatibility if needed
@Composable
fun WifeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    TerasSenjaTheme(darkTheme = darkTheme, content = content)
}
