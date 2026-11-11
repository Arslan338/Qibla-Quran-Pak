package com.stickers.qiblaquranpak.ui.nav

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.stickers.qiblaquranpak.ui.AppViewModel
import com.stickers.qiblaquranpak.ui.components.PendingScreen
import com.stickers.qiblaquranpak.ui.main.Navigator

/** Secondary destinations. Each feature screen is wired here. */
object Screens {
    fun register(g: NavGraphBuilder, nav: Navigator, vm: AppViewModel) = with(g) {
        composable<SettingsRoute> { PendingScreen("Settings", nav::back) }
        composable<PremiumRoute> { PendingScreen("Premium", nav::back) }
        composable<AiDeenRoute> { PendingScreen("Ai Deen", nav::back) }
        composable<SurahRoute> { PendingScreen("Surah", nav::back) }
        composable<DuasRoute> { PendingScreen("Duas", nav::back) }
        composable<DuaCategoryRoute> { PendingScreen("Duas", nav::back) }
        composable<TasbihRoute> { PendingScreen("Tasbih", nav::back) }
        composable<AyaatRoute> { PendingScreen("Ayaat", nav::back) }
        composable<NamesRoute> { PendingScreen("99 Names", nav::back) }
        composable<MosquesRoute> { PendingScreen("Mosques", nav::back) }
        composable<KalmasRoute> { PendingScreen("Kalmas", nav::back) }
        composable<DailyDeenRoute> { PendingScreen("Daily Deen", nav::back) }
        composable<AzkarRoute> { PendingScreen("Azkar", nav::back) }
        composable<AzkarCategoryRoute> { PendingScreen("Azkar", nav::back) }
        composable<HajjRoute> { PendingScreen("Hajj Umrah", nav::back) }
        composable<MoreRoute> { PendingScreen("More", nav::back) }
        composable<LiveRoute> { PendingScreen("Live", nav::back) }
        composable<PlacesRoute> { PendingScreen("Islamic History Places", nav::back) }
        composable<PlaceRoute> { PendingScreen("Place", nav::back) }
        composable<QuranLearningRoute> { PendingScreen("Quran Learning", nav::back) }
        composable<QaidaLearningRoute> { PendingScreen("Qaida Learning", nav::back) }
        composable<ProgressRoute> { PendingScreen("Progress Tracking", nav::back) }
        composable<CalendarRoute> { PendingScreen("Calendar", nav::back) }
        composable<LanguageSettingsRoute> { PendingScreen("Languages", nav::back) }
        composable<PrayerMethodsRoute> { PendingScreen("Prayer Methods", nav::back) }
        composable<PrayerAlertRoute> { PendingScreen("Prayer Time Alert", nav::back) }
    }
}
