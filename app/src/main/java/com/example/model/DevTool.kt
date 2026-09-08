package com.example.model

enum class DevCategory(val iconName: String) {
    GPU_GRAPHICS("gpu"),
    ANIMATIONS_WINDOW("speed"),
    NETWORK_ADB("wifi"),
    MEMORY_PROCESS("memory"),
    DEBUG_LOGCAT("bug"),
    INPUT_DISPLAY("touch"),
    AUDIO_MEDIA("volume"),
    SYSTEM_UI("dashboard"),
    SECURITY_PRIVACY("shield"),
    BATTERY_THERMAL("battery"),
    GAMING_TWEAKS("game"),
    ACCESSIBILITY_VISION("visibility")
}

enum class SafeLevel {
    SAFE_NATIVE,      // 100% safe, native Android UI / setting
    DEVELOPER_ONLY,   // Standard developer option
    ADVANCED_ADB      // Requires ADB / WRITE_SECURE_SETTINGS or direct developer menu
}

enum class SettingType {
    GLOBAL,
    SECURE,
    SYSTEM,
    INTENT_ONLY
}

data class ToolOption(
    val id: String,
    val label: String,
    val value: String,
    val description: String = ""
)

data class DevTool(
    val id: String,
    val name: String,
    val category: DevCategory,
    val description: String,
    val settingKey: String,
    val settingType: SettingType = SettingType.GLOBAL,
    val safeLevel: SafeLevel = SafeLevel.DEVELOPER_ONLY,
    val options: List<ToolOption> = emptyList(),
    val currentValue: String = "",
    val intentAction: String = "",
    val adbCommand: String = ""
)
