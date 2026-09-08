package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.DevToolsCatalog
import com.example.data.LanguageManager
import com.example.model.DevCategory
import com.example.model.DevTool
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
fun SessionsGridMatrix(
    allTools: List<DevTool>,
    language: AppLanguage,
    onSelectSession: (DevCategory) -> Unit
) {
    val categories = DevCategory.values()

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("sessions_grid_matrix"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(categories) { cat ->
            val catTools = allTools.filter { it.category == cat }
            val optionsCount = catTools.sumOf { it.options.size }
            val catColor = getCategoryColor(cat)

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .clickable { onSelectSession(cat) }
                    .testTag("session_card_${cat.name}"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WolfDarkSurface),
                border = BorderStroke(
                    1.dp,
                    Brush.verticalGradient(
                        listOf(catColor.copy(alpha = 0.6f), WolfDarkBorder)
                    )
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(catColor.copy(alpha = 0.2f))
                                .border(1.dp, catColor.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = getCategoryEmoji(cat),
                                fontSize = 16.sp
                            )
                        }

                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = catColor,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = LanguageManager.getCategoryName(cat, language),
                        color = TextWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${catTools.size} herramientas",
                            color = catColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "•",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                        Text(
                            text = "$optionsCount ops",
                            color = TextMuted,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

fun getCategoryEmoji(category: DevCategory): String {
    return when (category) {
        DevCategory.GPU_GRAPHICS -> "🎮"
        DevCategory.ANIMATIONS_WINDOW -> "⚡"
        DevCategory.NETWORK_ADB -> "🌐"
        DevCategory.MEMORY_PROCESS -> "🧠"
        DevCategory.DEBUG_LOGCAT -> "🐞"
        DevCategory.INPUT_DISPLAY -> "📱"
        DevCategory.AUDIO_MEDIA -> "🔊"
        DevCategory.SYSTEM_UI -> "🎛️"
        DevCategory.SECURITY_PRIVACY -> "🛡️"
        DevCategory.BATTERY_THERMAL -> "🔋"
        DevCategory.GAMING_TWEAKS -> "🚀"
        DevCategory.ACCESSIBILITY_VISION -> "👁️"
    }
}

fun getCategoryColor(category: DevCategory): Color {
    return when (category) {
        DevCategory.GPU_GRAPHICS -> Color(0xFF00E5FF)       // Bright Cyan
        DevCategory.ANIMATIONS_WINDOW -> Color(0xFFFFD600)  // Speed Yellow
        DevCategory.NETWORK_ADB -> Color(0xFF2979FF)        // Electric Blue
        DevCategory.MEMORY_PROCESS -> Color(0xFFD500F9)     // Neon Purple
        DevCategory.DEBUG_LOGCAT -> Color(0xFFFF1744)       // Red
        DevCategory.INPUT_DISPLAY -> Color(0xFFFF9100)      // Amber Orange
        DevCategory.AUDIO_MEDIA -> Color(0xFF00E676)        // Lime Green
        DevCategory.SYSTEM_UI -> Color(0xFF00B0FF)          // Sky Blue
        DevCategory.SECURITY_PRIVACY -> Color(0xFF76FF03)   // Shield Green
        DevCategory.BATTERY_THERMAL -> Color(0xFFFF5252)    // Coral Red
        DevCategory.GAMING_TWEAKS -> Color(0xFFE040FB)      // Magenta
        DevCategory.ACCESSIBILITY_VISION -> Color(0xFFFFAB40)// Peach
    }
}
