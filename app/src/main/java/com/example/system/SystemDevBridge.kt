package com.example.system

import android.app.ActivityManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.WindowManager
import com.example.model.DevTool
import com.example.model.SafeLevel
import com.example.model.SettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.delay

data class SystemTelemetry(
    val isDevModeUnlocked: Boolean,
    val isAdbEnabled: Boolean,
    val isWirelessAdbReady: Boolean,
    val totalRamMb: Long,
    val freeRamMb: Long,
    val usedRamPercent: Int,
    val refreshRateHz: Int,
    val batteryPercent: Int,
    val batteryTempCelsius: Float,
    val isCharging: Boolean,
    val uptimeFormatted: String,
    val androidVersion: String,
    val apiLevel: Int,
    val deviceModel: String
)

object SystemDevBridge {

    fun getTelemetry(context: Context): SystemTelemetry {
        // Dev mode check
        val isDevUnlocked = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }

        // ADB check
        val isAdb = try {
            Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.ADB_ENABLED,
                0
            ) == 1
        } catch (e: Exception) {
            false
        }

        // RAM check
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager?.getMemoryInfo(memInfo)
        val totalMb = memInfo.totalMem / (1024 * 1024)
        val availMb = memInfo.availMem / (1024 * 1024)
        val usedPercent = if (totalMb > 0) (((totalMb - availMb) * 100) / totalMb).toInt() else 0

        // Refresh rate
        val displayHz = try {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    context.display?.mode?.refreshRate?.toInt() ?: 60
                } catch (e: Exception) {
                    @Suppress("DEPRECATION")
                    windowManager?.defaultDisplay?.refreshRate?.toInt() ?: 60
                }
            } else {
                @Suppress("DEPRECATION")
                windowManager?.defaultDisplay?.refreshRate?.toInt() ?: 60
            }
        } catch (e: Exception) {
            60
        }

        // Battery
        val (batPercent, batTemp, isCharging) = try {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val bStatus = context.registerReceiver(null, ifilter)
            val level = bStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = bStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            val pct = if (level >= 0 && scale > 0) ((level.toFloat() / scale.toFloat()) * 100).toInt() else 85
            val tempTenths = bStatus?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) ?: 280
            val temp = tempTenths / 10.0f
            val plugged = bStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0) ?: 0
            Triple(pct, temp, plugged != 0)
        } catch (e: Exception) {
            Triple(85, 28.0f, false)
        }

        // Uptime
        val uptimeMs = SystemClock.elapsedRealtime()
        val hours = uptimeMs / (1000 * 60 * 60)
        val mins = (uptimeMs / (1000 * 60)) % 60
        val uptimeStr = "${hours}h ${mins}m"

        return SystemTelemetry(
            isDevModeUnlocked = isDevUnlocked,
            isAdbEnabled = isAdb,
            isWirelessAdbReady = isAdb,
            totalRamMb = totalMb,
            freeRamMb = availMb,
            usedRamPercent = usedPercent,
            refreshRateHz = displayHz,
            batteryPercent = batPercent,
            batteryTempCelsius = batTemp,
            isCharging = isCharging,
            uptimeFormatted = uptimeStr,
            androidVersion = "Android ${Build.VERSION.RELEASE}",
            apiLevel = Build.VERSION.SDK_INT,
            deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"
        )
    }

    fun streamTelemetry(context: Context, intervalMs: Long = 2000L): Flow<SystemTelemetry> = flow {
        while (true) {
            emit(getTelemetry(context))
            delay(intervalMs)
        }
    }.flowOn(Dispatchers.IO)

    fun openDeveloperSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            triggerHaptic(context)
            true
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openDeviceInfoSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_DEVICE_INFO_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            triggerHaptic(context)
            true
        } catch (e: Exception) {
            openGeneralSettings(context)
        }
    }

    fun openGeneralSettings(context: Context): Boolean {
        return try {
            val intent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            triggerHaptic(context)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun openIntentSafe(context: Context, action: String): Boolean {
        return try {
            val intent = Intent(action).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            triggerHaptic(context)
            true
        } catch (e: Exception) {
            openDeveloperSettings(context)
        }
    }

    fun applyOptionSafe(
        context: Context,
        tool: DevTool,
        optionValue: String,
        onRequiresAdb: (String) -> Unit
    ): Boolean {
        triggerHaptic(context)

        // Only modify what Android allows natively or guide safely
        var directSuccess = false
        try {
            when (tool.settingType) {
                SettingType.SYSTEM -> {
                    if (Settings.System.canWrite(context)) {
                        directSuccess = Settings.System.putString(context.contentResolver, tool.settingKey, optionValue)
                    }
                }
                SettingType.GLOBAL -> {
                    // Requires WRITE_SECURE_SETTINGS or direct developer menu
                    directSuccess = Settings.Global.putString(context.contentResolver, tool.settingKey, optionValue)
                }
                SettingType.SECURE -> {
                    directSuccess = Settings.Secure.putString(context.contentResolver, tool.settingKey, optionValue)
                }
                SettingType.INTENT_ONLY -> {
                    if (tool.intentAction.isNotEmpty()) {
                        openIntentSafe(context, tool.intentAction)
                        return true
                    }
                }
            }
        } catch (e: SecurityException) {
            directSuccess = false
        } catch (e: Exception) {
            directSuccess = false
        }

        if (!directSuccess) {
            // Android security sandbox protected this setting: generate the exact safe ADB shell command
            val exactCmd = if (tool.adbCommand.isNotEmpty()) {
                tool.adbCommand.replace(Regex("(put \\w+ [^\\s]+ )(\\S+)"), "$1$optionValue")
            } else {
                "adb shell settings put ${tool.settingType.name.lowercase()} ${tool.settingKey} $optionValue"
            }
            copyToClipboard(context, exactCmd)
            onRequiresAdb(exactCmd)
            return false
        }

        return true
    }

    fun copyToClipboard(context: Context, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Suzuki DevUnlocker Command", text)
        clipboard.setPrimaryClip(clip)
        triggerHaptic(context)
    }

    fun triggerHaptic(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                @Suppress("DEPRECATION")
                v?.vibrate(30)
            }
        } catch (_: Exception) {}
    }
}
