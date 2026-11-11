package com.stickers.qiblaquranpak.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.stickers.qiblaquranpak.R

@OptIn(ExperimentalTextApi::class)
private fun nunito(w: Int) = Font(
    R.font.nunito,
    weight = FontWeight(w),
    variationSettings = FontVariation.Settings(FontVariation.weight(w)),
)

/** Geometric sans used throughout the reference app. */
val Nunito = FontFamily(nunito(300), nunito(400), nunito(500), nunito(600), nunito(700), nunito(800))

/** Uthmani Quran script. */
val QuranFont = FontFamily(Font(R.font.amiri_quran))

private val base = TextStyle(fontFamily = Nunito, color = AppColors.TextPrimary)

val Typography = Typography(
    displayLarge = base.copy(fontSize = 44.sp, fontWeight = FontWeight.SemiBold),
    headlineLarge = base.copy(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    headlineMedium = base.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
    titleLarge = base.copy(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = base.copy(fontSize = 17.sp, fontWeight = FontWeight.Medium),
    titleSmall = base.copy(fontSize = 15.sp, fontWeight = FontWeight.Medium),
    bodyLarge = base.copy(fontSize = 16.sp),
    bodyMedium = base.copy(fontSize = 14.sp),
    bodySmall = base.copy(fontSize = 12.sp),
    labelLarge = base.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    labelMedium = base.copy(fontSize = 13.sp, fontWeight = FontWeight.Medium),
    labelSmall = base.copy(fontSize = 11.sp, fontWeight = FontWeight.Medium),
)
