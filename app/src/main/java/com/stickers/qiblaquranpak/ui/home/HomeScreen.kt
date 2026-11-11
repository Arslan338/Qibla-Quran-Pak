package com.stickers.qiblaquranpak.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.OpenInFull
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.AutoStories
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickers.qiblaquranpak.data.Prefs
import com.stickers.qiblaquranpak.data.content.Content
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.ui.PrayerUi
import com.stickers.qiblaquranpak.ui.components.ArrowCircle
import com.stickers.qiblaquranpak.ui.components.ArtPlaceholder
import com.stickers.qiblaquranpak.ui.components.CompassDial
import com.stickers.qiblaquranpak.ui.components.HSpace
import com.stickers.qiblaquranpak.ui.components.MosqueBackdrop
import com.stickers.qiblaquranpak.ui.components.NewRibbon
import com.stickers.qiblaquranpak.ui.components.PrayerGold
import com.stickers.qiblaquranpak.ui.components.SectionTitle
import com.stickers.qiblaquranpak.ui.components.VSpace
import com.stickers.qiblaquranpak.ui.components.display
import com.stickers.qiblaquranpak.ui.components.drawKaaba
import com.stickers.qiblaquranpak.ui.components.icon
import com.stickers.qiblaquranpak.ui.theme.AppColors
import com.stickers.qiblaquranpak.ui.theme.QuranFont
import com.stickers.qiblaquranpak.util.Fmt
import java.time.Duration

enum class HomeAction {
    LOCATION, AI_DEEN, PREMIUM, SETTINGS, VIEW_TIME, QIBLA, CHECK_IN,
    QURAN_LEARNING, QAIDA_LEARNING, PROGRESS, PRAYER_TAB,
    DUAS, TASBIH, AYAAT, NAMES, MOSQUES, KALMAS, DAILY_DEEN, AZKAR, HAJJ, MORE,
    LIVE_MAKKAH, LIVE_MADINAH, WIDGET_PRAYER, WIDGET_QIBLA, PLACES,
}

data class HomeExtras(
    /** Read lazily so only the compass composables recompose on sensor ticks. */
    val heading: () -> Float,
    val checkInDays: Int,
    val checkedInToday: Boolean,
    val quranProgress: Float,
    val qaidaProgress: Float,
    val showPremiumBanner: Boolean,
)

