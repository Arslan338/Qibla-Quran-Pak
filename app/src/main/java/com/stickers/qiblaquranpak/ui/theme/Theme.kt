package com.stickers.qiblaquranpak.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val Scheme = lightColorScheme(
    primary = AppColors.Green,
    onPrimary = AppColors.TextOnGreen,
    secondary = AppColors.GreenLight,
    tertiary = AppColors.Purple,
    background = AppColors.ScreenBg,
    surface = AppColors.ScreenBg,
    onBackground = AppColors.TextPrimary,
    onSurface = AppColors.TextPrimary,
    error = AppColors.Red,
)

/** The reference app is light-only; dark mode is intentionally not offered. */
@Composable
fun QiblaQuranPakTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = Scheme, typography = Typography, content = content)
}
