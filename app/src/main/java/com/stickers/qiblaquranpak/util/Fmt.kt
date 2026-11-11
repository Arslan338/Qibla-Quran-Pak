package com.stickers.qiblaquranpak.util

import java.time.Duration
import java.time.LocalDate
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

object Fmt {
    private val time12 = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
    private val long = DateTimeFormatter.ofPattern("dd MMMM, yyyy", Locale.ENGLISH)
    private val dayMonth = DateTimeFormatter.ofPattern("d/M/yyyy", Locale.ENGLISH)

    fun time(t: ZonedDateTime): String = time12.format(t)

    fun longDate(d: LocalDate): String = long.format(d)

    fun shortDate(d: LocalDate): String = dayMonth.format(d)

    /** Hijri month spellings as shown on the reference Home header. */
    fun hijriMonth(m: Int): String = listOf(
        "Muharram", "Safar", "Rabi al-awal", "Rabi al-thani", "Jumaada al-awal", "Jumaada al-thani",
        "Rajab", "Shaban", "Ramadan", "Shawwal", "Dhu al-Qadah", "Dhu al-Hijjah",
    )[m - 1]

    fun countdown(d: Duration): String {
        val s = d.seconds.coerceAtLeast(0)
        return "%02d : %02d : %02d".format(s / 3600, (s % 3600) / 60, s % 60)
    }

    /** "255° W" — rounded bearing plus the nearest of 8 compass points. */
    fun bearing(deg: Double): String {
        val d = ((deg % 360) + 360) % 360
        val points = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
        val p = points[(((d + 22.5) % 360) / 45).toInt()]
        return "${d.toInt()}° $p"
    }
}
