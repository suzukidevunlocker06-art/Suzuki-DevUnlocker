package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.system.SystemDevBridge
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
import kotlinx.coroutines.launch

@Composable
fun InternetToolsCard(
    telemetry: SystemTelemetry,
    language: AppLanguage,
    onOpenDevSettings: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var isTestingPing by remember { mutableStateOf(false) }
    var pingResultMs by remember { mutableStateOf<Long?>(null) }
    var pingHost by remember { mutableStateOf("8.8.8.8 (Google DNS)") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("internet_tools_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WolfDarkSurface),
        border = BorderStroke(
            1.dp,
            Brush.horizontalGradient(
                listOf(CyberBlueSecondary.copy(alpha = 0.5f), CyberBluePrimary.copy(alpha = 0.2f))
            )
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(CyberBluePrimary.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (telemetry.isInternetConnected) Icons.Default.Wifi else Icons.Default.WifiOff,
                            contentDescription = null,
                            tint = if (telemetry.isInternetConnected) CyberBlueSecondary else StatusRed,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column {
                        Text(
                            text = "DIAGNÓSTICO Y RENDIMIENTO DE RED",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "${telemetry.networkType} • IP: ${telemetry.ipAddress}",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (telemetry.isInternetConnected) StatusGreen.copy(alpha = 0.2f) else StatusRed.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (telemetry.isInternetConnected) StatusGreen else StatusRed)
                        )
                        Text(
                            text = if (telemetry.isInternetConnected) "EN LÍNEA" else "SIN INTERNET",
                            color = if (telemetry.isInternetConnected) StatusGreen else StatusRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Ping test box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WolfDarkSurfaceVariant)
                    .border(1.dp, WolfDarkBorder, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "LATENCIA REAL DE INTERNET (PING)",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = when {
                                isTestingPing -> "Midiendo..."
                                pingResultMs != null && pingResultMs!! >= 0 -> "${pingResultMs} ms"
                                pingResultMs != null && pingResultMs!! < 0 -> "Tiempo agotado"
                                else -> "Toca 'Probar Ping'"
                            },
                            color = when {
                                isTestingPing -> CyberBlueSecondary
                                pingResultMs != null && pingResultMs!! in 0..60 -> StatusGreen
                                pingResultMs != null && pingResultMs!! in 61..140 -> StatusAmber
                                pingResultMs != null && pingResultMs!! > 140 -> StatusRed
                                else -> TextWhite
                            },
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        if (pingResultMs != null && pingResultMs!! >= 0) {
                            Text(
                                text = "a $pingHost",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Button(
                    onClick = {
                        if (!isTestingPing) {
                            isTestingPing = true
                            coroutineScope.launch {
                                val result = SystemDevBridge.measureRealPing("8.8.8.8", 53, 1500)
                                pingResultMs = if (result >= 0) result else {
                                    // Fallback test to 1.1.1.1
                                    SystemDevBridge.measureRealPing("1.1.1.1", 53, 1500)
                                }
                                isTestingPing = false
                                SystemDevBridge.triggerHaptic(context)
                            }
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyberBluePrimary),
                    enabled = !isTestingPing
                ) {
                    if (isTestingPing) {
                        CircularProgressIndicator(modifier = Modifier.size(14.dp), color = TextWhite, strokeWidth = 2.dp)
                    } else {
                        Icon(imageVector = Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(14.dp), tint = TextWhite)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Probar Ping", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Network Developer Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val cmd = "adb shell settings put global mobile_data_always_on 1"
                        SystemDevBridge.copyToClipboard(context, cmd)
                        Toast.makeText(context, "⚡ Comando Datos Móviles Siempre Activos copiado", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, WolfDarkBorder)
                ) {
                    Text(text = "⚡ Acelerar 5G/Wi-Fi", color = CyberBlueSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = {
                        val cmd = "adb shell settings put global wifi_verbose_logging_enabled 1"
                        SystemDevBridge.copyToClipboard(context, cmd)
                        Toast.makeText(context, "📡 Comando Wi-Fi Verbose Logging copiado", Toast.LENGTH_SHORT).show()
                        onOpenDevSettings()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, WolfDarkBorder)
                ) {
                    Text(text = "📡 Wi-Fi Detallado", color = CyberBlueTertiary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
