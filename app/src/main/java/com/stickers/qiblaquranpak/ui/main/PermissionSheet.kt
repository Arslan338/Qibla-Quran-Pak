package com.stickers.qiblaquranpak.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.stickers.qiblaquranpak.ui.components.VSpace
import com.stickers.qiblaquranpak.ui.theme.AppColors

fun Context.hasNotificationPermission() =
    Build.VERSION.SDK_INT < 33 || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

fun Context.hasLocationPermission() =
    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

fun Context.openAppSettings() {
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", packageName, null)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

/** "Please provide these permissions" sheet with Notification + Location toggles. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PermissionSheet(onLocationGranted: () -> Unit, onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    var notif by remember { mutableStateOf(ctx.hasNotificationPermission()) }
    var loc by remember { mutableStateOf(ctx.hasLocationPermission()) }
    var rationale by remember { mutableStateOf(false) }

    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { notif = it }
    val locLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { res ->
        loc = res.values.any { it }
        if (loc) onLocationGranted() else rationale = true
    }
    LaunchedEffect(notif, loc) { if (notif && loc) onDismiss() }

    val sheet = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheet, containerColor = Color.White, dragHandle = null) {
        Column(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 16.dp, vertical = 20.dp)) {
            Text("Please provide these permissions", fontSize = 19.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
            VSpace(18.dp)
            PermRow("Notification", "To notify you when to pray", notif) {
                if (Build.VERSION.SDK_INT >= 33) notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
            VSpace(16.dp)
            PermRow("Location", "To provide prayer time precisely", loc) {
                locLauncher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
            }
            VSpace(10.dp)
        }
    }
    if (rationale) LocationRationaleDialog(onDismiss = { rationale = false })
}

@Composable
private fun PermRow(title: String, subtitle: String, granted: Boolean, onRequest: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).background(Color(0xFFE9E9E9)).padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, fontSize = 16.sp)
        }
        Switch(
            granted, { if (it && !granted) onRequest() },
            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2F8C83), checkedTrackColor = Color(0xFF8CC6C0), uncheckedThumbColor = Color.White, uncheckedTrackColor = Color(0xFF8E8E8E), uncheckedBorderColor = Color.Transparent),
        )
    }
}

/** Green-header "Permission" dialog shown after the user denies location. Grant opens app settings. */
@Composable
fun LocationRationaleDialog(onDismiss: () -> Unit) {
    val ctx = LocalContext.current
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(Color.White)) {
            Text(
                "Permission", color = Color.White, fontSize = 20.sp,
                modifier = Modifier.fillMaxWidth().background(AppColors.Green).padding(horizontal = 16.dp, vertical = 12.dp),
            )
            Text(
                "Qibla Compass will ask you to grant Location permission to better perform functions.",
                fontSize = 17.sp, modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 34.dp, bottom = 46.dp),
            )
            Row(Modifier.fillMaxWidth().padding(bottom = 20.dp), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center) {
                androidx.compose.material3.OutlinedButton(
                    onClick = onDismiss, shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.Green),
                    modifier = Modifier.padding(horizontal = 10.dp).width(104.dp).height(40.dp),
                ) { Text("Cancel", color = AppColors.Green, fontSize = 16.sp) }
                androidx.compose.material3.Button(
                    onClick = { onDismiss(); ctx.openAppSettings() }, shape = RoundedCornerShape(12.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = AppColors.Green),
                    modifier = Modifier.padding(horizontal = 10.dp).width(104.dp).height(40.dp),
                ) { Text("Grant", color = Color.White, fontSize = 16.sp) }
            }
        }
    }
}

