package com.stickers.qiblaquranpak.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.stickers.qiblaquranpak.ui.theme.Nunito
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Silver bezel compass. The dial rotates by -[heading] so N points to real north; the Kaaba marker sits on
 * the ring at [qibla] degrees. Rotation is animated along the shortest arc.
 */
@Composable
fun CompassDial(heading: Float, qibla: Float?, modifier: Modifier = Modifier, showKaaba: Boolean = true) {
    val measurer = rememberTextMeasurer()
    val anim = remember { Animatable(-heading) }
    LaunchedEffect(heading) {
        val target = -heading
        var delta = (target - anim.value) % 360f
        if (delta > 180f) delta -= 360f
        if (delta < -180f) delta += 360f
        anim.animateTo(anim.value + delta, tween(180))
    }
    // Static layers are recorded once per size; each sensor tick only changes the layer rotation
    // (no text measuring or path building per frame).
    Box(modifier) {
        Canvas(Modifier.fillMaxSize()) { drawBezel(center, min(size.width, size.height) / 2f) }
        Spacer(
            Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = anim.value }
                .drawWithCache {
                    val r = min(size.width, size.height) / 2f
                    val face = r * 0.86f
                    onDrawBehind {
                        drawFace(measurer, center, face)
                        drawRose(center, face * 0.48f)
                        if (showKaaba && qibla != null) drawKaabaOnRing(center, r * 0.93f, qibla, r * 0.17f)
                    }
                },
        )
        Canvas(Modifier.fillMaxSize()) {
            val r = min(size.width, size.height) / 2f
            drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFB9B9B9)), center, r * 0.07f), r * 0.07f, center)
        }
    }
}

private fun DrawScope.drawBezel(c: Offset, r: Float) {
    drawCircle(
        Brush.linearGradient(
            listOf(Color(0xFFF7F7F7), Color(0xFF9C9C9C), Color(0xFFEDEDED), Color(0xFF8A8A8A), Color(0xFFF2F2F2)),
            start = Offset(c.x - r, c.y - r), end = Offset(c.x + r, c.y + r),
        ),
        r, c,
    )
    drawCircle(Color(0xFF7D7D7D), r * 0.885f, c, style = Stroke(r * 0.012f))
}

private fun DrawScope.drawFace(m: TextMeasurer, c: Offset, r: Float) {
    drawCircle(Brush.radialGradient(listOf(Color.White, Color(0xFFF1F1F1)), c, r), r, c)
    for (d in 0 until 360 step 2) {
        val major = d % 10 == 0
        val len = if (major) r * 0.09f else r * 0.05f
        val a = Math.toRadians(d.toDouble() - 90)
        val o = Offset(c.x + (r * 0.98f) * cos(a).toFloat(), c.y + (r * 0.98f) * sin(a).toFloat())
        val i = Offset(c.x + (r * 0.98f - len) * cos(a).toFloat(), c.y + (r * 0.98f - len) * sin(a).toFloat())
        drawLine(Color(0xFF3A3A3A), i, o, strokeWidth = if (major) r * 0.012f else r * 0.006f)
    }
    val num = TextStyle(fontFamily = Nunito, fontSize = (r * 0.065f / density).sp, color = Color(0xFF333333))
    for (d in 0 until 360 step 20) drawRadialText(m, d.toString(), c, r * 0.8f, d.toFloat(), num)
    val big = TextStyle(fontFamily = Nunito, fontSize = (r * 0.13f / density).sp, fontWeight = FontWeight.Medium, color = Color(0xFF222222))
    val small = big.copy(fontSize = (r * 0.075f / density).sp)
    listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f).forEach { (t, d) -> drawRadialText(m, t, c, r * 0.62f, d, big) }
    listOf("NE" to 45f, "SE" to 135f, "SW" to 225f, "NW" to 315f).forEach { (t, d) -> drawRadialText(m, t, c, r * 0.6f, d, small) }
}

