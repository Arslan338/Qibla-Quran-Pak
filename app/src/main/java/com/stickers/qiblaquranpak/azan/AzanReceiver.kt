package com.stickers.qiblaquranpak.azan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.stickers.qiblaquranpak.App
import com.stickers.qiblaquranpak.data.AlertMode
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.data.alertMode
import com.stickers.qiblaquranpak.data.prayer.Prayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.LocalTime

class AzanReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        val app = context.applicationContext as App
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                val prefs = app.prefs.snapshot()
                if (intent.action == AzanScheduler.ACTION_ALARM) {
                    val prayer = intent.getStringExtra(AzanScheduler.EXTRA_PRAYER)?.let { runCatching { Prayer.valueOf(it) }.getOrNull() }
                    val time = intent.getLongExtra(AzanScheduler.EXTRA_TIME, System.currentTimeMillis())
                    val pre = intent.getBooleanExtra(AzanScheduler.EXTRA_PRE, false)
                    // Ignore alarms delivered very late (device was off, doze) — more than 30 min past.
                    if (prayer != null && System.currentTimeMillis() - time < 30 * 60_000L) {
                        val mode = prefs.alertMode(prayer)
                        when {
                            inDnd(prefs) -> AzanNotifier.showPrayer(context, prayer, time, AlertMode.SILENT)
                            pre -> AzanNotifier.showPreAlert(context, prayer, time)
                            mode == AlertMode.AZAN -> startAzan(context, prayer, time)
                            mode != AlertMode.SILENT -> AzanNotifier.showPrayer(context, prayer, time, mode)
                        }
                    }
                }
                AzanScheduler.reschedule(context, prefs)
            } finally {
                pending.finish()
            }
        }
    }

    private fun startAzan(context: Context, prayer: Prayer, time: Long) {
        val svc = Intent(context, AzanService::class.java)
            .putExtra(AzanScheduler.EXTRA_PRAYER, prayer.name)
            .putExtra(AzanScheduler.EXTRA_TIME, time)
        try {
            ContextCompat.startForegroundService(context, svc)
        } catch (e: Exception) {
            // Background FGS start can be refused (inexact alarm, Android 12+ restrictions):
            // fall back to a normal notification that plays the Azan channel sound.
            AzanNotifier.showPrayer(context, prayer, time, AlertMode.AZAN)
        }
    }

    private fun inDnd(prefs: androidx.datastore.preferences.core.Preferences): Boolean {
        if (prefs[Prefs.DND_ON] != true) return false
        val from = prefs[Prefs.DND_FROM] ?: (22 * 60)
        val to = prefs[Prefs.DND_TO] ?: (6 * 60)
        val now = LocalTime.now().let { it.hour * 60 + it.minute }
        return if (from <= to) now in from until to else now >= from || now < to
    }
}

/** Re-arms alarms after reboot, app update, clock / timezone change and exact-alarm permission grant. */
class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pending = goAsync()
        val app = context.applicationContext as App
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            try {
                AzanScheduler.reschedule(context, app.prefs.snapshot())
            } finally {
                pending.finish()
            }
        }
    }
}
