package com.stickers.qiblaquranpak

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.stickers.qiblaquranpak.data.AlertMode
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.ui.AppViewModel
import com.stickers.qiblaquranpak.ui.components.PendingScreen
import com.stickers.qiblaquranpak.ui.main.MainScreen
import com.stickers.qiblaquranpak.ui.main.Navigator
import com.stickers.qiblaquranpak.ui.main.hasNotificationPermission
import com.stickers.qiblaquranpak.ui.nav.*
import com.stickers.qiblaquranpak.ui.onboarding.LanguageScreen
import com.stickers.qiblaquranpak.ui.onboarding.OnboardingScreen
import com.stickers.qiblaquranpak.ui.onboarding.SplashScreen
import com.stickers.qiblaquranpak.ui.theme.QiblaQuranPakTheme
import com.stickers.qiblaquranpak.widget.Widgets
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    companion object { const val EXTRA_DEST = "dest" }

    private val vm: AppViewModel by viewModels()

    /** Destination requested by a notification tap; consumed by the NavHost. */
    private val pendingDest = mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= 29) window.isNavigationBarContrastEnforced = false
        pendingDest.value = intent?.getStringExtra(EXTRA_DEST)
        setContent {
            QiblaQuranPakTheme {
                AppRoot(vm, pendingDest.value, onDestConsumed = { pendingDest.value = null }, onExit = { finish() })
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        pendingDest.value = intent.getStringExtra(EXTRA_DEST)
    }

    override fun onStop() {
        super.onStop()
        Widgets.updateAll(this)
    }
}

@Composable
private fun AppRoot(vm: AppViewModel, dest: String?, onDestConsumed: () -> Unit, onExit: () -> Unit) {
    val nav = rememberNavController()
    val ctx = LocalContext.current
    val app = ctx.applicationContext as App
    val scope = rememberCoroutineScope()
    val navigator = remember(nav) { NavNavigator(nav) }

    LaunchedEffect(dest) {
        when (dest) {
            "prayer" -> nav.navigate(MainRoute(Tabs.PRAYER)) { popUpTo(0) }
            "player" -> {}
            null -> return@LaunchedEffect
        }
        onDestConsumed()
    }

    NavHost(nav, startDestination = SplashRoute, enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
        composable<SplashRoute> {
            // The splash waits for the first-run notification prompt to be answered, like the reference.
            val answered = androidx.compose.runtime.remember { kotlinx.coroutines.CompletableDeferred<Unit>() }
            val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { answered.complete(Unit) }
            LaunchedEffect(Unit) {
                val prefs = app.prefs.snapshot()
                val started = System.currentTimeMillis()
                if (prefs[Prefs.LANGUAGE] == null && Build.VERSION.SDK_INT >= 33 && !ctx.hasNotificationPermission()) {
                    notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    answered.await()
                }
                delay((2200 - (System.currentTimeMillis() - started)).coerceAtLeast(400))
                val next: Any = when {
                    prefs[Prefs.LANGUAGE] == null -> LanguageRoute
                    prefs[Prefs.ONBOARDED] != true -> OnboardingRoute
                    else -> MainRoute(Tabs.HOME)
                }
                nav.navigate(next) { popUpTo(SplashRoute) { inclusive = true } }
            }
            SplashScreen()
        }
        composable<LanguageRoute> {
            LanguageScreen(selected = null, onPick = { code ->
                scope.launch {
                    app.prefs.set(Prefs.LANGUAGE, code)
                    delay(350)
                    nav.navigate(OnboardingRoute) { popUpTo(LanguageRoute) { inclusive = true } }
                }
            })
        }
        composable<OnboardingRoute> {
            OnboardingScreen(initialAlerts = emptyMap()) { alerts, persistent ->
                scope.launch {
                    alerts.forEach { (p, on) ->
                        app.prefs.set(Prefs.alertKey(p), (if (on) AlertMode.AZAN else AlertMode.SILENT).name)
                    }
                    app.prefs.set(Prefs.PERSISTENT_NOTIF, persistent)
                    app.prefs.set(Prefs.ONBOARDED, true)
                    app.rescheduleAzan()
                    nav.navigate(MainRoute(Tabs.HOME)) { popUpTo(OnboardingRoute) { inclusive = true } }
                }
            }
        }
        composable<MainRoute> { entry ->
            MainScreen(vm, entry.toRoute<MainRoute>().tab, navigator, onExit)
        }
        Screens.register(this, navigator, vm)
    }
}

private class NavNavigator(private val nav: NavHostController) : Navigator {
    override fun go(route: Any) = nav.navigate(route) { launchSingleTop = true }
    override fun back() { nav.popBackStack() }
}