private fun DrawScope.drawRadialText(m: TextMeasurer, text: String, c: Offset, radius: Float, deg: Float, style: TextStyle) {
    val layout = m.measure(text, style)
    val a = Math.toRadians(deg.toDouble() - 90)
    val p = Offset(c.x + radius * cos(a).toFloat(), c.y + radius * sin(a).toFloat())
    withTransform({ rotate(deg, p) }) {
        drawText(layout, topLeft = Offset(p.x - layout.size.width / 2f, p.y - layout.size.height / 2f))
    }
}

private fun DrawScope.drawRose(c: Offset, r: Float) {
    // 8-point star: long cardinal points, short intercardinals, two-tone facets.
    for (k in 0 until 8) {
        val deg = k * 45f
        val len = if (k % 2 == 0) r else r * 0.62f
        val w = r * 0.14f
        rotate(deg, c) {
            val tip = Offset(c.x, c.y - len)
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x - w, c.y - w); lineTo(tip.x, tip.y); close() }, Color(0xFF2E2E2E))
            drawPath(Path().apply { moveTo(c.x, c.y); lineTo(c.x + w, c.y - w); lineTo(tip.x, tip.y); close() }, Color(0xFF6E6E6E))
        }
    }
    // Needle: red north, blue south.
    val nw = r * 0.12f
    drawPath(Path().apply { moveTo(c.x, c.y - r * 1.15f); lineTo(c.x - nw, c.y); lineTo(c.x + nw, c.y); close() }, Color(0xFFD32F2F))
    drawPath(Path().apply { moveTo(c.x, c.y + r * 1.15f); lineTo(c.x - nw, c.y); lineTo(c.x + nw, c.y); close() }, Color(0xFF2F6FD3))
}

private fun DrawScope.drawKaabaOnRing(c: Offset, radius: Float, deg: Float, s: Float) {
    val a = Math.toRadians(deg.toDouble() - 90)
    val p = Offset(c.x + radius * cos(a).toFloat(), c.y + radius * sin(a).toFloat())
    rotate(deg, p) { drawKaaba(Offset(p.x - s / 2, p.y - s / 2), s) }
}

/** Simple Kaaba glyph: black cube with a gold band and door. */
fun DrawScope.drawKaaba(topLeft: Offset, s: Float) {
    val top = Path().apply {
        moveTo(topLeft.x + s * 0.5f, topLeft.y)
        lineTo(topLeft.x + s, topLeft.y + s * 0.22f)
        lineTo(topLeft.x + s * 0.5f, topLeft.y + s * 0.44f)
        lineTo(topLeft.x, topLeft.y + s * 0.22f)
        close()
    }
    val left = Path().apply {
        moveTo(topLeft.x, topLeft.y + s * 0.22f)
        lineTo(topLeft.x + s * 0.5f, topLeft.y + s * 0.44f)
        lineTo(topLeft.x + s * 0.5f, topLeft.y + s)
        lineTo(topLeft.x, topLeft.y + s * 0.78f)
        close()
    }
    val right = Path().apply {
        moveTo(topLeft.x + s * 0.5f, topLeft.y + s * 0.44f)
        lineTo(topLeft.x + s, topLeft.y + s * 0.22f)
        lineTo(topLeft.x + s, topLeft.y + s * 0.78f)
        lineTo(topLeft.x + s * 0.5f, topLeft.y + s)
        close()
    }
    drawPath(top, Color(0xFF3A3A3A))
    drawPath(left, Color(0xFF111111))
    drawPath(right, Color(0xFF1E1E1E))
    val gold = Color(0xFFD9A93A)
    drawLine(gold, Offset(topLeft.x, topLeft.y + s * 0.36f), Offset(topLeft.x + s * 0.5f, topLeft.y + s * 0.58f), s * 0.07f)
    drawLine(gold, Offset(topLeft.x + s * 0.5f, topLeft.y + s * 0.58f), Offset(topLeft.x + s, topLeft.y + s * 0.36f), s * 0.07f)
    drawRect(gold, Offset(topLeft.x + s * 0.64f, topLeft.y + s * 0.62f), Size(s * 0.12f, s * 0.2f))
}
