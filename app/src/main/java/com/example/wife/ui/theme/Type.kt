package com.example.wife.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.R as GoogleFontsR

val fontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = GoogleFontsR.array.com_google_android_gms_fonts_certs
)

val LoraFont = GoogleFont("Lora")
val DMSansFont = GoogleFont("DM Sans")

val LoraFontFamily = FontFamily(
    Font(googleFont = LoraFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = LoraFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = LoraFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

val DMSansFontFamily = FontFamily(
    Font(googleFont = DMSansFont, fontProvider = fontProvider, weight = FontWeight.Normal),
    Font(googleFont = DMSansFont, fontProvider = fontProvider, weight = FontWeight.Medium),
    Font(googleFont = DMSansFont, fontProvider = fontProvider, weight = FontWeight.Bold)
)

object TerasSenjaTypography {
    val characterHeader = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 20.sp
    )
    val characterMessage = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )
    val userMessage = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    )
    val permissionCardTitle = TextStyle(
        fontFamily = LoraFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 15.sp
    )
    val captionStatusTimestamp = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 11.sp
    )
    val labelMedium = TextStyle(
        fontFamily = DMSansFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    )
}

val Typography = Typography(
    titleMedium = TerasSenjaTypography.characterHeader,
    bodyLarge = TerasSenjaTypography.characterMessage,
    bodyMedium = TerasSenjaTypography.userMessage,
    titleSmall = TerasSenjaTypography.permissionCardTitle,
    labelSmall = TerasSenjaTypography.captionStatusTimestamp,
    labelMedium = TerasSenjaTypography.labelMedium
)
