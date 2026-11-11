package com.stickers.qiblaquranpak.ui.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.outlined.Mosque
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickers.qiblaquranpak.data.prayer.Prayer
import com.stickers.qiblaquranpak.ui.components.ArtPlaceholder
import com.stickers.qiblaquranpak.ui.components.PrayerGold
import com.stickers.qiblaquranpak.ui.components.VSpace
import com.stickers.qiblaquranpak.ui.components.display
import com.stickers.qiblaquranpak.ui.components.icon
import com.stickers.qiblaquranpak.ui.home.MiniCompass
import com.stickers.qiblaquranpak.ui.theme.AppColors
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

/** Radial "spokes" spinner used by the splash and other loading states. */
@Composable
fun SpokeLoader(modifier: Modifier = Modifier, color: Color = AppColors.GoldText) {
    val t = rememberInfiniteTransition(label = "spokes")
    val angle by t.animateFloat(0f, 360f, infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "a")
    Canvas(modifier.size(48.dp)) {
        val r = size.minDimension / 2
        rotate(angle) {
            for (i in 0 until 12) {
                val a = Math.toRadians(i * 30.0)
                val alpha = 0.25f + 0.75f * (i / 11f)
                drawLine(
                    color.copy(alpha = alpha),
                    Offset(center.x + r * 0.45f * cos(a).toFloat(), center.y + r * 0.45f * sin(a).toFloat()),
                    Offset(center.x + r * cos(a).toFloat(), center.y + r * sin(a).toFloat()),
                    strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round,
                )
            }
        }
    }
}

@Composable
fun SplashScreen() {
    com.stickers.qiblaquranpak.ui.components.SystemBarIcons(darkIcons = false)
    val t = rememberInfiniteTransition(label = "splash")
    val scale by t.animateFloat(0.85f, 1.1f, infiniteRepeatable(tween(900), RepeatMode.Reverse), label = "s")
    Box(Modifier.fillMaxSize().background(AppColors.Green)) {
        Column(Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.size((44 * scale).dp).clip(CircleShape).background(Color.White).padding(4.dp)) {
                Box(Modifier.fillMaxSize().clip(CircleShape).background(Color(0xFF2B2B2B)).padding(3.dp)) { MiniCompass(0f, 315f) }
            }
            VSpace(60.dp)
            Text("QIBLA COMPASS", color = Color.White, fontSize = 27.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        }
        SpokeLoader(Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(bottom = 64.dp))
    }
}

data class Language(val code: String, val name: String, val native: String, val flag: String)

val Languages = listOf(
    Language("en", "English", "English", "🇬🇧"),
    Language("sq", "Albanian", "shqip", "🇦🇱"),
    Language("ar", "Arabic", "العربية", "🇸🇦"),
    Language("az", "Azerbaijani", "azərbaycan", "🇦🇿"),
    Language("bn", "Bangla", "বাংলা", "🇧🇩"),
    Language("bg", "Bulgarian", "български", "🇧🇬"),
    Language("my", "Burmese", "မြန်မာ", "🇲🇲"),
    Language("zh", "Chinese", "中文", "🇨🇳"),
    Language("nl", "Dutch", "Nederlands", "🇳🇱"),
    Language("fr", "French", "français", "🇫🇷"),
    Language("de", "German", "Deutsch", "🇩🇪"),
    Language("hi", "Hindi", "हिन्दी", "🇮🇳"),
    Language("id", "Indonesian", "Indonesia", "🇮🇩"),
    Language("it", "Italian", "italiano", "🇮🇹"),
    Language("kk", "Kazakh", "қазақ", "🇰🇿"),
    Language("ko", "Korean", "한국어", "🇰🇷"),
    Language("ms", "Malay", "Melayu", "🇲🇾"),
    Language("fa", "Persian", "فارسی", "🇮🇷"),
    Language("pt", "Portuguese", "português", "🇵🇹"),
    Language("ru", "Russian", "русский", "🇷🇺"),
    Language("es", "Spanish", "español", "🇪🇸"),
    Language("sw", "Swahili", "Kiswahili", "🇰🇪"),
    Language("tg", "Tajik", "тоҷикӣ", "🇹🇯"),
    Language("th", "Thai", "ไทย", "🇹🇭"),
    Language("tr", "Turkish", "Türkçe", "🇹🇷"),
    Language("ur", "Urdu", "اردو", "🇵🇰"),
    Language("uz", "Uzbek", "o‘zbek", "🇺🇿"),
)

