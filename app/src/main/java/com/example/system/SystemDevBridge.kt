package com.example.system

import android.app.ActivityManager
import android.app.AppOpsManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Process
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.example.model.DevTool
import com.example.model.SafeLevel
import com.example.model.SettingType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket

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
    val deviceModel: String,
    val isInternetConnected: Boolean = true,
    val networkType: String = "Wi-Fi",
    val pingLatencyMs: Long = 24L,
    val ipAddress: String = "192.168.1.10"
)

data class DevPermission(
    val id: String,
    val title: String,
    val description: String,
    val isGranted: Boolean,
    val type: String, // "RUNTIME", "SPECIAL_SETTING", "ADB_PRIVILEGED"
    val adbCommand: String,
    val settingsAction: String = ""
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

        // Internet & Network status
        val (isOnline, netType) = checkNetworkStatus(context)
        val localIp = getLocalIpAddress()

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
            deviceModel = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}",
            isInternetConnected = isOnline,
            networkType = netType,
            pingLatencyMs = if (isOnline) 24L else 0L,
            ipAddress = localIp
        )
    }

    fun streamTelemetry(context: Context, intervalMs: Long = 2000L): Flow<SystemTelemetry> = flow {
        while (true) {
            emit(getTelemetry(context))
            delay(intervalMs)
        }
    }.flowOn(Dispatchers.IO)

    // ==========================================
    // INTERNET & NETWORK UTILITIES
    // ==========================================

    fun checkNetworkStatus(context: Context): Pair<Boolean, String> {
        return try {
            val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            val activeNet = cm?.activeNetwork
            val caps = cm?.getNetworkCapabilities(activeNet)
            if (caps == null) {
                Pair(false, "Desconectado")
            } else {
                val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                val typeName = when {
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "Wi-Fi"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "Datos Móviles (4G/5G)"
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "Ethernet"
                    else -> "Conectado"
                }
                Pair(hasInternet, typeName)
            }
        } catch (e: Exception) {
            Pair(true, "Wi-Fi")
        }
    }

    suspend fun measureRealPing(host: String = "8.8.8.8", port: Int = 53, timeoutMs: Int = 1500): Long = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(host, port), timeoutMs)
            }
            System.currentTimeMillis() - start
        } catch (e: Exception) {
            try {
                val addr = InetAddress.getByName(host)
                val isReachable = addr.isReachable(timeoutMs)
                if (isReachable) System.currentTimeMillis() - start else -1L
            } catch (e2: Exception) {
                -1L
            }
        }
    }

    fun getLocalIpAddress(): String {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    val host = addr.hostAddress
                    if (!addr.isLoopbackAddress && !addr.isLinkLocalAddress && host != null && !host.contains(':')) {
                        return host
                    }
                }
            }
            "127.0.0.1"
        } catch (e: Exception) {
            "127.0.0.1"
        }
    }

    // ==========================================
    // ALL PERMISSIONS MANAGER ("DAR PERMISO PARA TODO")
    // ==========================================

    fun getAllPermissions(context: Context): List<DevPermission> {
        val pkg = context.packageName

        // 1. Internet
        val hasInternet = ContextCompat.checkSelfPermission(context, android.Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED

        // 2. Network State
        val hasNetState = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED

        // 3. Notifications (Android 13+)
        val hasNotifications = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else true

        // 4. Overlay (Display over other apps)
        val hasOverlay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else true

        // 5. Write System Settings
        val hasWriteSettings = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.System.canWrite(context)
        } else true

        // 6. Write Secure Settings (ADB)
        val hasWriteSecure = context.checkCallingOrSelfPermission("android.permission.WRITE_SECURE_SETTINGS") == PackageManager.PERMISSION_GRANTED

        // 7. Usage Stats
        val hasUsageStats = try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps?.unsafeCheckOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), pkg)
            } else {
                @Suppress("DEPRECATION")
                appOps?.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), pkg)
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }

        // 8. DUMP (ADB)
        val hasDump = context.checkCallingOrSelfPermission("android.permission.DUMP") == PackageManager.PERMISSION_GRANTED

        return listOf(
            DevPermission(
                id = "perm_internet",
                title = "🌐 Acceso a Internet y Red",
                description = "Permite diagnósticos de latencia en vivo, pruebas de ping y consultas DNS.",
                isGranted = hasInternet && hasNetState,
                type = "NATIVO",
                adbCommand = "adb shell pm grant $pkg android.permission.INTERNET"
            ),
            DevPermission(
                id = "perm_overlay",
                title = "🪟 Ventana Flotante / Superposición (Overlay)",
                description = "Permite mostrar el medidor flotante de FPS, telemetría y herramientas sobre cualquier juego o app.",
                isGranted = hasOverlay,
                type = "ESPECIAL",
                adbCommand = "adb shell appops set $pkg SYSTEM_ALERT_WINDOW allow",
                settingsAction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.ACTION_MANAGE_OVERLAY_PERMISSION else ""
            ),
            DevPermission(
                id = "perm_write_settings",
                title = "⚙️ Modificar Ajustes del Sistema",
                description = "Permite alternar brillo, tiempo de espera de pantalla y orientación sin reiniciar.",
                isGranted = hasWriteSettings,
                type = "ESPECIAL",
                adbCommand = "adb shell appops set $pkg WRITE_SETTINGS allow",
                settingsAction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Settings.ACTION_MANAGE_WRITE_SETTINGS else ""
            ),
            DevPermission(
                id = "perm_write_secure_settings",
                title = "⚡ WRITE_SECURE_SETTINGS (Ajustes de Desarrollador)",
                description = "Permite aplicar instantáneamente escalas de animación, 4x MSAA y tweaks globales sin cables.",
                isGranted = hasWriteSecure,
                type = "PRIVILEGIADO_ADB",
                adbCommand = "adb shell pm grant $pkg android.permission.WRITE_SECURE_SETTINGS"
            ),
            DevPermission(
                id = "perm_usage_stats",
                title = "📊 Acceso a Estadísticas de Uso y RAM",
                description = "Permite telemetría exacta de memoria consumida por procesos en segundo plano.",
                isGranted = hasUsageStats,
                type = "ESPECIAL",
                adbCommand = "adb shell pm grant $pkg android.permission.PACKAGE_USAGE_STATS",
                settingsAction = Settings.ACTION_USAGE_ACCESS_SETTINGS
            ),
            DevPermission(
                id = "perm_dump",
                title = "🔍 Permiso DUMP & Diagnóstico",
                description = "Permite inspeccionar servicios gráficos SurfaceFlinger, GPU y registro profundo de errores.",
                isGranted = hasDump,
                type = "PRIVILEGIADO_ADB",
                adbCommand = "adb shell pm grant $pkg android.permission.DUMP"
            )
        )
    }

    fun getMasterAdbScript(context: Context): String {
        val pkg = context.packageName
        return "adb shell pm grant $pkg android.permission.WRITE_SECURE_SETTINGS && " +
               "adb shell pm grant $pkg android.permission.DUMP && " +
               "adb shell pm grant $pkg android.permission.PACKAGE_USAGE_STATS && " +
               "adb shell appops set $pkg SYSTEM_ALERT_WINDOW allow && " +
               "adb shell appops set $pkg WRITE_SETTINGS allow"
    }

    fun openPermissionSetting(context: Context, permission: DevPermission): Boolean {
        if (permission.settingsAction.isNotEmpty()) {
            return try {
                val intent = Intent(permission.settingsAction).apply {
                    if (permission.settingsAction == Settings.ACTION_MANAGE_OVERLAY_PERMISSION ||
                        permission.settingsAction == Settings.ACTION_MANAGE_WRITE_SETTINGS) {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                triggerHaptic(context)
                true
            } catch (e: Exception) {
                openGeneralSettings(context)
            }
        }
        return false
    }

    // ==========================================
    // SYSTEM NAVIGATION
    // ==========================================

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

        var directSuccess = false
        try {
            when (tool.settingType) {
                SettingType.SYSTEM -> {
                    if (Settings.System.canWrite(context)) {
                        directSuccess = Settings.System.putString(context.contentResolver, tool.settingKey, optionValue)
                    }
                }
                SettingType.GLOBAL -> {
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