@Composable
fun HomeScreen(
    ui: PrayerUi,
    extras: HomeExtras,
    onAction: (HomeAction) -> Unit,
    onDua: (String) -> Unit,
    onPlace: (String) -> Unit,
    onClosePremium: () -> Unit,
) {
    val list = rememberLazyListState()
    val collapsed by remember { derivedStateOf { list.firstVisibleItemIndex > 0 || list.firstVisibleItemScrollOffset > 230 } }

    Box(Modifier.fillMaxSize().background(AppColors.Green)) {
        MosqueBackdrop(Modifier.fillMaxWidth().height(330.dp))
        LazyColumn(
            state = list, modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 74.dp + (if (extras.showPremiumBanner) 60.dp else 0.dp) + androidx.compose.foundation.layout.WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()),
        ) {
            item(key = "header") { Header(ui, extras.heading, onAction) }
            item(key = "sheet") {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                        .padding(top = 16.dp),
                ) {
                    CheckInCard(extras.checkInDays, extras.checkedInToday) { onAction(HomeAction.CHECK_IN) }
                    VSpace(20.dp)
                    LearnAndGrow(extras, onAction)
                    VSpace(12.dp)
                    ProgressBanner { onAction(HomeAction.PROGRESS) }
                    VSpace(10.dp)
                    DailyPrayersCard(ui) { onAction(HomeAction.PRAYER_TAB) }
                    VSpace(14.dp)
                    Box(Modifier.padding(horizontal = 12.dp).fillMaxWidth().height(1.dp).background(Color(0xFFEDEDED)))
                    VSpace(18.dp)
                    FeatureGrid(onAction)
                    VSpace(28.dp)
                    AiDeenSection { onAction(HomeAction.AI_DEEN) }
                    VSpace(14.dp)
                    DuaCarousel(onDua)
                    VSpace(26.dp)
                    NewFeeds(onAction)
                    VSpace(26.dp)
                    QuickAccess(ui, extras.heading, onAction)
                    VSpace(18.dp)
                    SectionTitle("Islamic History Places", trailing = "View more") { onAction(HomeAction.PLACES) }
                    VSpace(12.dp)
                    PlacesRow(onPlace)
                    VSpace(24.dp)
                }
            }
        }

        // Pinned bar: action icons always visible; prayer title appears once the big header scrolls away.
        Box(
            Modifier
                .fillMaxWidth()
                .then(if (collapsed) Modifier.background(AppColors.Green) else Modifier)
                .statusBarsPadding(),
        ) {
            AnimatedVisibility(collapsed, enter = fadeIn(), exit = fadeOut()) {
                Box(Modifier.fillMaxWidth().height(126.dp)) {
                    MosqueBackdrop(Modifier.fillMaxSize())
                    Column(Modifier.padding(start = 16.dp, top = 18.dp)) {
                        Text("Next prayer - ${ui.next?.display ?: "--"}", color = Color.White, fontSize = 20.sp)
                        Text(ui.nextTime?.let(Fmt::time) ?: "--:--", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            HeaderIcons(Modifier.align(Alignment.TopEnd).padding(top = 22.dp, end = 12.dp), onAction)
        }

        if (extras.showPremiumBanner) {
            PremiumBanner(Modifier.align(Alignment.BottomCenter), onClick = { onAction(HomeAction.PREMIUM) }, onClose = onClosePremium)
        }
    }
}

@Composable
private fun HeaderIcons(modifier: Modifier, onAction: (HomeAction) -> Unit) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.SmartToy, "Ai Deen", tint = Color(0xFFB9E3A5), modifier = Modifier.size(30.dp).clickable { onAction(HomeAction.AI_DEEN) })
        HSpace(18.dp)
        Box(
            Modifier.size(34.dp).border(1.8.dp, AppColors.GoldText, CircleShape).clickable { onAction(HomeAction.PREMIUM) },
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Filled.WorkspacePremium, "Premium", tint = AppColors.GoldText, modifier = Modifier.size(20.dp)) }
        HSpace(18.dp)
        Icon(Icons.Filled.Settings, "Settings", tint = Color.White, modifier = Modifier.size(28.dp).clickable { onAction(HomeAction.SETTINGS) })
    }
}

