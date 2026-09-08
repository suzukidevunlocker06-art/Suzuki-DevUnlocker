package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.ui.theme.TextWhite
import com.example.ui.theme.WolfDarkBackground
import com.example.ui.theme.WolfDarkBorder
import com.example.ui.theme.WolfDarkSurface
import com.example.ui.theme.WolfDarkSurfaceVariant

@Composable
fun DevToolCard(
    tool: DevTool,
    language: AppLanguage,
    selectedOption: String?,
    onSelectOption: (String) -> Unit,
    onOpenSystemIntent: () -> Unit,
    onCopyAdb: () -> Unit = {},
    onCardClick: () -> Unit,
    onOpenWirelessDebugging: () -> Unit = {}
) {
    // Detect binary tools (e.g. On/Off, 0/1, Desactivado/Activado)
    val isBinary = tool.options.size == 2 && tool.options.any { it.value == "1" || it.value == "true" }
    val isSwitchChecked = selectedOption == "1" || selectedOption == "true" ||
            (selectedOption == null && (tool.currentValue == "1" || tool.currentValue == "true"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick() }
            .testTag("tool_card_${tool.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WolfDarkSurface),
        border = BorderStroke(
            1.dp,
            if (selectedOption != null) CyberBlueSecondary.copy(alpha = 0.5f) else WolfDarkBorder.copy(alpha = 0.6f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Category Pill + Safe Badge + System Modification Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Category pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(CyberBluePrimary.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = LanguageManager.getCategoryName(tool.category, language).uppercase(),
                            color = CyberBlueSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Safe level badge
                    val (badgeText, badgeColor) = when (tool.safeLevel) {
                        SafeLevel.SAFE_NATIVE -> "100% SEGURO" to StatusGreen
                        SafeLevel.DEVELOPER_ONLY -> "AJUSTE NATIVO" to CyberBlueTertiary
                        SafeLevel.ADVANCED_ADB -> "MODIFICACIÓN AVANZADA" to StatusAmber
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeColor.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = badgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Header Action Buttons for Direct Phone Modification
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Wireless Debugging launcher
                    IconButton(
                        onClick = onOpenWirelessDebugging,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("open_wireless_adb_${tool.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wifi,
                            contentDescription = "Activar Depuración Inalámbrica",
                            tint = Color(0xFF00E676),
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    // Direct Phone Settings button
                    IconButton(
                        onClick = onOpenSystemIntent,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("open_intent_${tool.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Launch,
                            contentDescription = "Abrir Ajuste en el Teléfono",
                            tint = CyberBlueSecondary,
                            modifier = Modifier.size(17.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tool Name & Optional Direct Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = tool.name,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isBinary) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = isSwitchChecked,
                        onCheckedChange = { checked ->
                            val newVal = if (checked) {
                                tool.options.firstOrNull { it.value == "1" || it.value == "true" }?.value ?: "1"
                            } else {
                                tool.options.firstOrNull { it.value == "0" || it.value == "false" }?.value ?: "0"
                            }
                            onSelectOption(newVal)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = CyberBlueSecondary,
                            uncheckedThumbColor = TextGray,
                            uncheckedTrackColor = WolfDarkSurfaceVariant
                        ),
                        modifier = Modifier.testTag("switch_tool_${tool.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = tool.description,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            // Multiple Options Modification Chips
            if (tool.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "OPCIONES PARA MODIFICAR:",
                        color = CyberBlueSecondary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    if (selectedOption != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF00E676))
                            )
                            Text(
                                text = "Modificado en tu teléfono",
                                color = Color(0xFF00E676),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Scrollable Modification Options Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    tool.options.forEach { opt ->
                        val isSelected = selectedOption == opt.value
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (isSelected) CyberBluePrimary else WolfDarkSurfaceVariant
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) CyberBlueSecondary else WolfDarkBorder.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectOption(opt.value) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                                .testTag("opt_${tool.id}_${opt.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = TextWhite,
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                                Text(
                                    text = opt.label,
                                    color = if (isSelected) TextWhite else TextGray,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Phone Modification Footer Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WolfDarkBackground.copy(alpha = 0.6f))
                    .border(1.dp, WolfDarkBorder.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .clickable { onOpenSystemIntent() }
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = CyberBlueSecondary,
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Toca para abrir y modificar en el sistema",
                        color = TextWhite.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Launch,
                    contentDescription = null,
                    tint = CyberBlueSecondary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}
