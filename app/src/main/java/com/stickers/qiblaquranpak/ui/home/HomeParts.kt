package com.stickers.qiblaquranpak.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HolidayVillage
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.stickers.qiblaquranpak.ui.components.drawKaaba
import kotlin.math.cos
import kotlin.math.sin

object HomeIcons {
    val Duas: ImageVector = Icons.Filled.VolunteerActivism
    val Tasbih: ImageVector = Icons.Filled.Spa
    val Ayaat: ImageVector = Icons.Filled.FormatQuote
    val Names: ImageVector = Icons.Filled.AutoAwesome
    val Kalmas: ImageVector = Icons.Filled.Translate
    val DailyDeen: ImageVector = Icons.Filled.Timer
    val Azkar: ImageVector = Icons.Filled.WbSunny
    val Hajj: ImageVector = Icons.Filled.HolidayVillage
    val More: ImageVector = Icons.Filled.Dashboard
}

/** Network image with a gradient stand-in while loading / offline. */
@Composable
fun RemoteImage(url: String?, fallback: List<Color>, modifier: Modifier) {
    Box(modifier.background(Brush.linearGradient(fallback))) {
        if (url != null) AsyncImage(url, null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
    }
}

/** Black flat compass used by the Qibla widget and Home quick-access card. */
@Composable
fun MiniCompass(heading: Float, qibla: Float?) {
    val m = rememberTextMeasurer()
    Canvas(Modifier.fillMaxSize().graphicsLayer { rotationZ = -heading }) {
        val c = center
        val r = size.minDimension / 2f
        run {
            for (d in 0 until 360 step 15) {
                val a = Math.toRadians(d - 90.0)
                val len = if (d % 90 == 0) r * 0.22f else r * 0.14f
                drawLine(
                    Color.White,
                    Offset(c.x + (r - len) * cos(a).toFloat(), c.y + (r - len) * sin(a).toFloat()),
                    Offset(c.x + r * cos(a).toFloat(), c.y + r * sin(a).toFloat()),
                    strokeWidth = if (d % 90 == 0) r * 0.05f else r * 0.035f,
                )
            }
            val st = TextStyle(color = Color.White, fontSize = (r * 0.22f / density).sp, fontWeight = FontWeight.Bold)
            listOf("N" to 0, "E" to 90, "S" to 180, "W" to 270).forEach { (t, d) ->
                val l = m.measure(t, st)
                val a = Math.toRadians(d - 90.0)
                val p = Offset(c.x + r * 0.56f * cos(a).toFloat(), c.y + r * 0.56f * sin(a).toFloat())
                drawText(l, topLeft = Offset(p.x - l.size.width / 2f, p.y - l.size.height / 2f))
            }
            if (qibla != null) {
                rotate(qibla, c) {
                    drawPath(Path().apply { moveTo(c.x, c.y - r * 0.75f); lineTo(c.x - r * 0.1f, c.y); lineTo(c.x + r * 0.1f, c.y); close() }, Color(0xFFE53935))
                    drawPath(Path().apply { moveTo(c.x, c.y + r * 0.75f); lineTo(c.x - r * 0.1f, c.y); lineTo(c.x + r * 0.1f, c.y); close() }, Color.White)
                }
                val a = Math.toRadians(qibla - 90.0)
                val s = r * 0.34f
                val p = Offset(c.x + r * 0.78f * cos(a).toFloat(), c.y + r * 0.78f * sin(a).toFloat())
                drawKaaba(Offset(p.x - s / 2, p.y - s / 2), s)
            }
        }
    }
}
