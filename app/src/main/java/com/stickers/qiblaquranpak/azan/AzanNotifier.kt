package com.stickers.qiblaquranpak.azan

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.net.Uri
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.stickers.qiblaquranpak.MainActivity
import com.stickers.qiblaquranpak.R
import com.stickers.qiblaquranpak.data.AlertMode
import com.stickers.qiblaquranpak.data.prayer.Prayer
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

object AzanNotifier {
    const val CH_AZAN = "azan_v1"
    const val CH_BEEP = "prayer_beep_v1"
    const val CH_VIBRATE = "prayer_vibrate_v1"
    const val CH_SILENT = "prayer_silent_v1"
    const val CH_PLAYING = "azan_playing_v1"
    const val CH_PRE = "prayer_pre_v1"
    const val CH_PLAYER = "quran_player_v1"

    const val ID_PRAYER = 1001
    const val ID_PLAYING = 1002
    const val ID_PRE = 1003

    private val timeFmt = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)

    fun createChannels(context: Context) {
        val nm = context.getSystemService(NotificationManager::class.java)
        val alarmAttrs = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        nm.createNotificationChannels(
            listOf(
                NotificationChannel(CH_AZAN, "Azan", NotificationManager.IMPORTANCE_HIGH).apply {
                    setSound(rawUri(context, R.raw.azan_makkah), alarmAttrs)
                    enableVibration(true)
                },
                NotificationChannel(CH_BEEP, "Prayer alert (beep)", NotificationManager.IMPORTANCE_HIGH),
                NotificationChannel(CH_VIBRATE, "Prayer alert (vibrate)", NotificationManager.IMPORTANCE_HIGH).apply {
                    setSound(null, null)
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 500, 300, 500, 300, 500)
                },
                NotificationChannel(CH_SILENT, "Prayer alert (silent)", NotificationManager.IMPORTANCE_LOW).apply {
                    setSound(null, null)
                },
                NotificationChannel(CH_PLAYING, "Azan playing", NotificationManager.IMPORTANCE_HIGH).apply {
                    setSound(null, null)
                },
                NotificationChannel(CH_PRE, "Before prayer reminder", NotificationManager.IMPORTANCE_DEFAULT),
                NotificationChannel(CH_PLAYER, "Quran player", NotificationManager.IMPORTANCE_LOW).apply {
                    setSound(null, null)
                },
            ),
        )
    }

    fun rawUri(context: Context, res: Int): Uri =
        Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${context.packageName}/$res")

    fun openAppIntent(context: Context, dest: String = "prayer"): PendingIntent =
        PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java)
                .putExtra(MainActivity.EXTRA_DEST, dest)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

    fun title(prayer: Prayer) = if (prayer == Prayer.SUNRISE) "Sunrise" else "It's time for ${prayer.label} prayer"

    fun fmt(time: Long): String = timeFmt.format(Instant.ofEpochMilli(time).atZone(ZoneId.systemDefault()))

    fun showPrayer(context: Context, prayer: Prayer, time: Long, mode: AlertMode) {
        val channel = when (mode) {
            AlertMode.AZAN -> CH_AZAN
            AlertMode.BEEP -> CH_BEEP
            AlertMode.VIBRATE -> CH_VIBRATE
            AlertMode.SILENT -> CH_SILENT
        }
        notify(
            context, ID_PRAYER,
            NotificationCompat.Builder(context, channel)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title(prayer))
                .setContentText("${prayer.label} ${fmt(time)}")
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(openAppIntent(context))
                .setAutoCancel(true)
                .build(),
        )
    }

    fun showPreAlert(context: Context, prayer: Prayer, time: Long) {
        notify(
            context, ID_PRE,
            NotificationCompat.Builder(context, CH_PRE)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("${prayer.label} prayer is coming up")
                .setContentText("${prayer.label} at ${fmt(time)}")
                .setContentIntent(openAppIntent(context))
                .setAutoCancel(true)
                .build(),
        )
    }

    private fun notify(context: Context, id: Int, n: Notification) {
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) return
        NotificationManagerCompat.from(context).notify(id, n)
    }
}
