# Builder brief (shared by all feature builders)

Goal: rebuild the reference app "Muslim Prayer – Qibla Compass" (Visionary AI) pixel-for-pixel and behaviour-for-behaviour in Jetpack Compose. Logo/colors will be re-skinned later, so ALL colours must come from `AppColors` (add new tokens there only in your copy if missing — report them) or local `private val` constants at the top of your files.

## Sources of truth
- `G:\QiblaQuranPak\reference\SPEC.md` — read the sections for your feature + section G (design tokens), K (lists), L (settings defaults), M (share formats), N (toasts).
- `G:\QiblaQuranPak\reference\screens\*.png` (1440×3040 px, density 3.5 → dp = px / 3.5) and matching `.xml` uiautomator dumps (exact bounds in px). Open the PNGs to look at them; use the XML bounds for exact sizes.
- `G:\QiblaQuranPak\reference\RESEARCH.md` — verified libraries, APIs, URLs, licences.

## Rules
- Do NOT copy images, sounds, text bodies or code out of the reference APK (we never extracted it). Recreate visuals with Compose/vector drawing or `ArtPlaceholder` (same size + radius as the original artwork). Religious texts (duas, azkar, names, kalmas) must be authentic, written from standard public-domain sources (Quran / Hisnul Muslim hadith texts); translations in your own words or from permissively licensed sources noted in RESEARCH.md.
- No ads, no billing. Premium-locked items: show the lock UI exactly, and tapping opens the Premium screen route (`PremiumRoute`); keep the feature usable only if SPEC says it's free.
- Do NOT use adb or the phone. Do NOT touch G:\QiblaQuranPak — work only in your own copy (path in your prompt).
- Build with: `cd <your copy>; $env:JAVA_HOME="C:\Program Files\Android\Android Studio\jbr"; .\gradlew.bat :app:assembleDebug --console=plain -q` (PowerShell) or the bash equivalent. Your work is done only when this builds with zero errors and no new warnings in your files.
- Only edit files inside the packages/dirs you own (listed in your prompt) plus: `ui/nav/Screens.kt` (replace the PendingScreen lines for your routes with a call to your own `register` function), and — only if unavoidable — `gradle/libs.versions.toml`, `app/build.gradle.kts`, `AndroidManifest.xml`, `res/values/strings.xml`, `ui/theme/Color.kt`. List every such shared edit in your final report with the exact lines, because they will be merged by hand.
- New preference keys: declare them in your own package (e.g. `object QuranKeys { val X = intPreferencesKey("quran_x") }`), prefix the key string with your feature name. Read/write through `app.prefs.set(...)`, `app.prefs.data`, `app.prefs.snapshot()`.
- New routes: declare `@Serializable` route types in your own package; register them in your own `register(g: NavGraphBuilder, nav: Navigator, vm: AppViewModel)`.
- Keep it smooth: lists in LazyColumn with keys, no work on the main thread (assets/network on Dispatchers.IO), no recomposition loops, release sensors/players in DisposableEffect / lifecycle-aware collection.

## Existing code you should use (read these files first)
- `App.kt` — `app.prefs`, `app.quran` (QuranRepository), `app.location`, `app.compass`, `app.rescheduleAzan()`. Get it via `LocalContext.current.applicationContext as App`.
- `ui/AppViewModel.kt` — `vm.prayer: StateFlow<PrayerUi>` (place, today's times, next prayer + time, hijri, qibla bearing, now ticking each second), `vm.refreshLocation()`.
- `ui/main/MainScreen.kt` (tabs + bottom bar), `ui/main/Navigator` (`nav.go(route)`, `nav.back()`), `ui/nav/Routes.kt` (existing routes), `ui/nav/Screens.kt`.
- `ui/theme/*` — `AppColors`, `Nunito` font family (the reference font), `QuranFont` (Amiri Quran), `Typography`.
- `ui/components/*` — `GreenTopBar`, `ArtPlaceholder`, `ArrowCircle`, `NewRibbon`, `SectionTitle`, `VSpace`/`HSpace`, `CompassDial`, `drawKaaba`, `KaabaRingIcon`, `BottomBar`, prayer `icon()`/`display`, `PendingScreen`. `ui/onboarding/Onboarding.kt` has `SpokeLoader` (the app's loading spinner). `ui/home/HomeParts.kt` has `RemoteImage`, `MiniCompass`.
- `data/*` — `Prefs` (+ keys), `prayer/PrayerCalculator` (adhan2), `hijri/Hijri` (Umm al-Qura + offset, events), `quran/QuranRepository` (offline Uthmani text + English Saheeh + Urdu Junagarhi, surah/juz metadata, search), `location/LocationRepository`, `compass/Compass`, `content/Content.kt`, `content/Lessons.kt`.
- `audio/Reciters.kt`, `audio/PlaybackService.kt` (Media3 MediaSessionService — connect with `MediaController`).
- `azan/*` — alarms, notifications, Azan service.
- `util/Fmt.kt` — time/date/countdown/bearing formatting.

## Final report (reply with)
1. Files created/changed (paths relative to your copy).
2. Every shared-file edit with exact lines.
3. What matches the reference exactly, what differs and why, anything UNSURE.
4. Build result (last lines of the gradle output).
