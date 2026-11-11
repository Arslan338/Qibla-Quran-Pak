package com.stickers.qiblaquranpak.ui.components

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Sets status/navigation bar icon colour for the current screen.
 * [darkIcons] = true on light backgrounds (language, onboarding, white screens).
 */
@Composable
fun SystemBarIcons(darkIcons: Boolean) {
    val view = LocalView.current
    if (view.isInEditMode) return
    DisposableEffect(darkIcons) {
        val window = (view.context as? Activity)?.window
        if (window != null) {
            val c = WindowCompat.getInsetsController(window, view)
            c.isAppearanceLightStatusBars = darkIcons
            // Bottom areas are white on every screen, so navigation-bar icons stay dark.
            c.isAppearanceLightNavigationBars = true
        }
        onDispose { }
    }
}