@Composable
private fun Header(ui: PrayerUi, heading: () -> Float, onAction: (HomeAction) -> Unit) {
    Column(Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp)) {
        VSpace(18.dp)
        Row(
            Modifier
                .clip(RoundedCornerShape(50))
                .background(Color.White.copy(alpha = 0.3f))
                .clickable { onAction(HomeAction.LOCATION) }
                .padding(horizontal = 12.dp, vertical = 5.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.LocationOn, null, tint = Color.White, modifier = Modifier.size(18.dp))
            HSpace(6.dp)
            Text(
                when {
                    ui.place == null -> "Location not available"
                    ui.place.city.isBlank() -> "%.2f, %.2f".format(ui.place.lat, ui.place.lng)
                    else -> ui.place.city
                },
                color = Color.White, fontSize = 16.sp,
            )
        }
        Row(Modifier.fillMaxWidth()) {
            Column(Modifier.weight(1f).padding(top = 22.dp)) {
                Icon(Icons.Outlined.MenuBook, null, tint = Color(0xFFE6B95B), modifier = Modifier.size(30.dp))
                VSpace(16.dp)
                Text("Next prayer - ${ui.next?.display ?: "--"}", color = Color.White, fontSize = 20.sp)
                Text(ui.nextTime?.let(Fmt::time) ?: "--:--", color = Color.White, fontSize = 40.sp, fontWeight = FontWeight.Bold)
                VSpace(6.dp)
                Row(Modifier.clickable { onAction(HomeAction.VIEW_TIME) }, verticalAlignment = Alignment.CenterVertically) {
                    Text("View time", color = Color.White, fontSize = 19.sp)
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
            Box(Modifier.padding(top = 2.dp).size(162.dp).clickable { onAction(HomeAction.QIBLA) }) {
                CompassDial(heading(), ui.qibla?.toFloat(), Modifier.fillMaxSize())
                Icon(Icons.Filled.OpenInFull, null, tint = Color.White.copy(alpha = 0.35f), modifier = Modifier.align(Alignment.BottomEnd).size(16.dp))
            }
        }
        VSpace(14.dp)
        Box(Modifier.fillMaxWidth().height(1.dp).background(Color(0xFF3D6E48)))
        VSpace(14.dp)
        Row(Modifier.fillMaxWidth()) {
            Text(Fmt.longDate(ui.now.toLocalDate()), color = Color.White, fontSize = 17.sp, modifier = Modifier.weight(1f))
            Text("(${ui.hijri.day} ${Fmt.hijriMonth(ui.hijri.month)}, ${ui.hijri.year})", color = Color.White, fontSize = 17.sp)
        }
        VSpace(26.dp)
    }
}

@Composable
private fun CheckInCard(days: Int, checkedToday: Boolean, onCheckIn: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .border(1.dp, Color(0xFFB9C7BE), RoundedCornerShape(16.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Check in 7 days get reward", color = AppColors.Green, fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Day $days/7", color = AppColors.TextSecondary, fontSize = 16.sp)
        }
        VSpace(12.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.SpaceBetween) {
                for (d in 1..7) {
                    val done = d <= days
                    val today = d == days + 1 && !checkedToday
                    Box(
                        Modifier
                            .size(28.dp)
                            .then(if (done) Modifier.background(AppColors.Green, CircleShape) else Modifier)
                            .border(if (today || done) 1.5.dp else 1.dp, if (today || done) AppColors.Green else Color(0xFF9E9E9E), CircleShape),
                        contentAlignment = Alignment.Center,
                    ) { Text("$d", fontSize = 15.sp, color = if (done) Color.White else AppColors.TextPrimary) }
                }
            }
            HSpace(14.dp)
            Box(
                Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (checkedToday) Color(0xFF8FA595) else AppColors.Green)
                    .clickable(enabled = !checkedToday, onClick = onCheckIn)
                    .padding(horizontal = 9.dp, vertical = 7.dp),
            ) { Text(if (checkedToday) "Checked" else "Check in", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold) }
        }
    }
}

@Composable
private fun LearnAndGrow(extras: HomeExtras, onAction: (HomeAction) -> Unit) {
    Text("Learn & Grow", color = Color(0xFF7CC242), fontSize = 22.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 12.dp))
    VSpace(10.dp)
    Row(Modifier.padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LearnCard(
            Modifier.weight(1f), "Quran Learning", "Learn to read the Quran.", extras.quranProgress,
            listOf(Color(0xFF2F6B3A), Color(0xFF1F4D27)), Color(0xFF4E8A59), Icons.Outlined.MenuBook,
        ) { onAction(HomeAction.QURAN_LEARNING) }
        LearnCard(
            Modifier.weight(1f), "Qaida Learning", "Learn Arabic alphabets.", extras.qaidaProgress,
            listOf(Color(0xFF5A2DAE), Color(0xFF3B1D86)), Color(0xFF7553BD), Icons.Outlined.AutoStories,
        ) { onAction(HomeAction.QAIDA_LEARNING) }
    }
}

