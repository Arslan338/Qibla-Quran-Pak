package com.stickers.qiblaquranpak.ui.qibla

import androidx.compose.runtime.Composable
import com.stickers.qiblaquranpak.ui.components.PendingScreen

@Composable
fun QiblaScreen(ui: com.stickers.qiblaquranpak.ui.PrayerUi, heading: com.stickers.qiblaquranpak.data.compass.Heading?, nav: com.stickers.qiblaquranpak.ui.main.Navigator, onBack: () -> Unit) {
    PendingScreen("QiblaScreen")
}
