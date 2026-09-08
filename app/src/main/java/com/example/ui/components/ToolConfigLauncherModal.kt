package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.LanguageManager
import com.example.model.DevTool
import com.example.model.SafeLevel
import com.example.ui.theme.CyberBluePrimary
import com.example.ui.theme.CyberBlueSecondary
import com.example.ui.theme.CyberBlueTertiary
import com.example.ui.theme.StatusAmber
import com.example.ui.theme.StatusGreen
import com.example.ui.theme.TextGray
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WolfDarkBackground
import com.example.ui.theme.WolfDarkBorder
import com.example.ui.theme.WolfDarkSurface
import com.example.ui.theme.WolfDarkSurfaceVariant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolConfigLauncherModal(
    tool: DevTool,
    language: AppLanguage,
    selectedOption: String?,
    onOpenWirelessDebugging: () -> Unit,
    onOpenSmartSettings: () -> Unit,
    onOpenDeveloperOptions: () -> Unit,
    onOpenAboutPhone: () -> Unit,
    onCopyAdb: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentAdbCommand = if (selectedOption != null) {
        tool.adbCommand.replace(Regex("(put \\w+ [^\\s]+ )(\\S+)"), "$1$selectedOption")
    } else {
        tool.adbCommand.ifEmpty {
            "adb shell settings put ${tool.settingType.name.lowercase()} ${tool.settingKey} 1"
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WolfDarkSurface,
        contentColor = TextWhite,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyberBluePrimary.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "CONFIGURACIÓN DEL TELÉFONO",
                                color = CyberBlueSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        val (safeBadge, safeColor) = when (tool.safeLevel) {
                            SafeLevel.SAFE_NATIVE -> "100% SEGURO" to StatusGreen
                            SafeLevel.DEVELOPER_ONLY -> "OPCIONES DEV" to CyberBlueTertiary
                            SafeLevel.ADVANCED_ADB -> "ADB / WIRELESS" to StatusAmber
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(safeColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = safeBadge,
                                color = safeColor,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tool.name,
                        color = TextWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cerrar", tint = TextGray)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = tool.description,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // ACCIONES PRINCIPALES DEL SISTEMA (TELEFONO & WIRELESS)
            // ========================================================
            Text(
                text = "⚡ ACCESO DIRECTO AL SISTEMA & TELÉFONO",
                color = CyberBlueSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botón 1: Activar Depuración Inalámbrica (Principal y Destacado)
            Button(
                onClick = onOpenWirelessDebugging,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00E676).copy(alpha = 0.18f),
                    contentColor = Color(0xFF00E676)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, Color(0xFF00E676), RoundedCornerShape(12.dp))
                    .testTag("btn_launcher_wireless_debugging")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF00E676).copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = null,
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📡 Activar Depuración Inalámbrica",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )
                        Text(
                            text = "Abre la pantalla de Android 11+ para emparejar por Wi-Fi sin PC",
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botón 2: Abrir Ajustes Específicos de la Herramienta en el Teléfono
            Button(
                onClick = onOpenSmartSettings,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBluePrimary,
                    contentColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_launcher_tool_settings")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberBlueSecondary.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            tint = TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚙️ Abrir Ajustes de esta Herramienta",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Te lleva a la pantalla exacta de configuración en tu teléfono",
                            fontSize = 11.sp,
                            color = TextGray
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Launch,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Botones Secundarios: Opciones Desarrollador + Desbloquear Modo Dev
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenDeveloperOptions,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlueTertiary),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_launcher_dev_options")
                ) {
                    Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Opciones Dev", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                OutlinedButton(
                    onClick = onOpenAboutPhone,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_launcher_about_phone")
                ) {
                    Icon(imageVector = Icons.Default.PhoneAndroid, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Acerca del Teléfono", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // CONSOLA ADB Y COMANDO DIRECTO
            // ========================================================
            Text(
                text = "💻 COMANDO ADB CON 1 TOQUE",
                color = CyberBlueSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(6.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WolfDarkBackground)
                    .border(1.dp, WolfDarkBorder, RoundedCornerShape(10.dp))
                    .clickable { onCopyAdb(currentAdbCommand) }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Terminal,
                            contentDescription = null,
                            tint = CyberBlueTertiary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = currentAdbCommand,
                            color = TextWhite,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            maxLines = 2
                        )
                    }

                    IconButton(
                        onClick = { onCopyAdb(currentAdbCommand) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copiar",
                            tint = CyberBlueSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ========================================================
            // GUÍA PASO A PASO: DEPURACIÓN INALÁMBRICA SIN PC
            // ========================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WolfDarkSurfaceVariant)
                    .border(1.dp, WolfDarkBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = CyberBlueTertiary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guía: ¿Cómo activar sin computadora?",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "1. Conecta tu teléfono a una red Wi-Fi.\n" +
                               "2. Toca «Activar Depuración Inalámbrica» arriba.\n" +
                               "3. Activa el interruptor y pulsa «Vincular dispositivo con código de sincronización».\n" +
                               "4. Usa aplicaciones como Shizuku o LADB en tu teléfono para emparejar con el puerto y código de 6 dígitos.\n" +
                               "5. ¡Listo! Podrás aplicar cualquier herramienta de desarrollador al instante.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
            ) {
                Text(text = "Cerrar", fontSize = 12.sp)
            }
        }
    }
}
