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
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
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
    onCopyAdb: () -> Unit,
    onCardClick: () -> Unit,
    onOpenWirelessDebugging: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onCardClick() }
            .testTag("tool_card_${tool.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WolfDarkSurface),
        border = BorderStroke(1.dp, WolfDarkBorder.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header: Category Pill + Safe Badge + Action Icons
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
                        SafeLevel.DEVELOPER_ONLY -> "DEV NATIVO" to CyberBlueTertiary
                        SafeLevel.ADVANCED_ADB -> "ADB REQUERIDO" to StatusAmber
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

                // Action buttons: Wireless Debugging + Copy ADB + Open Native Settings
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    if (tool.adbCommand.isNotEmpty()) {
                        IconButton(
                            onClick = onCopyAdb,
                            modifier = Modifier
                                .size(32.dp)
                                .testTag("copy_adb_${tool.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = "Copy ADB Command",
                                tint = CyberBlueTertiary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = onOpenSystemIntent,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("open_intent_${tool.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Launch,
                            contentDescription = "Open in Android Settings",
                            tint = CyberBlueSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tool Name
            Text(
                text = tool.name,
                color = TextWhite,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Description
            Text(
                text = tool.description,
                color = TextGray,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )

            if (tool.options.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Quick Options Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
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
                                    color = if (isSelected) CyberBlueSecondary else WolfDarkBorder.copy(alpha = 0.4f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { onSelectOption(opt.value) }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("opt_${tool.id}_${opt.id}")
                        ) {
                            Text(
                                text = opt.label,
                                color = if (isSelected) TextWhite else TextGray,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}
