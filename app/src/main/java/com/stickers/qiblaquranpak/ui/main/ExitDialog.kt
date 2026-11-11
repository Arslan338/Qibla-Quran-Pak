package com.stickers.qiblaquranpak.ui.main

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.stickers.qiblaquranpak.ui.components.VSpace
import com.stickers.qiblaquranpak.ui.theme.AppColors

fun Context.openPlayStore() {
    val uri = Uri.parse("market://details?id=$packageName")
    try {
        startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    } catch (_: ActivityNotFoundException) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$packageName")).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

fun Context.sendFeedback(rating: Int) {
    val i = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        .putExtra(Intent.EXTRA_SUBJECT, "Qibla Compass feedback ($rating★)")
    try { startActivity(i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) } catch (_: ActivityNotFoundException) {
        Toast.makeText(this, "No email app found", Toast.LENGTH_SHORT).show()
    }
}

/** "Your Good Rating Make a Difference" — shown when backing out of Home. */
@Composable
fun ExitDialog(onDismiss: () -> Unit, onExit: () -> Unit) {
    val ctx = LocalContext.current
    var rating by remember { mutableIntStateOf(0) }
    Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White).padding(horizontal = 20.dp, vertical = 18.dp)) {
            Text("Your Good Rating Make a Difference", fontSize = 16.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            VSpace(14.dp)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                for (i in 1..5) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(48.dp)) {
                        Icon(
                            if (i <= rating) Icons.Filled.Star else Icons.Outlined.StarOutline, "$i stars",
                            tint = if (i <= rating) Color(0xFFFFB300) else Color.Black,
                            modifier = Modifier.size(34.dp).clickable { rating = i },
                        )
                        Text(when (i) { 1 -> "Bad"; 5 -> "Good"; else -> "" }, fontSize = 14.sp, color = AppColors.TextSecondary)
                    }
                }
            }
            VSpace(30.dp)
            Row(Modifier.align(Alignment.End), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    if (rating == 0) "Cancel" else "Rate", fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        when {
                            rating == 0 -> onDismiss()
                            rating >= 4 -> { ctx.openPlayStore(); onDismiss() }
                            else -> { ctx.sendFeedback(rating); onDismiss() }
                        }
                    }.padding(8.dp),
                )
                Spacer(Modifier.width(18.dp))
                Text("Exit", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppColors.Green, modifier = Modifier.clickable(onClick = onExit).padding(8.dp))
            }
        }
    }
}
