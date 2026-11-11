package com.stickers.qiblaquranpak.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.stickers.qiblaquranpak.data.location.Place
import com.stickers.qiblaquranpak.data.prayer.Method
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.data.prayer.PrayerSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

/** How a prayer's alert behaves. */
enum class AlertMode { AZAN, BEEP, VIBRATE, SILENT }

class Prefs(private val context: Context) {
    private val store get() = context.dataStore

    val data: Flow<Preferences> get() = store.data

    suspend fun snapshot(): Preferences = store.data.first()

    suspend fun <T> set(key: Preferences.Key<T>, value: T) { store.edit { it[key] = value } }

    suspend fun remove(key: Preferences.Key<*>) { store.edit { it.remove(key) } }

    suspend fun toggleInSet(key: Preferences.Key<Set<String>>, item: String) {
        store.edit {
            val cur = it[key].orEmpty()
            it[key] = if (item in cur) cur - item else cur + item
        }
    }

    // ---- Location ----
    val place: Flow<Place?> = store.data.map { it.place() }

    suspend fun savePlace(p: Place) {
        store.edit {
            it[LAT] = p.lat; it[LNG] = p.lng; it[ALT] = p.alt; it[CITY] = p.city; it[COUNTRY] = p.country
        }
    }

    // ---- Prayer settings ----
    val prayerSettings: Flow<PrayerSettings> = store.data.map { it.prayerSettings() }

    companion object {
        val ONBOARDED = booleanPreferencesKey("onboarded")
        val LANGUAGE = stringPreferencesKey("language")
        val LAT = doublePreferencesKey("lat")
        val LNG = doublePreferencesKey("lng")
        val ALT = doublePreferencesKey("alt")
        val CITY = stringPreferencesKey("city")
        val COUNTRY = stringPreferencesKey("country")
        val METHOD = stringPreferencesKey("method")
        val HANAFI = booleanPreferencesKey("hanafi")
        val HIJRI_OFFSET = intPreferencesKey("hijri_offset")
        val NOTIFICATIONS_ON = booleanPreferencesKey("notifications_on")
        val PRE_ALERT_MIN = intPreferencesKey("pre_alert_min")
        val DND_ON = booleanPreferencesKey("dnd_on")
        val DND_FROM = intPreferencesKey("dnd_from_min")
        val DND_TO = intPreferencesKey("dnd_to_min")
        val CHECKIN_DAYS = intPreferencesKey("checkin_days")
        val CHECKIN_LAST = longPreferencesKey("checkin_last_epoch_day")
        val TOKENS = intPreferencesKey("tokens")
        val FAV_SURAHS = stringSetPreferencesKey("fav_surahs")
        val BOOKMARKS = stringSetPreferencesKey("bookmarks") // "surah:ayah"
        val LAST_READ = stringPreferencesKey("last_read") // "surah:ayah"
        val RECITER = stringPreferencesKey("reciter")
        val TRANSLATION = stringPreferencesKey("translation")
        val ARABIC_SIZE = intPreferencesKey("arabic_size")
        val TRANSLATION_SIZE = intPreferencesKey("translation_size")
        val TASBIH_COUNT = intPreferencesKey("tasbih_count")
        val TASBIH_TARGET = intPreferencesKey("tasbih_target")
        val TASBIH_TOTAL = intPreferencesKey("tasbih_total")
        val TASBIH_VIBRATE = booleanPreferencesKey("tasbih_vibrate")
        val TASBIH_SOUND = booleanPreferencesKey("tasbih_sound")
        val COMPASS_STYLE = intPreferencesKey("compass_style")
        val RATED = booleanPreferencesKey("rated")
        val QURAN_LESSONS_DONE = intPreferencesKey("quran_lessons_done")
        val QAIDA_LESSONS_DONE = intPreferencesKey("qaida_lessons_done")
        val PERSISTENT_NOTIF = booleanPreferencesKey("persistent_notif")
        val PREMIUM_OFFER_SHOWN = booleanPreferencesKey("premium_offer_shown")

        fun offsetKey(p: Prayer) = intPreferencesKey("offset_${p.name}")
        fun alertKey(p: Prayer) = stringPreferencesKey("alert_${p.name}")
        /** Set of "PRAYER" names offered on a given epoch day. */
        fun trackedKey(epochDay: Long) = stringSetPreferencesKey("tracked_$epochDay")
    }
}

fun Preferences.place(): Place? {
    val lat = this[Prefs.LAT] ?: return null
    val lng = this[Prefs.LNG] ?: return null
    return Place(lat, lng, this[Prefs.ALT] ?: 0.0, this[Prefs.CITY].orEmpty(), this[Prefs.COUNTRY].orEmpty())
}

fun Preferences.prayerSettings() = PrayerSettings(
    method = this[Prefs.METHOD]?.let { runCatching { Method.valueOf(it) }.getOrNull() } ?: Method.KARACHI,
    hanafi = this[Prefs.HANAFI] ?: true,
    offsets = Prayer.entries.map { this[Prefs.offsetKey(it)] ?: 0 },
)

fun Preferences.alertMode(p: Prayer): AlertMode =
    this[Prefs.alertKey(p)]?.let { runCatching { AlertMode.valueOf(it) }.getOrNull() }
        ?: if (p == Prayer.SUNRISE) AlertMode.SILENT else AlertMode.AZAN