@Composable
private fun LearnCard(
    modifier: Modifier, title: String, subtitle: String, progress: Float,
    colors: List<Color>, track: Color, art: ImageVector, onClick: () -> Unit,
) {
    Box(modifier.height(240.dp)) {
        Column(
            Modifier
                .padding(top = 10.dp)
                .fillMaxSize()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(colors))
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Row {
                ArtPlaceholder(Modifier.size(96.dp, 82.dp), listOf(Color.White.copy(alpha = 0.12f), Color.White.copy(alpha = 0.04f)), art, radius = 10.dp)
                Spacer(Modifier.weight(1f))
                ArrowCircle(34.dp)
            }
            Spacer(Modifier.weight(1f))
            Text(title, color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(subtitle, color = Color.White.copy(alpha = 0.7f), fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            VSpace(12.dp)
            Text("Progress", color = Color.White, fontSize = 14.sp)
            VSpace(6.dp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)).background(track)) {
                    Box(Modifier.fillMaxWidth(progress.coerceIn(0f, 1f)).height(6.dp).background(Color.White))
                }
                HSpace(8.dp)
                Text("%.1f%%".format(progress * 100), color = Color.White, fontSize = 14.sp)
            }
        }
        NewRibbon(Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun ProgressBanner(onClick: () -> Unit) {
    Box(
        Modifier
            .padding(horizontal = 12.dp)
            .fillMaxWidth()
            .height(97.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF1F4A2B), Color(0xFF2C5E3A), Color(0xFF16361F))))
            .clickable(onClick = onClick),
    ) {
        Column(Modifier.align(Alignment.CenterStart).padding(start = 16.dp, end = 120.dp)) {
            Text("Progress Tracking", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            Text("Track your learning journey and progress.", color = Color.White.copy(alpha = 0.85f), fontSize = 15.sp, lineHeight = 18.sp)
        }
        ArtPlaceholder(Modifier.align(Alignment.CenterEnd).padding(end = 62.dp).size(70.dp), listOf(Color.White.copy(alpha = 0.10f), Color.Transparent), Icons.Outlined.MenuBook, radius = 10.dp)
        ArrowCircle(43.dp, Color(0xFFB9E3C4), Modifier.align(Alignment.CenterEnd).padding(end = 16.dp))
    }
}

@Composable
private fun DailyPrayersCard(ui: PrayerUi, onExplore: () -> Unit) {
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFF8FAE0), Color(0xFFEEF8E6), Color(0xFFE5F6ED))))
            .border(1.5.dp, Color(0xFFD7E6B6), RoundedCornerShape(18.dp))
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(8.dp)).background(Brush.verticalGradient(listOf(Color(0xFF4F9A4F), Color(0xFF2B6A30)))), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.SelfImprovement, null, tint = Color.White, modifier = Modifier.size(22.dp))
            }
            HSpace(12.dp)
            Column(Modifier.weight(1f)) {
                Text("Daily Prayers", color = AppColors.Green, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Text("Perfect Your Prayer", color = AppColors.Green, fontSize = 14.sp)
            }
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(Brush.verticalGradient(listOf(Color(0xFF5E9B44), Color(0xFF346A35))))
                    .clickable(onClick = onExplore)
                    .padding(horizontal = 14.dp, vertical = 6.dp),
            ) { Text("Explore Time", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
        }
        VSpace(30.dp)
        PrayerTimeline(ui)
    }
}

@Composable
private fun PrayerTimeline(ui: PrayerUi) {
    val prayers = Prayer.entries.filter { it.isPrayer }
    val currentIndex = prayers.indexOf(ui.next ?: Prayer.FAJR)
    Box(Modifier.fillMaxWidth()) {
        // connectors behind the dots: solid for reached, dashed for upcoming
        Canvas(Modifier.fillMaxWidth().height(20.dp).offset(y = 30.dp)) {
            val step = size.width / prayers.size
            for (i in 0 until prayers.size - 1) {
                val x1 = step * i + step / 2 + 12.dp.toPx()
                val x2 = step * (i + 1) + step / 2 - 12.dp.toPx()
                val y = size.height / 2
                val reached = i < currentIndex
                drawLine(
                    if (reached) AppColors.Green else Color(0xFF9DB79D),
                    Offset(x1, y), Offset(x2, y), 1.5.dp.toPx(),
                    pathEffect = if (reached) null else PathEffect.dashPathEffect(floatArrayOf(8f, 8f)),
                )
            }
        }
        Row(Modifier.fillMaxWidth()) {
            prayers.forEachIndexed { i, p ->
                val active = i == currentIndex
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(p.icon(), null, tint = PrayerGold, modifier = Modifier.size(22.dp))
                    VSpace(6.dp)
                    Box(Modifier.height(24.dp), contentAlignment = Alignment.Center) {
                        Box(
                            Modifier
                                .size(if (active) 23.dp else 20.dp)
                                .background(AppColors.GreenSoft, CircleShape)
                                .border(if (active) 1.5.dp else 1.dp, if (active) AppColors.Green else Color(0xFFB4C99E), CircleShape),
                        )
                    }
                    VSpace(10.dp)
                    Text(p.display, color = AppColors.Green, fontSize = 15.sp)
                    Text(ui.today?.get(p)?.let(Fmt::time) ?: "--:--", color = AppColors.TextPrimary, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, maxLines = 1, softWrap = false)
                }
            }
        }
    }
}

