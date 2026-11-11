package com.stickers.qiblaquranpak.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/** Temporary body for a screen that is still being built. */
@Composable
fun PendingScreen(name: String, onBack: (() -> Unit)? = null) {
    Column(Modifier.fillMaxSize().background(Color.White)) {
        if (onBack != null) GreenTopBar(name, onBack)
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(name) }
    }
}
