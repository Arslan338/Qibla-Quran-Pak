package com.stickers.qiblaquranpak.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.stickers.qiblaquranpak.ui.theme.AppColors

/** Green toolbar with back arrow used by every secondary screen. */
@Composable
fun GreenTopBar(title: String, onBack: () -> Unit, actions: @Composable () -> Unit = {}) {
    Row(
        Modifier
            .fillMaxWidth()
            .background(AppColors.Green)
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.White) }
        Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        actions()
    }
}

/** Circular outlined "↗" button shown on feature cards. */
@Composable
fun ArrowCircle(size: Dp = 34.dp, color: Color = Color.White, modifier: Modifier = Modifier) {
    Box(
        modifier
            .size(size)
            .border(1.5.dp, color.copy(alpha = 0.85f), CircleShape),
        contentAlignment = Alignment.Center,
    ) { Icon(Icons.Filled.NorthEast, null, tint = color, modifier = Modifier.size(size * 0.55f)) }
}

/** Red "NEW" ribbon tab that hangs over a card's top edge. */
@Composable
fun NewRibbon(modifier: Modifier = Modifier) {
    Box(modifier.width(64.dp).height(24.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width; val h = size.height
            drawPath(Path().apply {
                moveTo(w * 0.1f, 0f); lineTo(w * 0.9f, 0f); lineTo(w, h * 0.35f); lineTo(w * 0.86f, h)
                quadraticTo(w * 0.5f, h * 1.25f, w * 0.14f, h); lineTo(0f, h * 0.35f); close()
            }, Brush.verticalGradient(listOf(Color(0xFFE8473C), Color(0xFFC62E24))))
        }
        Text("NEW", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}

/**
 * Stand-in for the reference app's painted artwork. Same size and corner radius as the original;
 * swap in real artwork later.
 */
@Composable
fun ArtPlaceholder(
    modifier: Modifier,
    colors: List<Color> = listOf(Color(0xFF3F7A4A), Color(0xFF1D3F24)),
    icon: ImageVector? = null,
    iconTint: Color = Color.White.copy(alpha = 0.6f),
    radius: Dp = 0.dp,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier
            .clip(RoundedCornerShape(radius))
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center,
    ) {
        if (icon != null) Icon(icon, null, tint = iconTint, modifier = Modifier.fillMaxSize(0.5f))
        content()
    }
}

/** Decorative mosque silhouette (dome, minaret, crescent) drawn on the green header. */
@Composable
fun MosqueBackdrop(modifier: Modifier = Modifier) {
    Canvas(modifier) {
        val w = size.width
        val h = size.height
        val light = Color(0xFF3D6E48)
        // Minaret (centre-left): slim shaft, a balcony band and an onion cap; fades out downwards.
        val mx = w * 0.28f
        val shaft = Brush.verticalGradient(listOf(Color(0xFF4F7F57), Color(0xFF3D6E48).copy(alpha = 0f)), startY = h * 0.14f, endY = h * 0.62f)
        drawRect(shaft, Offset(mx - w * 0.022f, h * 0.14f), androidx.compose.ui.geometry.Size(w * 0.044f, h * 0.48f))
        drawRect(Color(0xFF5B8A61), Offset(mx - w * 0.034f, h * 0.27f), androidx.compose.ui.geometry.Size(w * 0.068f, h * 0.014f))
        drawPath(Path().apply {
            moveTo(mx - w * 0.026f, h * 0.15f)
            quadraticTo(mx - w * 0.04f, h * 0.09f, mx, h * 0.045f)
            quadraticTo(mx + w * 0.04f, h * 0.09f, mx + w * 0.026f, h * 0.15f)
            close()
        }, Color(0xFFC9D98A).copy(alpha = 0.8f))
        // Right dome
        drawPath(Path().apply {
            moveTo(w * 0.72f, h); quadraticTo(w * 0.72f, h * 0.45f, w * 0.9f, h * 0.3f)
            quadraticTo(w * 1.08f, h * 0.45f, w * 1.08f, h); close()
        }, light)
        // Crescent
        drawCircle(Color(0xFFF1E3A6), w * 0.05f, Offset(w * 0.9f, h * 0.06f))
        drawCircle(AppColors.Green, w * 0.045f, Offset(w * 0.875f, h * 0.045f))
    }
}

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier, color: Color = AppColors.TextPrimary, trailing: String? = null, onTrailing: () -> Unit = {}) {
    Row(modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = color, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        if (trailing != null) {
            Text(trailing, color = AppColors.Green, fontSize = 17.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable(onClick = onTrailing))
        }
    }
}

@Composable
fun VSpace(h: Dp) = Spacer(Modifier.height(h))

@Composable
fun HSpace(w: Dp) = Spacer(Modifier.width(w))