private data class Feature(val label: String, val icon: ImageVector, val action: HomeAction)

@Composable
private fun FeatureGrid(onAction: (HomeAction) -> Unit) {
    val features = listOf(
        Feature("Duas", HomeIcons.Duas, HomeAction.DUAS),
        Feature("Tasbih", HomeIcons.Tasbih, HomeAction.TASBIH),
        Feature("Ayaat", HomeIcons.Ayaat, HomeAction.AYAAT),
        Feature("Names", HomeIcons.Names, HomeAction.NAMES),
        Feature("Mosques", Icons.Outlined.Mosque, HomeAction.MOSQUES),
        Feature("Kalmas", HomeIcons.Kalmas, HomeAction.KALMAS),
        Feature("Daily Deen", HomeIcons.DailyDeen, HomeAction.DAILY_DEEN),
        Feature("Azkar", HomeIcons.Azkar, HomeAction.AZKAR),
        Feature("Hajj Umrah", HomeIcons.Hajj, HomeAction.HAJJ),
        Feature("More", HomeIcons.More, HomeAction.MORE),
    )
    Column(Modifier.padding(horizontal = 6.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        features.chunked(5).forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { f ->
                    Column(
                        Modifier.weight(1f).clickable { onAction(f.action) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Box(
                            Modifier
                                .size(52.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF245729))
                                .border(1.5.dp, Color(0xFFBCDF4D), RoundedCornerShape(10.dp)),
                            contentAlignment = Alignment.Center,
                        ) { Icon(f.icon, null, tint = Color.White, modifier = Modifier.size(30.dp)) }
                        VSpace(6.dp)
                        Text(f.label, fontSize = 17.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
    }
}

@Composable
private fun AiDeenSection(onClick: () -> Unit) {
    Text(
        buildAnnotatedString {
            withStyle(SpanStyle(color = AppColors.Red)) { append("Ai ") }
            append("Deen")
        },
        fontSize = 22.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp),
    )
    VSpace(14.dp)
    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(223.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(Brush.linearGradient(listOf(Color(0xFFE9D9A1), Color(0xFFD7B96A), Color(0xFFE8D7A0))))
            .clickable(onClick = onClick),
    ) {
        ArtPlaceholder(
            Modifier.align(Alignment.BottomEnd).padding(end = 8.dp).size(140.dp, 200.dp),
            listOf(Color(0xFF3F6E66), Color(0xFF2B5650)), Icons.Filled.SmartToy, radius = 60.dp,
        )
        Column(Modifier.padding(start = 18.dp, top = 18.dp)) {
            Text("Ai Deen", color = Color(0xFF4A3410), fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
            Text("INQUIRES", color = Color(0xFF4A3410), fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            VSpace(6.dp)
            Text("Ask Freely. Learn Deeply.\nOnly With Ai Deen.", color = Color(0xFF6A5326), fontSize = 16.sp, lineHeight = 19.sp)
            VSpace(12.dp)
            Box(
                Modifier
                    .width(210.dp)
                    .height(55.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.horizontalGradient(listOf(Color(0xFF2B5A34), Color(0xFF5F8A62), Color(0xFF2B5A34)))),
                contentAlignment = Alignment.Center,
            ) { Text("Explore Now", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold) }
        }
    }
}

@Composable
private fun DuaCarousel(onDua: (String) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(Content.homeDuas, key = { it.id }) { d ->
            Column(
                Modifier
                    .width(270.dp)
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Brush.verticalGradient(listOf(Color(0xFF6E8DB0), Color(0xFFA9BCD1), Color(0xFFDCE4EE))))
                    .clickable { onDua(d.id) },
            ) {
                Row(Modifier.fillMaxWidth().height(44.dp).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(d.title, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, modifier = Modifier.weight(1f), maxLines = 1)
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, null, tint = Color.White)
                }
                Box(
                    Modifier
                        .padding(start = 4.dp, end = 4.dp, bottom = 4.dp)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F4F8))
                        .border(1.dp, Color(0xFFC8D3E2), RoundedCornerShape(12.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        d.arabic, fontFamily = QuranFont, fontSize = 16.sp, lineHeight = 26.sp, textAlign = TextAlign.Center,
                        style = androidx.compose.ui.text.TextStyle(textDirection = TextDirection.Rtl), maxLines = 4, overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}

@Composable
private fun NewFeeds(onAction: (HomeAction) -> Unit) {
    Text("New feeds", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp))
    VSpace(14.dp)
    Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        LiveCard(Modifier.weight(1f), "Makkah Live Broadcast", Content.places[0].imageUrl, listOf(Color(0xFF4A6B8F), Color(0xFF1E2D3E))) { onAction(HomeAction.LIVE_MAKKAH) }
        LiveCard(Modifier.weight(1f), "Medina Live Broadcast", Content.places[1].imageUrl, listOf(Color(0xFF6B8F6A), Color(0xFF2A3E2A))) { onAction(HomeAction.LIVE_MADINAH) }
    }
}

@Composable
private fun LiveCard(modifier: Modifier, title: String, image: String?, colors: List<Color>, onClick: () -> Unit) {
    Column(modifier.clickable(onClick = onClick)) {
        Box(Modifier.fillMaxWidth().aspectRatio(1.5f).clip(RoundedCornerShape(16.dp))) {
            RemoteImage(image, colors, Modifier.fillMaxSize())
            Row(
                Modifier.align(Alignment.TopEnd).padding(8.dp).clip(RoundedCornerShape(6.dp)).background(Color(0xFFE53935)).padding(horizontal = 6.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(7.dp).background(Color.White, CircleShape))
                HSpace(4.dp)
                Text("LIVE", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Icon(Icons.Filled.PlayCircleOutline, null, tint = Color.White, modifier = Modifier.align(Alignment.Center).size(46.dp))
        }
        VSpace(10.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(31.dp).clip(CircleShape).background(Color(0xFFD9E7F2)), contentAlignment = Alignment.Center) {
                Icon(Icons.Outlined.Mosque, null, tint = AppColors.Green, modifier = Modifier.size(20.dp))
            }
            HSpace(10.dp)
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

@Composable
private fun QuickAccess(ui: PrayerUi, heading: () -> Float, onAction: (HomeAction) -> Unit) {
    Text("Quick Access", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp))
    Text("Add to home screen", fontSize = 17.sp, color = AppColors.TextSecondary, modifier = Modifier.padding(horizontal = 12.dp))
    VSpace(20.dp)
    // Next prayer widget preview
    Box(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
        Column(
            Modifier
                .padding(end = 10.dp, top = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(AppColors.Green)
                .padding(18.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon((ui.next ?: Prayer.FAJR).icon(), null, tint = PrayerGold, modifier = Modifier.size(22.dp))
                        HSpace(10.dp)
                        Text("NEXT PRAYER", color = Color(0xFFE6D08A), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                    }
                    VSpace(8.dp)
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(ui.next?.display ?: "--", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        HSpace(8.dp)
                        Text(ui.nextTime?.let(Fmt::time) ?: "", color = Color(0xFFE6D08A), fontSize = 18.sp)
                    }
                }
                Box(
                    Modifier.clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.1f))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    Countdown(ui.nextTime)
                }
            }
            VSpace(14.dp)
            Row(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(Color.White.copy(alpha = 0.06f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(14.dp)).padding(4.dp),
            ) {
                Prayer.entries.filter { it.isPrayer }.forEach { p ->
                    val active = p == ui.next
                    Column(
                        Modifier.weight(1f).clip(RoundedCornerShape(12.dp))
                            .then(if (active) Modifier.background(Color.White.copy(alpha = 0.18f)) else Modifier)
                            .padding(vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Icon(p.icon(), null, tint = PrayerGold, modifier = Modifier.size(22.dp))
                        Text(p.display, color = Color(0xFFDDE6DE), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Text(ui.today?.get(p)?.let(Fmt::time) ?: "", color = Color(0xFFDDE6DE), fontSize = 12.sp)
                    }
                }
            }
        }
        GoldPlus(Modifier.align(Alignment.TopEnd)) { onAction(HomeAction.WIDGET_PRAYER) }
    }
    VSpace(14.dp)
    // Qibla widget preview
    Box(Modifier.padding(horizontal = 12.dp).fillMaxWidth()) {
        Row(
            Modifier
                .padding(end = 10.dp, top = 10.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, Color(0xFFBFE39A), RoundedCornerShape(20.dp))
                .background(Color.White)
                .clickable { onAction(HomeAction.QIBLA) }
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.size(110.dp).clip(RoundedCornerShape(16.dp)).background(Color.Black).padding(8.dp)) {
                MiniCompass(heading(), ui.qibla?.toFloat())
            }
            HSpace(20.dp)
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Canvas(Modifier.size(26.dp)) { drawKaaba(Offset.Zero, size.minDimension) }
                    HSpace(8.dp)
                    Text("Qibla", color = AppColors.Green, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                }
                Text("Direction to Kaaba", color = AppColors.TextSecondary, fontSize = 15.sp)
                VSpace(10.dp)
                Row(
                    Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(Color(0xFFE9EEEA)).padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(Modifier.weight(1f)) {
                        Text("Bearing", fontSize = 13.sp, color = AppColors.Green)
                        Text(ui.qibla?.let { Fmt.bearing(it) } ?: "--", fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = AppColors.Green)
                    }
                    Box(Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)).background(AppColors.Green), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Navigation, null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
                VSpace(10.dp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(12.dp).background(Color(0xFF66BB6A), CircleShape))
                    HSpace(14.dp)
                    Text(ui.place?.city.orEmpty(), color = AppColors.Green, fontSize = 15.sp)
                }
            }
        }
        GoldPlus(Modifier.align(Alignment.TopEnd)) { onAction(HomeAction.WIDGET_QIBLA) }
    }
}

@Composable
private fun GoldPlus(modifier: Modifier, onClick: () -> Unit) {
    Box(
        modifier.size(34.dp).clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFFF3DE94), Color(0xFFD9B95A))))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) { Icon(Icons.Filled.Add, "Add widget", tint = AppColors.Green) }
}

@Composable
private fun PlacesRow(onPlace: (String) -> Unit) {
    LazyRow(contentPadding = PaddingValues(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        items(Content.places, key = { it.id }) { p ->
            Box(Modifier.width(200.dp).height(120.dp).clip(RoundedCornerShape(12.dp)).clickable { onPlace(p.id) }) {
                RemoteImage(p.imageUrl, listOf(Color(0xFF8C7A63), Color(0xFF3E342A)), Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f)))))
                Text(p.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.BottomStart).padding(12.dp), maxLines = 2)
            }
        }
    }
}

