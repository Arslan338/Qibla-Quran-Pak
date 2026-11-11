package com.stickers.qiblaquranpak.azan

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.datastore.preferences.core.Preferences
import com.stickers.qiblaquranpak.data.AlertMode
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.data.alertMode
import com.stickers.qiblaquranpak.data.place
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.data.prayer.PrayerCalculator
import com.stickers.qiblaquranpak.data.prayerSettings
import java.time.ZonedDateTime

/**
 * Keeps one exact alarm per upcoming prayer (today + tomorrow) and an optional pre-alert.
 * Called on app start, after settings change, after every alarm, on boot and on time/zone change.
 */
object AzanScheduler {

    const val EXTRA_PRAYER = "prayer"
    const val EXTRA_TIME = "time"
    const val EXTRA_PRE = "pre"

    fun canExact(context: Context): Boolean {
        val am = context.getSystemService(AlarmManager::class.java)
        return Build.VERSION.SDK_INT < 31 || am.canScheduleExactAlarms()
    }

    fun reschedule(context: Context, prefs: Preferences) {
        val am = context.getSystemService(AlarmManager::class.java)
        // Clear everything we may have set before.
        for (code in 0 until SLOTS * 2) {
            pending(context, code, null)?.let { am.cancel(it) }
        }
        val place = prefs.place() ?: return
        if (prefs[Prefs.NOTIFICATIONS_ON] == false) return
        val settings = prefs.prayerSettings()
        val preMin = prefs[Prefs.PRE_ALERT_MIN] ?: 0
        val now = ZonedDateTime.now()

        val events = (0..1).flatMap { d ->
            val day = PrayerCalculator.day(place.lat, place.lng, now.toLocalDate().plusDays(d.toLong()), settings, now.zone)
            Prayer.entries.map { it to day[it] }
        }.filter { it.second.isAfter(now) }.take(SLOTS)

        events.forEachIndexed { i, (prayer, time) ->
            if (prefs.alertMode(prayer) == AlertMode.SILENT) return@forEachIndexed
            set(am, context, i, prayer, time, pre = false)
            if (preMin > 0 && prayer.isPrayer) {
                val preTime = time.minusMinutes(preMin.toLong())
                if (preTime.isAfter(now)) set(am, context, SLOTS + i, prayer, preTime, pre = true)
            }
        }
    }

    private fun set(am: AlarmManager, context: Context, code: Int, prayer: Prayer, time: ZonedDateTime, pre: Boolean) {
        val pi = pending(context, code, Triple(prayer, time.toInstant().toEpochMilli(), pre)) ?: return
        val at = time.toInstant().toEpochMilli()
        if (canExact(context)) {
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        } else {
            am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, at, pi)
        }
    }

    private fun pending(context: Context, code: Int, data: Triple<Prayer, Long, Boolean>?): PendingIntent? {
        val intent = Intent(context, AzanReceiver::class.java).setAction(ACTION_ALARM)
        if (data != null) {
            intent.putExtra(EXTRA_PRAYER, data.first.name).putExtra(EXTRA_TIME, data.second).putExtra(EXTRA_PRE, data.third)
            return PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }
        return PendingIntent.getBroadcast(context, code, intent, PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE)
    }

    const val ACTION_ALARM = "com.stickers.qiblaquranpak.AZAN_ALARM"
    private const val SLOTS = 12
}
