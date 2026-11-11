# Qibla Quran Pak: technical research and decisions

Researched 2026-11-03. Every version below was read from Maven Central or Google Maven metadata on that date. Every URL marked **[200]** was fetched with curl from this machine and returned HTTP 200.

**Project baseline (from `G:\QiblaQuranPak`):** AGP 9.3.3, Gradle 9.5.0, Kotlin 2.2.10 (compose plugin), Compose BOM 2026.02.01 (Compose UI/runtime 1.10.4), compileSdk/targetSdk 37, minSdk 27, JDK 11 target. Built-in Kotlin is on (the project already omits `kotlin-android`).

> **Verified by build:** a scratch copy of this project with the dependency set in section 11 compiled with `:app:testDebugUnitTest`. That covers KSP + Room codegen, adhan2 prayer times, Qibla and HijrahDate (BUILD SUCCESSFUL, 2 min 21 s). The scratch copy is at `C:\Users\Admin\AppData\Local\Temp\claude\G--QiblaQuranPak\a8fc43c0-08b5-409a-8c32-3e78b4bfb8f9\scratchpad\compat`.
> Probe output for Karachi on 2026-11-03 (Karachi method, Hanafi): Fajr 05:23 PKT, Asr 16:14 PKT, Qibla 267.74°, Hijri (Umm al-Qura) 23 Jumada al-Ula 1448.

## Biggest gotcha: Kotlin 2.2.10 cannot read libraries built with Kotlin 2.4

The Kotlin 2.2 compiler reads metadata up to 2.3 only. Several "latest" releases are now compiled with Kotlin 2.4.x, and any one of them breaks the build. **Verified:** swapping in `adhan2:0.0.7` fails with `Module was compiled with an incompatible version of Kotlin. The binary version of its metadata is 2.4.0, expected version is 2.2.0`. It also drags `kotlin-stdlib 2.4.0` in, which then breaks Room's generated code too.

| Library | Latest | Built with | Last version usable on Kotlin 2.2.10 |
|---|---|---|---|
| `com.batoulapps.adhan:adhan2` | 0.0.7 | stdlib 2.4.0 | **0.0.6** (stdlib 2.2.20) |
| `io.coil-kt.coil3:coil-compose` | 3.6.3 | stdlib 2.4.10, CMP foundation 1.12 | **3.4.0** (stdlib 2.3.10); 3.3.0 is the safest (2.2.0) |
| `org.maplibre.compose:maplibre-compose` | 0.20.0 | stdlib 2.4.20 | **0.12.1** (2.2.21) / 0.14.0 (2.3.21, pulls Compose 1.11) |
| `kotlinx-serialization-json` | 1.10.0 | stdlib 2.3.0 | **1.9.0** (pairs with Kotlin 2.2) |

**Option B:** move Kotlin to **2.4.21** (latest stable; AGP 9.3 only requires KGP ≥ 2.2.10). KSP 2.3.x is no longer tied to the Kotlin version. Bump the compose and serialization plugins to the same version. That unlocks the latest version of everything above. If you stay on 2.2.10, run `./gradlew :app:dependencies | grep kotlin-stdlib` whenever you add a library.

---

## 1. Prayer times and Hijri

### Prayer times: **batoulapps Adhan, Kotlin Multiplatform (`adhan2`)**
- **Coordinates:** `com.batoulapps.adhan:adhan2:0.0.6` on Maven Central. Gradle metadata resolves `adhan2-jvm` automatically. Depends on `kotlinx-datetime 0.7.1`. Use 0.0.7 (released 2026-06-13) only on Kotlin ≥ 2.4.
- **Licence:** MIT. Repo: github.com/batoulapps/adhan-kotlin, pushed 2026-10-29, not archived. github.com/batoulapps/adhan-java now redirects there.
- **Calculation methods** (enum read from the 0.0.6 jar): `MUSLIM_WORLD_LEAGUE, EGYPTIAN, KARACHI, UMM_AL_QURA, DUBAI, QATAR, KUWAIT, MOON_SIGHTING_COMMITTEE, SINGAPORE, NORTH_AMERICA, TURKEY, OTHER` (OTHER means custom angles).
- **Madhab:** `SHAFI` (Asr shadow 1×, also Maliki/Hanbali), `HANAFI` (2×). **Pakistan default: `KARACHI` + `HANAFI`.**
- **Other parameters:** `HighLatitudeRule` (`MIDDLE_OF_THE_NIGHT, SEVENTH_OF_THE_NIGHT, TWILIGHT_ANGLE`), `Rounding` (`NEAREST, NONE`), `Shafaq` (`GENERAL, AHMER, ABYAD`, for Moonsighting Committee), `PrayerAdjustments(fajr=…, …)` for per-prayer minute offsets. Also includes `Qibla(coordinates).direction` and `SunnahTimes` (middle and last third of the night).
- **Gotcha:** returns `kotlin.time.Instant`, which is `@ExperimentalTime` on Kotlin 2.2. Add `@file:OptIn(kotlin.time.ExperimentalTime::class)` or `optIn("kotlin.time.ExperimentalTime")` in `compilerOptions`. Convert with `java.time.Instant.ofEpochMilli(i.toEpochMilliseconds())`.
- **Gotcha:** Umm al-Qura Isha is "90 min after Maghrib", but in Ramadan Makkah uses 120. Add +30 to Isha in Ramadan for that method, which the library does not do.
- **Alternative:** `com.batoulapps.adhan:adhan:1.2.1` is pure Java (last published 2021-03-06) and has no Kotlin-version coupling. Its POM has an empty licence block, but the repo is MIT. Use it only if you want zero Kotlin metadata risk.

```kotlin
val params = CalculationMethod.KARACHI.parameters.copy(madhab = Madhab.HANAFI,
    highLatitudeRule = HighLatitudeRule.SEVENTH_OF_THE_NIGHT, prayerAdjustments = PrayerAdjustments(fajr = 0))
val pt = PrayerTimes(Coordinates(lat, lng), DateComponents(y, m, d), params)  // pt.fajr, sunrise, dhuhr, asr, maghrib, isha
```