@Composable
fun PremiumBanner(modifier: Modifier, onClick: () -> Unit, onClose: () -> Unit) {
    Row(
        modifier
            .navigationBarsPadding()
            .padding(bottom = 74.dp)
            .fillMaxWidth()
            .height(60.dp)
            .background(Brush.horizontalGradient(listOf(Color(0xFFAC9443), Color(0xFFC0B17C), Color(0xFFCDB975))))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(Icons.Filled.EmojiEvents, null, tint = Color(0xFF3A2C0A), modifier = Modifier.size(36.dp))
        HSpace(12.dp)
        Column(Modifier.weight(1f)) {
            Text("Don't Like Ads?", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Text("Get premium for only PKR2,800 / year", fontSize = 14.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Icon(Icons.Filled.Close, "Close", tint = Color.White, modifier = Modifier.size(18.dp).clip(CircleShape).background(Color(0x55FFFFFF)).clickable(onClick = onClose))
            VSpace(8.dp)
            Text("Other Plans", fontSize = 14.sp, textDecoration = TextDecoration.Underline)
        }
    }
}

/** Only this Text recomposes every second. */
@Composable
private fun Countdown(target: java.time.ZonedDateTime?) {
    val now by com.stickers.qiblaquranpak.ui.components.rememberNow()
    val left = target?.let { Duration.between(now, it) } ?: Duration.ZERO
    Text(Fmt.countdown(left), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
}
