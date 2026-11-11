# Muslim Prayer – Qibla Compass (v2.13.0) — UI / Behaviour Reference Spec

Target package: `prayertime.compassdirection.qiblafinder.hijricalender` (launcher name **"Qibla Compass"**, internal classes `com.example.compass.*`).
Observed on Pixel 4 XL, Android 13, 1440×3040 px, density 560 → **1 dp = 3.5 px**. All dp values below = uiautomator bounds ÷ 3.5 (rounded). Font sizes in sp are estimated from glyph/line height (cap-height ≈ 0.7 × sp) and marked "≈".
Colours are exact hex samples from the PNG screenshots (sRGB). Where a colour is a gradient, start → end are given.
Screenshots: `screens/NN_name.png` (+ `.xml` uiautomator dump with exact bounds where the screen was idle enough to dump). Videos: `video/*.mp4`.
"UNSURE" marks anything not verifiable from observation.

Conventions used in this spec
- "Card" = rounded rectangle; radius in dp; "stroke" = border width/colour.
- Positions are given top→bottom; x/y in dp from screen top-left (status bar = 0–28 dp, 98 px).
- The app is **edge-to-edge**: content draws under the status bar (status-bar icons dark on light screens, light on dark green headers). The system 3-button navigation bar is transient (auto-hidden, appears over content on swipe); the app's own bottom nav sits at the very bottom of the 3040 px screen.
- Main font family looks like **Nunito** (rounded geometric sans: AI Deen / Timely Prayers titles, body text) — UNSURE exact family; Roboto is used for Android-native dialogs/pickers. Arabic Quran text: IndoPak-style naskh by default (option "Uthmani"); Arabic in Duas/Tasbih uses a standard naskh (looks like the system Noto Naskh Arabic / "Al Qalam"-like) — UNSURE.

---

## 1. Launch, splash & first-run flow (navigation order)

```
Launcher icon → Splash (green, ~2–4 s, spinner) ──(first launch)──► POST_NOTIFICATIONS system prompt (shown ON the splash)
  → Interstitial ad (full screen video, "Continue ▶|" skip after ~10 s / [Close] X)
  → Language selection (Select a language)  → Next
  → Onboarding pager: 1 Find Qibla → 2 AI Deen → [full-screen native ad page] → 3 Timely Prayers → Start
  → "Set notifications for each prayer" → Continue
  → Interstitial ad → Home (MainActivity) + "Please provide these permissions" bottom sheet
  → (premium "50% OFF" dialog appears behind/after the sheet on first Home)
```
Subsequent launches: Splash → (app-open/interstitial ad, frequently) → Home. Returning from background shows an **app-open ad** ("Continue to app" button) — `30_ad_app_open_on_resume.png`.

