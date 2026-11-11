@file:OptIn(kotlin.time.ExperimentalTime::class)

package com.stickers.qiblaquranpak.data.prayer

import com.batoulapps.adhan2.CalculationMethod
import com.batoulapps.adhan2.CalculationParameters
import com.batoulapps.adhan2.Coordinates
import com.batoulapps.adhan2.HighLatitudeRule
import com.batoulapps.adhan2.Madhab
import com.batoulapps.adhan2.PrayerAdjustments
import com.batoulapps.adhan2.PrayerTimes
import com.batoulapps.adhan2.Qibla
import com.batoulapps.adhan2.data.DateComponents
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

enum class Prayer(val label: String, val isPrayer: Boolean) {
    FAJR("Fajr", true),
    SUNRISE("Sunrise", false),
    DHUHR("Zuhr", true),
    ASR("Asr", true),
    MAGHRIB("Maghrib", true),
    ISHA("Isha", true),
}

enum class Method(val label: String, val adhan: CalculationMethod) {
    KARACHI("University of Islamic Sciences, Karachi", CalculationMethod.KARACHI),
    MWL("Muslim World League", CalculationMethod.MUSLIM_WORLD_LEAGUE),
    ISNA("Islamic Society of North America (ISNA)", CalculationMethod.NORTH_AMERICA),
    EGYPTIAN("Egyptian General Authority of Survey", CalculationMethod.EGYPTIAN),
    UMM_AL_QURA("Umm Al-Qura University, Makkah", CalculationMethod.UMM_AL_QURA),
    DUBAI("Dubai", CalculationMethod.DUBAI),
    QATAR("Qatar", CalculationMethod.QATAR),
    KUWAIT("Kuwait", CalculationMethod.KUWAIT),
    MOONSIGHTING("Moonsighting Committee", CalculationMethod.MOON_SIGHTING_COMMITTEE),
    SINGAPORE("Singapore", CalculationMethod.SINGAPORE),
    TURKEY("Diyanet, Turkey", CalculationMethod.TURKEY),
}

data class PrayerSettings(
    val method: Method = Method.KARACHI,
    val hanafi: Boolean = true,
    val highLatitude: HighLatitudeRule? = null,
    /** Minute offsets in Prayer.ordinal order. */
    val offsets: List<Int> = List(6) { 0 },
)

data class DayTimes(val date: LocalDate, val times: Map<Prayer, ZonedDateTime>) {
    operator fun get(p: Prayer): ZonedDateTime = times.getValue(p)
}

object PrayerCalculator {

    fun day(lat: Double, lng: Double, date: LocalDate, settings: PrayerSettings, zone: ZoneId = ZoneId.systemDefault()): DayTimes {
        val base = settings.method.adhan.parameters
        val o = settings.offsets
        val params: CalculationParameters = base.copy(
            madhab = if (settings.hanafi) Madhab.HANAFI else Madhab.SHAFI,
            highLatitudeRule = settings.highLatitude ?: base.highLatitudeRule,
            prayerAdjustments = PrayerAdjustments(o[0], o[1], o[2], o[3], o[4], o[5]),
        )
        val pt = PrayerTimes(Coordinates(lat, lng), DateComponents(date.year, date.monthValue, date.dayOfMonth), params)
        fun z(i: kotlin.time.Instant) = Instant.ofEpochMilli(i.toEpochMilliseconds()).atZone(zone)
        return DayTimes(
            date,
            linkedMapOf(
                Prayer.FAJR to z(pt.fajr),
                Prayer.SUNRISE to z(pt.sunrise),
                Prayer.DHUHR to z(pt.dhuhr),
                Prayer.ASR to z(pt.asr),
                Prayer.MAGHRIB to z(pt.maghrib),
                Prayer.ISHA to z(pt.isha),
            ),
        )
    }

    /** Next upcoming prayer (sunrise excluded) from [now], rolling into tomorrow's Fajr. */
    fun next(lat: Double, lng: Double, now: ZonedDateTime, settings: PrayerSettings): Pair<Prayer, ZonedDateTime> {
        val today = day(lat, lng, now.toLocalDate(), settings, now.zone)
        Prayer.entries.filter { it.isPrayer }.forEach { p ->
            if (today[p].isAfter(now)) return p to today[p]
        }
        val tomorrow = day(lat, lng, now.toLocalDate().plusDays(1), settings, now.zone)
        return Prayer.FAJR to tomorrow[Prayer.FAJR]
    }

    /** The prayer whose time window contains [now] (null before Fajr's window starts today). */
    fun current(today: DayTimes, now: ZonedDateTime): Prayer? =
        Prayer.entries.filter { it.isPrayer }.lastOrNull { !today[it].isAfter(now) }

    fun qibla(lat: Double, lng: Double): Double = Qibla(Coordinates(lat, lng)).direction
}
