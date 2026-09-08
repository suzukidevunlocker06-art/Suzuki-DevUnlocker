package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.example.ui.theme.WolfDarkBackground
import com.example.ui.theme.WolfDarkBorder
import com.example.ui.theme.WolfDarkSurface
import com.example.ui.theme.WolfDarkSurfaceVariant
import kotlinx.coroutines.launch

@Composable
fun FullSessionsAccordion(
    toolsByCategory: Map<DevCategory, List<DevTool>>,
    language: AppLanguage,
    appliedOptions: Map<String, String>,
    onSelectOption: (DevTool, String) -> Unit,
    onOpenSystemIntent: (DevTool) -> Unit,
    onCopyAdb: (DevTool) -> Unit,
    onInspectTool: (DevTool) -> Unit,
    listState: LazyListState,
    onOpenWirelessDebugging: (DevTool) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    val categories = DevCategory.values().filter { toolsByCategory[it]?.isNotEmpty() == true }

    // Map to track expanded status for each category (default: first 3 expanded, or all)
    val expandedStates = remember {
        mutableStateMapOf<DevCategory, Boolean>().apply {
            categories.forEachIndexed { index, cat ->
                this[cat] = (index == 0 || index == 1) // first 2 open by default
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Master toolbar: Expand All / Collapse All & Quick Session Navigator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${categories.size} SESIONES COMPLETAS DISPONIBLES",
                color = CyberBlueTertiary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                // Expand all button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(CyberBluePrimary.copy(alpha = 0.2f))
                        .border(1.dp, CyberBluePrimary.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable {
                            categories.forEach { expandedStates[it] = true }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("expand_all_sessions_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.UnfoldMore, contentDescription = null, tint = TextWhite, modifier = Modifier.size(12.dp))
                        Text(text = "Expandir Todas", color = TextWhite, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Collapse all button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(WolfDarkSurfaceVariant)
                        .border(1.dp, WolfDarkBorder, RoundedCornerShape(6.dp))
                        .clickable {
                            categories.forEach { expandedStates[it] = false }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("collapse_all_sessions_btn"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(imageVector = Icons.Default.UnfoldLess, contentDescription = null, tint = TextGray, modifier = Modifier.size(12.dp))
                        Text(text = "Colapsar", color = TextGray, fontSize = 9.sp)
                    }
                }
            }
        }

        // Quick Jump Pill Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            categories.forEach { cat ->
                val isExpanded = expandedStates[cat] == true
                val catColor = getCategoryColor(cat)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isExpanded) catColor.copy(alpha = 0.2f) else WolfDarkSurfaceVariant)
                        .border(1.dp, if (isExpanded) catColor else WolfDarkBorder.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable {
                            expandedStates[cat] = !(expandedStates[cat] ?: false)
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("jump_pill_${cat.name}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = getCategoryEmoji(cat), fontSize = 12.sp)
                        Text(
                            text = LanguageManager.getCategoryName(cat, language),
                            color = if (isExpanded) TextWhite else TextGray,
                            fontSize = 10.sp,
                            fontWeight = if (isExpanded) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Complete LazyColumn with grouped session accordions
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("full_sessions_lazy_column"),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            categories.forEach { cat ->
                val catTools = toolsByCategory[cat] ?: emptyList()
                val isExpanded = expandedStates[cat] == true
                val catColor = getCategoryColor(cat)
                val optionsCount = catTools.sumOf { it.options.size }

                // Category Section Header Item
                item(key = "section_header_${cat.name}") {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                expandedStates[cat] = !isExpanded
                            }
                            .testTag("session_accordion_${cat.name}"),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isExpanded) WolfDarkSurfaceVariant else WolfDarkSurface
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isExpanded) catColor.copy(alpha = 0.8f) else WolfDarkBorder
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(catColor.copy(alpha = 0.2f))
                                        .border(1.dp, catColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = getCategoryEmoji(cat), fontSize = 18.sp)
                                }

                                Column {
                                    Text(
                                        text = LanguageManager.getCategoryName(cat, language),
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = "${catTools.size} herramientas",
                                            color = catColor,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(text = "•", color = TextMuted, fontSize = 10.sp)
                                        Text(
                                            text = "$optionsCount opciones",
                                            color = TextMuted,
                                            fontSize = 10.sp,
                                            fontFamily = FontFamily.Monospace
                                        )
                                    }
                                }
                            }

                            Icon(
                                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = if (isExpanded) "Collapse" else "Expand",
                                tint = if (isExpanded) catColor else TextGray,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // If expanded, render all tools of this section
                if (isExpanded) {
                    items(items = catTools, key = { it.id }) { tool ->
                        DevToolCard(
                            tool = tool,
                            language = language,
                            selectedOption = appliedOptions[tool.id],
                            onSelectOption = { value -> onSelectOption(tool, value) },
                            onOpenSystemIntent = { onOpenSystemIntent(tool) },
                            onCopyAdb = { onCopyAdb(tool) },
                            onCardClick = { onInspectTool(tool) },
                            onOpenWirelessDebugging = { onOpenWirelessDebugging(tool) }
                        )
                    }
                }
            }
        }
    }
}
