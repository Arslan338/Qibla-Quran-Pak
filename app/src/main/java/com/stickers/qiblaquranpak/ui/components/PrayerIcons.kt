package com.stickers.qiblaquranpak.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WbTwilight
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.stickers.qiblaquranpak.data.prayer.Prayer

val PrayerGold = Color(0xFFF0B83C)

fun Prayer.icon(): ImageVector = when (this) {
    Prayer.FAJR -> Icons.Filled.WbTwilight
    Prayer.SUNRISE -> Icons.Filled.WbTwilight
    Prayer.DHUHR -> Icons.Filled.LightMode
    Prayer.ASR -> Icons.Filled.WbSunny
    Prayer.MAGHRIB -> Icons.Filled.WbTwilight
    Prayer.ISHA -> Icons.Filled.Bedtime
}

/** Spelling used on the reference Home / Prayer screens. */
val Prayer.display: String
    get() = when (this) {
        Prayer.FAJR -> "Fajar"
        Prayer.SUNRISE -> "Sunrise"
        Prayer.DHUHR -> "Zuhur"
        Prayer.ASR -> "Asar"
        Prayer.MAGHRIB -> "Maghrib"
        Prayer.ISHA -> "Isha"
    }