### Hijri: **use `java.time.chrono.HijrahDate` (built in, API 26+) with a user day offset**
- `HijrahChronology.INSTANCE` is **Umm al-Qura** (`Hijrah-umalqura`). It works on minSdk 27 with no library or desugaring needed. Verified: `HijrahDate.from(LocalDate.of(2026,11,2))` gives `AH 1448-05-22`.
- **Range:** the OpenJDK Umm al-Qura table covers roughly AH 1300–1600 (about 1882–2174 CE) and throws `DateTimeException` outside it. This is the documented OpenJDK range; it is not stated on the Android reference page.
- **±day adjustment** (Pakistan's Ruet-e-Hilal often runs 1 day behind Saudi Arabia). Store `hijriOffsetDays` (−2..+2) in DataStore:
  ```kotlin
  fun hijriToday(offset: Int): HijrahDate = HijrahDate.from(LocalDate.now().plusDays(offset.toLong()))
  // Hijri → Gregorian for event lists: HijrahChronology.INSTANCE.date(1448, 9, 1).let { LocalDate.from(it).minusDays(offset.toLong()) }
  ```
  Also advance the Hijri day at **Maghrib**, not midnight, if you want correct Islamic day semantics (e.g. "Laylat al-Qadr tonight").
- **Month names:** `DateTimeFormatter.ofPattern("d MMMM y", Locale("ar"))` gives Arabic names. Ship your own English and Urdu month-name arrays, because ICU names vary between devices.
- **Alternatives:** `android.icu.util.IslamicCalendar` (API 24, `ULocale("en@calendar=islamic-umalqura")`), or `com.github.msarhan:ummalqura-calendar:2.0.2` (MIT, last pushed 2023). It is not needed.
- **Events to compute** (Hijri month/day): 1/1 Islamic New Year, 1/10 Ashura, 3/12 Mawlid (show it as a label only, not an obligation), 7/27 Isra & Mi'raj, 8/15 Shab-e-Barat, 9/1 Ramadan start, 9/27 Laylat al-Qadr (also odd nights 21–29), 10/1 Eid al-Fitr, 12/8 Day of Tarwiyah, 12/9 Arafah, 12/10 Eid al-Adha, 12/11–13 Tashreeq.

## 2. Quran text, metadata and fonts

### Text: **Tanzil Uthmani text (Hafs), bundled offline**
- **Download [200]:** `https://tanzil.net/pub/download/index.php?marks=true&sajdah=true&rub=true&tatweel=true&quranType=uthmani&outType=txt-2&agree=true`. Format is `sura|aya|text`, 6,236 lines, **1.40 MB** UTF-8 (about 300 KB gzipped). XML and SQL outputs are also available. Other `quranType` values include `uthmani-min` and `simple`, for search.
- **Licence: CC BY 3.0 with a no-changes condition.** Verbatim copies only ("CHANGING IT IS NOT ALLOWED"). You must credit the Tanzil Project with a link to tanzil.net, and keep the copyright notice in derived files. Put the notice in a `license` table in the DB and on the About screen. Converting the format is fine; never edit the text.
- **Metadata [200]:** `https://tanzil.net/res/text/metadata/quran-data.xml` (77 KB, cc-by). Contains suras (`name` Arabic, `tname` transliteration, `ename` English, `type` Meccan/Medinan, `order` of revelation, `ayas`, `rukus`, `start`), plus juz (30), hizb quarters (240), manzil, ruku, **page** (604, Madani mushaf) and sajda boundaries. A JS version is at `…/quran-data.js` (25 KB). Add Urdu surah names yourself.
- **Bundle format:** a prepackaged **SQLite via Room `createFromAsset("databases/quran.db")`**. Tables: `surah`, `ayah(id, sura, aya, juz, hizbQuarter, page, text_uthmani, text_simple)`, `translation(ayah_id, edition, text)`, FTS4 over `text_simple`. Expect about 3–4 MB for the Arabic text plus metadata and FTS, plus about 1–1.5 MB per bundled translation. The APK compresses assets to about 40%.
  - **Gotcha:** Room validates the asset DB schema against its identity hash. Generate the DB from Room's exported schema JSON (set `room { schemaDirectory(...) }`) using a small JVM or Python build script.
- **Alternative source:** the QUL resource library from Tarteel (qul.tarteel.ai/resources) has SQLite/JSON for word-by-word data, mushaf page layouts and KFGQPC-encoded text. Licences are per resource, so check each one.

### Fonts
| Font | Licence | Size | Use |
|---|---|---|---|
| **Amiri Quran** (`AmiriQuran-Regular.ttf`, github.com/google/fonts/ofl/amiriquran) **[200]** | **SIL OFL 1.1** | **137 KB** | **Recommended Quran font.** Renders Tanzil Uthmani marks correctly. |
| Noto Naskh Arabic (`NotoNaskhArabic[wght].ttf`, google/fonts ofl) **[200]** | SIL OFL 1.1 | 308 KB (variable) | UI Arabic and Urdu, duas, tafsir body |
| Amiri Regular (google/fonts ofl/amiri) | OFL 1.1 | 431 KB | Alternative body font |
| KFGQPC Uthmanic Script HAFS (fonts.qurancomplex.gov.sa) | **Proprietary notice:** "may not be reproduced, modified without the express written approval" of KFGQPC | about 90 KB woff2 | Only with **written permission**. It is also designed for KFGQPC-encoded text, not Tanzil text. |

Put fonts in `res/font/` and use `FontFamily(Font(R.font.amiri_quran))`. **Android 15+ gotcha:** `elegantTextHeight` now defaults to true for Arabic. Give Quran text generous `lineHeight` (about 2.0 em) so marks don't clip.

## 3. Translations and tafsir APIs

| API | Auth | Limits | Notes |
|---|---|---|---|
| **api.alquran.cloud/v1** | none | headers show **12 req/s** (`X-RateLimit-Limit-Second: 12`) | Best for fetching editions on demand. Returns juz/page/hizb per ayah. |
| **api.quran.com/api/v4** (legacy) | **none, still works on 2026-11-03** | not published | Deprecated in favour of Quran Foundation, so don't hard-depend on it |
| **Quran Foundation Content API** `https://apis.quran.foundation/content/api/v4` | **OAuth2 client credentials** (`POST https://oauth2.quran.foundation/oauth2/token`, `grant_type=client_credentials&scope=content`, headers `x-auth-token` + `x-client-id`, token 3600 s, no refresh) | — | Docs say the secret must stay on a **backend**, so it is unusable directly from the app. Prelive (`apis-prelive…`) has only surahs 1–2. |
| **quranenc.com/api/v1** | none | not stated | **Best legal source for bundling translations offline.** Terms allow republishing unmodified with attribution and the version number, and forbid "inappropriate advertisements". |
| **spa5k/tafsir_api on jsDelivr** | none | jsDelivr fair use | MIT repo. Content comes from QUL, quran.com and altafsir. 123 tafsirs. Pin a tag or self-host. |

**Exact endpoints (all [200] unless noted):**
- **Translations by language:**
  - `https://api.alquran.cloud/v1/edition?format=text&type=translation&language=ur` (returns `ur.jalandhry, ur.ahmedali, ur.kanzuliman, ur.maududi, …`)
  - `https://api.quran.com/api/v4/resources/translations?language=en` (126 translations, 69 languages)
  - `https://quranenc.com/api/v1/translations/list/ur` (returns `urdu_junagarhi` v1.1.3; en: `english_saheeh` 1.1.2, `english_rwwad`, `english_hilali_khan`)
- **Surah with translation(s):**
  - `https://api.alquran.cloud/v1/surah/1/editions/quran-uthmani,en.sahih,ur.jalandhry`
  - `https://api.quran.com/api/v4/verses/by_chapter/1?translations=20,234&fields=text_uthmani&per_page=300` (20 = Saheeh Intl, 234 = Jalandhari Urdu, 54 = Junagarhi, 97 = Maududi Urdu, 85 = Abdel Haleem, 84 = Taqi Usmani. The old ID 131 now returns empty.)
  - `https://quranenc.com/api/v1/translation/sura/english_saheeh/1` and `…/translation/aya/urdu_junagarhi/2/255` (include footnotes)
- **Tafsir per ayah:**
  - quran.com: `https://api.quran.com/api/v4/tafsirs/{id}/by_ayah/2:255`. IDs: **169** Ibn Kathir (EN, abridged), 168 Ma'arif al-Qur'an (EN), 817 Tazkirul Quran (EN), **160** Ibn Kathir (UR), 159 Bayan ul Quran (UR), 818 Tazkir ul Quran (UR), **16** Muyassar (AR), 14 Ibn Kathir (AR), 91 Sa'di (AR), 15 Tabari, 90 Qurtubi, 94 Baghawi, 93 Wasit. Full list: `https://api.quran.com/api/v4/resources/tafsirs`.
  - spa5k: `https://cdn.jsdelivr.net/gh/spa5k/tafsir_api@main/tafsir/{slug}/{surah}/{ayah}.json` or `…/{slug}/{surah}.json`. Slugs: `en-tafisr-ibn-kathir` (sic), `en-tafsir-maarif-ul-quran`, `en-al-jalalayn`, `ur-tafseer-ibn-e-kaseer` **[200]**, `ur-tafsir-bayan-ul-quran`, `ar-tafsir-muyassar` **[200]**, `ar-tafsir-ibn-kathir`, `ar-tafseer-al-saddi`, `ar-tafsir-al-mukhtasar`. List: `…/tafsir/editions.json`. Returns plain text (no HTML).
  - alquran.cloud: `https://api.alquran.cloud/v1/ayah/2:255/editions/ar.muyassar,ar.jalalayn` (Arabic tafsir only: `ar.muyassar, ar.jalalayn, ar.qurtubi, ar.miqbas, ar.waseet, ar.baghawi`). List: `/v1/edition/type/tafsir`.
- **Recommendation:** bundle Arabic plus **English Saheeh and Urdu Junagarhi from QuranEnc** (allowed to republish). Download other translations on demand from alquran.cloud and cache them in Room. Fetch tafsir lazily from spa5k (no auth, plain text), falling back to the quran.com v4 legacy API.
- **Licence gotchas:** Tanzil translations are "for non-commercial purposes only", so don't bundle them in an ad-supported app. quran.com HTML tafsir text contains `<h1>/<p>` and `<sup foot_note=…>`, so strip it or render it with `AnnotatedString.fromHtml`.

## 4. Quran audio

**URL patterns** (`SSS` = 3-digit surah, `AAA` = 3-digit ayah, `N` = global ayah 1–6236):
- **Per ayah, everyayah.com:** `https://everyayah.com/data/{folder}/{SSS}{AAA}.mp3`. Folder list as JSON: `https://everyayah.com/data/recitations.js` (about 85 folders, includes `ayahCount`). CORS-enabled, served from Google Cloud Storage.
- **Per ayah, islamic.network CDN** (same files as everyayah): `https://cdn.islamic.network/quran/audio/{bitrate}/{edition}/{N}.mp3`. **Bitrate varies by edition and a wrong bitrate gives 403.** Get the exact URL from `https://api.alquran.cloud/v1/ayah/262/ar.alafasy` (`audio` + `audioSecondary`).
- **Per surah, islamic.network:** `https://cdn.islamic.network/quran/audio-surah/128/{edition}/{S}.mp3`. Editions list: `https://api.alquran.cloud/v1/edition?format=audio` (190 editions, about 150 `surahbysurah`).
- **Per surah, mp3quran.net:** reciters API `https://www.mp3quran.net/api/v3/reciters?language=eng` (244 reciters). Note that `mp3quran.net/api/...` now 301-redirects to `www.`. Each `moshaf` has a `server` field and a `surah_list`. URL = `{server}{SSS}.mp3`. **Old `serverN.mp3quran.net` URLs 301 to `https://cdn.mp3quran.net/audio/{slug}/{rN}/`**, so always read `server` from the API. Also accepts `language=ar` or `language=ur`.
- **quran.com:** per surah `https://api.quran.com/api/v4/chapter_recitations/{recitationId}/{chapter}` returns `download.quranicaudio.com/qdc/...`; per ayah `/api/v4/recitations/{id}/by_chapter/{ch}` returns relative URLs prefixed with `https://verses.quran.com/` **[200]**. Also returns word timing segments (useful for highlighting).

**Reciters.** Every everyayah folder was tested with 002255 **[200]**. Every mp3quran server was tested with 114.mp3 **[200]**. Each alquran.cloud edition was tested at the bitrate shown.

| # | Reciter | everyayah folder (per ayah) | mp3quran `server` (full surah) | alquran.cloud edition (bitrate) |
|---|---|---|---|---|
| 1 | Mishary Rashid Alafasy | `Alafasy_128kbps` | `https://cdn.mp3quran.net/audio/mishary-alafasy/r1/` | `ar.alafasy` (128; surah: 128) |
| 2 | Abdul Basit, Murattal | `Abdul_Basit_Murattal_192kbps` | `…/abdulbasit-abdulsamad/r3/` | `ar.abdulbasitmurattal` (64/192) |
| 3 | Abdul Basit, Mujawwad | `Abdul_Basit_Mujawwad_128kbps` | `…/abdulbasit-abdulsamad/r1/` | `ar.abdulbasitmujawwad` (surah) |
| 4 | Abdur-Rahman As-Sudais | `Abdurrahmaan_As-Sudais_192kbps` | `…/abdulrahman-sudais/r1/` | `ar.abdurrahmaansudais` (64/192) |
| 5 | Saud Ash-Shuraim | `Saood_ash-Shuraym_128kbps` | `…/saud-shuraim/r1/` | `ar.saoodshuraym` (64) |
| 6 | Mahmoud Khalil Al-Husary | `Husary_128kbps` | `…/mahmoud-husary/r1/` | `ar.husary` (128) |
| 7 | Al-Husary, Mujawwad / Muallim | `Husary_128kbps_Mujawwad` / `Husary_Muallim_128kbps` | `…/mahmoud-husary/r2/` | `ar.husarymujawwad` (128) |
| 8 | Muhammad Siddiq Al-Minshawi, Murattal | `Minshawy_Murattal_128kbps` | `…/muhammad-minshawi/r1/` | `ar.minshawi` (128) |
| 9 | Al-Minshawi, Mujawwad | `Minshawy_Mujawwad_192kbps` | `…/muhammad-minshawi/r2/` | `ar.minshawimujawwad` |
| 10 | Maher Al-Muaiqly | `MaherAlMuaiqly128kbps` | `…/maher-muaiqly/r1/` | `ar.mahermuaiqly` (128) |
| 11 | Saad Al-Ghamdi | `Ghamadi_40kbps` (low bitrate only) | `…/saad-ghamdi/r1/` | — |
| 12 | Yasser Al-Dosari | `Yasser_Ad-Dussary_128kbps` | `…/yasser-dosari/r1/` | `ar.yasseraldossari` (surah 128) |
| 13 | Abu Bakr Ash-Shatri | `Abu_Bakr_Ash-Shaatree_128kbps` | `…/abubakr-shatri/r1/` | `ar.shaatree` (128) |
| 14 | Ahmed Al-Ajmi | `ahmed_ibn_ali_al_ajamy_128kbps` | `…/ahmad-ajmi/r1/` | `ar.ahmedajamy` (128) |
| 15 | Ali Al-Hudhaifi | `Hudhaify_128kbps` | `…/ali-hudhaifi/r1/` | `ar.hudhaify` (128) |
| 16 | Muhammad Jibreel | `Muhammad_Jibreel_128kbps` | `…/muhammad-jibreel/r1/` | `ar.muhammadjibreel` (128) |
| 17 | Abdullah Basfar | `Abdullah_Basfar_192kbps` | `…/abdullah-basfar/r1/` | `ar.abdullahbasfar` (32/64/192) |
| 18 | Hani Ar-Rifai | `Hani_Rifai_192kbps` | `…/hani-rifai/r1/` | `ar.hanirifai` (64/192) |
| 19 | Nasser Al-Qatami | `Nasser_Alqatami_128kbps` | `…/nasser-qatami/r1/` | — |
| 20 | Salah Al-Budair | `Salah_Al_Budair_128kbps` | `…/salah-budair/r1/` | — |
| 21 | Abdullah Awad Al-Juhani | `Abdullaah_3awwaad_Al-Juhaynee_128kbps` | `…/abdullah-juhani/r1/` | — |
| 22 | Abdullah Al-Matrood | `Abdullah_Matroud_128kbps` | `…/abdullah-matrood/r1/` | — |
| 23 | Muhammad Ayyub | `Muhammad_Ayyoub_128kbps` | `…/muhammad-ayyub/r1/` | `ar.muhammadayyoub` (128) |
| 24 | Fares Abbad | `Fares_Abbad_64kbps` | `…/fares-abbad/r1/` | — |
| 25 | Khalid Al-Qahtani | `Khaalid_Abdullaah_al-Qahtaanee_192kbps` | `…/khalid-qahtani/r1/` | — |
| 26 | Ali Jaber | `Ali_Jaber_64kbps` | `…/ali-jaber/r1/` | — |
| 27 | Salah Bukhatir | `Salaah_AbdulRahman_Bukhatir_128kbps` | `…/salah-bukhatir/r1/` | — |
| 28 | Idrees Abkar | — | `…/idrees-abkar/r1/` | — |

(`…` = `https://cdn.mp3quran.net/audio`). An Urdu translation audio track exists as everyayah `translations/urdu_shamshad_ali_khan_46kbps` and alquran.cloud `ur.khan`.

**Spot-checks (curl -I), all 200 audio/mpeg:** `https://everyayah.com/data/Alafasy_128kbps/001001.mp3` (146,830 B), `https://cdn.islamic.network/quran/audio-surah/128/ar.alafasy/1.mp3` (1,420,155 B), `https://cdn.mp3quran.net/audio/mishary-alafasy/r1/001.mp3` (837,734 B), `https://download.quranicaudio.com/qdc/mishari_al_afasy/murattal/1.mp3` (793,327 B), plus the 28 + 26 + 11 table checks above.

**Gotchas:**
- None of these hosts publishes a licence for the recordings. They exist for free distribution and are used by major open-source Quran apps. Stream, and download only per user action into app-private storage. Never re-host the files.
- Validate `surah_list` (some mp3quran moshafs are incomplete, e.g. Khalid Al-Ghamdi has 102 surahs). Fall back to another source on 404/403.
- Store a reciter catalogue in your own JSON (remote-config) so URLs can be fixed without an app update.

## 5. Player: **Media3 1.11.1**
```toml
media3 = "1.11.1"   # stable (1.12.0-alpha02 exists); stdlib 2.2.10, so fine
androidx-media3-exoplayer = { module = "androidx.media3:media3-exoplayer", version.ref = "media3" }
androidx-media3-session   = { module = "androidx.media3:media3-session",   version.ref = "media3" }
androidx-media3-ui-compose = { module = "androidx.media3:media3-ui-compose", version.ref = "media3" }  # optional Compose controls
```
- Use `class QuranPlaybackService : MediaSessionService()` that owns an `ExoPlayer` and a `MediaSession`. The UI connects through `MediaController.Builder(ctx, SessionToken(ctx, ComponentName(ctx, QuranPlaybackService::class.java))).buildAsync()`. Media3 posts the media-style notification and handles foreground state itself (`DefaultMediaNotificationProvider`).
- **Manifest:** `<service android:name=".QuranPlaybackService" android:foregroundServiceType="mediaPlayback" android:exported="true"><intent-filter><action android:name="androidx.media3.session.MediaSessionService"/></intent-filter></service>`, plus `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK`, `POST_NOTIFICATIONS` (API 33+) and `INTERNET`.
- Per-ayah playback means one `MediaItem` per ayah (gapless playlist). Use `MediaMetadata.extras` for sura/aya so the reader can highlight the current ayah. Repeat-ayah and range repeat map to `setRepeatMode` or a custom playlist.
- For **offline downloads**, keep it simple: use WorkManager (`androidx.work:work-runtime-ktx:2.11.2`) to download MP3s into `filesDir/audio/{reciter}/`, then play them with `file://` MediaItems. Avoid Media3 `DownloadService`: it needs a `dataSync` FGS, which has a 6 h/24 h cap on Android 15+.
- `setAudioAttributes(AudioAttributes.Builder().setUsage(C.USAGE_MEDIA).setContentType(C.AUDIO_CONTENT_TYPE_SPEECH).build(), true)` gives audio focus handling. Also set `setHandleAudioBecomingNoisy(true)`.

## 6. Nearby mosques map

**Recommendation: MapLibre with OpenFreeMap vector tiles (no key) and Overpass for mosque data. Don't use osmdroid.**
- **osmdroid is archived** (read-only since 2024-11-20, last release `org.osmdroid:osmdroid-android:6.1.20` from 2024-08-18, Apache-2.0). Avoid it for a new app. If you use raster `tile.openstreetmap.org` with any library: send a unique app User-Agent (library defaults are blocked), show visible attribution, honour caching, and **no offline or bulk prefetch**. Commercial apps "may be withdrawn at any point".
- **MapLibre Native Android:** `org.maplibre.gl:android-sdk:13.7.1` (BSD-2, stdlib 2.2.10, so fine). Newer calendar-versioned `2026.11.2-globe` builds also exist. **Compose wrapper:** `org.maplibre.compose:maplibre-compose:0.12.1` on Kotlin 2.2 (built with 2.2.21, bundles android-sdk 12.0.1, CMP foundation 1.9.2; compile-verified). Use latest 0.20.0 only on Kotlin 2.4. Or host `MapView` from android-sdk in `AndroidView`.
- **Free style URLs (no key) [200]:** `https://tiles.openfreemap.org/styles/liberty` (OpenFreeMap; also `bright`, `positron`) and `https://demotiles.maplibre.org/style.json` (demo only). Show "© OpenMapTiles © OpenStreetMap contributors".
- **Overpass query** (mosques within radius R metres of lat,lng; `out center` gives a point for ways and relations):
  ```
  [out:json][timeout:25];
  nwr["amenity"="place_of_worship"]["religion"="muslim"](around:3000,24.8607,67.0011);
  out center tags;
  ```
  POST it as `data=` to `https://overpass-api.de/api/interpreter`. Fallbacks: `https://overpass.private.coffee/api/interpreter` and `https://maps.mail.ru/osm/tools/overpass/api/interpreter`. Useful tags: `name`, `name:ar`, `name:ur`, `denomination`, `addr:*`, `opening_hours`.
  - **Fair use:** under 10,000 queries and 1 GB per day **shared across all your users**. "Regular" use should be about 100× lower. Send a User-Agent, cache results (round location to about 500 m and cache 24 h), and wait 30 s after a 429 or 406. Commercial or high volume use means self-hosting or a paid instance (Geofabrik, overpass.ch, etc.).
  - **Verification gap:** from this network, the overpass-api.de TCP connection timed out and the mail.ru mirror returned 504 "server too busy". The query syntax follows the official Overpass docs but was **not live-verified**. That overload is itself a reason to cache aggressively and have a fallback.
- **Google Maps Compose + Places (comparison):** `com.google.maps.android:maps-compose:9.0.1`, `play-services-maps:20.1.0`, `com.google.android.libraries.places:places:6.0.2`. The mobile Maps SDK is **free and unlimited** but needs an API key. **Places API (New) Nearby Search Pro is free for 5,000 calls/month, then $32 per 1,000** (Enterprise: 1,000 free, then $35 per 1,000). That costs real money at scale and needs billing enabled. Google data quality for mosques is better in Pakistan. A hybrid (Google map + Overpass data) is possible but unnecessary.

## 7. Live Makkah and Madinah streams

- **Official channels (verified on youtube.com, both live and embeddable, oEmbed 200):**
  - Makkah: **KSA Qur'an TV**, handle `@saudiqurantv`, channel ID **`UCos52azQNBgW63_9uDJoPDA`**. Live video at research time: `eC4LfEVxvKg`.
  - Madinah: **Saudi Sunnah TV (قناة السنة النبوية)**, handle `@SaudiSunnahTv`, channel ID **`UCROKYPep-UuODNwyipe6JMw`**. Live video at research time: `Rs7St51oDDc`.
- **Player:** `com.pierfrancescosoffritti.androidyoutubeplayer:core:13.1.0` (MIT, minSdk 21, stdlib 2.1.0, so fine; published 2026-11-01). It is a View, so wrap it in `AndroidView` and call `lifecycle.addObserver(view)`. Use `IFramePlayerOptions.Builder(ctx).controls(1).fullscreen(1).build()` with `enableAutomaticInitialization = false`. Use `loadOrCueVideo(lifecycle, id, 0f)`, not `loadVideo` in `onReady`.
- **Live IDs change** when a stream restarts. Approach, in order:
  1. A **remote JSON/remote-config** with `{makkah: videoId, madinah: videoId}` that you update; the app caches the last good value.
  2. Optional server-side resolver: fetch `https://www.youtube.com/@saudiqurantv/live`, whose `<link rel="canonical">` gives the current `watch?v=` (verified). Alternatively, YouTube Data API v3 `search?channelId=…&eventType=live&type=video` costs 100 quota units per call, so run it on a server only.
  3. Fallback: open `https://www.youtube.com/@saudiqurantv/live` with `Intent.ACTION_VIEW` (YouTube app).
- **ToS gotchas:** no background or audio-only playback of YouTube (Play will reject it), don't hide the YouTube UI or branding, and don't name the feature in a way that implies YouTube affiliation.

## 8. Qibla compass

- **Kaaba:** **21.4225° N, 39.82617° E** (Wikipedia coordinates; Adhan uses 21.4225241, 39.8261818).
- **Bearing** (great-circle initial bearing from true north):
  ```kotlin
  fun qibla(lat: Double, lng: Double): Double {
      val φ = Math.toRadians(lat); val φk = Math.toRadians(21.4225241); val Δλ = Math.toRadians(39.8261818 - lng)
      val θ = atan2(sin(Δλ), cos(φ) * tan(φk) - sin(φ) * cos(Δλ))
      return (Math.toDegrees(θ) + 360) % 360
  }   // Karachi 267.74°, Lahore 260.31° (computed; matches adhan2).  Or just use adhan2 Qibla(Coordinates).direction.
  ```
- **Sensors:** use `Sensor.TYPE_ROTATION_VECTOR` (fused gyro, accel and mag) first. Then `SensorManager.getRotationMatrixFromVector`, then `remapCoordinateSystem` for the display rotation (and for a phone held upright, `AXIS_X, AXIS_Z`), then `getOrientation()[0]` = azimuth relative to magnetic north. Fall back to `TYPE_ACCELEROMETER` + `TYPE_MAGNETIC_FIELD` with `getRotationMatrix(R, I, gravity, geomagnetic)`. `TYPE_GEOMAGNETIC_ROTATION_VECTOR` is a low-power option with no gyro.
- **True north:** `azimuthTrue = azimuthMag + GeomagneticField(lat.toFloat(), lng.toFloat(), alt.toFloat(), System.currentTimeMillis()).declination`. Pakistan's declination is about +1° to +3°; it is larger in other places.
- **Smoothing:** low-pass on **sin/cos**, not on degrees, to avoid the 359→0 wrap: `s = s + α(sinθ − s); c = c + α(cosθ − c); θ = atan2(s, c)` with α ≈ 0.1–0.2 at `SENSOR_DELAY_GAME`. Animate the dial with the shortest-arc delta.
- **Calibration:** in `onAccuracyChanged`, treat `SENSOR_STATUS_ACCURACY_LOW` or `UNRELIABLE` on the magnetic or rotation sensor as "needs calibration". Show a figure-8 prompt. Also flag interference when |B| = √(x²+y²+z²) from `TYPE_MAGNETIC_FIELD` falls outside about 25–65 µT. If there's no magnetometer (`getDefaultSensor == null`), show a map-line Qibla (the great-circle line on the map) instead.

## 9. Location and city name

- **Recommended:** `com.google.android.gms:play-services-location:21.4.0` (stdlib 1.9.0). Call `FusedLocationProviderClient.getCurrentLocation(CurrentLocationRequest.Builder().setPriority(Priority.PRIORITY_BALANCED_POWER_ACCURACY).setMaxUpdateAgeMillis(30 min).build(), token)` after trying `lastLocation`. **`ACCESS_COARSE_LOCATION` is enough** for prayer times and Qibla (about 2 km error gives a negligible angle and seconds of timing). Request fine location only for the mosques map.
- **Fallback for no GMS (Huawei, AOSP):** `LocationManagerCompat.getCurrentLocation(lm, LocationManager.NETWORK_PROVIDER, …)` from androidx.core. Detect GMS with `GoogleApiAvailability.isGooglePlayServicesAvailable`. Always offer **manual city selection**.
- **Reverse geocoding:**
  - Online and free: `android.location.Geocoder`. On API 33+ use the async `getFromLocation(lat, lng, 1, listener)`; below that, the blocking call on `Dispatchers.IO`. Check `Geocoder.isPresent()`; it depends on a device backend, usually GMS.
  - **Offline:** bundle a trimmed **GeoNames `cities15000`** (CC BY 4.0, 3.4 MB zip at `https://download.geonames.org/export/dump/cities15000.zip` [200]). Keep name, asciiname, alternate Urdu/Arabic names, lat, lng, country, timezone, which comes to about 1.5 MB in SQLite. Use it for nearest-city lookup and manual search, with credit to GeoNames.
  - Nominatim (OSM) is a last resort: at most 1 req/s for the **whole app**, needs a custom UA, caching, no autocomplete, and a commercial or self-hosted instance for many users.

## 10. Azan scheduling on Android 12–16

- **Exact alarm permission:**
  - Declare **`SCHEDULE_EXACT_ALARM`** (user-granted "Alarms & reminders"). Check `AlarmManager.canScheduleExactAlarms()`. If false, explain why and launch `Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM` (with `package:` URI).
  - It is **not granted by default** to fresh installs targeting 33+, and it is denied after backup/restore. Listen to `AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED` to reschedule.
  - Play policy reserves **`USE_EXACT_ALARM`** (auto-granted) for apps whose core function is "an alarm or timer app" or "a calendar app that shows event notifications". **Prayer apps are not listed**, so using it risks rejection unless you argue the Azan alarm is the core function. Safer path: `SCHEDULE_EXACT_ALARM`, plus an inexact fallback (`setAndAllowWhileIdle`) when it's denied.
- **Scheduling:** use `setExactAndAllowWhileIdle(RTC_WAKEUP, t, pi)`. `setAlarmClock()` is the most reliable through Doze but shows an alarm icon and "next alarm" in the system UI, which is acceptable for an Azan alarm and worth offering as an option. Schedule a **rolling window**: only the next prayer, or today's remaining five. Reschedule:
  - in the alarm receiver after each fire;
  - on `BOOT_COMPLETED`, `LOCKED_BOOT_COMPLETED` (with direct-boot-aware storage), `TIME_SET`, `TIMEZONE_CHANGED`, `MY_PACKAGE_REPLACED` and the permission-state broadcast;
  - when location or settings change.
  
  Use unique `requestCode` per prayer and `FLAG_IMMUTABLE`.
- **Playing the Azan:**
  - Start a **foreground service with `android:foregroundServiceType="mediaPlayback"`** (+ `FOREGROUND_SERVICE_MEDIA_PLAYBACK`) from the exact-alarm receiver. Exact alarms are exempt from background FGS start limits.
  - **Android 15+:** `BOOT_COMPLETED` receivers may not start `mediaPlayback` (or dataSync, camera, phoneCall, mediaProjection, microphone) FGS, so the boot receiver must only reschedule.
  - **Android 15+:** audio focus is granted only to the top app or an FGS, so play inside the FGS.
  - Use `AudioAttributes.USAGE_ALARM` so it follows alarm volume and plays in silent mode (make this a user option). Provide a "Stop" action, stop on volume-key press, and call `stopSelf` when playback completes.
  - Play Console needs a **foreground-service declaration** (description, impact, video) for `mediaPlayback`.
- **Full-screen intent (Android 14+):** `USE_FULL_SCREEN_INTENT` is auto-granted only to apps whose core function is calling or alarms (Play policy enforced from 2025-01-22, with a Play Console declaration). For a prayer app, assume it is **not granted**. Check `NotificationManager.canUseFullScreenIntent()`, offer `Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT`, and otherwise rely on a high-importance heads-up notification with a "Stop Azan" action.
- **Notification channels:** a channel's sound is **immutable after creation**. Create one channel per sound per prayer type (e.g. `azan_makkah_v1`, `azan_fajr_v1`, `silent_reminder`). To change a sound, delete the old channel and create a new ID. Set the sound via `setSound(Uri.parse("android.resource://$pkg/${R.raw.azan}"), AudioAttributes(USAGE_ALARM))`. In practice, keep the channel **silent or short** and play the full Azan in the FGS, so users can stop it and it isn't cut off. Request `POST_NOTIFICATIONS` at runtime (API 33+). Fajr needs its own Azan with "As-salatu khayrun min an-nawm".
- **OEM battery killers** (Xiaomi, Oppo, Vivo, Huawei): show a one-time guide (see dontkillmyapp.com) and link `Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS`. Don't request `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` without a policy-acceptable reason.
- **Free Azan audio with clear licences (Wikimedia Commons, metadata checked through the API):**
  - `File:Azan.ogg`: **CC BY-SA 4.0**, 183 s, 1.47 MB. https://commons.wikimedia.org/wiki/File:Azan.ogg
  - `File:The_Adhan_-_Muslim_Call_to_Prayer_-_Aaqib_Azeez.mp3`: **CC BY-SA 4.0**, 1.45 MB. https://commons.wikimedia.org/wiki/File:The_Adhan_-_Muslim_Call_to_Prayer_-_Aaqib_Azeez.mp3
  - `File:Time_of_Adhan,_Great_Mosque_of_Mecca_-_Jan_6,_2018_.webm`: **CC BY 3.0** video (189 s, Masjid al-Haram). You may extract and transcode the audio with attribution; that gives a genuine Makkah Azan that you can legally ship.
  - CC BY-SA requires attribution and that any *modified* audio stays CC BY-SA. Bundling the file doesn't make the app share-alike. Credit performer, uploader and licence on an Attributions screen. Don't rip Azans from YouTube or random "free download" sites, because their rights are unknown. Commissioning a local muezzin recording with a written release is the cleanest long-term option.

## 11. Other libraries (compile-verified together on AGP 9.3.3 / Kotlin 2.2.10 / BOM 2026.02.01)

**AGP 9 built-in Kotlin:**
- **Do not apply `org.jetbrains.kotlin.android`.** AGP 9 compiles Kotlin itself, and applying the plugin errors out. Keep `org.jetbrains.kotlin.plugin.compose`. Add `org.jetbrains.kotlin.plugin.serialization` (version = kotlin) for type-safe navigation.
- `kotlin-kapt` is incompatible; use KSP, or `com.android.legacy-kapt` if you must.
- `android.kotlinOptions {}` is gone; use `kotlin { compilerOptions { … } }`. `jvmTarget` defaults to `compileOptions.targetCompatibility`.

**KSP:**
- KSP has had independent versioning since 2.3.0. Latest is **2.3.13** (2026-10-30). Built-in Kotlin support arrived in 2.3.1; Google's AGP 9 guidance says to use ≥ 2.3.6; 2.3.12 raised the minimum AGP to 8.12.
- **2.3.13 + Room 2.8.5 compiled fine on Kotlin 2.2.10.** The old coupled `2.2.10-2.0.2` exists but is KSP1-era, so don't use it with AGP 9.

```toml
[versions]
ksp = "2.3.13"
room = "2.8.5"              # stdlib 2.1.20
datastore = "1.3.0"         # stdlib 2.2.20
navigation = "2.9.7"        # matches BOM 2026.02.01 (Compose 1.10.x); 2.10.2 pulls compose-runtime 1.11.2 + lifecycle 2.11, so use it with BOM ≥ 2026.05.00
serialization = "1.9.0"
coil = "3.4.0"              # 3.5+ needs Kotlin 2.4
lottie = "6.7.1"            # stdlib 1.9.22
media3 = "1.11.1"
work = "2.11.2"             # 2.12.0 also fine (stdlib 2.1.20)
adhan2 = "0.0.6"
ayp = "13.1.0"
maplibreCompose = "0.12.1"
playLocation = "21.4.0"

[libraries]
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
datastore-preferences = { module = "androidx.datastore:datastore-preferences", version.ref = "datastore" }
navigation-compose = { module = "androidx.navigation:navigation-compose", version.ref = "navigation" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "serialization" }
coil-compose = { module = "io.coil-kt.coil3:coil-compose", version.ref = "coil" }
coil-network-okhttp = { module = "io.coil-kt.coil3:coil-network-okhttp", version.ref = "coil" }   # Coil 3 has no network by default
lottie-compose = { module = "com.airbnb.android:lottie-compose", version.ref = "lottie" }
media3-exoplayer = { module = "androidx.media3:media3-exoplayer", version.ref = "media3" }
media3-session = { module = "androidx.media3:media3-session", version.ref = "media3" }
work-runtime = { module = "androidx.work:work-runtime-ktx", version.ref = "work" }
adhan2 = { module = "com.batoulapps.adhan:adhan2", version.ref = "adhan2" }
youtube-player = { module = "com.pierfrancescosoffritti.androidyoutubeplayer:core", version.ref = "ayp" }
maplibre-compose = { module = "org.maplibre.compose:maplibre-compose", version.ref = "maplibreCompose" }
play-services-location = { module = "com.google.android.gms:play-services-location", version.ref = "playLocation" }

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
room = { id = "androidx.room", version.ref = "room" }
```
- In the app module: `room { schemaDirectory("$projectDir/schemas") }` and `ksp(libs.room.compiler)`.
- **Type-safe navigation:** `@Serializable data class SurahRoute(val number: Int)` with `composable<SurahRoute> { it.toRoute<SurahRoute>() }`. Navigation 3 (`androidx.navigation3`) is still 1.3.0-alpha, so stay on Navigation 2.
- Newer BOMs, if you want them: 2026.05.00 = Compose 1.11.1, 2026.06.01 = 1.11.4, 2026.09.00 = 1.12.1 (material3 stays 1.4.0). Compose 1.11 and 1.12 artifacts are built with stdlib 2.1.20, so they are OK on Kotlin 2.2.
- **Not needed for this app:** `androidx.sqlite:sqlite-bundled:2.7.1` (bundled SQLite). Hilt is optional: `androidx.hilt:hilt-navigation-compose:1.4.0`.

## 12. Bundled content datasets

- **99 Names of Allah:** the list itself, from the Tirmidhi 3507 narration (Arabic plus transliteration), is public-domain religious text. English and Urdu *meanings* in published lists are copyrightable, and most GitHub JSONs have no licence or are GPL-3.0 (e.g. IcyDrae/99-Names-of-Allah). **Recommendation:** author `asma_ul_husna.json` in-house as `[{n, ar, translit, en, ur, ref}]`. Write short meanings yourself and have them reviewed by a scholar. About 20 KB.
- **Azkar / Hisnul Muslim:**
  - `Seen-Arabic/Morning-And-Evening-Adhkar-DB` is **MIT** (pushed 2026-02-14). It covers morning and evening adhkar only (Arabic, English, references) and is safe to bundle with credit.
  - hisnmuslim.com has a JSON API (`http://www.hisnmuslim.com/api/en/husn_en.json`, `…/ar/husn_ar.json`, per chapter `…/api/en/27.json`). It is **HTTP only** (needs a cleartext exception) and has **no stated licence**. The book is © Sa'id al-Qahtani and translations are copyrighted. Get written permission before bundling the full book.
  - Other repos found (rn0x/Adhkar-json, osamayy/Azkar-db, Hugging Face M-AI-C/hisnulmuslim) have **no licence**, so don't bundle them.
  - Fallback: curate about 150 duas yourself. Use Arabic matn from hadith (public domain), your own English and Urdu renderings, and a reference (e.g. "Bukhari 6306"). Format: `azkar.json` `[{category, ar, translit, en, ur, repeat, ref, audio?}]`.
- **Hajj and Umrah guide:** there's no open dataset. Write it in-house: steps for 8–13 Dhul Hijjah (Ihram/Miqat, Tawaf, Sa'i, Mina, Arafah, Muzdalifah, Rami, Nahr, Halq, Tawaf al-Ifadah, Wada'), with duas, checklists and Ihram prohibitions, and have it reviewed. Cite the Saudi Ministry of Hajj & Umrah (haj.gov.sa) and Nusuk for official guidance without copying text. Markdown or JSON, about 50 KB plus images.
- **Attributions screen:** required for Tanzil, QuranEnc, GeoNames, OSM/OpenMapTiles, Wikimedia Azan files, the fonts (OFL), MIT libraries and the adhkar DB.

## 13. Recitation checking (microphone)

| Option | Size | Licence | Verdict |
|---|---|---|---|
| **`tarteel-ai/whisper-tiny-ar-quran`**, ONNX export `Sharjeelbaig/whisper-tiny-ar-quran-onnx` (int8: encoder 10.2 MB + merged decoder 30.2 MB + tokenizer 3.7 MB) | **about 44 MB** quantized (151 MB fp32 PyTorch) | Apache-2.0 | **Best on-device choice.** Fine-tuned on Quran recitation and outputs diacritised Arabic. Run with `com.microsoft.onnxruntime:onnxruntime-android:1.31.1` (+ about 10–15 MB native per ABI). Ship it as an optional download (Play Asset Delivery on-demand or your CDN), not in the base APK. |
| `tarteel-ai/whisper-base-ar-quran` (307k downloads), ONNX `eventhorizon0/tarteel-ai-onnx-whisper-base-ar-quran` (int8 encoder 23 MB + decoder 52 MB) | about 80 MB int8 (290 MB fp32) | Apache-2.0 | More accurate and about 2–3× slower. Good for flagship phones. |
| `TBOGamer22/wav2vec2-quran-phonetics`, `mohammed/fastconformer-quran-ar` (CC BY 4.0) | 300 MB+ | Apache-2.0 / CC BY | Phoneme-level; useful for tajweed research but too heavy for a v1. |
| Vosk Arabic `vosk-model-ar-mgb2-0.4` (`com.alphacephei:vosk-android:0.3.75`) | 318 MB | Apache-2.0 | Trained on broadcast MSA, not Quran, and too big. The 1.3 GB linto model is AGPL. **Not recommended.** |
| Android `SpeechRecognizer` with `EXTRA_LANGUAGE="ar-SA"` (on-device via `createOnDeviceSpeechRecognizer`, API 31+) | 0 (system) | system | Free and zero size, but output **has no tashkeel**, normalises Quranic orthography, often needs network, and varies by OEM (and is absent on non-GMS phones). **Good enough for "which ayah / next word" memorisation checks** if you compare against `text_simple` with diacritics stripped. **Not usable for tajweed or harakat errors.** |

**Recommendation:**
- v1: use `SpeechRecognizer` (ar-SA) with fuzzy word matching against the Tanzil simple text for hifz practice.
- v2: optional download of the whisper-tiny Quran ONNX model for offline, diacritics-aware checking.

Gotchas:
- Needs the `RECORD_AUDIO` runtime permission and a Play Data safety declaration (state that audio is on-device only).
- Whisper takes 30 s windows, so chunk by ayah.
- There is no public Tarteel SDK or API.

---

### Sources
- Maven Central and Google Maven `maven-metadata.xml` and POM/module files for every coordinate above, fetched 2026-11-03.
- [AGP built-in Kotlin migration](https://developer.android.com/build/migrate-to-built-in-kotlin) · [AGP 9 release notes](https://developer.android.com/build/releases/agp-9-0-0-release-notes) · [KSP releases](https://github.com/google/ksp/releases)
- [adhan-kotlin](https://github.com/batoulapps/adhan-kotlin) · [HijrahChronology](https://developer.android.com/reference/java/time/chrono/HijrahChronology)
- [Tanzil text licence](https://tanzil.net/docs/text_license) · [Tanzil translations terms](https://tanzil.net/trans/) · [QuranEnc API and terms](https://quranenc.com/en/home/api/) · [Quran Foundation OAuth quickstart](https://api-docs.quran.foundation/docs/quickstart) · [migration guide](https://api-docs.quran.foundation/docs/quickstart/migration/) · [spa5k/tafsir_api](https://github.com/spa5k/tafsir_api) · [alquran.cloud OpenAPI](https://api.alquran.cloud/v1/documentation/openapi/yaml)
- [KFGQPC font licence (ScanCode)](https://scancode-licensedb.aboutcode.org/kfgqpc-uthmanic-script-hafs.LICENSE) · [Amiri Quran OFL](https://github.com/google/fonts/tree/main/ofl/amiriquran)
- [everyayah recitations.js](https://everyayah.com/data/recitations.js) · [mp3quran API v3](https://www.mp3quran.net/api/v3/reciters?language=eng)
- [Overpass API wiki](https://wiki.openstreetmap.org/wiki/Overpass_API) · [OSM tile policy](https://operations.osmfoundation.org/policies/tiles/) · [Nominatim policy](https://operations.osmfoundation.org/policies/nominatim/) · [osmdroid (archived)](https://github.com/osmdroid/osmdroid) · [Google Maps pricing](https://developers.google.com/maps/billing-and-pricing/pricing)
- [android-youtube-player](https://github.com/PierfrancescoSoffritti/android-youtube-player)
- [Alarms guide](https://developer.android.com/develop/background-work/services/alarms) · [Play exact alarm policy](https://support.google.com/googleplay/android-developer/answer/13161072) · [Play FGS/FSI requirements](https://support.google.com/googleplay/android-developer/answer/16965181) · [Android 14 behavior changes](https://developer.android.com/about/versions/14/behavior-changes-14) · [Android 15 behavior changes](https://developer.android.com/about/versions/15/behavior-changes-15)
- [GeoNames dumps](https://download.geonames.org/export/dump/) · Wikimedia Commons file pages linked in section 10.
- [Hugging Face tarteel-ai models](https://huggingface.co/tarteel-ai) · [Vosk models](https://alphacephei.com/vosk/models)
