package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.system.SystemTelemetry
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.CyberBlueSecondary
import com.example.ui.theme.CyberBlueTertiary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.StatusRed
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WolfDarkBorder
import com.example.ui.theme.WolfDarkSurface
import com.example.ui.theme.WolfDarkSurfaceVariant

@Composable
fun TelemetryHeader(
    telemetry: SystemTelemetry,
    language: AppLanguage,
    onOpenDevSettings: () -> Unit,
    onOpenDeviceInfo: () -> Unit,
    onOpenPermissions: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("telemetry_header_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WolfDarkSurface),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(CyberBluePrimary.copy(alpha = 0.6f), CyberBlueSecondary.copy(alpha = 0.2f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top status line with live internet and permissions shortcut
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (telemetry.isDevModeUnlocked) StatusGreen else StatusAmber)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = LanguageManager.getUiString("realtime_telemetry", language).uppercase(),
                        color = CyberBlueSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Live Internet status pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (telemetry.isInternetConnected) StatusGreen.copy(alpha = 0.15f) else StatusRed.copy(alpha = 0.15f))
                            .border(1.dp, if (telemetry.isInternetConnected) StatusGreen.copy(alpha = 0.4f) else StatusRed.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = if (telemetry.isInternetConnected) "🌐 ONLINE" else "🔴 OFFLINE",
                            color = if (telemetry.isInternetConnected) StatusGreen else StatusRed,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Super Permissions button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberBluePrimary.copy(alpha = 0.2f))
                        .border(1.dp, CyberBluePrimary, RoundedCornerShape(6.dp))
                        .clickable { onOpenPermissions() }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                        .testTag("super_permissions_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🛡️ SUPER PERMISOS",
                        color = TextWhite,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metrics Grid (4 items)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = LanguageManager.getUiString("dev_mode", language),
                    value = if (telemetry.isDevModeUnlocked) {
                        LanguageManager.getUiString("active", language)
                    } else {
                        LanguageManager.getUiString("inactive", language)
                    },
                    icon = Icons.Default.DeveloperMode,
                    valueColor = if (telemetry.isDevModeUnlocked) StatusGreen else StatusAmber
                )

                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = LanguageManager.getUiString("adb_status", language),
                    value = if (telemetry.isAdbEnabled) "ONLINE" else "USB OFF",
                    icon = Icons.Default.Usb,
                    valueColor = if (telemetry.isAdbEnabled) StatusGreen else TextMuted
                )

                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = "RAM USAGE",
                    value = "${telemetry.usedRamPercent}% (${telemetry.freeRamMb}MB)",
                    icon = Icons.Default.Memory,
                    valueColor = if (telemetry.usedRamPercent < 80) CyberBlueTertiary else StatusAmber
                )

                MetricChip(
                    modifier = Modifier.weight(1f),
                    label = "BATTERY",
                    value = "${telemetry.batteryPercent}% ${telemetry.batteryTempCelsius}°C",
                    icon = Icons.Default.Speed,
                    valueColor = if (telemetry.batteryTempCelsius < 38f) TextWhite else StatusAmber
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Unlocking Developer Options Wizard Banner
            if (!telemetry.isDevModeUnlocked) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF241505))
                        .border(1.dp, StatusAmber.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LockOpen,
                                contentDescription = null,
                                tint = StatusAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = LanguageManager.getUiString("unlock_title", language),
                                color = TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = LanguageManager.getUiString("unlock_desc", language),
                            color = TextGray,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onOpenDeviceInfo,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusAmber,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.testTag("open_about_phone_btn")
                            ) {
                                Text(
                                    text = LanguageManager.getUiString("open_device_info", language),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = onOpenDevSettings,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                            ) {
                                Text(
                                    text = LanguageManager.getUiString("open_dev_settings", language),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            } else {
                // Developer options are unlocked: show 1-tap open button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OPCIONES DESARROLLADOR ACTIVAS",
                            color = StatusGreen,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = LanguageManager.getUiString("safe_android_notice", language),
                            color = TextMuted,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }

                    Button(
                        onClick = onOpenDevSettings,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CyberBluePrimary,
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("open_dev_settings_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeveloperMode,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = LanguageManager.getUiString("open_dev_settings", language),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricChip(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    valueColor: Color
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(WolfDarkSurfaceVariant)
            .border(1.dp, WolfDarkBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyberBlueSecondary,
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    fontFamily = FontFamily.Monospace
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = valueColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
