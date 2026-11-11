package com.stickers.qiblaquranpak.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import com.stickers.qiblaquranpak.App
import com.stickers.qiblaquranpak.MainActivity
import com.stickers.qiblaquranpak.R
import com.stickers.qiblaquranpak.data.place
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.data.prayer.PrayerCalculator
import com.stickers.qiblaquranpak.data.prayerSettings
import com.stickers.qiblaquranpak.util.Fmt
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.ZonedDateTime

object Widgets {
    enum class Kind { PRAYER, QIBLA }

    fun requestPin(context: Context, kind: Kind) {
        val mgr = AppWidgetManager.getInstance(context)
        val provider = ComponentName(context, if (kind == Kind.PRAYER) PrayerWidget::class.java else QiblaWidget::class.java)
        if (mgr.isRequestPinAppWidgetSupported) {
            mgr.requestPinAppWidget(provider, null, null)
        } else {
            Toast.makeText(context, "Long-press your home screen, tap Widgets and add Qibla Compass.", Toast.LENGTH_LONG).show()
        }
    }

    /** Re-renders every placed widget; cheap, called after alarms and location changes. */
    fun updateAll(context: Context) {
        val app = context.applicationContext as App
        CoroutineScope(SupervisorJob() + Dispatchers.Default).launch {
            val prefs = app.prefs.snapshot()
            val mgr = AppWidgetManager.getInstance(context)
            val place = prefs.place()
            val open = PendingIntent.getActivity(
                context, 10, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            )

            mgr.getAppWidgetIds(ComponentName(context, PrayerWidget::class.java)).forEach { id ->
                val v = RemoteViews(context.packageName, R.layout.widget_prayer)
                if (place != null) {
                    val now = ZonedDateTime.now()
                    val settings = prefs.prayerSettings()
                    val today = PrayerCalculator.day(place.lat, place.lng, now.toLocalDate(), settings)
                    val (next, time) = PrayerCalculator.next(place.lat, place.lng, now, settings)
                    v.setTextViewText(R.id.w_next_name, next.label)
                    v.setTextViewText(R.id.w_next_time, Fmt.time(time))
                    v.setTextViewText(R.id.w_fajr, Fmt.time(today[Prayer.FAJR]))
                    v.setTextViewText(R.id.w_dhuhr, Fmt.time(today[Prayer.DHUHR]))
                    v.setTextViewText(R.id.w_asr, Fmt.time(today[Prayer.ASR]))
                    v.setTextViewText(R.id.w_maghrib, Fmt.time(today[Prayer.MAGHRIB]))
                    v.setTextViewText(R.id.w_isha, Fmt.time(today[Prayer.ISHA]))
                    v.setChronometer(R.id.w_countdown, android.os.SystemClock.elapsedRealtime() + (time.toInstant().toEpochMilli() - System.currentTimeMillis()), null, true)
                    v.setChronometerCountDown(R.id.w_countdown, true)
                } else {
                    v.setTextViewText(R.id.w_next_name, "Open app to set location")
                }
                v.setOnClickPendingIntent(R.id.w_root, open)
                mgr.updateAppWidget(id, v)
            }

            mgr.getAppWidgetIds(ComponentName(context, QiblaWidget::class.java)).forEach { id ->
                val v = RemoteViews(context.packageName, R.layout.widget_qibla)
                if (place != null) {
                    v.setTextViewText(R.id.w_bearing, Fmt.bearing(PrayerCalculator.qibla(place.lat, place.lng)))
                    v.setTextViewText(R.id.w_city, place.city)
                }
                v.setOnClickPendingIntent(R.id.w_root, open)
                mgr.updateAppWidget(id, v)
            }
        }
    }
}

class PrayerWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) = Widgets.updateAll(context)
}

class QiblaWidget : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) = Widgets.updateAll(context)
}
