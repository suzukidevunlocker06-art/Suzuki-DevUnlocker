package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.DevToolsCatalog
import com.example.data.LanguageManager
import com.example.model.DevCategory
import com.example.model.DevTool
import com.example.system.SystemDevBridge
import com.example.system.SystemTelemetry
import com.example.ui.components.AnimatedWolfLogo
import com.example.ui.components.DevToolCard
import com.example.ui.components.LanguageSelectorDialog
import com.example.ui.components.TelemetryHeader
import com.example.ui.components.ToolDetailModal
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
fun MainScreen(
    currentLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    // Real-time telemetry stream
    val telemetryFlow = remember { SystemDevBridge.streamTelemetry(context) }
    val telemetry by telemetryFlow.collectAsState(initial = SystemDevBridge.getTelemetry(context))

    // Search and category filters
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<DevCategory?>(null) }
    var showLanguageDialog by remember { mutableStateOf(false) }
    var inspectTool by remember { mutableStateOf<DevTool?>(null) }

    // User-selected options state mapping: toolId -> optionValue
    val appliedOptions = remember { mutableStateMapOf<String, String>() }

    // Filtered tools with derivedStateOf for 60/120 fps fluid scrolling
    val allTools = remember { DevToolsCatalog.allTools }
    val filteredTools by remember(searchQuery, selectedCategory) {
        derivedStateOf {
            allTools.filter { tool ->
                val matchesCategory = selectedCategory == null || tool.category == selectedCategory
                val matchesQuery = if (searchQuery.isBlank()) true else {
                    val q = searchQuery.trim().lowercase()
                    tool.name.lowercase().contains(q) ||
                    tool.description.lowercase().contains(q) ||
                    tool.settingKey.lowercase().contains(q) ||
                    LanguageManager.getCategoryName(tool.category, currentLanguage).lowercase().contains(q)
                }
                matchesCategory && matchesQuery
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(WolfDarkBackground)
            .testTag("main_screen_scaffold"),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { SystemDevBridge.openDeveloperSettings(context) },
                containerColor = CyberBluePrimary,
                contentColor = TextWhite,
                shape = CircleShape,
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("fab_open_developer_options")
            ) {
                Icon(
                    imageVector = Icons.Default.DeveloperMode,
                    contentDescription = "Open Developer Options",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        containerColor = WolfDarkBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // App Top Bar with animated wolf logo and version badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Small animated wolf logo
                    AnimatedWolfLogo(
                        size = 42.dp,
                        showRings = true,
                        isStartup = false,
                        onClick = {
                            SystemDevBridge.triggerHaptic(context)
                            Toast.makeText(context, "Suzuki DevEngine v3.4 Active", Toast.LENGTH_SHORT).show()
                        }
                    )

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SUZUKI",
                                color = TextWhite,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "DEV",
                                color = CyberBlueSecondary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberBluePrimary)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "v3.4 PRO",
                                    color = TextWhite,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Text(
                            text = LanguageManager.getUiString("tools_count_badge", currentLanguage),
                            color = CyberBlueTertiary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Action buttons: Language Selector & Quick Refresh
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(WolfDarkSurfaceVariant)
                            .border(1.dp, WolfDarkBorder, RoundedCornerShape(8.dp))
                            .clickable { showLanguageDialog = true }
                            .padding(horizontal = 8.dp, vertical = 6.dp)
                            .testTag("language_switch_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = currentLanguage.flag, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = currentLanguage.code.uppercase(),
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }

            // Real-time telemetry header
            TelemetryHeader(
                telemetry = telemetry,
                language = currentLanguage,
                onOpenDevSettings = { SystemDevBridge.openDeveloperSettings(context) },
                onOpenDeviceInfo = { SystemDevBridge.openDeviceInfoSettings(context) }
            )

            // Minimalist Search Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_tools_input"),
                    placeholder = {
                        Text(
                            text = LanguageManager.getUiString("search_hint", currentLanguage),
                            color = TextMuted,
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = CyberBlueSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = TextGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = WolfDarkSurface,
                        unfocusedContainerColor = WolfDarkSurface,
                        focusedBorderColor = CyberBlueSecondary,
                        unfocusedBorderColor = WolfDarkBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = {})
                )
            }

            // Suzuki Quick Presets Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PresetChip(
                    icon = Icons.Default.FlashOn,
                    label = "⚡ 0.5x Speed",
                    onClick = {
                        coroutineScope.launch {
                            val cmd = "adb shell settings put global window_animation_scale 0.5 && adb shell settings put global transition_animation_scale 0.5 && adb shell settings put global animator_duration_scale 0.5"
                            SystemDevBridge.copyToClipboard(context, cmd)
                            snackbarHostState.showSnackbar("⚡ Presets 0.5x copiado. Abre Ajustes de Desarrollador para aplicar.")
                            SystemDevBridge.openDeveloperSettings(context)
                        }
                    }
                )

                PresetChip(
                    icon = Icons.Default.SportsEsports,
                    label = "🎮 Gaming Turbo",
                    onClick = {
                        coroutineScope.launch {
                            val cmd = "adb shell settings put global debug.egl.force_msaa 1 && adb shell setprop debug.touch.gaming 1"
                            SystemDevBridge.copyToClipboard(context, cmd)
                            snackbarHostState.showSnackbar("🎮 Perfil Gaming Turbo copiado al portapapeles.")
                            SystemDevBridge.openDeveloperSettings(context)
                        }
                    }
                )

                PresetChip(
                    icon = Icons.Default.BatteryChargingFull,
                    label = "🔋 Eco Batería",
                    onClick = {
                        coroutineScope.launch {
                            val cmd = "adb shell settings put global cached_apps_freezer enabled && adb shell settings put global background_process_limit 2"
                            SystemDevBridge.copyToClipboard(context, cmd)
                            snackbarHostState.showSnackbar("🔋 Perfil Eco Batería copiado al portapapeles.")
                        }
                    }
                )

                PresetChip(
                    icon = Icons.Default.PhotoCamera,
                    label = "📸 Demo Screenshot",
                    onClick = {
                        coroutineScope.launch {
                            val cmd = "adb shell am broadcast -a com.android.systemui.demo -e command enter -e battery level 100 -e clock hhmm 1200"
                            SystemDevBridge.copyToClipboard(context, cmd)
                            snackbarHostState.showSnackbar("📸 Modo Demo capturas listo.")
                        }
                    }
                )
            }

            // Category Filter Scrollable Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // "All" chip
                val isAllSelected = selectedCategory == null
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isAllSelected) CyberBluePrimary else WolfDarkSurfaceVariant)
                        .border(
                            1.dp,
                            if (isAllSelected) CyberBlueSecondary else WolfDarkBorder.copy(alpha = 0.4f),
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { selectedCategory = null }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                        .testTag("cat_chip_all")
                ) {
                    Text(
                        text = "${LanguageManager.getUiString("all_categories", currentLanguage)} (${allTools.size})",
                        color = if (isAllSelected) TextWhite else TextGray,
                        fontSize = 11.sp,
                        fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }

                DevCategory.values().forEach { cat ->
                    val isCatSelected = selectedCategory == cat
                    val catCount = allTools.count { it.category == cat }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCatSelected) CyberBluePrimary else WolfDarkSurfaceVariant)
                            .border(
                                1.dp,
                                if (isCatSelected) CyberBlueSecondary else WolfDarkBorder.copy(alpha = 0.4f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedCategory = cat }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("cat_chip_${cat.name}")
                    ) {
                        Text(
                            text = "${LanguageManager.getCategoryName(cat, currentLanguage)} ($catCount)",
                            color = if (isCatSelected) TextWhite else TextGray,
                            fontSize = 11.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // Active Tool Results Counter
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredTools.size} HERRAMIENTAS ACTIVAS",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                if (searchQuery.isNotEmpty() || selectedCategory != null) {
                    Text(
                        text = "Limpiar Filtros",
                        color = CyberBlueSecondary,
                        fontSize = 11.sp,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedCategory = null
                        }
                    )
                }
            }

            // Fluid LazyColumn for tools list
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("tools_lazy_column"),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(
                    items = filteredTools,
                    key = { it.id }
                ) { tool ->
                    DevToolCard(
                        tool = tool,
                        language = currentLanguage,
                        selectedOption = appliedOptions[tool.id],
                        onSelectOption = { value ->
                            appliedOptions[tool.id] = value
                            SystemDevBridge.applyOptionSafe(
                                context = context,
                                tool = tool,
                                optionValue = value,
                                onRequiresAdb = { cmd ->
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar("Comando ADB copiado: $cmd")
                                    }
                                }
                            )
                        },
                        onOpenSystemIntent = {
                            if (tool.intentAction.isNotEmpty()) {
                                SystemDevBridge.openIntentSafe(context, tool.intentAction)
                            } else {
                                SystemDevBridge.openDeveloperSettings(context)
                            }
                        },
                        onCopyAdb = {
                            val cmd = if (tool.adbCommand.isNotEmpty()) {
                                val opt = appliedOptions[tool.id]
                                if (opt != null) {
                                    tool.adbCommand.replace(Regex("(put \\w+ [^\\s]+ )(\\S+)"), "$1$opt")
                                } else tool.adbCommand
                            } else {
                                "adb shell settings put ${tool.settingType.name.lowercase()} ${tool.settingKey} 1"
                            }
                            SystemDevBridge.copyToClipboard(context, cmd)
                            coroutineScope.launch {
                                snackbarHostState.showSnackbar("Comando ADB copiado al portapapeles")
                            }
                        },
                        onCardClick = {
                            inspectTool = tool
                        }
                    )
                }
            }
        }
    }

    // Modal dialogs
    if (showLanguageDialog) {
        LanguageSelectorDialog(
            currentLanguage = currentLanguage,
            onLanguageSelected = onLanguageChange,
            onDismiss = { showLanguageDialog = false }
        )
    }

    inspectTool?.let { tool ->
        ToolDetailModal(
            tool = tool,
            language = currentLanguage,
            selectedOption = appliedOptions[tool.id],
            onSelectOption = { value ->
                appliedOptions[tool.id] = value
                SystemDevBridge.applyOptionSafe(
                    context = context,
                    tool = tool,
                    optionValue = value,
                    onRequiresAdb = { cmd ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Comando ADB copiado: $cmd")
                        }
                    }
                )
            },
            onOpenSystemIntent = {
                if (tool.intentAction.isNotEmpty()) {
                    SystemDevBridge.openIntentSafe(context, tool.intentAction)
                } else {
                    SystemDevBridge.openDeveloperSettings(context)
                }
            },
            onCopyAdb = { cmd ->
                SystemDevBridge.copyToClipboard(context, cmd)
                coroutineScope.launch {
                    snackbarHostState.showSnackbar("Comando copiado: $cmd")
                }
            },
            onDismiss = { inspectTool = null }
        )
    }
}

@Composable
private fun PresetChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(WolfDarkSurface)
            .border(1.dp, CyberBluePrimary.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = CyberBlueSecondary,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = label,
                color = TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}
