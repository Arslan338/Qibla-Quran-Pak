package com.stickers.qiblaquranpak.azan

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.stickers.qiblaquranpak.R
import com.stickers.qiblaquranpak.data.prayer.Prayer

/** Plays the full Azan as an alarm-usage stream with a "Stop" action. */
class AzanService : Service() {

    private var player: MediaPlayer? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopSelf()
            return START_NOT_STICKY
        }
        val prayer = intent?.getStringExtra(AzanScheduler.EXTRA_PRAYER)?.let { runCatching { Prayer.valueOf(it) }.getOrNull() } ?: Prayer.DHUHR
        val time = intent?.getLongExtra(AzanScheduler.EXTRA_TIME, System.currentTimeMillis()) ?: System.currentTimeMillis()

        val stop = PendingIntent.getService(
            this, 1, Intent(this, AzanService::class.java).setAction(ACTION_STOP),
            PendingIntent.FLAG_IMMUTABLE,
        )
        val n = NotificationCompat.Builder(this, AzanNotifier.CH_PLAYING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(AzanNotifier.title(prayer))
            .setContentText("${prayer.label} ${AzanNotifier.fmt(time)}")
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setContentIntent(AzanNotifier.openAppIntent(this))
            .setDeleteIntent(stop)
            .addAction(0, "Stop", stop)
            .setOngoing(true)
            .build()
        ServiceCompat.startForeground(this, AzanNotifier.ID_PLAYING, n, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)

        player?.release()
        val res = if (prayer == Prayer.FAJR) R.raw.azan_aaqib else R.raw.azan_makkah
        player = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_ALARM).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            resources.openRawResourceFd(res).use { setDataSource(it.fileDescriptor, it.startOffset, it.length) }
            setOnCompletionListener { stopSelf() }
            setOnErrorListener { _, _, _ -> stopSelf(); true }
            prepare()
            start()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        player?.release()
        player = null
        ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object { const val ACTION_STOP = "stop" }
}