### 1.1 Splash — `01_splash_t1…t6.png`, `video/01b_splash_relaunch.mp4`
- Full-screen solid background **#254D2D** (primary dark green); status bar transparent with white icons.
- Centre: small compass icon (white round compass face, red/white needle, tiny Kaaba cube at 10 o'clock) ≈ 19 dp diameter at (126,281 dp) at t≈0.3 s; it **animates** (scales up / slides) – in the relaunch video a second element (paper-plane/kite-like blue triangle) slides in next to it (UNSURE exact choreography — watch `01b_splash_relaunch.mp4`).
- Title **"QIBLA COMPASS"** – white, serif small-caps style font (Times/Playfair-like, bold), ≈ 22 sp, centred at y≈337 dp (px 1795).
- Loader at bottom: a **radial "sunburst" line spinner** (12 short strokes, gold/cream #F3E6A8-ish strokes on green, strokes fade in sequence, like iOS activity indicator) ≈ 28 dp, centred x, y≈ 790 dp (px 2760). Rotates continuously.
- Duration ≈ 2.5–4 s (waits for ad load). On first launch the notification permission prompt appears over the splash (`01_splash_t3`, dim scrim #0F1F12-ish).

### 1.2 Notification permission (system) — `02_perm_notifications_system.png/.xml`
- Android 13 system dialog: "Allow **Qibla Compass** to send you notifications?" – [Allow] [Don’t allow]. No in-app pre-rationale before it. Shown immediately on splash, first launch only. (We tapped Allow.)

### 1.3 Interstitial after splash — `03_ad_interstitial_after_splash.png`
- Google AdMob interstitial (AdActivity). Top: segmented progress bar; after the countdown a pill **"Continue ▶|"** appears top-right; a [Close] X (25×24 dp at 375,12 dp) also appears. Some creatives auto-open the Play Store install sheet – dismiss with Back. **Do not replicate (spec target has no ads).**

### 1.4 Language selection (first run) — `05_language_select_top.png/.xml`, `05_language_select_bottom.png`, `05_language_select_finger_anim1/2.png`, `06_language_selected_english.png/.xml`
Purpose: choose app language before onboarding. Also reachable from Settings → Languages (`94_settings_languages.png`).
Layout (dp):
- Background **#D9E4E4** (pale grey-teal) full screen.
- Toolbar area 0–71 dp. Title **"Select a language"** at (24,41) 156×26 dp, colour **#254D2D**, ≈20 sp, extra-bold rounded (Nunito ExtraBold-like).
- After a language is selected a **"Next ›"** text button appears top-right (306,36, 82×35 dp): text #254D2D ≈20 sp bold + chevron.
- List (RecyclerView) x 24–387 dp (363 dp wide), from y 79 dp to 548 dp (above the native ad). Item pitch 63 dp: card 363×51 dp, gap 12 dp.
  - Card fill **#E4ECED**, radius ≈10 dp, no elevation.
  - Flag image 35×35 dp at x 32 dp (rounded-rect flag icons with 4 dp radius & subtle shadow).
  - Name at x 83 dp: **bold** ≈16 sp **#091214**; immediately followed (4 dp gap) by native name in parentheses, regular ≈14 sp **#5C5E5E**: e.g. "English (English)".
  - Selected card: fill **#BACFCF**, stroke 1.5 dp (5 px) **#254D2D**.
  - First run only: an animated **pointing-hand icon** (blue hand, 73×63 dp area, right side of the first row) pulses/fades to invite a tap (`fingerAnimation`).
- Bottom: native ad card 391×265 dp (x 10, y 556) — light blue #C5DDE1 rounded 16 dp, "Ads" yellow tag, media, green **Install** pill (#254D2D, white text). (Ad, not to be cloned.)
Languages (in order, name + hint): English (English), Albanian (shqip), Arabic (العربية), Azerbaijani (azərbaycan), Bangla (বাংলা), Bulgarian (български), Burmese (မြန်မာ), Catalan (català), Chinese (中文), Croatian (hrvatski), Czech (čeština), Dutch (Nederlands), Finnish (suomi), French (français), German (Deutsch), Greek (Ελληνικά), Gujarati (ગુજરાતી), Hebrew (עברית), Hindi (हिन्दी), Hungarian (magyar), Indonesian (Indonesia) **[listed twice – app bug]**, Italian (italiano), Japanese (日本語), Kannada (ಕನ್ನಡ), Korean (한국어), Malay (Melayu), Malayalam (മലയാളം), Marathi (मराठी), Norwegian (norsk), Odia (ଓଡ଼ିଆ), Persian (فارسی), Polish (polski), Portuguese (português), Romanian (română), Russian (русский), Slovak (slovenčina), Slovenian (slovenščina), Spanish (español), Swedish (svenska), Tamil (தமிழ்), Telugu (తెలుగు), Thai (ไทย), Turkish (Türkçe), Ukrainian (українська), Urdu (اردو), Uzbek (o‘zbek), Vietnamese (Tiếng Việt). Default highlighted: none until tap (device locale English preselection not shown).
Behaviour: tap selects (single choice) → Next appears → Next goes to onboarding. Settings variant (`94`): green toolbar "‹ Languages" with ✓ (done) icon top-right; first item pre-selected; no ad, no finger.

### 1.5 Onboarding pager — `07_onboarding_1_find_qibla.png/.xml`, `08_onboarding_2_ai_deen.png/.xml`, `09_onboarding_fullscreen_native_ad_page.png`, `10_onboarding_3_timely_prayers.png/.xml`
- Background **#FFFFFF**. Horizontal pager (swipe or button).
- Hero image: full-width 411×338 dp (page 1, y 28–366) / 411×387 dp (page 2): 3-D rendered gold artwork on white (page 1: ornate gold compass with Kaaba cube; page 2: ornate gold speech-bubble frame with black screen "Ai" smile logo; page 3: gold arch/mihrab with person praying on carpet, lanterns and tasbih).
- Title centred: page1 "**Find Qibla**" (y 390, 121×33 dp), page2 "**AI Deen**", page3 "**Timely Prayers**"; colour #254D2D, ≈26 sp, ExtraBold.
- Body centred, 24 dp side margins, colour #254D2D, ≈17 sp regular, line height ≈27 dp:
  1. "Always face the Kaaba with our accurate Qibla finder."
  2. "Your smart Islamic companion for instant answers, guidance, and learning."
  3. "Get accurate prayer times based on your location."
- Indicator row y≈514 dp (centre x): active = pill 16×8 dp #254D2D (radius 4); inactive = dots 8 dp **#A9A9A9**; spacing 8 dp.
- Right action at (321,494) 74×48 dp: "**Next**" text button (#254D2D, ≈20 sp ExtraBold) on pages 1–2; on page 3 a **filled button "Start"**: 78×35 dp, fill #254D2D, radius 10 dp, white ≈20 sp ExtraBold.
- Native ad occupies the lower 275 dp (pages 1/3). Between pages 2 and 3 the pager contains a **full-screen native ad page** with a grey circular X (48 dp) top-right.

### 1.6 "Set notifications for each prayer" — `11_onboarding_notification_setup.png/.xml`, `12_onboarding_notification_setup_toggled.png`
(Same list later in Settings → Prayer Time Alert, `92_settings_prayer_time_alert.png`.)
- White background. Gold 3-D bell in a gold-rimmed circle, 91×91 dp centred at y 52 dp.
- Title "Set notifications for\neach prayer" centred (99,159; 214×65 dp), #254D2D ≈26 sp SemiBold/Bold.
- Six rows (x 23–388 dp, 365×52 dp, pitch 67 dp starting y 250 dp): card fill **#F4F4F4**, radius ≈12 dp; left gold illustrated icon 36 dp at x 32 (Fajar: crescent over horizon lines; Sunrise: sun with up-arrow; Zuhur: small sun with rays; Asar: full rayed sun; Maghrib: half sun on horizon; Isha: crescent with sparkles) – gold gradient #E7C77A→#F6E7B8; label at x 81 dp black ≈20 sp medium: **Fajar, Sunrise, Zuhur, Asar, Maghrib, Isha**; Material switch at right (324,262) 48×27 dp.
  - Switch ON: thumb **#244B2C**, track **#89A090**; OFF: thumb #FAFAFA, track #BCBCBE.
  - Defaults: Fajar ON, **Sunrise OFF**, Zuhur ON, Asar ON, Maghrib ON, Isha ON.
- Checkbox row (y 639–708): round check 31 dp: checked = filled **#67AD5B** circle with white ✓; unchecked = 2 dp grey #BDBDBD ring. Text "Always show prayer schedule in notifications." black ≈18 sp (two lines). Default **checked** → enables the ongoing prayer-schedule notification (see §9).
- "**Continue**" button: (23,723) 365×50 dp, fill #254D2D, radius 12 dp, white ≈22 sp. → interstitial ad → Home.
- Banner ad (320×50-style adaptive) at the bottom.

### 1.7 Home permission bottom sheet — `14_home_permission_sheet_loading_ad.png`, `15_home_permission_sheet.png/.xml`, `22_home_permission_sheet_with_native_ad.png`, `25_permission_sheet_both_granted.png/.xml`
Shown on Home every time Home resumes while any of {notification, location} is not granted. Also contains a native ad (shimmer placeholder while loading: pale blue #EAF2FD blocks).
- Modal bottom sheet, white, top corners square (0 dp) (UNSURE – appears square), full width, dim scrim behind (#000 ~60%).
- Title "Please provide these permissions" centred, black ≈18 sp Bold, at y 615 dp (collapsed state) / 269 dp (with ad).
- Two cards 375 dp wide (x 11–385), height ≈73 dp, fill **#E8E8E8**, radius ≈20 dp, gap 12 dp:
  - "Notification" (Bold ≈18 sp black) / "To notify you when to pray" (≈17 sp black) + switch.
  - "Location" / "To provide prayer time precisely" + switch.
  - Switches here use Material3 teal default: ON thumb **#3B8585**, track **#7CB1B2**; OFF thumb white, track #909090.
- Toggling Location ON → system location prompt. Tap outside → sheet closes (and if location still denied → rationale dialog).

### 1.8 Location permission flow — `16_perm_location_system_prompt.png/.xml`, `17_perm_location_denied_result.png`, `18_dialog_location_rationale_after_deny.png/.xml`, `24_location_toggle_second_attempt.png`, `23_after_location_granted.png` (system notif settings page – opened accidentally), `21_home_top_location_denied.png`
- First toggle → **system dialog** "Allow Qibla Compass to access this device’s location?" with Precise/Approximate and [While using the app] [Only this time] [Don’t allow]. No app pre-dialog (the sheet itself is the rationale).
- **Deny** → stays on sheet, toggle stays OFF. Closing the sheet shows the app dialog **"Permission"**:
  - Dialog 370 dp wide (x 21), white, radius ≈10 dp; header bar 43 dp **#254D2D** with title "Permission" white ≈18 sp, left-aligned at 21 dp padding.
  - Body: "Qibla Compass will ask you to grant Location permission to better perform functions." #4A4A4A ≈17 sp, 10 dp side padding.
  - Buttons (104×39 dp each, radius ≈12 dp): **Cancel** (white fill, 2 dp stroke #254D2D, text #254D2D) and **Grant** (fill #254D2D, white text), centred with 20 dp gap.
  - Cancel → dialog closes; Home works with **"Location not available"** chip and next-prayer time "--:--" (see §2.1).
  - Grant / toggling the sheet's Location switch again after a denial → opens **Android App info** for the app (Settings › Apps › Qibla Compass) so the user grants manually (no second system prompt).
- After granting (While using + Precise) and returning: sheet shows both switches ON; it must be dismissed by tapping outside. Location chip shows city ("Islamabad"); prayer times computed.

### 1.9 Premium popup on first Home — `19_dialog_premium_50off.png/.xml`, `20_dialog_premium_quit_confirm.png/.xml`
See §11 Premium. Appears automatically on Home (first session, and again occasionally after returning to Home, and when tapping locked items).

---

## 2. Home tab (MainActivity › Home fragment)
Screens: `26_home_01_top.png` … `26_home_16.png` (scroll sequence top→bottom), `21_home_top_location_denied.png`, `C02b_home_checked_in.png`, `C02_home_checked_in_day1.png`. uiautomator could NOT dump Home (continuous compass/marquee animation never idles) – dimensions below are pixel-measured from the PNGs.

Overall structure: a **collapsing green header** (CoordinatorLayout/AppBar style) with a white rounded **content sheet** scrolling over it, a promo strip "Don t Like Ads?" pinned above the bottom nav, the **bottom navigation**, and (when an ad loads) an adaptive **banner ad** under the bottom nav.

### 2.1 Header (expanded) – y 0–333 dp
- Background **#254D2D** with decorative illustration: light-green translucent mosque silhouettes (a minaret left of centre, domes on the right, ~#3A6B44→#5B8F5F), a pale-yellow crescent moon (#F3F0A0→#E0E890) hanging top-right over the status bar, and a small gold rolled-prayer-mat icon at (26,130 dp).
- **Location chip** at (16,63 dp), height 31 dp, width wraps text (≈ 110 dp for "Islamabad", ≈ 120 dp for "Location not available"): fill white ~70 % (renders **#BBC9BF**), fully rounded, pin-outline icon 18 dp + city text ≈17 sp, both **#1A361F**.
- Top-right icons (centres x ≈ 284, 332, 379 dp; y ≈ 67 dp), each ≈ 28 dp:
  1. **Support / feedback face** – speech-bubble face, light-green gradient (#E6F59C→#B5E39B), dark-green eyes & mouth.
  2. **Crown** – gold ring (#E1CA7A, 2 dp) with filled gold crown → Premium.
  3. **Gear** – white outline cog → Settings (§8).
- Left column (x 16 dp): "**Next prayer - Asar**" white ≈20 sp (y ≈ 170 dp); time "**02:52 PM**" white ≈44 sp ExtraBold (y ≈ 195–225 dp); "**View time ›**" white ≈22 sp (y ≈ 252 dp). Without location: "Next prayer" and "--:--" dashes.
- Right: **compass widget** ≈ 161 dp diameter at (217,109 dp) — the silver "Default" dial, rotating live with the magnetometer, Kaaba marker on the rim. Small **expand icon** (two diagonal arrows, white ~50 %, 24 dp) at (361,267 dp).
- Divider 1 dp **#3D6E48**, x 16→396 dp, y 285 dp.
- Date row y ≈ 300–316 dp: left "**03 November, 2026**", right "**(22 Jumaada al-awal, 1448)**" (right-aligned at 396 dp), both white ≈20 sp.
- Collapsed header (after scrolling): ≈ 130 dp tall; shows "Next prayer - Maghrib" (≈17 sp) and "05:14 PM" (≈36 sp ExtraBold) + the 3 icons; mosque art remains.

### 2.2 White content sheet
Starts at y 333 dp, white, **top corner radius ≈ 20 dp**, overlaps the header.

#### 2.2.1 7-day check-in card (y 349–437 dp)
- x 16–396 dp, height 88 dp, white, **stroke 1 dp #B9C7BE**, radius ≈ 12 dp.
- Title "**Check in 7 days get reward**" (after check-in: "**Already checked in today**") at x 21 dp, **#254D2D** ≈20 sp ExtraBold. Right: "**Day 0/7**" → "Day 1/7", ≈18 sp **#8D8D8D**.
- 7 circles, diameter 28 dp, centre pitch ≈ 25 dp + 13 dp gap (px 130→265 per circle ≈ 38.5 dp), numbers 1–7 ≈18 sp #091214. Today = stroke 1 dp **#254D2D**; others stroke 1 dp **#B9C7BE**; completed = filled **#67AD5B** with white ✓.
- "**Check in**" button ≈ 49×31 dp at right (px 1080–1250, 1371–1480), fill #254D2D, radius ≈ 8 dp, white ≈18 sp SemiBold; disabled after check-in: fill **#939393**.
- Tap → records today's check-in, no dialog. Rewards are AI-Learning tokens: Day1 11 · Day2 16 · Day3 21 · Day4 26 · Day5 31 · Day6 36 · Day7 41 (see token sheet §6.3).

#### 2.2.2 "Learn & Grow"
- Title "**Learn & Grow**" **#8AC441** ≈22 sp ExtraBold, x 8 dp, y ≈ 465 dp.
- Two cards (px 42–690 and 748–1398 → **185 dp wide, gap 16 dp**, height ≈ 229 dp, radius ≈ 16 dp):
  - **Quran Learning** – diagonal gradient **#367543 → #24552F**, faint mosque line art; 3-D Quran-on-rehal image top-left (≈ 60 % width); circular outline "↗" button (1.5 dp white ring, 36 dp) top-right; red **"NEW"** ribbon tab centred on the top edge (#E05748, fold #B13634, white ≈14 sp); title "Quran Learning" ≈24 sp ExtraBold **#F0FEF5**; subtitle "Learn to read the Quran." ≈17 sp **#85B198** as a horizontal **marquee**; "Progress" ≈15 sp white; 3 dp progress track (white 40 %) + "0.0%" right ≈16 sp white.
  - **Qaida Learning** – gradient **#5729A6 → #2C1177**; image: purple open book with Arabic letters and "ع" medallion; title "Qaida Learning", subtitle "Learn Arabic alphabets." (light lavender); same progress row.
  - Tap → §6.1 / §6.5.

#### 2.2.3 Progress Tracking banner
- x 8–404 dp, ≈ 97 dp tall, radius 16 dp, dark green gradient (#2A5344 → #1F4232) with photo of Quran on a lit stand + mosque silhouettes on the right; "**Progress Tracking**" white ≈22 sp Bold; "Track your learning journey\nand progress." white ≈16 sp; "↗" outline circle. → §6.7.

#### 2.2.4 Daily Prayers card
- x 8–404 dp, ≈ 150 dp, radius 16 dp, stroke 1 dp light green (#CDE3A0), fill very light cream gradient **#F5F4CF → #F7FAE0**, faint mosque watermark.
- Green rounded-square icon 32 dp (white praying person); "**Daily Prayers**" ≈20 sp Bold black; "Perfect Your Prayer" ≈15 sp #333.
- Right pill "**Explore Time**" ≈ 110×34 dp, gradient #3F8C48→#2B6B35, white ≈16 sp Bold, radius 17 dp → Prayer tab.
- Timeline: 5 gold prayer icons above a 2 dp line with 5 nodes (≈ 14 dp light-olive circles #D5DFAE; next prayer node larger with #A9C278 ring); labels below: name ≈13 sp #555, time ≈13 sp Bold black: Fajar 05:04 AM · Zuhur 11:51 AM · Asar 02:52 PM · Maghrib 05:14 PM · Isha 06:39 PM.

#### 2.2.5 Shortcut grid (2 × 5)
- Tile **48×48 dp**, radius ≈ 10 dp, fill **#254D2D**, white line icon ≈ 26 dp; label ≈15 sp #222 centred below. Column centres x ≈ 49, 126, 204, 283, 360 dp.
- Row 1: **Duas** (raised hands), **Tasbih** (hand with beads), **Ayaat** (text lines + quote), **Names** ("الله" in frame), **Mosques** (mosque dome).
- Row 2: **Kalmas** (circle + Arabic word), **Daily Deen** (phone/reel), **Azkar** ("الله" calligraphy), **Hajj Umr…** (Kaaba; label ellipsized "Hajj Umrah"), **More** (4-square grid → Features sheet §7.11).

#### 2.2.6 In-feed native ad (≈ 391×300 dp) – not cloned.

#### 2.2.7 "Ai Deen" section
- Title "**Ai**" (red #E53935) + "**Deen**" (black) ≈20 sp Bold.
- Banner ≈ 396×172 dp, radius 16 dp, gold/sand gradient (#E9D38D → #C9A64F) with arched windows, crescent and a man reading Quran (right). Text in **#3B2F12**: "**AI Deen**" ≈32 sp heavy display, "**INQUIRES**" ≈20 sp Bold, "Ask Freely. Learn Deeply.\nOnly With Ai Deen." ≈14 sp; button "**Explore Now**" ≈ 150×40 dp, radius 10, dark green gradient, white ≈18 sp → AI Deen (§7.12).
- Horizontal carousel of suggestion cards (≈ 245×140 dp, radius 16, sky-cloud gradient #A9C7E4→#E8F0F8, inner white 70 % panel): header "**When Waking up**" + "→" (white ≈17 sp Bold), Arabic dua ≈15 sp black centred; next "Surah: Infit…" (Al-Infitar).

#### 2.2.8 "New feeds" (live streams)
- Title "**New feeds**" ≈20 sp Medium.
- Two thumbnails ≈ 185×110 dp, radius 10 dp: Makkah (Kaaba photo) & Medina (Masjid an-Nabawi), each with red pill "**• LIVE**" (#E53935, white ≈12 sp, top-right) and centred white ring play icon (48 dp). Under: 24 dp round avatar + "**Makkah Live Broa…**" / "**Medina Live Broad…**" ≈16 sp Medium, ellipsized. → §7.13.

#### 2.2.9 "Quick Access" (home-screen widget previews)
- Title "**Quick Access**" ≈20 sp Medium; subtitle "Add to home screen" ≈15 sp #777.
- **Next Prayer widget** preview ≈ 380×125 dp, radius 16, fill #254D2D with lighter mosque band at bottom: small sun icon + "NEXT PRAYER" (gold ≈12 sp), "**Asar**" white ≈22 sp Bold + "02:52 PM" ≈16 sp; right countdown pill "**00 : 00 : 14**" (white ≈22 sp on white 15 %, radius 10; counts up after prayer start, otherwise down); bottom translucent strip with 5 columns (gold icon, name, time ≈11 sp white), current column highlighted with lighter box.
- **Qibla widget** preview ≈ 380×125 dp, white, stroke 1 dp #CDE3A0, radius 16: black rounded-square compass (≈ 80 dp) left; Kaaba icon + "**Qibla**" ≈22 sp Bold #254D2D, "Direction to Kaaba" ≈13 sp #777; grey inner box (#EEF1EE, radius 10) "Bearing" + "**255° W**" ≈20 sp + dark-green square ▲ button; green dot + "Islamabad".
- Each has a round gold **"+"** button (26 dp, #E1CA7A) overlapping its top-right corner = add widget to launcher (UNSURE – not tapped).

#### 2.2.10 "Islamic History Places" (last section)
- Row: "**Islamic History Places**" ≈20 sp Medium; right "**View more**" ≈15 sp #333 → §7.14.
- Horizontal photo cards ≈ 185×110 dp, radius 10, bottom dark gradient + white caption ≈13 sp ("Kaaba (Makkah, Saudi Arabia)", "Zamzam Well (Makkah, Saudi Arabia)", …).

### 2.3 Pinned promo strip "Don t Like Ads?" (Home only)
- Full width ≈ 62 dp, horizontal gold gradient **#D7BE6C → #F1E3A6 → #C9A64F**; 3-D black/gold gift box ≈ 36 dp left; "**Don t Like Ads?**" (sic) ≈20 sp Bold black; "Get premium for only PKR2,800 / year" ≈16 sp #333; underlined italic "**Other Plans**" right → Premium; small "×" circle top-right closes it.

### 2.4 Bottom navigation (all main tabs) – best seen in `60_qibla_01_main.png`
- Height **71 dp** (px 2791–3040), white, top divider **2 dp #E8EDEA**.
- Slots 83 dp wide: icon 24 dp + label ≈14 sp. Inactive **#868686**, active **#254D2D**. Icons: Home (rounded house with dot), Prayer (calendar grid), Learning (open book), Quran (framed mushaf). Red badge dot **#EA3323** 8 dp on Quran until visited.
- Centre **Compass** button: circle **58 dp**, fill #254D2D, 6 dp white inner ring, white Kaaba glyph with compass needle pointing up. Flush with bar.
- Switching tabs: instant (no transition). Back from a tab → Home; Back on Home → exit dialog (§10).
- When a banner ad is loaded, it is placed *below* the nav (≈ 60 dp) and the nav is pushed up.

---

## 3. Prayer tab (Prayer Times + Hijri calendar)
Screens: `50_prayer_01_top.png/.xml`, `50_prayer_02_fajr_ticked.png`, `50_prayer_03_scrolled.png`, `50_prayer_04_bottom.png`, `52_prayer_month_expanded.png/.xml`, `52b_prayer_month_swiped_next.png`, `52c_prayer_gregorian_toggle.png`, `52d_prayer_future_day_selected.png`, `52e_toast_prayer_not_started.png`, `53*_converter*.png`, `54*_islamic_events*.png`, `55_event_detail_*.png`, `51*_prayer_*alarm*.png`.
Entry: bottom nav "Prayer"; also "Pray Now" on the prayer-time full-screen alert, "Explore Time" (home). Whole page scrolls vertically (header scrolls away).

### 3.1 Header (white background)
- Left (x 21 dp, y 32): "**Jumaada al-awal, 1448**" ≈18 sp SemiBold black (#000); below "**October, 2026**" ≈15 sp black (Gregorian month(s) spanned by the visible week; expanded month shows "October - November, 2026").
- Right: toggle chip "**Gregorian ⇌**" at (255,36) 95×29 dp: white, stroke 1 dp #000 (≈ #333), fully rounded, text ≈16 sp black + ⇌ icon. Tap swaps the calendar basis: label becomes "**Hijri ⇌**", header lines swap ("November, 2026" / "Jumaada al-awal - Jumaada al-THaany, 1448"), grid shows Gregorian day numbers large.
- Gear icon (359,36) 31 dp outline black → **Converter dialog** (§3.5).

### 3.2 Week strip (collapsed calendar, default)
- Day-name row y 92 dp: "SAT SUN MON TUE WED THU FRI" ≈17 sp **#757575**, columns 56 dp pitch starting x 29 dp (horizontally scrollable, last column cut off).
- Day cells 47×48 dp (radius ≈ 8 dp), white fill, **stroke 2 dp #EEEEEE**: big number = **Hijri day** ≈18 sp Bold black (#000), small number = Gregorian day ≈13 sp **#9E9E9E**. (Big 20/small 31 … 23/3.)
- Today cell: fill **#E8EDEA** (light grey-green), stroke 2 dp #DADADA, plus a short **green arc #67AD5B** hugging the top-right corner (3 dp).
- Selected cell: fill **#254D2D**, numbers white / white 70 %.
- Under the strip: centred double-chevron "⌄⌄" icon (48×35 dp) → expands to month grid; right "**Today**" underlined ≈16 sp **#4C7056** → jumps to today.

### 3.3 Month grid (expanded) – `52_prayer_month_expanded.png`
- Header row SUN…SAT (≈17 sp #757575, 53 dp columns from x 21).
- Cells 53 dp pitch, cell 47×47 dp same style as strip; month = current **Hijri** month (Jumaada al-awal 1448: 1 = 12 Oct … 30 = 10 Nov). Leading empty cells for alignment.
- Chevron changes to "⌃⌃" (collapse). Horizontal swipe = previous/next Hijri month (header updates, e.g. "Jumaada al-THaany, 1448 / November - December, 2026").
- Tap a day → selects it; prayer list below shows that day's times (e.g. 5 Nov Fajar 05:05 AM, Sunrise 06:30 AM). Ticking a future prayer → toast "**Fajar has not started yet**" (`52e`).

### 3.4 Tabs: "Prayer Time" | "Islamic Events"
- Tab row (16,215) 379×48 dp; labels ≈17 sp: selected **#254D2D** Medium with 2 dp **#254D2D** underline (indicator spans the half), unselected **#666666**.

#### 3.4.1 Prayer Time tab
- Summary card (16,268) 379×~70 dp, fill **#F5F5F5**, radius ≈ 10 dp, faint grey mosque skyline illustration: "**Prayer**" ≈18 sp black left, "**0 Times**" ≈18 sp right (count of ticked prayers today: "1 Times"); progress bar (34,326) 343×4 dp, track **#CFCFCF**, fill **#67AD5B** (fraction ticked/5).
- 6 prayer rows (x 16–395 dp, 81 dp tall, gap 7 dp, start y 364 dp): white, **stroke 1 dp #E0E0E0**, radius ≈ 12 dp.
  - Gold illustrated icon 42 dp at x 30 (same icon set as onboarding).
  - Name at (87,378): **italic** ≈18 sp **#4C7056** ("Fajar", "Sunrise", "Zuhur", "Asar", "Maghrib", "Isha").
  - Time at (87,401): ≈24 sp **Bold black** ("05:04 AM" format hh:mm a).
  - Right: alarm-clock icon 31 dp (black outline stopwatch; Sunrise shows a **crossed-out** stopwatch = alert off) and check circle 31 dp (unchecked = 1.5 dp ring **#BDBDBD**; checked = filled **#67AD5B** + white ✓).
  - **Next prayer row** highlighted: stroke 3 dp **#7BB972** with soft outer glow **#EAF4EA** (≈ 3 dp).
  - Tap check circle → toggles tracking for that prayer (only allowed once the prayer time has started; else toast). Updates the summary count/progress. Persists per day.
  - Tap alarm icon → **"<Prayer> Alarm Setting"** screen (§3.6). (Sunrise alarm icon tap: no visible response observed – UNSURE.)
- Today's times observed (Islamabad, Karachi method, Shafi): Fajar 05:04 AM, Sunrise 06:28 AM, Zuhur 11:51 AM, Asar 02:52 PM, Maghrib 05:14 PM, Isha 06:39 PM.

#### 3.4.2 Islamic Events tab – `54_prayer_islamic_events.png/.xml`
- List of event cards (x 16–395 dp, ≈ 89 dp tall, gap 7 dp): fill **#EEEEEE**, radius ≈ 12 dp, no stroke.
  - Name ≈19 sp Medium black at (32,300); Hijri date ≈16 sp **#9E9E9E** below; Gregorian date right-aligned ≈18 sp **#4C7056** (format d/M/yyyy); info "ⓘ" icon 29 dp #8A8A8A at right.
- Events (fixed list, Hijri year of current calendar):
  1. Islamic New Year – 1 Muharram 1448 AH – 17/6/2026
  2. Milad un Nabi – 12 Rabi-ul-Awal 1448 AH – 26/8/2026
  3. Ramadan – 1 Ramadan 1448 AH – 8/2/2027
  4. Laylat al Kadr – 27 Ramadan 1448 AH – 6/3/2027
  5. Eid-al-Fitr – 1 Shawwal 1448 AH – 10/3/2027
  6. Waqf al Arafa – 9 Dhu'l-Hijjah 1448 AH – 16/5/2027
  7. Eid-al-Adha – 10 Dhu'l-Hijjah 1448 AH – 17/5/2027
- Tap ⓘ → **event dialog** (`55_event_detail_*`): width 380 dp, radius ≈ 12 dp; green header **#254D2D** with title ≈19 sp Bold white centred and Hijri date ≈16 sp white; white body with a 4–8 line encyclopedic description ≈15 sp #333 (Roboto); centred text button "**Close**" ≈17 sp #254D2D. (Descriptions are Wikipedia-style summaries; full text visible in the screenshots – re-source the texts, don't copy.) No reminder/alarm option on events.

### 3.5 Hijri ⇄ Gregorian Converter dialog – `53_prayer_hijri_settings.png/.xml`, `53b_converter_gregorian_tab.png`, `53d_converter_wheel_scrolled.png`
- Dialog x 16–395 dp, white, radius ≈ 16 dp, header bar ≈ 57 dp **#254D2D** with "**Converter**" white ≈20 sp centred.
- Segmented toggle (76,278) 260×30 dp: pill track **#EEF3F6**, selected segment fill **#254D2D** white Bold ≈16 sp "**Hijri**", other "**Georgian**" (sic) ≈16 sp #777.
- Three NumberPicker wheels (day 64 dp, month 125 dp, year 64 dp): selected value #254D2D ≈17 sp between two 2 dp **#254D2D** lines; neighbour values faded #A5B7AB. Hijri mode wheels: 21–23 / Raby` al-THaany… / 1447–1449. Gregorian mode: 2–4 / October… / 2025–2027.
- Result line: "**3 November 2026**" (in Hijri mode) or "**22 Jumaada al-awal 1448**" (Gregorian mode) ≈26 sp Bold #333 centred, updates live while scrolling.
- Buttons 143×41 dp, radius ≈ 12 dp: "**Cancel**" (white, 2 dp #254D2D stroke, #254D2D Bold text) and "**Convert**" (fill #254D2D, white Bold). Convert just keeps result (no navigation observed).
- NOTE: converter/home show 22 Jumaada al-awal while the calendar strip shows 23 for today → app has a 1-day offset between widgets (log tag `hijri_diff: 0`). Choose one consistent algorithm (UNSURE which is "right"; Umm al-Qura gives 22).

### 3.6 Prayer Alarm Setting screen – `51_prayer_fajr_sound_dialog.png/.xml`, `51b_prayer_alarm_playing.png`, `51c_prayer_alarm_time_tap.png`
(Full screen, not a dialog.)
- Top bar 84 dp **#254D2D**: back chevron (16,42 dp) 39 dp white; title "**Fajar Alarm Setting**" white ≈22 sp Bold.
- Row card (31,131) 349×~48 dp, fill #F4F4F4, radius 12: "**Set Alarm Time**" ≈20 sp Medium black + switch (ON default, green style §1.6).
- Time banner (31,192) 349×87 dp, radius ≈ 12 dp, background image (dark purple/brown Islamic lanterns & Kaaba illustration, #2A1823-ish), centred white "**05:04 AM**" ≈32 sp ExtraBold. Tap → Material TimePicker "**Set Alarm Time**" (clock dial, green header, CANCEL/OK) initialised to *current* time (custom alarm time override – UNSURE whether it offsets).
- Sound list (31,295) 349 dp wide, rows 63 dp, divider 1 dp **#767676** (inset 5 dp):
  - "Silent" (speaker-mute icon, no play button), "Beep", "Azaan 1", "Azaan 2", "Azaan 3" – label ≈18 sp Bold black; speaker outline icon 24 dp left; right: ▶ play 34 dp **#254D2D** (turns into ❚❚ pause while previewing) and radio 34 dp (unselected 2 dp grey #8A8A8A ring; selected = #254D2D ring + dot).
  - Default selection **Azaan 1** (all prayers). Selecting a radio saves immediately.

### 3.7 Calculation settings
See Settings › Prayer Methods (§8.2).
---

## 4. Compass tab (Qibla)
Screens: `60_qibla_01_main.png/.xml`, `60_qibla_t0.png`, `61_qibla_calibration_dialog.png/.xml`, `62_qibla_styles.png/.xml`, `62b_qibla_styles_bottom.png`, `63_qibla_locked_style_tap.png`, `63b_qibla_royalty_tap_premium.png`, `65*_qibla_map*.png`.

### 4.1 Compass screen
- White background. Top bar (0,32) 73 dp: title "**Compass**" (24,32) **#254D2D** ≈28 sp ExtraBold; three 40 dp icon buttons, outline style **#254D2D** 2 dp stroke: ⓘ (how_to_caliberate → calibration dialog), folded map (compass_map → Qibla map), compass-in-circle (compass_styles → Compass Designs).
- Compass block (10,83) 391×495 dp (layered ImageViews: dial, north, needle, qibla) – Default design: realistic **silver bezel** (chrome gradient ring #F2F2F2/#9E9E9E), white-grey face (#E6E6E6 radial), black degree ticks every 2°, numerals 0,20,…340 ≈12 sp black, cardinal letters **N E S W** ≈28 sp italic black, inter-cardinals NE/SE/SW/WN (sic "WN") smaller; compass-rose star in dark grey/black with **red north needle** (#C62828 gradient) and **blue south needle** (#2F5BD3 gradient), chrome hub.
  - Centre white circle 40 dp with current heading digits "**336**" ≈22 sp black (no ° sign).
  - **Kaaba marker**: 3-D Kaaba cube in a white rounded holder sitting on the bezel at the Qibla bearing; rotates with the dial (the whole dial rotates opposite to device heading so N points north; Kaaba stays at 255° on the dial).
  - Rotation is continuous, smoothed (low-pass filter); no haptics observed when aligned (UNSURE).
- "**Qibla Angle : 255°**" pill (117,589) 178×34 dp: white, stroke 1.5 dp **#254D2D**, radius 17, black ≈22 sp Medium.
- Info row y 646–704 dp, 3 columns centred at x 87/206/322 dp: outline black icons 32 dp (sunrise with up-arrow, mountains, sunset with down-arrow) and values ≈20 sp black: "**06:28 AM**" (sunrise), "**496 m**" (altitude from GPS), "**05:14 PM**" (sunset).
- Buttons row y 725–769 dp: two outlined buttons 188×44 dp (x 8 and 216), white, stroke 1.5 dp **#091214**, radius ≈ 14 dp; left icon 24 dp (green mushaf) + "**Continue read Quran**" ≈18 sp **#254D2D**; right icon (hand with tasbih, green) + "**Tasbih**". → Quran tab (surah list) / Tasbeeh screen. Interstitial ads often show on these transitions.
- Thin grey line #868686 (1 dp) above the nav (y 785 dp).
- Heading 0–360 shown; when location is unknown the Qibla angle/Kaaba marker is absent (UNSURE – not observed).

### 4.2 Calibration dialog – `61_qibla_calibration_dialog.png`
- Centred white dialog x 11–400 dp (≈ 389×476 dp), radius ≈ 16 dp, scrim 60 %.
- Bullet (filled **#254D2D** circle 12 dp) + "Calibrate your device by making 5 or 6 times large 8-shaped loops in front of you." ≈19 sp #222.
- Illustration 359×156 dp: blue 3-D figure-eight ribbon arrow with a hand holding a phone.
- Bullet + "The arrow should point in the right direction."
- Button "**Start Compass**" (137,594) 126×42 dp, fill #254D2D, radius ≈ 6 dp, white ≈17 sp (Roboto). → dismiss.
- Shown when the ⓘ icon is tapped (and automatically when sensor accuracy is low – UNSURE).

### 4.3 Compass Designs – `62_qibla_styles.png/.xml`, `62b_qibla_styles_bottom.png`
- Top bar 100 dp **#254D2D**, back chevron + "**Compass Designs**" white ≈22 sp Bold.
- 2-column grid (x 12 and 212 dp), tile 188×223 dp, gap 24 dp vertical: white, stroke 2 dp **#C7C7C7**, radius ≈ 12 dp; preview image 164×164 dp; bottom label band (≈ 35 dp, fill **#F1F1F1**) with name ≈18 sp black; locked tiles have a yellow padlock icon (#F2B526) before the name. Selected tile: stroke 2 dp **#254D2D**.
- Designs (10): **Default** (selected, free), **Royalty** (gold ornate compass on gold-damask background; tile stroke gold #D4B95E; no padlock but tapping still opens the premium dialog), **Silver** 🔒, **Main** 🔒 (dark-green dial with white 270° digital centre), **Mythic** 🔒 (navy star rose, dotted ring), **Modern** 🔒 (navy rose with red needle), **Minimal** 🔒, **Readable** 🔒, **Retro** 🔒 (navy ring), **Golden** 🔒 (gold bezel). Locked → Premium "50% OFF" dialog (§11).

### 4.4 Qibla Direction map – `65_qibla_map.png/.xml`, `65b…65e`
- Activity `MapsCompass`. Top bar 81 dp #254D2D: back + "**Qibla Direction**" white ≈22 sp Bold.
- **Google Maps** (SDK) full screen below; compass button top-left (maps default).
- Polyline: solid **red (#E53935, ~4 dp)** geodesic line from user location (blue arrow marker) to Mecca (custom Kaaba-cube marker ≈ 36 dp). Initial camera = default world view (dashed straight line visible at low zoom).
- Bottom-right stacked white square buttons (42 dp, radius 4, shadow): ◎ current location (zoom to user, street level), Kaaba icon (fit both points in bounds).
- Bottom-left dark-green square button (34 dp, #254D2D, white layers icon) → expands vertical list of 3 white icon buttons above it: map (normal), image (satellite), mountain (terrain).
- Offline: map tiles don't load (UNSURE – Maps cache).
---

## 5. Tasbeeh counter (ReadTasbeeh activity)
Screens: `70_tasbih_01.png/.xml`, `70_tasbih_02_count5.png`, `70_tasbih_03_count33.png`, `70_tasbih_04_after33.png/.xml`, `70_tasbih_05_last_dhikr.png`, `71_tasbih_reset_tap.png/.xml`, `72_tasbih_sound_toggled.png`, `73_tasbih_daily_goal_edit.png/.xml`.
Entry: Home grid "Tasbih", Features sheet, Compass "Tasbih" button.

- Top bar 0–99 dp **#254D2D**: back chevron (0,42) 55 dp white; "**Tasbeeh**" white ≈22 sp (x 55 dp); right icons white: ↻ reset (301,42) 55 dp, 🔊 sound toggle (356,42) 55 dp (muted state = speaker with ✕).
- Upper area (y 99–441 dp) fill **#E5ECED**:
  - "**Current Dhikr**" ≈20 sp Bold #091214 centred (y 115).
  - Horizontal ViewPager of dhikr cards (peek of next card on the right): card ≈ 281×239 dp (x 65), fill **#ECF1F2**, stroke 1.5 dp **#254D2D**, radius ≈ 20 dp; Arabic ≈22 sp black (naskh) centred; transliteration ≈22 sp **#828282** below.
  - Page counter "**1/15**" ≈20 sp #091214 (y 398).
- Lower area (y 441–821) fill **#ECF1F2**:
  - "Daily goal: **33**" ≈20 sp Bold #4E4D4D with the number in **#254D2D**; edit (pencil-square) icon 37 dp #254D2D.
  - Counter: circular progress ring 267 dp (72,507): track **#BABABA** ≈ 10 dp; progress arc **gold gradient #C09F3C (top) → #F6E4AA (bottom)** drawn clockwise from 12 o'clock; inner white circle ≈ 210 dp with soft inner shadow/bevel (#E9EFEF edge).
  - Inside: count "**0**" ≈64 sp ExtraBold **#254D2D**; "**Loop 0**" ≈18 sp #828282.
  - Hint "Tap circle above to use counter" ≈18 sp **#868686** at bottom (y ≈ 816).
- Behaviour: tap anywhere on the circle → +1 (with click sound unless muted; vibration UNSURE). When count reaches the goal (33) the ring is full and **Loop** increments (Loop 1); next tap restarts count at 1 (ring restarts). Count persists per dhikr.
- Reset (↻) → dialog "**Reset Tasbeeh**" (`71`): white bottom-ish dialog, radius 16, title ≈20 sp Bold #254D2D centred; text "Press  Reset  to clear the counter and start a new."; "Clear loop counter too." + checkbox (unchecked default); buttons 179×47 dp: "**Cancel**" (white, #254D2D 1.5 dp stroke) / "**Reset**" (#254D2D fill, white), radius 12.
- Daily goal edit → dialog "**Change**" (`73`): 2×2 option buttons 159×47 dp ("33", "99", "100", and an EditText "Customize..." hint #BDBDBD) – white with subtle shadow, selected one stroke 1.5 dp #254D2D, radius 10; "Cancel" / "Save" buttons as above. Default 33.
- 15 dhikr (transliteration as shown): Subhan Allah · Al Hamdulillah · Allah u Akbar · Astaghfirllah · La Ilaha Illallah · La haula Wala Quata Illah Billah · Astaghfirullah wa Atubu ilai · Allahumma Salli Ala Sayyidina Muhammad · Subhan Allah, Al Hamdulillah, Wala Ilaha Illallah, Wallah u Akbar · La Ilaha Illallah u Wallah u Akbaru Wala haula Wala Quata Illah Billah · Astaghfirullah Hallazi La Ilaha Illaa Hual Hayyul Qayyumu Wa Atubu Ilaih · Ash hadu Alla Ilaha Illallahu Wa Ash Hadu Anna Muhammadan Abduhu Warasuluh · La Ilaha Illallah, Wahdahu la Shareeka Lahu, Lahul Mulku, Wala Hul Hamdu, Wahua Ala Kulli Shai In Qadeer · Subhan Allah Wal Hamdulillah Wala Ilaha Illallah Wallah u Akbar Wala haula Wala Quata Illah Billahil Aliyyil Azeem · La Ilaha Illallah u Wahdahu La Shareeka Lahoo Lahul Mulku Walahul Hamdu Yuhyee Wayumeetu Wahua Ala Kulli Shai In Qadeer. (Arabic strings are in `70_tasbih_*.xml`.)
- Leaving Tasbeeh frequently shows an interstitial.

---

## 8. Settings
Screens: `90_settings_01_top.png/.xml`, `90_settings_02_bottom.png/.xml`, `90_settings_03_end.png/.xml`, `92_settings_prayer_time_alert.png/.xml`, `93_settings_prayer_methods*.png/.xml`, `94_settings_languages.png/.xml`, `97_settings_dnd_toggle.png/.xml`, `98_settings_dnd_time_picker.png/.xml`, `91_share_app_sheet.png/.xml`.
Entry: gear icon on Home header.

### 8.1 Settings main
- Top bar 0–92 dp **#254D2D**: back chevron 39 dp white (16,42); "**Settings**" white ≈22 sp Bold (55,42).
- ScrollView, white, side margin 23 dp:
  1. **Premium banner** (23,108) 365×156 dp, radius ≈ 16 dp: night photo of a white mosque with blue glow; bottom-right gold pill "👑 **Get Premium**" (132×34 dp, gold gradient #D6BA5D→#B59433, black ≈17 sp Bold, crown icon) → Premium.
  2. Row "**Prayer Methods**" (23,279) 365×62 dp: fill **#F4F4F4**, radius ≈ 12 dp; left round icon 42 dp (fill #ECF1F2, dark-green glyph: person praying with stars); label ≈20 sp Medium black at x 91 dp; right chevron › grey #8A8A8A.
  3. Section header "*Notification Settings*" italic ≈20 sp #091214 (y 357).
  4. "**Do Not Disturb**" card (≈ 75 dp): moon icon; title ≈20 sp + subtitle "Inactive on downtime mode" ≈17 sp; switch (default OFF). When ON the card expands: "Select downtime to disable notifications", and a row "From **10:00 PM** ▾  To **08:00 AM** ▾" (time pickers – Material clock dialog, green header).
  5. "**Prayer Time Alert**" › → per-prayer notification toggles (same list as §1.6, top bar "Prayer Time Alert").
  6. "**Namaz Notifications**" + switch (default ON) – prayer-mat icon.
  7. "**Ayaat Notifications**" + switch (default ON) – Quran icon.
  8. "**Dua Notifications**" + switch (default ON) – raised hands icon.
  9. Section "*About app*".
  10. "**Languages**" › (文 icon) → Languages list (§1.4 settings variant, green top bar with ✓).
  11. "**Manage Subscription**" › ("SUB" bell icon) → opens **Google Play › Subscriptions** (external).
  12. "**Share App**" › (share icon) → Android share intent (resolver) with text **"Download this great app at: https://bit.ly/3bkbh9q "** (no subject).
  13. "Privacy Policy" › (underlined label, shield-lock icon) → opens browser at `https://codeconnectech.com/privacy-policy-muslim-prayer-qiblah-compass/`.
- Row style: 62 dp, gap 16 dp; icons are 42 dp circles fill **#ECF1F2** with #254D2D glyphs.

### 8.2 Prayer Methods – `93_settings_prayer_methods.png`
- Top bar #254D2D "‹ Prayer Methods".
- Section label "**Juristic**" ≈20 sp **#254D2D** Medium with a 1 dp #E0E0E0 rule to the right.
- Radio rows (≈ 42 dp): "**Shafii (Default)**" (selected) / "**Hanafi**" – label ≈18 sp black; radio right (selected: #254D2D ring + dot, 24 dp; unselected #8A8A8A ring).
- Section "**Methods**" + rule. Rows (≈ 62 dp): name ≈18 sp black + angles ≈15 sp **#B1B1B1**, radio right:
  1. University of Islamic Sciences, Karachi – Fajar Angle: 18°, Isha Angle: 18° **(selected default – Pakistan)**
  2. Shia Ithna-Ashari, Leva Institute, Qum – Fajar Angle: 16°, Isha Angle: 14°
  3. Islamic Society of North America (ISNA) – Fajar Angle: 15°, Isha Angle: 15°
  4. Muslim World League (MWL) – Fajar Angle: 18°, Isha Angle: 17°
  5. Om Al-Qurra University – Fajar Angle: 18.5°, Isha 90 minutes after Maghrib
  6. Egyptian General Authority of Survey – Fajar Angle: 19.5°, Isha Angle: 17.5°
  7. Institute of Geophysics, University of Tehran – Fajar Angle: 17.7°, Isha Angle: 14°
  8. Morocco – Fajar Angle: 19°, Isha Angle: 17°
  9. Jabatan Kemajuan Islam Malaysia (JAKIM) – Fajar Angle: 20°, Isha Angle: 18°
  10. Majlis Ugama Islam Singapura, Singapore – Fajar Angle: 20°, Isha Angle: 18°
  11. Union Organization Islamic de France – Fajar Angle: 12°, Isha Angle: 12°
  12. Diyanet İşleri Başkanlığı, Turkey – Fajar Angle: 18°, Isha Angle: 17°
- No manual minute adjustments / high-latitude options were found (UNSURE – none visible).

---

## 9. Notifications & prayer alert
Screens: `80_notification_prayer_asar.png/.xml`, `81_notification_expanded.png/.xml`, `82_after_tap_prayer_notification.png/.xml`, `83_notification_ongoing_schedule_crop.png`.
Channels (system settings): "alarm_notification_channel", "Ongoing Notification Channel", "qiblaChannelName", "Qibla Compass" (names exactly as shown – developer placeholders).
- **Prayer-time notification** (id 4444, channel `qiblaChannelId`, importance HIGH, category ALARM, heads-up, full-screen intent): small icon = black compass glyph; title "**Prayer Time: 02:52 PM**"; text "**It s time for Asar...**"; expanded bigText "**It s time for Asar. Find the Qibla direction for your location.**" (apostrophes missing in the original). Azan sound plays (selected sound per prayer).
- Tap → **PrayerTimeLockscreenActivity** (`82`): full-screen mosque photo background (pastel domes & minarets); close ✕ white 31 dp top-right; centred ring 247 dp: gold gradient stroke ≈ 10 dp (**#FBF0A7 top → #DECC85 → #C9A85A bottom**) with inner translucent white disc (~70 %, renders #E8EDE7); inside "**02:52 PM**" ≈34 sp ExtraBold black and "**Asar**" ≈26 sp black. Buttons: "**Pray Now**" (31,613) 349×54 dp fill #254D2D radius 12, white ≈20 sp; "**Quran**" and "**Qibla**" 167×55 dp outlined white 2 dp, radius 14, white ≈20 sp + icons. Pray Now → opens app (splash → Prayer tab).
- **Ongoing schedule notification** (id 1234, low importance, custom RemoteViews, DecoratedCustomViewStyle) – shown when "Always show prayer schedule in notifications" is ON: one row "Fajar 05:04 | Zuhur 11:51 | Asar 14:52 | **Maghrib 17:14** | Isha 18:39" (24-h; names ≈14 sp #555, times ≈18 sp #555; next prayer **Bold #254D2D**).
- No media-style notification for Quran audio was observed (player is in-app only).

---

## 10. Exit & rating dialogs
- First Back on Home (once): `27_dialog_exit_rating.png/.xml` – white dialog 320×179 dp, radius 16; "Your Good Rating Make a Difference" ≈15 sp black centred; 5 outline stars 48 dp; "Bad" (under star 1) / "Good" (under star 5) ≈13 sp #555; text buttons "**Cancel**" (black Bold) and "**Exit**" (#254D2D). Selecting 1–3 stars (we tapped 2) opens a **Gmail compose** to `partner.xtech.04@gmail.com` subject "Feedback from Qibla Compass" (4–5 stars presumably opens Play in-app review – UNSURE). Exit → finish.
- Later Back on Home: `29_dialog_exit_with_native_ad.png/.xml` – "Do you want to exit the app?" ≈18 sp Bold, "Cancel" / "Exit" text buttons, native ad below (288 dp wide dialog).
---

## A. Quran tab
Screens: `A01…A31`.

### A.1 Quran index – `A01_quran_surah_list_top.png/.xml`, `A02_quran_surah_list_bottom.png`, `A03_quran_fav_toggle.png`, `A04_quran_favourite_tab.png/.xml`, `A05_quran_juzz_tab.png/.xml`, `A05b_quran_juzz_bottom.png`, `A06_quran_search_yas.png`, `A06b_quran_search_fat.png/.xml`
- White background. Title "**Quran**" (10,40) **#254D2D** ≈28 sp ExtraBold; top-right icon (mushaf outline 32 dp, #254D2D) → opens the **Display** settings sheet (§A.4).
- Search field (10,89) 391×57 dp: fill **#ECF1F2**, fully rounded (28 dp); magnifier icon 24 dp #254D2D at left; hint "**Search Quran s name**" (sic) ≈20 sp **#868686**; when typing a ✕ clear icon (40 dp, #254D2D) appears at right. Filters the current tab by English name substring, case-insensitive (e.g. "fat" → Al-Fatiha, Fatir; "yas" → nothing because the name is "Ya-Seen"). No results → empty white area (no empty-state message).
- Tabs (y 174 dp): "**Surah**" | "**Juzz**" | "**Favourite**" ≈24 sp; selected #254D2D SemiBold with 3 dp rounded underline indicator #254D2D under the text width; unselected **#868686**. On Favourite tab a right-aligned dark-green pill "**By Surah ▾**" (91×25 dp, #254D2D, white ≈14 sp) – filter (By Surah / By Juzz – UNSURE).
- Row card (x 10–401 dp, 73 dp tall, gap 16 dp): fill **#F7F7F7**, radius ≈ 20 dp, no stroke/shadow.
  - Number badge: 8-point star outline (Rub el Hizb, 2 dp #254D2D) 34 dp at x 21 with number ≈14 sp #254D2D.
  - English name ≈22 sp Medium black (x 63); subtitle "Meccan - 7 Verses" ≈15 sp black.
  - Arabic name "سورة الفاتحة" ≈22 sp **#254D2D** right-aligned at x 343.
  - Heart 40 dp: outline **#E15241** (favourite = filled #E15241). Tap toggles favourite (no toast).
- Surah naming (sample): Al-Fatiha, Al-Baqarah, Al-Imran, An-Nisa, Al-Ma'idah, Al-An'am, Al-A'raaf, Al-Anfaal, At-Tawbah, Yunus, Hud, Yusuf, Ar-Ra'd, Ibrahim, … Ya-Seen, … Al-Ikhlaas, Al-Falaq, An-Naas (standard 114 with "Meccan"/"Medinan" and verse count).
- Juzz tab rows: number badge + name + Arabic + heart: 1 Alif Lam Meem (الٓمّٓ), 2 Sayaqool, 3 Tilkal Rusul, 4 Lan Tanaalu, 5 Wal Muhsanat, 6 La Yuhibullah, 7 Wa Iza Samiu, 8 Walau Annana, 9 Qalal Mala, 10 Wa A'lamu, 11 Ya'tathirun, 12 Wa Ma Min Dabbah, 13 Wa Ma Ubarri'u, 14 Rubama, 15 Subhanalladhi, 16 Qal Alam, 17 Iqtarabat, 18 Qadd Aflaha, 19 Wa Qalalladhina, 20 Amman Khalaq, 21 Utlu Ma Uhiya, 22 Wa Man Yaqnut, 23 Wa Maliyy, 24 Faman Azlamu, 25 Ilayhi Yuraddu, 26 Ha Meem, 27 Qala Fama Khatbukum, 28 Qadd Sami Allah, 29 Tabarakalladhi, 30 Amma Yatasa'alun.
- Tap a row → interstitial (sometimes) → Surah reader (`QuranPak` activity).

### A.2 First-open settings wizard (shown on first Surah open) – `A10_fatiha_01.png/.xml`, `A11_quran_settings_themes.png/.xml`, `A11b_quran_settings_themes_scrolled.png`, `A11c_quran_settings_fonts.png/.xml`
Three consecutive centred dialogs (370 dp wide, white, radius ≈ 20 dp), each with title "**Settings**" (#254D2D ≈20 sp Bold) and a gold gradient "**Done**" button (74×33 dp, #D6BA5D→#B59433 with light sheen #E7CF7C, text #254D2D Bold, radius 4) top-right:
1. **Mode**: two image cards 165×130 dp – "Reading mode" (Arabic-only preview) and "**Ayat by ayat**" (Arabic + "Translation:" + "Tafseer:" preview, selected: stroke 2 dp gold **#D2C28B**, ✓ in green circle); unselected stroke 2 dp **#A5B7AB**, empty circle.
2. **Themes**: horizontal list of theme thumbnails 100×100 dp with buttons "In Use" (gold gradient) / "Use" (white, 1.5 dp #254D2D stroke); 4 themes: light green (default), cream/beige, dark green, white/grey.
3. **Font**: "Arabic font size" slider (A … **A**), "Translation/Tafseer size" slider, "Choose font": **IndoPak** (selected: #254D2D fill, white) | **Uthmani** (white, grey stroke), divider, "Click here to view all Quran Setting →" (opens Display sheet).
Sliders: Material SeekBar, track #D9D9D9, active + thumb #254D2D (thumb 14 dp).

### A.3 Surah reader (Ayat by ayat mode) – `A15_fatiha_ayat_by_ayat_top.png/.xml`, `A16_fatiha_copy_toast.png`, `A18_fatiha_tafseer.png/.xml`, `A19*`, `A21*`, `A23*`, `A25_fatiha_reading_mode.png/.xml`, `A26b_fatiha_uthmani_font.png`, `A27_fatiha_dark_green_theme.png`, `A30_baqarah_top.png/.xml`, `A30b_baqarah_scrolled.png`, `A31_baqarah_tafseer_sheet.png`
Theme 1 (default "light green"):
- Page background **#DBEAE1** (very light mint) with a faint large Quran/rehal watermark illustration; app bar area **#E4F1E8**.
- App bar (y 28–100 dp): back chevron 40 dp **#254D2D**; surah name "**Al-Fatiha**" ≈24 sp #254D2D (x 58); right icons 39 dp outline #254D2D: mushaf (Display settings sheet) and headphones (Reciters list).
- Bismillah header: calligraphic "بسم الله الرحمن الرحيم" inside an ornamental frame with floral end-pieces, dark green #254D2D, full width ≈ 55 dp tall (image). (Shown on every surah; for Al-Fatiha ayah 1 is also Bismillah.)
- Ayah card (x 16–395 dp, radius ≈ 20 dp, fill **#DBEBE4**, soft shadow ≈ 4 dp elevation (#C1CEC9 edge), gap 12 dp):
  - Top-left chip "**Tafseer**" (90×27 dp): 1.5 dp **#254D2D** stroke, fully rounded, list icon + text ≈18 sp #254D2D.
  - Arabic text right-aligned, ≈30 sp black (IndoPak font), with end-of-ayah marker ۝ (circle with Arabic-Indic numeral). In IndoPak Bismillah has an empty circle; in Uthmani Bismillah is numbered ١ and following ayat shift.
  - "**Translation:**" ≈20 sp Bold **#D2BE3F** (mustard gold).
  - Translation ≈19 sp black, line-height ≈ 1.6.
  - Action bar (24,368) 363×50 dp: inner rounded box (radius 14) slightly darker (#D5E6DE) with 1 dp #D0E0D8 stroke, three actions separated by 1 dp dividers: ▶ "**Play**", ⧉ "**Copy**", ➦ "**Share**" ≈18 sp **#719585** with icons 20 dp same colour.
- Floating player (16,751) 379×69 dp (+ label), fill **#CBDDD0** (~90 % opaque), radius ≈ 28 dp, overlapping content:
  - "**1.0x**" speed ≈20 sp Bold black left → popup menu (dark #2E3A30 rounded 10 dp) with 0.5x, 0.75x, **1.0x** (boxed white 1.5 dp), 1.5x, 2.0x.
  - ⏮ previous (outline double-chevron, #8AA394 light), ▶/❚❚ play-pause in 61 dp **#254D2D** circle (white glyph), ⏭ next; 🔁 repeat (black) at right – tap toggles with toast "**Repeat mode is off.**" / on (icon crossed when off). Default ON.
  - Reciter name "**Rashid Al-Afasy**" ≈18 sp Medium #254D2D centred under the controls.
  - Audio is **streamed** (needs network); first play may take several seconds; an ExoPlayer "dead thread" bug occasionally prevents playback until the surah is reopened. No ayah highlighting/auto-scroll unless "Auto Scroll while playing" is ON.
- Ayah **Copy** → copies "<Arabic>\n<translation>" and toast "**Text copied to clipboard**".
- Ayah **Share** → Android Sharesheet (ChooserActivity) with text "<Arabic>\n<translation>" (e.g. "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيمِ\nIn the name of Allah, the Entirely Merciful, the Especially Merciful").
- **Tafseer** → full-height bottom sheet (white, radius 16 top): title surah name ≈20 sp Bold, "Chapter:1 Verse:1" ≈16 sp, close ✕ 24 dp right; scrolling Tafsir Ibn Kathir (English) text ≈17 sp black with embedded Arabic. Tafseer languages: English, Arabic, Bengali, Urdu, Russia.
- **Reading mode** (`A25`): same chrome; each ayah is only an Arabic card (no tafseer/translation/actions).
- **Dark green theme** (`A27`): page **#224426/#315337**, cards **#2C4A32**, Arabic & text white, "Translation:" #D2BE3F, player translucent dark green with white controls.
- Translation language change applies after reopening the surah (bug: not live).

### A.4 Display settings sheet – `A13_quran_all_settings_sheet.png/.xml`, `A13b…`, `A13c…`, `A14_translation_language_picker.png/.xml`, `A14b…`, `A14c_tafseer_language_picker.png/.xml`, `A26_quran_settings_uthmani_preview.png`
Full-height bottom sheet, white, top radius ≈ 20 dp. Header "**Display**" ≈22 sp Bold centred, ✕ close right.
- "Mode" (as wizard step 1).
- "Select your Language": rows (fill **#ECF1F2**, radius 16, 52 dp): "**Tafseer Languages**" … "English ▾" and "**Translation Languages**" … "English ▾" (value ≈15 sp #4C7056). Tapping opens a popup list with checkboxes (single selection): Translation – English, Indonesian, Bengali, Turkish, Urdu, Russia; Tafseer – English, Arabic, Bengali, Urdu, Russia.
- "Show": rows "**Translation**" + switch (default ON) and "**Auto Scroll while playing**" + switch (default OFF).
- "Themes" (4 thumbnails, In Use/Use).
- "Choose font" IndoPak | Uthmani.
- "Font Settings": live preview card (#ECF1F2, radius 16): Bismillah Arabic ≈26 sp, Urdu line "شُروع اَللہ کے پاک نام سے جو بڑا مہر بان نہايت رحم والا ہے" in **#3C9A5F** green ≈15 sp, English "In the name of God, the merciful and compassionate" ≈18 sp; then the two size sliders.

### A.5 Reciters (Qari list) – `A20_quran_audio_button.png/.xml`, `A20b_reciters_bottom.png`
- Full-height sheet/screen, white; header "‹ **Reciters**" ≈22 sp Bold.
- 2-column grid, tile ≈ 142×142 dp photo (radius 10, stroke 1.5 dp #B9C7BE; selected stroke 2 dp gold #D2C28B with green ✓ badge top-right); name below ≈18 sp Medium black centred (2 lines max). Premium reciters show a small gold crown badge bottom-right of the photo.
- **Full Qari list (13)**: 1 Rashid Al-Afasy (free, default) · 2 Ahmad bin Ali Al-Ajmi 👑 · 3 Ali Al-Hudhaify 👑 · 4 Saad Al-Ghamdi 👑 · 5 Saud Al-Shuraim 👑 · 6 Abdul Basit 👑 · 7 Abdul Rahmaan Al-Sudais 👑 · 8 Nasser Al-qatami 👑 · 9 Yasser Ad-Dussary 👑 · 10 Maher Al-Muaiqly 👑 · 11 Siddiq Al-Minshawi 👑 · 12 Muhammad Abdul-Kareem 👑 · 13 Hani Abdullah Ar-Rafai 👑. Tapping a premium reciter → Premium dialog (UNSURE – not tapped).
- No download option observed (streaming only).
---

## 6. AI Learning (Quran Learning / Qaida Learning / Progress) – reached from Home "Learn & Grow"
### 6.1 Quran Learning list – `42_quran_learning_main.png/.xml`
- White. Top bar: back ‹ (16,48) black 24 dp; "**Quran Learning**" ≈22 sp Medium black; right: search 🔍 24 dp black, a small light-green rounded square (48 dp, #E6F4EA) with a "bookmark-note" icon (saved/recent – UNSURE), token pill (51×48 dp): stroke 1.5 dp #67AD5B, fill #E8F5E9, coin icon 18 dp + count "**11**" ≈20 sp Bold.
- "Learning Reminders" card (16,108) 379×~50 dp: fill #F7FAF7, stroke 1 dp #E3E8E3, radius 16; alarm-clock-calendar 3-D icon in white circle (40 dp); title ≈17 sp Medium; "Stay on track with timely learning reminders." ≈15 sp #777; button "**Set Reminder**" (95×48 dp touch, visual 95×34) fill **#5E9C4B**/#4E8A3F, white ≈15 sp, radius 8 → §6.6.
- 1 dp divider #E3E8E3.
- Surah lesson cards (x 16–395, 92 dp pitch, card ≈ 76 dp): pastel horizontal gradient fill per row (green #E3F1DC→white, blue #E6ECFA, purple #F1E8FA, orange #FCEDE3, teal #E3F0F2, pink #F8E6EE, amber #FBF2DE … repeating), radius 12, coloured curved corner accent at bottom-left (same hue, darker); silver medallion number badge (40 dp) "01"; faint flower watermark; English name ≈20 sp Medium black + "7 Ayahs" ≈15 sp #888; Arabic "سورة الفاتحة" ≈22 sp in the row's accent colour (green #4C8A3A, blue #2E4FA8, purple #6A2FA3, orange #E06A2A, teal #2D7F86, pink #B03A70, amber #B7862A); right pill "**Start**" (fill = 15 % tint of accent, text accent ≈20 sp Medium, radius 20). 114 surahs.

### 6.2 Token rules
- Each AI pronunciation check consumes tokens; free daily tokens shown in the pill (11 on day 1). "Go Pro" = unlimited.

### 6.3 Daily tokens sheet – `40_quran_learning_tokens_sheet.png/.xml`, `41_*`, `41b_tokens_days_end.png`, `video/40_tokens_sheet_confetti.mp4`
- Opens automatically the first time Quran Learning is opened each day. Modal bottom sheet, white, top radius ≈ 28 dp, drag handle 44×4 dp #C4C4C4.
- **Confetti animation** looping (coloured rectangles, stars, dots: red #E53935, pink #F095C6, cyan #4FC3F7, green #81C784) behind content.
- Hero: 3-D stack of green/gold tokens with "FREE" tag (140×140 dp).
- "**Tokens Available Today**" ≈22 sp Bold #091214; "Keep learning with your tokens, or go PRO for unlimited AI Learning." ≈17 sp **#7C7C7C** centred.
- Token count box (16,~283) 363×52 dp: white, stroke 1.5 dp #E0E0E0, radius 16; centred coin + "**11**" ≈20 sp Bold; orange tilted badge "**FREE**" (#ECAB3F, black Bold) on the top-right corner.
- Horizontal day cards 93×52 dp?? (px 84–539 → **≈ 92×52 dp**, radius 16): current day card stroke 2 dp #254D2D, fill **#E6ECE8**, "Day 1" ≈15 sp, green ✓ circle top-right, coin + "11" Bold; future days greyed (#F8F8F8 fill, text #BDBDBD, grey ✓). Values Day1 11 … Day7 41 (+5/day).
- Buttons 174×52 dp, radius 26: "**Continue**" (fill #F3F6F4, stroke 1.5 dp #94AA9A, text #254D2D Bold ≈20 sp) and "**Go Pro**" (fill #254D2D, white Bold) → Premium.
- Native ad below.

### 6.4 Surah practice – (Quran Learning "Start") – same practice UI as Qaida §6.5 but with ayah words (UNSURE – not opened).

### 6.5 Qaida Learning – `C10_qaida_learning.png/.xml`, `C10b_qaida_list_bottom_fab.png`, `C11_qaida_lesson_intro_bouncing_sounds.png/.xml`, `C12_qaida_lesson_content.png/.xml`, `C13_qaida_tile_tap.png`, `C14_qaida_practice.png/.xml`, `C15_perm_record_audio_prompt.png/.xml`, `C16…C21`
- List: top bar "‹ **Qaida Learning**" + note icon + token pill; Learning Reminders card; rows (white card, stroke 1 dp #EEE, radius 12, 74 dp): coloured rounded-square book icon (pastel), title ≈18 sp Medium ("Letter Basics", "Letter Forms", "Short Vowels", "Vowel Extensions", "Tanween Basics", "Silent Letters", "Double Sounds", "Bouncing Sounds", "Sound Lengthening", "Soft Sounds", "Strong Sounds", "Alif Pronunciation", "Laam Pronunciation", "Raa Pronunciation", "Nasal Sounds" + 1 more = 16 lessons per Progress screen), subtitle "Single Lesson" ≈14 sp #888, tinted "Start" pill. Scroll-to-top FAB: light-green circle 48 dp with "⌃⌃".
- **Lesson Intro** (`C11`): top bar "‹ Lesson Intro"; title (e.g. "Bouncing Sounds") ≈20 sp Bold; bullet list of rules ≈17 sp; bottom "**Continue**" full-width green button.
- **Lesson grid** (`C12`): top bar "‹ Bouncing Sounds" + progress bar (thin grey) + book icon right; filter chips "All" (selected #254D2D fill white) · "● Correct" (green dot) · "● Intermediate" (orange dot) · "● Incorrect" (red dot) – chips white with 1 dp #E0E0E0 stroke, radius 20; 3-column grid of word tiles 116×116 dp (white, stroke 1 dp #E0E0E0, radius 16) with Arabic word ≈34 sp black.
- **Practice screen** (`C14`): big word card 379×~245 dp (cream #FBF9F3 with ornament edges, radius 20): Arabic ≈60 sp **#254D2D**, transliteration "'Adnin" ≈22 sp Bold #254D2D; speaker button (outline circle 32 dp) bottom-left "Listen"; "1/10" counter. Horizontal strip of word chips (120×109 dp; selected stroke 1.5 dp #254D2D fill #EEF3EF). Recording card (white, stroke 1 dp #E0E0E0, radius 20): green mic button 50 dp (#2E6B3A gradient) + "**Microphone Access Required**" / "Enable microphone access to record your voice." → tap → **RECORD_AUDIO system prompt** ("Allow Qibla Compass to record audio?" While using / Only this time / Don’t allow). After grant: "**Tap to Record Your Voice**" / "Speak clearly and tap again to stop."; recording: red stop button (#E53950), dotted level meter + timer "00:07", "Recording... Tap to Stop"; after stop: play ▶ / waveform dots / ✕ discard and "Your recording is ready. Tap Send Voice."; outlined brown pill "**Send Voice ▷**" (stroke #7A5A3A, radius 20) → uploads for AI scoring (not performed in this capture – privacy). Header icons: 🌐 language → "Select Languages" sheet (Arabic, English, Indonesian, Malay, Hindi, Turkish, Urdu; selected English stroke #254D2D); ⓘ → "Discover How AI Learning Works?" sheet with animated demo ("Record your voice and get instant feedback. Mistakes are highlighted to help you improve your pronunciation.").

### 6.6 Learning reminder – `C22_learning_set_reminder.png/.xml`
- White screen, back ‹; 3-D gold bell + calendar illustration; "**Schedule Your Learning Time**" ≈22 sp Bold; "For better progress, try learning at least 4 days a week." ≈16 sp #777.
- Cards (#F4F6F4 / white with 1 dp #EEE stroke, radius 16): "Reminder On/Off" switch (ON), "Notification – Allow notifications for reminders." switch (ON), "Repeat" with 7 day chips Mon…Sun (white, stroke #E0E0E0, radius 8, ≈13 sp), "Set Time ›" (subtitle "Set Time").
- "**Save Reminder**" full-width green button (radius 26).

### 6.7 Progress Tracking – `C30_progress_tracking.png/.xml`, `C31_progress_tracking_qaida.png/.xml`, `C32_progress_view_stats.png/.xml`
- Top bar "‹ **Progress Tracking**". Segmented chips "**Quran**" (selected #254D2D white) | "Qaida" (white, stroke #CCC), radius 20.
- Progress card (white, stroke 1 dp #E8E8E8, radius 16): green book icon + "Quran Progress" ≈18 sp **#254D2D**; "**0%**" ≈30 sp Bold + "Completed"; "0 of 6236 Ayahs" (Qaida: "0 of 16 Lessons") ≈14 sp #777; ring 80 dp grey #DDD with "0%" ≈26 sp; button "**View Stats →**" full width green gradient #3F8C48→#2B6B35, radius 12.
- "Learning Badges": empty card "No Badges Yet / Complete a lesson to earn your first badge."
- "Milestone Badges": vertical dotted timeline (yellow dots #F2E36B) with cards: First Steps – Begin your Qaida journey (active, colourful badge); Quran Path – Begin your Quran journey; Qaida Mastery – Complete your Qaida learning; Quran Milestone – Complete your Quran learning (greyed).
- **Stats** (`C32`): "‹ Qaida Progress" + month dropdown "November 2026 ▾" (outlined pill); purple gradient hero (#3A1C8C→#1C0F4A) "Overall Progress / 0% / Great Job! Keep it up." with book-on-rehal art; "Overview" 3 cards (Lessons Learned 0 – This Month; Avg. Accuracy 0% – Keep Practicing (orange); Mistakes 0 – Keep Practicing (red)) each with round tinted icon; "Learning Activity" bar chart Wed…Tue (empty grey pill bars, today label green).

## 6B. Learning tab (bottom nav) – Islamic Learning courses
Screens: `B00_learning_loading.png`, `B01_learning_tab_top.png`, `B02_learning_tab_scrolled.png/.xml`, `B03_learning_tab_bottom.png`, `B10…B18`.
- Top bar 0–92 dp **#254D2D**: back ‹ white + "**Islamic Learning**" white ≈22 sp Bold. Loading state: centred indeterminate circular spinner #254D2D (36 dp) on white.
- "**All Courses**" ≈22 sp Medium black (16,124).
- Course card (16,165) 379 dp wide, white, stroke 1 dp #CCCCCC, radius ≈ 16: banner image 359×93 dp radius 12 (dark-blue arch with golden lattice window); title UPPERCASE ≈20 sp Bold **#362E13** (dark brown); description ≈16 sp black; 1 dp divider; footer: level & duration ("Beginner - 3 weeks") and "5 chapters - 29 lessons" ≈14 sp **#295632**; button "**Check now**" 180×48 dp, #254D2D, radius 10, white ≈17 sp. Coming-soon course: banner overlaid with "COMING SOON" white ≈20 sp Bold, button "**Visit later**".
- Courses: 1) **INTRODUCTION TO ISLAM - A JOURNEY TO FAITH** – "Learn the essentials of Islam to deepen your understanding and faith." Beginner - 3 weeks, 5 chapters - 29 lessons. 2) **DEEP DIVE INTO THE QUR’AN** – "Explore the profound meanings and teachings of key Surahs in the Qur'an, unlocking deeper understanding and spiritual connection." Intermediate - 4 weeks, 7 chapters - 50 lessons. 3) **THE LIFE OF PROPHET MUHAMMAD (PBUH)** (Coming soon) – "Discover the life, character, and leadership of Prophet Muhammad (PBUH) and learn how to apply his teachings in daily life." Intermediate - 4 weeks, 8 chapters - 54 lessons.
- Native ads interleaved between courses.
- **Course detail** (`B10`): green header with mosque photo (top 0–~435 dp, bottom radius 20): back ‹; title ≈22 sp Bold white; description; 1 dp white 30 % divider; key-value rows (white ≈16 sp, values Bold right): Level – Beginner; Time to complete – 3 weeks; Chapters – 5; Lessons – 24; Condition to pass – You have pass 85% test; divider; "**500k+**enrollers" and "by**Admin**". Body: "About Course" ≈20 sp Bold + paragraph; "What you get after this course" + dash list. Sticky bottom "**Enroll now!**" button (#254D2D, radius 16, white ≈18 sp).
- **Lesson reader** (`B11`, `B12`): white; top bar back ‹, centred lesson title "What is Islam?" ≈18 sp Bold, right black circle 40 dp with white house icon (→ course home). Body: hero image (radius 16) + rich text (H2 ≈20 sp Bold, body ≈16 sp, bullet lists). Bottom bar (#F2F2F2): left green square ‹ (32 dp, radius 6), centre page pill "1/2" (grey circle 40 dp), right green square › — on the last page replaced by "**Take Quiz**" (#254D2D, radius 6).
- **Quiz** (`B13…B17`): image, question ≈20 sp Bold; options as pills (fill **#E6E6E6**, radius 24, 46 dp) with letter badge (A/B/C/D in 20 dp #254D2D circle, white letter); selected = fill **#CEE6D5** stroke 1.5 dp #254D2D; after "Check Answer": correct option fill **#254D2D** white text; wrong selection fill **#F3C0BF** stroke #E57373; bottom feedback panel full width: incorrect = **#F3C0BF** "Incorrect" + explanation Bold; correct = **#D5E7DF** "Correct" + explanation; button changes "Check Answer" → "**Next Quiz**" (#254D2D full width, radius 10). After the quiz the next lesson opens.
---

## 7. Feature screens (Home grid / Features sheet)

### 7.1 Duas – `D01_duas_categories.png/.xml`, `D01b_duas_categories_bottom.png`, `D02_duas_travel_list.png/.xml`, `D03_dua_detail.png/.xml`, `D04_duas_search.png/.xml`
- Top bar #254D2D: ‹ "**Duas**" white ≈22 sp Bold; 🔍 white right (tap → inline search EditText replaces title, filters).
- "Dua of the day" card (16,~112) 379×~140 dp, fill **#254D2D**, radius ≈ 20: "Dua of the day" **#D6C56A** gold ≈20 sp; left: 3-D open Quran on rehal in a dark circle (80 dp, #3E6448); right: Arabic dua white ≈18 sp (e.g. "اَلْحَمْدُ لِلَّهِ الَّذي أَحْيَانَا بَعْدَ مَا أَمَاتَنَا وَإِلَيْهِ النُّشُورُ"); white circular "→" 24 dp bottom-right.
- Native ad.
- Category grid 3 columns: tile ≈ 113×120 dp white, radius 16, elevation ≈ 2 dp (#E0E0E0 shadow); round **#254D2D** icon circle 60 dp with gold/white illustrated glyph; label ≈16 sp Medium black centred (2 lines).
- **12 categories**: All · Morning and Evening · Home And Family · Food And Drink · Joy And Distress · Travel · Prayer · Praising Allah · Hajj And Umrah · Good Etiquette · Nature · Sickness And Death.
- Category list (e.g. "Travel Duas"): white rows 379×~55 dp, stroke 1 dp #E0E0E0, radius 12, elevation 1; left icon (raised hands in a #ECF1F2 circle, 30 dp) + purpose ≈17 sp #333 ("When mounting an animal or any transport", "For traveling").
- Dua detail: top bar title = purpose (≈20 sp, may wrap 2 lines). Card(s) fill **#ECF1F2**, radius 16: number badge (32 dp #254D2D circle, white "1"); Arabic ≈24 sp black right-aligned (line-height 1.6); transliteration ≈20 sp black; translation ≈16 sp **#4A4A4A**; "Reference: Abu Dawud 3/34, Sahih At-Tirmizi 5/501" ≈14 sp **#254D2D**. Multiple duas = multiple numbered cards.

### 7.2 Tasbih – see §5.

### 7.3 Ayaat – `D10_ayaat.png/.xml`
- Top bar #254D2D "‹ Ayaat". Full-bleed background: purple/blue night-sky space art with planets and mountains.
- Centre card (60,~100) 291×~300 dp: translucent white 25 % (frosted), stroke 2 dp gold **#E1CA7A**, radius 20: "Surah: Yousaf" white ≈20 sp; Arabic white ≈18 sp; English white ≈19 sp; footer separator + 3 icon buttons (copy, speaker, share) white 24 dp. Left/right white chevrons outside the card (prev/next).
- Thumbnail strip below: 4 visible small cards (≈ 80×120 dp, white/grey #EEE, stroke #CCC, selected stroke white 2 dp) showing Arabic snippets; "**1/35**" counter white ≈20 sp. 35 ayaat.
- Share text format: "Surah: Yousaf\n\nAyat: 86\n\n<Arabic>\n\n<English>\n\n<Urdu>\n\nI am using this android application …" (tail truncated in preview – contains app promo + Play link; UNSURE exact tail).
- Copy → toast; speaker → plays audio/TTS (UNSURE).

### 7.4 Names (Asma ul Husna) – `D20_names_99.png/.xml`, `D22_names_playing.png`
- Top bar #254D2D: ‹ "**Asma ul Husna**" white ≈22 sp; right "Scroll" ≈17 sp white + switch (ON = auto-scroll with audio; white thumb on green track).
- List rows (x 19–392 dp, 62 dp, gap 15): fill **#EBF1F2**, radius 12: number in **#254D2D** circle 28 dp (white ≈16 sp); transliteration ≈20 sp Bold black ("Ar-Rahmaan"); meaning ≈14 sp black ("The Beneficent"); Arabic right ≈24 sp black ("الرَّحْمَنُ").
- Bottom mini player (white): "Asma ul Husna" ≈17 sp Medium; ⟲5 / ▶ (36 dp #254D2D circle) / ⟳5 buttons; seek bar (#254D2D thumb, grey track) with "0:00" … "3:23". Audio streams the 99 names recitation; list auto-scrolls when Scroll is ON. No detail screen on tap.
- 99 names standard list (1 Ar-Rahmaan, 2 Ar-Raheem, 3 Al-Malik, 4 Al-Quddus, 5 As-Salam "The Source of Peace, The Flawless", 6 Al-Mu’min "The Infuser of Faith", 7 Al-Muhaymin "The Preserver of Safety", 8 Al-Aziz "All Mighty", 9 Al-Jabbar "The Compeller, The Restorer", …).

### 7.5 Mosques (Nearby Mosques) – `D30_mosques_tap_premium_popup.png`, `D31_mosques.png/.xml`, `D31b_mosques_loaded.png/.xml`, `D32_mosques_map.png/.xml`
- Top bar #254D2D "‹ Nearby Mosques" + map-pin icon (39 dp, white) right.
- List of nearby mosques (Google Places, UNSURE); in this capture **empty state**: grey mosque glyph (≈ 76 dp, **#C2CEC5**) + "**No Nearby Mosque available**" ≈18 sp #555 centred near top.
- Map icon → Google Map centred on user (red default marker), no mosque markers shown.

### 7.6 Kalmas – `D40_kalmas.png/.xml`, `D41_kalma_detail.png/.xml`
- Top bar #254D2D "‹ Kalmas". 6 banner cards (x 12–396 dp, ≈ 90 dp, radius 16) each with a different illustrated mosque/desert background (1 pink→purple dusk, 2 green, 3 purple night, 4 mint with camels, 5 brown desert, 6 mauve); left translucent number circle 28 dp; centred title white ≈20 sp Bold + subtitle ≈12 sp white:
  1 Kalema Tayyab (Word of Purity) · 2 Kalema Shahadat (Word of Testimony) · 3 Kalema Tamjeed (Word of Glorification) · 4 Kalema Touheed (Word of Oneness) · 5 Kalema Astaghfar (Word of Seeking Forgiveness) · 6 Kalema Radd-e-Kufar (Word of Rejecting Disbelief).
- Detail: dark purple illustrated card (16,~100) 379×~330 dp radius 20: title white ≈22 sp Bold; "🔊 Audio" outlined white pill (stroke 1 dp, radius 16) right; Arabic white ≈22 sp; transliteration white ≈17 sp; "**Meaning**" **#F2B526** ≈18 sp; meaning white. Below on white: "**Intro & Description**" ≈18 sp Bold + paragraph ≈17 sp. Bottom "**Next**" full-width green button (radius 28) → next kalma.

### 7.7 Daily Deen – `D50_daily_deen.png/.xml`, `D51_daily_deen_next.png`, `D52_daily_deen_text_toggle.png`
- Full-screen illustrated background (misty green palm-forest mountains), status bar transparent; close ✕ (white 50 % circle 32 dp) top-right.
- Text panel (16,~130) 379×~180 dp: white 75 % frosted, radius 20: Arabic ≈20 sp black (right-aligned) + English ≈17 sp black.
- Right vertical action column (white icons + labels ≈12 sp): 🔊 Sound, ▤ Text (toggle text panel), ➦ Share.
- Bottom: reference "An-Nahl 16-97" white ≈18 sp Medium; audio progress "0:00" + seek bar (white thumb, white 50 % track). Audio recitation auto-plays. Reel-style (one item observed; vertical swipe did not change item – UNSURE more items daily).

### 7.8 Azkar – `D60_azkar.png/.xml`, `D61_azkar_scrolled_landscape.png` (captured in landscape – this activity is not orientation-locked; the device rotated)
- Top bar #254D2D "‹ Azkar" + tab row on green: "**Morning Azkar**" (selected, white with 3 dp white underline) | "**Evening Azkar**" (white 60 %).
- Cards fill **#ECF1F2** radius 16: count badge (32 dp #254D2D circle, white number = repetition count/ordinal "2", "3", …), Arabic ≈22 sp black, translation ≈18 sp black (no transliteration observed), reference (UNSURE). Long scrolling list (~15+ items).

### 7.9 Hajj & Umrah – `D70_hajj_umrah.png/.xml`, `D70b_hajj_umrah_scrolled.png/.xml`, `D71_hajj_track_steps.png/.xml`, `D71b_*`, `D72_hajj_step1_detail.png/.xml` (landscape capture)
- Top bar #254D2D "‹ **Hajj & Umrah preparation**".
- "Track My Hajj" hero card (peach #FCE4D6, radius 16): title ≈26 sp Bold **#3B2F12**; "Mark each step of your sacred journey with purpose, reflection, and heartfelt gratitude." ≈13 sp; dark-green "Track Now" button (radius 20); 3-D Kaaba art right.
- Guide cards (photo, radius 16, dark overlay, white caps title top-left ≈14 sp Bold): **UMRAH INTRO GUIDE**, **HAJJ INTRO GUIDE**, **PREPRATION FOR HAJJ** (sic), **HOW TO PERFORM HAJJ**.
- **Track My Hajj** (`D71`): vertical zig-zag timeline (central grey 2 dp line, nodes 10 dp; completed node #254D2D), step pills alternate left/right (white, stroke 1 dp #CCC, radius 30, ≈ 170×38 dp) with number + name + small illustration; current step pill stroke 1.5 dp #254D2D, with animated pointing hand. Steps observed: **1. Enter State of Ihram, 2. Tawaf al Qudoom, 3. Safa & Marwa, 4. Go to Mina** (further steps above, UNSURE – likely Arafat, Muzdalifah, Rami, Qurbani, Halq, Tawaf al-Ifadah, Farewell Tawaf).
- Step detail (`D72`): top bar = step name; Google **satellite map** with red marker at the location; dark-green banner "**Explore the Street View**" with diagonal gold "PRO" ribbon (premium); white bottom sheet (top radius 20, handle "⌃") with photo and step text.

### 7.10 Zakat Calculator – `D90_zakat_calculator.png/.xml`, `D91_zakat_calculate_form.png/.xml`
- Intro: illustration + "**Welcome\nZakat Calculator**" ≈30 sp Bold centred; "Calculate your Zakat accurately based on Nisab and Islamic guidelines." ≈15 sp #555; buttons full width 379×51 dp radius 12: "**Learn Zakat with AI Deen**" (outlined/secondary) — "OR" — "**Calculate Your Zakat**" (#254D2D).
- Form "Set Up Your Zakat Calculation": "What is Nisab?" + explanation ("Nisab is the minimum amount of wealth a Muslim must possess for one lunar yer for Zakat to be mandatory." sic); "Choose Nisab Based On" radios: **Gold (7.5 Tola – Fixed)** / **Silver (52.5 Tola – Fixed)**; "Currency" … "USD" (picker); "Price per Tola" EditText hint "Enter current market price per Tola"; bottom "**Calculate Nisab**" button. (Further steps not captured – UNSURE.)

### 7.11 Features sheet (Home "More") – `D80_more.png/.xml`
- Bottom sheet white, top radius 20, title "**Features**" ≈20 sp Bold centred, ✕ grey right.
- Grid 5 columns of the same green tiles (48 dp, stroke 1 dp lime #C9E39B around tile, label ≈15 sp): Duas, Tasbih, Ayaat, Names, Mosques, Kalmas, Daily Deen, Azkar, **History Places**, Hajj Umrah, **Zakat Calculator**.

### 7.12 AI Deen chat – NOT CAPTURED
Entry points: Home "Ai Deen › Explore Now", Zakat "Learn Zakat with AI Deen", onboarding page 2. The device lost network before it could be opened (see §F). UNSURE: layout. Build as a chat screen using the app's tokens (green #254D2D bubbles for user, #F4F4F4 for AI) – placeholder only.

### 7.13 Live Makkah / Madinah – NOT CAPTURED
Home "New feeds" cards (Makkah Live Broadcast / Medina Live Broadcast, red LIVE badge). Player type (YouTube vs in-app ExoPlayer/HLS) could not be verified (offline). UNSURE.

### 7.14 Islamic History Places gallery – PARTIAL
Home section cards (Kaaba (Makkah, Saudi Arabia), Zamzam Well (Makkah, Saudi Arabia), …) and "View more" / Features "History Places" tile exist; gallery & detail screens not opened (offline). UNSURE.

---

## 11. Premium (paywall) – observe only, never purchase
### 11.1 "50% OFF" offer dialog – `19_dialog_premium_50off.png/.xml`, `63_qibla_locked_style_tap.png`, `D30_mosques_tap_premium_popup.png`
- Auto-shown on Home (first session and periodically when returning to Home), and when tapping any locked item (compass designs, premium reciters, PRO street view…).
- Dialog (21,161) 370×462 dp, radius ≈ 24 dp, fill **#0F2715** (very dark green). Top ≈ 45 % = photo (mosque domes & minarets at sunset, fading into the dark green).
- "**50% OFF**" (117,277) ≈52 sp ExtraBold with **gold metallic gradient** (#F6E7A6 → #C9A85A → #8E6F2A), small gold ornament divider below.
- "ONLY**Rs 1,100.00**/WEEK" ≈22 sp Bold gold (#D8C27A) — "ONLY" and "/WEEK" same style (price from Play Billing).
- "**Unlock Spiritual Peace with Barakah**" ≈19 sp SemiBold **#92F588** (bright green).
- 4 feature rows with white line icons 24 dp (ADS crossed, wifi-off, compass-themes, audio-wave) + text ≈19 sp **#E5E5E5**: "No Ads, focus on prayer", "Offline Mode", "Multiple Qibla Themes", "Diverse Quran Reciters".
- "**Purchase Now!**" (45,520) 322×55 dp, radius ≈ 12, horizontal **gold gradient #C1A13E → #D2BA6A → #B99D43** with an animated shimmer sweep (`purchaseShimmerOverlay`), text #404040 ≈22 sp Bold.
- "*One-time offer. Donâ€™t close!*" (mojibake for "Don’t" in the original) italic ≈17 sp **#F7E6AD**.
- Close: white circle 32 dp with black ✕ centred below the dialog (190,639 dp).
### 11.2 Quit-offer confirm – `20_dialog_premium_quit_confirm.png/.xml`
- Shown when closing 11.1. Dialog 370×187 dp (#0F2714, radius 24): "**Are you sure to quit?**" white ≈26 sp Bold; "This sale off only appear this time only Donâ€™t miss your chance!" white ≈19 sp; (optional banner ad inside); buttons 159×38 dp radius 10: "**Yes**" (transparent, 1.5 dp white stroke, white text) and "**No**" (gold gradient + shimmer, #4B4B4B text). Yes → close both; No → back to offer.
### 11.3 Full Premium screen "QIBLA PRO" – `P01_premium_screen.png/.xml`, `P01_premium_screen_on_launch.png`
- Shown on app launch (sometimes) and from crown icon / "Get Premium" / "Other Plans" / "Go Pro".
- Full screen: top ≈ 55 % art – crescent moon & mosque in an ornate paper-cut arch frame (gold/beige), fading to **#0F2714**; close ✕ (white circle 32 dp, 48 dp touch) top-right (346,32).
- "**QIBLA PRO**" (68,267) ≈40 sp serif display, gold gradient, with gold diamond glyph before it.
- "Unlock Spiritual Peace with Barakah" #92F588 ≈19 sp; 4 feature rows (#E5E5E5) as 11.1.
- Plan cards (24,525) 363 dp wide:
  - **Yearly Package** card 363×65 dp, fill **#FFFEE3** (cream), stroke 2 dp gold #C7AF57 (selected), radius 12: crown coin icon 25 dp; "Yearly Package" ≈19 sp Bold black; "Just **Rs 3,350.00** per year" ≈15 sp; right "**PKR64.42**/week" ≈15 sp Bold; tag "3 days trial" (gold small pill); top-right badge "**Saved 94%**" (gold #C9A64F, white ≈11 sp, top-right corner tab).
  - **Weekly Package** card 363×61 dp, white, stroke 1 dp #DDD: "Weekly Package" + "Rs 1,100.00 /week".
- CTA (24,694) 363×55 dp gold gradient (#C0A03D → #D2BA6A): "**Continue**" ≈20 sp Bold black + "Get 3 days free and subscribe" ≈16 sp.
- "Subscription auto re-new. Cancel anytime" ≈15 sp white 80 %; links "Terms of Service | Privacy Policy" underlined ≈15 sp **#92F588**-ish/white.
- Prices are Play Billing localized (PKR here). Home strip shows "PKR2,800 / year" (a different SKU/offer) – UNSURE.

---

## F. Offline behaviour (observed when the device lost Wi-Fi – `F01_no_internet_dialog.png/.xml`, `F02_offline_home_dialog_again.png`)
- On Home, when there is no active network, a **non-cancellable dialog "No Internet"** appears (Back and outside-tap do nothing): white card 300 dp wide, radius ≈ 20; illustration of a man in white ghutra (80 dp); "**No Internet**" ≈26 sp Bold black; "Application requires an internet connection to function." ≈17 sp #333; "Upgrade to premium allows offline usage." ≈17 sp gold (#BDAD75 / ~#C9A64F); button "**Go to Settings**" full-width 300×~50 dp #254D2D, radius 12, white ≈19 sp → opens Android **Wi-Fi settings**. On return the dialog shows again.
- Behind the dialog Home still renders prayer times computed **offline** from the cached location ("Next prayer - Zuhur 11:51 AM", "Islamabad"), and the compass works (sensor-only).
- Therefore for free users all features are effectively blocked offline (Quran text, Duas, Tasbih included) – premium promises "Offline Mode".
- Online-only by nature: Quran audio (streamed), Asma-ul-Husna audio, Daily Deen audio, Learning courses (loaded from network – spinner), AI pronunciation, AI Deen, Live streams, Mosques (Places), Maps tiles, ads.
- Quran Arabic/translation/tafseer appear **bundled** (instant load, multiple languages switch without download) – UNSURE (could not verify offline because of the blocking dialog).
- Per-feature offline test with `svc wifi/data disable` could NOT be done: the device's Wi-Fi became unavailable (Wi-Fi service reports "disabled" and does not re-enable; no SIM) – see unreached list.

---

## G. Global design tokens
### G.1 Colour palette
| Token | Hex | Use |
|---|---|---|
| primary / brandGreen | **#254D2D** | headers, top bars, buttons, selected tabs, icons, tiles, titles |
| primaryDarkBg | **#0F2715** (#0F2714) | premium dialogs/screens background |
| primarySwitchThumb | #244B2C | switch thumb ON (track #89A090) |
| onPrimary | #FFFFFF | text/icons on green |
| successGreen | **#67AD5B** | ticks, checked circles, progress fill, today arc |
| nextPrayerStroke | #7BB972 (+glow #EAF4EA) | next prayer row border |
| lime | **#8AC441** | "Learn & Grow" title |
| neonGreen | **#92F588** | premium "Unlock Spiritual Peace…" |
| gold | **#E1CA7A** (gradient #C1A13E → #D2BA6A → #B99D43) | crown, Done/Purchase buttons, ayaat card stroke, + buttons |
| goldLight | #F6E4AA / #FBF0A7 | ring gradient ends |
| goldText | **#D2BE3F** | "Translation:" label |
| goldLabel | #D6C56A | "Dua of the day", NEXT PRAYER |
| cream | #FFFEE3 | yearly plan card |
| bgLanguage | #D9E4E4 | language screen background |
| cardLanguage | #E4ECED / selected #BACFCF | language rows |
| surfaceGrey | **#F4F4F4** | setting rows / prayer toggle rows |
| surfaceGrey2 | #F7F7F7 | surah list rows |
| surfaceMint | **#ECF1F2** | search field, dua cards, icon circles, tasbih lower bg |
| surfaceMint2 | #E5ECED | tasbih upper bg |
| eventCard | #EEEEEE | Islamic events |
| permissionCard | #E8E8E8 | permission sheet cards |
| strokeLight | #E0E0E0 | prayer rows, tiles |
| strokeGreenGrey | #B9C7BE | check-in card / circles |
| dayCellStroke | #EEEEEE | calendar cells |
| readerBg | #DBEAE1 (bar #E4F1E8, card #DBEBE4, player #CBDDD0) | Quran reader light-green theme |
| readerDark | page #224426, card #2C4A32 | Quran dark theme |
| textPrimary | **#091214** / #000000 | body text |
| textSecondary | #5C5E5E, #666666, #757575 | hints, day names |
| textMuted | #868686 | inactive nav, search hint |
| textGrey | #9E9E9E / #B1B1B1 | small dates, method angles |
| textGreenMuted | **#4C7056** | prayer names (italic), event dates, "Today" |
| actionGreenMuted | #719585 | Play/Copy/Share in reader |
| heart / errorRed | **#E15241**; red badge **#EA3323**; LIVE #E53935; NEW ribbon #E05748/#B13634 | |
| quizWrong | #F3C0BF; quizCorrect #D5E7DF; quizSelected #CEE6D5 | |
| learnPurple | #5729A6 → #2C1177 | Qaida card |
| learnGreen | #367543 → #24552F | Quran Learning card |
| promoGold | #D7BE6C → #F1E3A6 → #C9A64F | "Don t Like Ads?" strip |
| navDivider | #E8EDEA (2 dp) | bottom nav top line |
| headerDivider | #3D6E48 | home header rule |
| locationChip | white 70 % on green (#BBC9BF), text #1A361F | |

### G.2 Typography (estimated)
- Family: Nunito-like rounded sans for app UI (weights Regular 400, Medium 500, SemiBold 600, Bold 700, ExtraBold 800). Roboto in system dialogs/pickers and some feature screens (Dua detail translation, tafseer, event dialogs).
- Scale (sp): Display 52 (50% OFF), 44 (home time), 40 (QIBLA PRO); H1 28 (screen titles "Compass", "Quran"); H2 24–26 (onboarding titles, surah name, card titles); H3 22 (top-bar titles, section heads); Title 20 (row titles, buttons); Body 17–18; Body-small 15–16; Caption 13–14; Micro 11–12.
- Arabic: Quran reader ≈30 sp (IndoPak default; Uthmani option), Duas/Azkar ≈22–24 sp, Asma ≈24 sp, Qaida tiles 34 sp, practice word 60 sp. Arabic colour black (or #254D2D for headings/surah names).
- Header serif "QIBLA COMPASS" (splash) and "QIBLA PRO" (paywall): Times/Playfair-like serif caps.

### G.3 Spacing & sizes
- Screen side margin: 16 dp (most), 23–24 dp (settings, language), 8–10 dp (home cards, Quran list).
- Card gaps: 12 dp (language, ayah cards), 16 dp (surah list, settings), 7 dp (prayer rows/events).
- Top bars: 84–100 dp incl. status bar (≈ 56 dp content) – colour #254D2D; back chevron 24 dp icon in 39–48 dp touch; title starts x 55 dp.
- Bottom nav: **71 dp**, centre button **58 dp**; icons 24 dp.
- Buttons: primary filled 50–55 dp tall full width (radius 12); small 34–48 dp; pills fully rounded.
- Switch: Material SwitchCompat 48×27 dp.
- Icon tiles: 48 dp, radius 10.

### G.4 Corner radii
- 4 dp (map buttons, Done button), **10 dp** (language rows, Start button, tiles, dialogs headers), **12 dp** (setting rows, prayer rows, primary buttons, check-in card), **16 dp** (home cards, course cards, Duas tiles, widgets), **20 dp** (content sheet top, ayah cards, surah rows, dialogs), 24 dp (premium dialogs), full/pill (chips, search, Start pills).

### G.5 Elevation / shadows
- Mostly flat (fills + 1 dp strokes). Visible shadows: ayah cards (~4 dp soft), Duas category tiles (~2 dp), map buttons (~2 dp), tasbih inner circle bevel, bottom sheets (scrim 60 % black).

### G.6 Motion
- Splash: compass icon scale/slide + sunburst spinner. Language finger: pulse/fade loop (~1 s). Token sheet: looping confetti. Purchase/No buttons: shimmer sweep (~1.5 s loop). Home: subtitle marquee; live compass rotation. Tasbih ring: arc animates per tap. Learning: circular indeterminate spinner #254D2D.

---

## H. Navigation map
```
Splash ─► [first run] Notif prompt ─► Language ─► Onboarding(3) ─► Prayer notification setup ─► Home(+permission sheet)
Home
 ├─ Location chip ─► location refresh
 ├─ Support face icon ─► (not opened, UNSURE: feedback/contact)
 ├─ Crown ─► Premium (QIBLA PRO)
 ├─ Gear ─► Settings ─► Prayer Methods | Prayer Time Alert | Languages | Manage Subscription(Play) | Share App(intent) | Privacy Policy(browser) | DND time pickers
 ├─ View time / Explore Time ─► Prayer tab
 ├─ Compass expand ─► Compass tab
 ├─ Check in ─► (state)   Learn&Grow ─► Quran Learning ─► token sheet / lesson practice ; Qaida Learning ─► lesson intro ─► grid ─► practice(mic)
 ├─ Progress Tracking ─► Progress ─► View Stats
 ├─ Grid: Duas ─► category ─► list ─► detail | Tasbih | Ayaat | Names | Mosques ─► map | Kalmas ─► detail | Daily Deen | Azkar | Hajj Umrah ─► Track My Hajj ─► step detail / guides | More ─► Features sheet (+History Places, Zakat Calculator ─► form)
 ├─ Ai Deen ─► AI chat (not captured)   New feeds ─► Live player (not captured)
 ├─ Quick Access + ─► pin widget (UNSURE)   Islamic History Places ─► gallery (not captured)
 └─ Back ─► Exit/rating dialog
Prayer tab ─► week/month calendar, converter, Islamic events ─► event dialog, alarm icon ─► Alarm Setting
Compass tab ─► calibration dialog, Compass Designs (premium), Qibla map, Continue read Quran ─► Quran tab, Tasbih
Learning tab ─► Course detail ─► Enroll ─► Lessons ─► Quiz
Quran tab ─► Surah/Juzz/Favourite, search, Display sheet ─► Surah reader ─► Tafseer sheet, Reciters, player, Copy/Share
Notification ─► PrayerTimeLockscreenActivity ─► Pray Now (Prayer tab) / Quran / Qibla
```
Interstitial ads appear on many forward/back transitions (see §J).

## I. Permission map
| Permission | Where asked | Pre-rationale | Deny behaviour |
|---|---|---|---|
| POST_NOTIFICATIONS | On splash, first launch (system dialog) | none | Home permission sheet keeps showing Notification toggle OFF; toggle re-requests / opens settings |
| ACCESS_FINE/COARSE_LOCATION | Home "Please provide these permissions" sheet → Location toggle | the sheet text "To provide prayer time precisely" | Sheet stays; on close → "Permission" dialog (Cancel/Grant). Cancel → Home shows "Location not available", "--:--". Grant or toggle again → Android App info page. Sheet re-appears on every Home resume until granted |
| RECORD_AUDIO | AI Learning practice (Qaida/Quran Learning) when tapping the mic | in-card "Microphone Access Required – Enable microphone access to record your voice." | UNSURE (allowed in this capture) |
| SCHEDULE_EXACT_ALARM, USE_FULL_SCREEN_INTENT, RECEIVE_BOOT_COMPLETED, VIBRATE, WAKE_LOCK, FOREGROUND_SERVICE(_DATA_SYNC) | install-time (normal/special) – used by prayer alarms, full-screen prayer alert, ongoing notification | – | – |
| BIOMETRIC/FINGERPRINT | no UI observed (UNSURE) | – | – |

## J. Ads placement list (do NOT replicate; listed for completeness)
- Interstitial: after splash; after onboarding "Continue"; on leaving Tasbeeh / Compass Designs; before opening Surah reader (sometimes); Back from Ayaat; "Continue read Quran"; various back navigations (frequency-capped).
- App-open ad: on returning to the app ("Continue to app").
- Native ads: language screen bottom; onboarding pages 1/3; full-screen native page inside the onboarding pager; Home permission sheet; Home feed (after grid); exit dialog; Duas screen; Learning course list (between courses); token sheet; Qaida language sheet; quit-offer dialog.
- Banner: notification-setup screen bottom; under the bottom nav on Home/Prayer tabs.
- Self-promo: "Don t Like Ads?" strip; premium dialogs.

## K. Lists
- **Qari list (13)**: Rashid Al-Afasy (free/default), Ahmad bin Ali Al-Ajmi, Ali Al-Hudhaify, Saad Al-Ghamdi, Saud Al-Shuraim, Abdul Basit, Abdul Rahmaan Al-Sudais, Nasser Al-qatami, Yasser Ad-Dussary, Maher Al-Muaiqly, Siddiq Al-Minshawi, Muhammad Abdul-Kareem, Hani Abdullah Ar-Rafai (all except the first premium).
- **Dua categories (12)**: All, Morning and Evening, Home And Family, Food And Drink, Joy And Distress, Travel, Prayer, Praising Allah, Hajj And Umrah, Good Etiquette, Nature, Sickness And Death.
- **Azkar categories**: Morning Azkar, Evening Azkar.
- **Kalmas (6)**: see §7.6.
- **Hajj steps** (observed): 1 Enter State of Ihram, 2 Tawaf al Qudoom, 3 Safa & Marwa, 4 Go to Mina (more steps not visible – UNSURE). Guides: Umrah Intro Guide, Hajj Intro Guide, Prepration for Hajj, How to Perform Hajj.
- **Islamic events (7)**: §3.4.2.
- **Compass designs (10)**: §4.3.
- **Quran themes (4)**: light green (default), cream, dark green, white.
- **Translation languages**: English, Indonesian, Bengali, Turkish, Urdu, Russia. **Tafseer**: English, Arabic, Bengali, Urdu, Russia. **AI learning languages**: Arabic, English, Indonesian, Malay, Hindi, Turkish, Urdu.
- **Qaida lessons (16)**: §6.5. **Courses (3)**: §6B.
- **Tasbih dhikr (15)**: §5. **Alarm sounds**: Silent, Beep, Azaan 1, Azaan 2, Azaan 3.

## L. Settings & defaults
| Setting | Default |
|---|---|
| App language | chosen at first run (English) |
| Juristic | Shafii |
| Calculation method | University of Islamic Sciences, Karachi (auto for Pakistan – UNSURE) |
| Prayer alerts | Fajar, Zuhur, Asar, Maghrib, Isha ON; Sunrise OFF |
| Always show prayer schedule notification | ON |
| Alarm sound | Azaan 1 (each prayer), Set Alarm Time ON |
| Do Not Disturb | OFF (window 10:00 PM – 08:00 AM) |
| Namaz / Ayaat / Dua notifications | ON / ON / ON |
| Quran mode | Ayat by ayat |
| Quran theme | light green |
| Quran font | IndoPak; Arabic size mid; translation size small |
| Translation / Tafseer language | English / English |
| Show translation | ON; Auto scroll while playing OFF |
| Reciter | Rashid Al-Afasy; speed 1.0x; repeat ON |
| Compass design | Default |
| Tasbih goal | 33; sound ON |
| Asma ul Husna auto-scroll | ON |
| Learning reminder | Reminder ON, Notification ON, no days, no time |

## M. Share / copy formats
- Share App: `Download this great app at: https://bit.ly/3bkbh9q ` (plain text, resolver chooser).
- Quran ayah Share/Copy: `<Arabic ayah>\n<translation>`; copy toast "Text copied to clipboard".
- Ayaat share: `Surah: <name>\n\nAyat: <n>\n\n<Arabic>\n\n<English>\n\n<Urdu>\n\nI am using this android applicatio…` (rest truncated in preview – UNSURE; likely app promo + Play link).
- Feedback (low rating): email intent to partner.xtech.04@gmail.com, subject "Feedback from Qibla Compass".

## N. Toasts observed
"Fajar has not started yet" · "Text copied to clipboard" · "Repeat mode is off." / "Repeat mode is on." (UNSURE exact on-text).

## O. Not reached / UNSURE (summary)
- AI Deen chat, Live Makkah/Madinah player, Islamic History Places gallery & detail, Home header support/face icon target, Quick Access "+" widget pinning, Zakat result screens, Learn Zakat with AI Deen, Hajj guide pages and remaining Hajj steps, Evening Azkar content, Quran Learning surah practice, AI voice scoring result (not sent for privacy), 4–5-star rating path, premium reciter tap, per-feature offline tests (Wi-Fi on the device became stuck "disabled"; no SIM), Android home-screen widgets on the launcher, Ayaat speaker behaviour, Mosques with results (empty state only).
- Some screens (Azkar, Hajj) were captured in landscape because those activities are not orientation-locked and the phone was rotated; build them portrait using the same components.
