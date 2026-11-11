package com.stickers.qiblaquranpak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImportContacts
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickers.qiblaquranpak.ui.theme.AppColors
import androidx.compose.foundation.clickable

private data class NavItem(val label: String, val icon: ImageVector?)

private val items = listOf(
    NavItem("Home", Icons.Filled.Home),
    NavItem("Prayer", Icons.Filled.CalendarMonth),
    NavItem("", null),
    NavItem("Learning", Icons.Filled.ImportContacts),
    NavItem("Quran", Icons.AutoMirrored.Filled.MenuBook),
)

/** White bar with four tabs and the raised round Qibla button in the centre. */
@Composable
fun BottomBar(selected: Int, quranBadge: Boolean, onSelect: (Int) -> Unit) {
    Box(Modifier.fillMaxWidth()) {
        Row(
            Modifier
                .fillMaxWidth()
                .shadow(8.dp)
                .background(Color.White)
                .navigationBarsPadding()
                .height(74.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEachIndexed { i, item ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(74.dp)
                        .clickable(remember { MutableInteractionSource() }, null) { onSelect(i) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (item.icon != null) {
                        val tint = if (selected == i) AppColors.Green else AppColors.NavInactive
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Box {
                                Icon(item.icon, null, tint = tint, modifier = Modifier.size(28.dp))
                                if (i == 4 && quranBadge) {
                                    Box(
                                        Modifier
                                            .align(Alignment.TopEnd)
                                            .offset(x = 6.dp, y = (-4).dp)
                                            .size(9.dp)
                                            .background(AppColors.Red, CircleShape),
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(item.label, color = tint, fontSize = 13.sp)
                        }
                    }
                }
            }
        }
        // Centre Qibla button, raised above the bar.
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-6).dp)
                .size(62.dp)
                .clip(CircleShape)
                .background(AppColors.Green)
                .border(3.dp, Color.White, CircleShape)
                .clickable { onSelect(2) },
            contentAlignment = Alignment.Center,
        ) {
            KaabaRingIcon(Modifier.size(42.dp))
        }
    }
}

/** White ring with a north tick and a Kaaba in the middle, as on the centre nav button. */
@Composable
fun KaabaRingIcon(modifier: Modifier = Modifier, color: Color = Color.White) {
    Canvas(modifier) {
        val r = size.minDimension / 2f
        drawCircle(color, r * 0.86f, center, style = Stroke(r * 0.12f))
        // ticks at E, S, W
        listOf(90f, 180f, 270f).forEach { d ->
            val a = Math.toRadians(d.toDouble() - 90)
            val o = Offset(center.x + r * 0.86f * kotlin.math.cos(a).toFloat(), center.y + r * 0.86f * kotlin.math.sin(a).toFloat())
            val i = Offset(center.x + r * 0.62f * kotlin.math.cos(a).toFloat(), center.y + r * 0.62f * kotlin.math.sin(a).toFloat())
            drawLine(color, i, o, r * 0.1f)
        }
        // north arrow
        val p = androidx.compose.ui.graphics.Path().apply {
            moveTo(center.x, center.y - r); lineTo(center.x - r * 0.2f, center.y - r * 0.62f); lineTo(center.x + r * 0.2f, center.y - r * 0.62f); close()
        }
        drawPath(p, color)
        // kaaba block
        val s = r * 0.85f
        drawRect(color, Offset(center.x - s / 2, center.y - s / 2.4f), Size(s, s * 0.9f), style = Stroke(r * 0.1f))
        drawLine(color, Offset(center.x - s / 2, center.y - s * 0.12f), Offset(center.x + s / 2, center.y - s * 0.12f), r * 0.1f)
        drawRect(color, Offset(center.x - s * 0.12f, center.y + s * 0.05f), Size(s * 0.24f, s * 0.38f))
    }
}
