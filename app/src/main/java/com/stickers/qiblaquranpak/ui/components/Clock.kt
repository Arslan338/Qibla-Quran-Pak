package com.stickers.qiblaquranpak.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.produceState
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

/** Current time, updated each second; keep its readers small (e.g. a countdown Text). */
@Composable
fun rememberNow(): State<ZonedDateTime> = produceState(ZonedDateTime.now()) {
    while (true) {
        delay(1000 - System.currentTimeMillis() % 1000)
        value = ZonedDateTime.now()
    }
}
