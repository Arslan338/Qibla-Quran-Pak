package com.stickers.qiblaquranpak

import android.app.Application
import com.stickers.qiblaquranpak.azan.AzanNotifier
import com.stickers.qiblaquranpak.azan.AzanScheduler
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.data.compass.Compass
import com.stickers.qiblaquranpak.data.location.LocationRepository
import com.stickers.qiblaquranpak.data.quran.QuranRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class App : Application() {
    val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val prefs by lazy { Prefs(this) }
    val quran by lazy { QuranRepository(this) }
    val location by lazy { LocationRepository(this) }
    val compass by lazy { Compass(this) }

    override fun onCreate() {
        super.onCreate()
        AzanNotifier.createChannels(this)
        appScope.launch { AzanScheduler.reschedule(this@App, prefs.snapshot()) }
    }

    fun rescheduleAzan() {
        appScope.launch { AzanScheduler.reschedule(this@App, prefs.snapshot()) }
    }
}
