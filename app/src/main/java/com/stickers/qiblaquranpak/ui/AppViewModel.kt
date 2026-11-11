package com.stickers.qiblaquranpak.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.stickers.qiblaquranpak.App
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.data.hijri.Hijri
import com.stickers.qiblaquranpak.data.hijri.HijriDay
import com.stickers.qiblaquranpak.data.location.Place
import com.stickers.qiblaquranpak.data.place
import com.stickers.qiblaquranpak.data.prayer.DayTimes
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.data.prayer.PrayerCalculator
import com.stickers.qiblaquranpak.data.prayerSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZonedDateTime

data class PrayerUi(
    val place: Place?,
    val today: DayTimes?,
    val next: Prayer?,
    val nextTime: ZonedDateTime?,
    val current: Prayer?,
    val hijri: HijriDay,
    val qibla: Double?,
    val now: ZonedDateTime,
)

/** Activity-scoped state shared by Home, Prayer and Qibla tabs. */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val app = application as App
    val prefs: Prefs = app.prefs

    // Prayer times have minute precision, so state only needs to change on minute boundaries.
    // Second-by-second countdowns use rememberNow() locally instead of recomposing whole screens.
    private val ticker = flow {
        while (true) {
            emit(ZonedDateTime.now())
            delay(60_000 - System.currentTimeMillis() % 60_000)
        }
    }

    private val _locating = MutableStateFlow(false)
    val locating: StateFlow<Boolean> = _locating.asStateFlow()

    val prayer: StateFlow<PrayerUi> = combine(prefs.data, ticker) { p, now ->
        val place = p.place()
        val settings = p.prayerSettings()
        val hijri = Hijri.of(now.toLocalDate(), p[Prefs.HIJRI_OFFSET] ?: 0)
        if (place == null) {
            PrayerUi(null, null, null, null, null, hijri, null, now)
        } else {
            val today = PrayerCalculator.day(place.lat, place.lng, now.toLocalDate(), settings, now.zone)
            val (next, nextTime) = PrayerCalculator.next(place.lat, place.lng, now, settings)
            PrayerUi(place, today, next, nextTime, PrayerCalculator.current(today, now), hijri, PrayerCalculator.qibla(place.lat, place.lng), now)
        }
    }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000),
        PrayerUi(null, null, null, null, null, Hijri.of(LocalDate.now()), null, ZonedDateTime.now()),
    )

    /** Refreshes GPS location; returns false when permission is missing or no fix could be obtained. */
    fun refreshLocation(onDone: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            _locating.value = true
            val place = app.location.currentPlace()
            if (place != null) {
                prefs.savePlace(place)
                app.rescheduleAzan()
            }
            _locating.value = false
            onDone(place != null)
        }
    }
}
