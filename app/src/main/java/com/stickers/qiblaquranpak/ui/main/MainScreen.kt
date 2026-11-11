package com.stickers.qiblaquranpak.ui.main

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.emptyPreferences
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stickers.qiblaquranpak.App
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.ui.AppViewModel
import com.stickers.qiblaquranpak.ui.components.BottomBar
import com.stickers.qiblaquranpak.ui.home.HomeAction
import com.stickers.qiblaquranpak.ui.home.HomeExtras
import com.stickers.qiblaquranpak.ui.home.HomeScreen
import com.stickers.qiblaquranpak.ui.nav.*
import com.stickers.qiblaquranpak.widget.Widgets
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate

/** Navigation callbacks the tabs need; implemented by the NavHost. */
interface Navigator {
    fun go(route: Any)
    fun back()
}

@Composable
fun MainScreen(vm: AppViewModel, initialTab: Int, nav: Navigator, onExit: () -> Unit) {
    val ctx = LocalContext.current
    val app = ctx.applicationContext as App
    val scope = rememberCoroutineScope()
    var tab by rememberSaveable { mutableStateOf(initialTab) }
    com.stickers.qiblaquranpak.ui.components.SystemBarIcons(darkIcons = tab == Tabs.PRAYER || tab == Tabs.LEARNING || tab == Tabs.QURAN)
    val ui by vm.prayer.collectAsStateWithLifecycle()
    val prefs by vm.prefs.data.collectAsState(emptyPreferences())

    // Compass only runs while a tab that shows it is visible (and the app is in foreground).
    val needCompass = tab == Tabs.HOME || tab == Tabs.QIBLA
    val place = ui.place
    val headingFlow = remember(needCompass, place?.lat, place?.lng) {
        if (needCompass && app.compass.isAvailable) app.compass.headings(place?.lat ?: 21.4, place?.lng ?: 39.8, place?.alt ?: 0.0)
            // Ignore sub-half-degree jitter so the UI is not invalidated for invisible changes.
            .distinctUntilChanged { a, b -> kotlin.math.abs(a.azimuth - b.azimuth) < 0.5f && a.accuracy == b.accuracy }
        else emptyFlow()
    }
    val heading by headingFlow.collectAsStateWithLifecycle(null)

    var showPerms by rememberSaveable { mutableStateOf(!ctx.hasLocationPermission() || !ctx.hasNotificationPermission()) }
    var showExit by remember { mutableStateOf(false) }
    var premiumBannerClosed by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (ctx.hasLocationPermission()) vm.refreshLocation()
    }

    BackHandler {
        if (tab != Tabs.HOME) tab = Tabs.HOME else showExit = true
    }

    val today = LocalDate.now().toEpochDay()
    val checkDays = prefs[Prefs.CHECKIN_DAYS] ?: 0
    val lastCheck = prefs[Prefs.CHECKIN_LAST] ?: Long.MIN_VALUE

    Box(Modifier.fillMaxSize()) {
        when (tab) {
            Tabs.HOME -> HomeScreen(
                ui = ui,
                extras = HomeExtras(
                    heading = { heading?.azimuth ?: 0f },
                    checkInDays = if (lastCheck < today - 1) 0 else checkDays,
                    checkedInToday = lastCheck == today,
                    quranProgress = (prefs[Prefs.QURAN_LESSONS_DONE] ?: 0) / com.stickers.qiblaquranpak.data.content.Lessons.quran.size.toFloat(),
                    qaidaProgress = (prefs[Prefs.QAIDA_LESSONS_DONE] ?: 0) / com.stickers.qiblaquranpak.data.content.Lessons.qaida.size.toFloat(),
                    showPremiumBanner = !premiumBannerClosed,
                ),
                onAction = { a ->
                    when (a) {
                        HomeAction.LOCATION -> if (ctx.hasLocationPermission()) vm.refreshLocation { ok ->
                            if (!ok) Toast.makeText(ctx, "Unable to get location. Turn on GPS and try again.", Toast.LENGTH_SHORT).show()
                        } else showPerms = true
                        HomeAction.QIBLA -> tab = Tabs.QIBLA
                        HomeAction.VIEW_TIME, HomeAction.PRAYER_TAB -> tab = Tabs.PRAYER
                        HomeAction.CHECK_IN -> scope.launch {
                            val days = if (lastCheck == today - 1) checkDays + 1 else 1
                            app.prefs.set(Prefs.CHECKIN_DAYS, if (days > 7) 1 else days)
                            app.prefs.set(Prefs.CHECKIN_LAST, today)
                            if (days == 7) {
                                app.prefs.set(Prefs.TOKENS, (prefs[Prefs.TOKENS] ?: 0) + 50)
                                Toast.makeText(ctx, "MashaAllah! 7 days completed. 50 tokens rewarded.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(ctx, "Checked in: Day $days/7", Toast.LENGTH_SHORT).show()
                            }
                        }
                        HomeAction.WIDGET_PRAYER -> Widgets.requestPin(ctx, Widgets.Kind.PRAYER)
                        HomeAction.WIDGET_QIBLA -> Widgets.requestPin(ctx, Widgets.Kind.QIBLA)
                        else -> HomeRoutes.route(a)?.let(nav::go)
                    }
                },
                onDua = { nav.go(com.stickers.qiblaquranpak.ui.nav.DuasRoute) },
                onPlace = { nav.go(com.stickers.qiblaquranpak.ui.nav.PlaceRoute(it)) },
                onClosePremium = { premiumBannerClosed = true },
            )
            Tabs.PRAYER -> com.stickers.qiblaquranpak.ui.prayer.PrayerScreen(vm, nav)
            Tabs.QIBLA -> com.stickers.qiblaquranpak.ui.qibla.QiblaScreen(ui, heading, nav, onBack = { tab = Tabs.HOME })
            Tabs.LEARNING -> com.stickers.qiblaquranpak.ui.learning.LearningScreen(nav)
            Tabs.QURAN -> com.stickers.qiblaquranpak.ui.quran.QuranIndexScreen(nav)
        }
        Box(Modifier.align(Alignment.BottomCenter)) {
            BottomBar(tab, quranBadge = prefs[Prefs.LAST_READ] == null) { tab = it }
        }
    }

    if (showPerms) PermissionSheet(onLocationGranted = { vm.refreshLocation() }, onDismiss = { showPerms = false })
    if (showExit) ExitDialog(onDismiss = { showExit = false }, onExit = onExit)
}

private object HomeRoutes {
    fun route(a: HomeAction): Any? = when (a) {
        HomeAction.AI_DEEN -> AiDeenRoute
        HomeAction.PREMIUM -> PremiumRoute
        HomeAction.SETTINGS -> SettingsRoute
        HomeAction.QURAN_LEARNING -> QuranLearningRoute
        HomeAction.QAIDA_LEARNING -> QaidaLearningRoute
        HomeAction.PROGRESS -> ProgressRoute
        HomeAction.DUAS -> DuasRoute
        HomeAction.TASBIH -> TasbihRoute
        HomeAction.AYAAT -> AyaatRoute
        HomeAction.NAMES -> NamesRoute
        HomeAction.MOSQUES -> MosquesRoute
        HomeAction.KALMAS -> KalmasRoute
        HomeAction.DAILY_DEEN -> DailyDeenRoute
        HomeAction.AZKAR -> AzkarRoute
        HomeAction.HAJJ -> HajjRoute
        HomeAction.MORE -> MoreRoute
        HomeAction.LIVE_MAKKAH -> LiveRoute(0)
        HomeAction.LIVE_MADINAH -> LiveRoute(1)
        HomeAction.PLACES -> PlacesRoute
        else -> null
    }
}
