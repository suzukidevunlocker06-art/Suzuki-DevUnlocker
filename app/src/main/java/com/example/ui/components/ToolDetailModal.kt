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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Info
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
fun ToolDetailModal(
    tool: DevTool,
    language: AppLanguage,
    selectedOption: String?,
    onSelectOption: (String) -> Unit,
    onOpenSystemIntent: () -> Unit,
    onCopyAdb: (String) -> Unit,
    onDismiss: () -> Unit,
    onOpenWirelessDebugging: () -> Unit = {},
    onOpenDeveloperOptions: () -> Unit = {}
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = WolfDarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
                .testTag("tool_detail_modal")
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberBluePrimary.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = LanguageManager.getCategoryName(tool.category, language).uppercase(),
                        color = CyberBlueSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tool Title
            Text(
                text = tool.name,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Setting key badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(WolfDarkBackground)
                    .border(1.dp, WolfDarkBorder, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${tool.settingType.name.lowercase()}.${tool.settingKey}",
                    color = CyberBlueTertiary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Detailed explanation
            Text(
                text = tool.description,
                color = TextGray,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Android Security Notice Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(WolfDarkSurfaceVariant)
                    .border(1.dp, WolfDarkBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = when (tool.safeLevel) {
                            SafeLevel.SAFE_NATIVE -> StatusGreen
                            SafeLevel.DEVELOPER_ONLY -> CyberBlueTertiary
                            SafeLevel.ADVANCED_ADB -> StatusAmber
                        },
                        modifier = Modifier
                            .size(20.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = when (tool.safeLevel) {
                                SafeLevel.SAFE_NATIVE -> "Nivel de Seguridad: Seguro Nativo"
                                SafeLevel.DEVELOPER_ONLY -> "Nivel de Seguridad: Opciones Desarrollador"
                                SafeLevel.ADVANCED_ADB -> "Nivel de Seguridad: Requiere ADB o Shizuku"
                            },
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = LanguageManager.getUiString("safe_android_notice", language),
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Options List
            Text(
                text = "OPCIONES DISPONIBLES (${tool.options.size})",
                color = CyberBlueSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                tool.options.forEach { opt ->
                    val isSelected = selectedOption == opt.value
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) CyberBluePrimary.copy(alpha = 0.25f)
                                else WolfDarkSurfaceVariant
                            )
                            .border(
                                width = 1.dp,
                                color = if (isSelected) CyberBlueSecondary else WolfDarkBorder.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSelectOption(opt.value) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = opt.label,
                                color = if (isSelected) TextWhite else TextWhite.copy(alpha = 0.9f),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            if (opt.description.isNotEmpty()) {
                                Text(
                                    text = opt.description,
                                    color = TextGray,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(CyberBlueSecondary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = WolfDarkBackground,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Copyable ADB Command box
            if (tool.adbCommand.isNotEmpty()) {
                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "COMANDO ADB SHELL (SUZUKI / PC)",
                    color = CyberBlueSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                val curAdbCmd = if (selectedOption != null) {
                    tool.adbCommand.replace(Regex("(put \\w+ [^\\s]+ )(\\S+)"), "$1$selectedOption")
                } else tool.adbCommand

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(WolfDarkBackground)
                        .border(1.dp, WolfDarkBorder, RoundedCornerShape(10.dp))
                        .clickable { onCopyAdb(curAdbCmd) }
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
                                text = curAdbCmd,
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 2
                            )
                        }

                        IconButton(
                            onClick = { onCopyAdb(curAdbCmd) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = CyberBlueSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ========================================================
            // ACCIONES DIRECTAS EN TU TELÉFONO
            // ========================================================
            Text(
                text = "⚡ ACCIONES DIRECTAS EN TU TELÉFONO",
                color = CyberBlueSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Botón 1: Activar Depuración Inalámbrica
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
                    .testTag("modal_btn_wireless_debugging")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = null,
                        tint = Color(0xFF00E676),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "📡 Activar Depuración Inalámbrica",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00E676)
                        )
                        Text(
                            text = "Abre la pantalla de Android para emparejar por Wi-Fi sin PC",
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

            // Botón 2: Abrir Ajustes de la Herramienta en el Teléfono
            Button(
                onClick = onOpenSystemIntent,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyberBluePrimary,
                    contentColor = TextWhite
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("modal_open_system_btn")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = TextWhite,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "⚙️ Abrir Ajustes de esta Herramienta",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                        Text(
                            text = "Te lleva a la pantalla de configuración en tu teléfono",
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

            // Botones Secundarios: Opciones Desarrollador + Listo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenDeveloperOptions,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CyberBlueTertiary),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(imageVector = Icons.Default.DeveloperMode, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Opciones Dev", fontSize = 11.sp)
                }

                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Listo", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Guía Rápida de Depuración Inalámbrica
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
                            text = "¿Cómo usar Depuración Inalámbrica?",
                            color = TextWhite,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "1. Toca «Activar Depuración Inalámbrica» para abrir Ajustes.\n" +
                               "2. Pulsa «Vincular dispositivo con código de sincronización».\n" +
                               "3. Copia el código de 6 dígitos y el puerto en Shizuku o LADB.\n" +
                               "4. ¡Listo! Podrás aplicar cualquier ajuste sin computadora.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }
        }
    }
}
