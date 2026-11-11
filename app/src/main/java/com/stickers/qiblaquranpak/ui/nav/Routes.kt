package com.stickers.qiblaquranpak.ui.nav

import kotlinx.serialization.Serializable

@Serializable data object SplashRoute
@Serializable data object LanguageRoute
@Serializable data object OnboardingRoute
@Serializable data class MainRoute(val tab: Int = 0)
@Serializable data object SettingsRoute
@Serializable data object PremiumRoute
@Serializable data object AiDeenRoute
@Serializable data class SurahRoute(val surah: Int, val ayah: Int = 1)
@Serializable data object DuasRoute
@Serializable data class DuaCategoryRoute(val id: String)
@Serializable data object TasbihRoute
@Serializable data object AyaatRoute
@Serializable data object NamesRoute
@Serializable data object MosquesRoute
@Serializable data object KalmasRoute
@Serializable data object DailyDeenRoute
@Serializable data object AzkarRoute
@Serializable data class AzkarCategoryRoute(val id: String)
@Serializable data object HajjRoute
@Serializable data object MoreRoute
@Serializable data class LiveRoute(val which: Int)
@Serializable data object PlacesRoute
@Serializable data class PlaceRoute(val id: String)
@Serializable data object QuranLearningRoute
@Serializable data object QaidaLearningRoute
@Serializable data object ProgressRoute
@Serializable data object CalendarRoute
@Serializable data object LanguageSettingsRoute
@Serializable data object PrayerMethodsRoute
@Serializable data object PrayerAlertRoute

object Tabs {
    const val HOME = 0
    const val PRAYER = 1
    const val QIBLA = 2
    const val LEARNING = 3
    const val QURAN = 4
}
