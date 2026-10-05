package com.example.wife.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// Light Theme Palette
val LightPaper = Color(0xFFF3EAD9)
val LightPaperRaised = Color(0xFFFBF6EB)
val LightInk = Color(0xFF23304A)
val LightInkMuted = Color(0xFF6B5D48)
val LightHairline = Color(0xFFCDBFA5)
val LightTerracotta = Color(0xFFB5482A)
val LightTerracottaDark = Color(0xFF8E3A20)
val LightDanger = Color(0xFF8E2A1C)
val LightOnInk = Color(0xFFF3EAD9)

// Dark Theme Palette
val DarkPaper = Color(0xFF161E2E)
val DarkPaperRaised = Color(0xFF1F2A3F)
val DarkInk = Color(0xFFF3EAD9)
val DarkInkMuted = Color(0xFFA89B83)
val DarkHairline = Color(0xFF33405A)
val DarkTerracotta = Color(0xFFB5482A)
val DarkTerracottaDark = Color(0xFFE08A68)
val DarkDanger = Color(0xFFE5705E)
val DarkOnInk = Color(0xFF161E2E)

@Immutable
data class TerasSenjaColors(
    val paper: Color,
    val paperRaised: Color,
    val ink: Color,
    val inkMuted: Color,
    val hairline: Color,
    val terracotta: Color,
    val terracottaDark: Color,
    val danger: Color,
    val onInk: Color
)

val LightTerasSenjaColors = TerasSenjaColors(
    paper = LightPaper,
    paperRaised = LightPaperRaised,
    ink = LightInk,
    inkMuted = LightInkMuted,
    hairline = LightHairline,
    terracotta = LightTerracotta,
    terracottaDark = LightTerracottaDark,
    danger = LightDanger,
    onInk = LightOnInk
)

val DarkTerasSenjaColors = TerasSenjaColors(
    paper = DarkPaper,
    paperRaised = DarkPaperRaised,
    ink = DarkInk,
    inkMuted = DarkInkMuted,
    hairline = DarkHairline,
    terracotta = DarkTerracotta,
    terracottaDark = DarkTerracottaDark,
    danger = DarkDanger,
    onInk = DarkOnInk
)

val LocalTerasSenjaColors = staticCompositionLocalOf { LightTerasSenjaColors }
