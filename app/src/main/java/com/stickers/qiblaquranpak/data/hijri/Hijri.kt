package com.stickers.qiblaquranpak.data.hijri

import java.time.LocalDate
import java.time.chrono.HijrahDate
import java.time.temporal.ChronoField

data class HijriDay(val day: Int, val month: Int, val year: Int) {
    val monthName: String get() = Hijri.MONTHS[month - 1]
    val monthNameArabic: String get() = Hijri.MONTHS_AR[month - 1]
    override fun toString() = "$day $monthName $year"
}

data class IslamicEvent(val name: String, val month: Int, val day: Int)

object Hijri {
    val MONTHS = listOf(
        "Muharram", "Safar", "Rabi-ul-Awal", "Rabi-ul-Akhir", "Jumada-al-Awal", "Jumada-al-Akhir",
        "Rajab", "Sha'ban", "Ramadan", "Shawwal", "Dhul-Qadah", "Dhul-Hijjah",
    )
    val MONTHS_AR = listOf(
        "مُحَرَّم", "صَفَر", "رَبِيع الأَوَّل", "رَبِيع الثَّانِي", "جُمَادَىٰ الأُولَىٰ", "جُمَادَىٰ الآخِرَة",
        "رَجَب", "شَعْبَان", "رَمَضَان", "شَوَّال", "ذُو القَعْدَة", "ذُو الحِجَّة",
    )

    val EVENTS = listOf(
        IslamicEvent("Islamic New Year", 1, 1),
        IslamicEvent("Day of Ashura", 1, 10),
        IslamicEvent("Milad un Nabi", 3, 12),
        IslamicEvent("Isra and Mi'raj", 7, 27),
        IslamicEvent("Shab-e-Barat", 8, 15),
        IslamicEvent("First Day of Ramadan", 9, 1),
        IslamicEvent("Laylat al-Qadr", 9, 27),
        IslamicEvent("Eid al-Fitr", 10, 1),
        IslamicEvent("Day of Arafah", 12, 9),
        IslamicEvent("Eid al-Adha", 12, 10),
    )

    /** [offset] shifts the Umm al-Qura date by whole days (e.g. -1 for Pakistan moon sighting). */
    fun of(date: LocalDate, offset: Int = 0): HijriDay {
        val h = HijrahDate.from(date.plusDays(offset.toLong()))
        return HijriDay(h.get(ChronoField.DAY_OF_MONTH), h.get(ChronoField.MONTH_OF_YEAR), h.get(ChronoField.YEAR))
    }

    /** Gregorian date for a Hijri day, honouring the same [offset] used by [of]. */
    fun toGregorian(year: Int, month: Int, day: Int, offset: Int = 0): LocalDate =
        LocalDate.from(HijrahDate.of(year, month, day)).minusDays(offset.toLong())

    fun lengthOfMonth(year: Int, month: Int): Int = HijrahDate.of(year, month, 1).lengthOfMonth()

    /** Events of [hijriYear] with their Gregorian dates. */
    fun eventsOf(hijriYear: Int, offset: Int = 0): List<Pair<IslamicEvent, LocalDate>> =
        EVENTS.map { it to toGregorian(hijriYear, it.month, it.day, offset) }
}