@Composable
fun LanguageScreen(selected: String?, onPick: (String) -> Unit, onDone: (() -> Unit)? = null) {
    com.stickers.qiblaquranpak.ui.components.SystemBarIcons(darkIcons = true)
    var current by remember { mutableStateOf(selected) }
    Column(Modifier.fillMaxSize().background(AppColors.GreyBg).statusBarsPadding()) {
        Row(Modifier.fillMaxWidth().padding(start = 24.dp, end = 16.dp, top = 22.dp, bottom = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Select a language", color = AppColors.Green, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            if (current != null && onDone != null) {
                Box(Modifier.size(36.dp).clip(CircleShape).background(AppColors.Green).clickable { onDone() }, contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Check, "Done", tint = Color.White)
                }
            }
        }
        LazyColumn(contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(Languages, key = { it.code }) { l ->
                val sel = l.code == current
                Row(
                    Modifier
                        .fillMaxWidth()
                        .height(51.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(AppColors.GreyItem)
                        .then(if (sel) Modifier.border(1.5.dp, AppColors.Green, RoundedCornerShape(12.dp)) else Modifier)
                        .clickable { current = l.code; onPick(l.code) }
                        .padding(horizontal = 9.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(width = 33.dp, height = 26.dp).clip(RoundedCornerShape(4.dp)).background(Color.White), contentAlignment = Alignment.Center) {
                        Text(l.flag, fontSize = 20.sp)
                    }
                    Spacer(Modifier.width(17.dp))
                    Text(l.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(" (${l.native})", fontSize = 14.sp, color = AppColors.TextSecondary)
                    Spacer(Modifier.weight(1f))
                    if (sel) Icon(Icons.Filled.Check, null, tint = AppColors.Green)
                }
            }
        }
    }
}

private data class Page(val title: String, val body: String, val art: ImageVector, val colors: List<Color>)

private val pages = listOf(
    Page("Find Qibla", "Always face the Kaaba with our accurate Qibla finder.", Icons.Filled.Explore, listOf(Color(0xFFE9C46A), Color(0xFF9C6B1E))),
    Page("AI Deen", "Your smart Islamic companion for instant answers, guidance, and learning.", Icons.Filled.SmartToy, listOf(Color(0xFFD9B44A), Color(0xFF3A2A12))),
    Page("Timely Prayers", "Get accurate prayer times based on your location.", Icons.Outlined.Mosque, listOf(Color(0xFFD6A757), Color(0xFF7A4E1C))),
)

/** Three intro pages followed by the per-prayer notification setup. */
@Composable
fun OnboardingScreen(initialAlerts: Map<Prayer, Boolean>, onFinish: (alerts: Map<Prayer, Boolean>, persistent: Boolean) -> Unit) {
    com.stickers.qiblaquranpak.ui.components.SystemBarIcons(darkIcons = true)
    var showSetup by remember { mutableStateOf(false) }
    if (showSetup) {
        NotificationSetup(initialAlerts, onFinish)
        return
    }
    val pager = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()
    Column(Modifier.fillMaxSize().background(Color.White).statusBarsPadding().navigationBarsPadding()) {
        HorizontalPager(pager, Modifier.weight(1f)) { i ->
            val p = pages[i]
            Column(Modifier.fillMaxSize().padding(horizontal = 24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                VSpace(40.dp)
                ArtPlaceholder(Modifier.size(310.dp), p.colors, p.art, Color.White.copy(alpha = 0.8f), radius = 155.dp)
                VSpace(40.dp)
                Text(p.title, color = AppColors.Green, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
                VSpace(14.dp)
                Text(p.body, color = AppColors.Green, fontSize = 18.sp, textAlign = TextAlign.Center, lineHeight = 24.sp)
            }
        }
        Row(Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp), verticalAlignment = Alignment.CenterVertically) {
                repeat(pages.size) { i ->
                    Box(
                        Modifier
                            .animateContentSize()
                            .height(8.dp)
                            .width(if (i == pager.currentPage) 16.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (i == pager.currentPage) AppColors.Green else Color(0xFFB0B0B0)),
                    )
                }
            }
            Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
                val last = pager.currentPage == pages.lastIndex
                Box(
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (last) AppColors.Green else Color.Transparent)
                        .clickable {
                            if (last) showSetup = true else scope.launch { pager.animateScrollToPage(pager.currentPage + 1) }
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(if (last) "Start" else "Next", color = if (last) Color.White else AppColors.Green, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}

@Composable
private fun NotificationSetup(initial: Map<Prayer, Boolean>, onFinish: (Map<Prayer, Boolean>, Boolean) -> Unit) {
    val state = remember { mutableStateListOf(*Prayer.entries.map { initial[it] ?: (it != Prayer.SUNRISE) }.toTypedArray()) }
    var persistent by remember { mutableStateOf(true) }
    Column(
        Modifier.fillMaxSize().background(Color.White).statusBarsPadding().navigationBarsPadding().padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VSpace(26.dp)
        Box(
            Modifier.size(90.dp).clip(CircleShape).background(Brush.radialGradient(listOf(Color(0xFFF5D98A), Color(0xFFB7802E)))),
            contentAlignment = Alignment.Center,
        ) { Icon(Icons.Filled.NotificationsActive, null, tint = Color(0xFF6B4410), modifier = Modifier.size(52.dp)) }
        VSpace(20.dp)
        Text("Set notifications for\neach prayer", color = AppColors.Green, fontSize = 25.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center, lineHeight = 33.sp)
        VSpace(26.dp)
        Prayer.entries.forEachIndexed { i, p ->
            Row(
                Modifier.fillMaxWidth().height(51.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFF4F4F4)).padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(p.icon(), null, tint = PrayerGold, modifier = Modifier.size(30.dp))
                Spacer(Modifier.width(14.dp))
                Text(p.display, fontSize = 19.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Switch(
                    state[i], { state[i] = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AppColors.Green, checkedTrackColor = Color(0xFF8AA897), uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFFC4C4C4), uncheckedBorderColor = Color.Transparent),
                )
            }
            VSpace(16.dp)
        }
        Row(Modifier.fillMaxWidth().clickable { persistent = !persistent }.padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier.size(26.dp).clip(CircleShape).background(if (persistent) Color(0xFF5CB85C) else Color(0xFFD0D0D0)),
                contentAlignment = Alignment.Center,
            ) { if (persistent) Icon(Icons.Filled.Check, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
            Spacer(Modifier.width(20.dp))
            Text("Always show prayer schedule in notifications.", fontSize = 17.sp)
        }
        VSpace(18.dp)
        Box(
            Modifier.fillMaxWidth().height(51.dp).clip(RoundedCornerShape(12.dp)).background(AppColors.Green)
                .clickable { onFinish(Prayer.entries.mapIndexed { i, p -> p to state[i] }.toMap(), persistent) },
            contentAlignment = Alignment.Center,
        ) { Text("Continue", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.SemiBold) }
    }
}
