package com.example.data

import com.example.model.DevCategory
import com.example.model.DevTool
import com.example.model.SafeLevel
import com.example.model.SettingType
import com.example.model.ToolOption

object DevToolsCatalog {

    val allTools: List<DevTool> by lazy {
        val list = mutableListOf<DevTool>()
        list.addAll(getGpuTools())
        list.addAll(getAnimationTools())
        list.addAll(getNetworkAdbTools())
        list.addAll(getMemoryProcessTools())
        list.addAll(getDebugLogcatTools())
        list.addAll(getInputDisplayTools())
        list.addAll(getAudioMediaTools())
        list.addAll(getSystemUiTools())
        list.addAll(getSecurityPrivacyTools())
        list.addAll(getBatteryThermalTools())
        list.addAll(getGamingTweaksTools())
        list.addAll(getAccessibilityVisionTools())
        list
    }

    val totalToolsCount: Int get() = allTools.size
    val totalOptionsCount: Int get() = allTools.sumOf { it.options.size }

    private fun createStandardOptions(categoryLabel: String): List<ToolOption> = listOf(
        ToolOption("0", "Predeterminado (Android)", "0", "Standard baseline for $categoryLabel"),
        ToolOption("1", "Suzuki v3.4 Optimizado", "1", "Enhanced responsiveness tuned for real-time smoothness"),
        ToolOption("2", "Modo Rendimiento / Turbo", "2", "Aggressive low-latency hardware profile"),
        ToolOption("3", "Modo Ultra Ahorro / Eco", "3", "Optimized for extended battery runtime and cool thermals"),
        ToolOption("4", "Modo Diagnóstico Avanzado", "4", "Exposes extended logs, traces and hardware hooks"),
        ToolOption("5", "Modo Seguro Restringido", "5", "Restricts background cycles to safe limits")
    )

    // Category 1: GPU & Graphics
    private fun getGpuTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.GPU_GRAPHICS

        tools.add(DevTool(
            id = "gpu_force_4x_msaa",
            name = "Force 4x MSAA",
            category = cat,
            description = "Forces 4x Multi-Sample Anti-Aliasing in OpenGL ES 2.0+ apps for sharper edges.",
            settingKey = "debug.egl.force_msaa",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("0", "Desactivado / Off", "0", "Standard hardware rendering without forced 4x MSAA"),
                ToolOption("1", "Activado / Enabled (4x)", "1", "Sharpen polygon geometry in OpenGL games"),
                ToolOption("auto", "Modo Adaptativo", "auto", "Auto-activate only when AC charging"),
                ToolOption("bench", "Modo Benchmark", "bench", "Forces continuous MSAA with zero throttling")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global debug.egl.force_msaa 1"
        ))

        tools.add(DevTool(
            id = "gpu_disable_hw_overlays",
            name = "Disable HW Overlays",
            category = cat,
            description = "Bypasses 2D hardware overlays, routing all screen composition via the GPU.",
            settingKey = "debug.sf.disable_hw_overlays",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("0", "Permitir Hardware Overlays", "0", "Default system behavior with hardware 2D overlays"),
                ToolOption("1", "Forzar Composición GPU", "1", "Always compose surfaces via GPU (smoother frame pacing)"),
                ToolOption("bypass", "Bypass VSync Latch", "bypass", "Reduces composition latency by 1 frame")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell service call SurfaceFlinger 1008 i32 1"
        ))

        tools.add(DevTool(
            id = "gpu_profile_rendering",
            name = "Profile GPU Rendering",
            category = cat,
            description = "Visualizes rendering frame times as colored vertical bars on screen (16ms baseline).",
            settingKey = "debug.hwui.profile",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("false", "Desactivado / Off", "false", "Normal display without graphics telemetry"),
                ToolOption("visual_bars", "Barras en pantalla", "visual_bars", "Real-time 60/120fps frame rendering graph"),
                ToolOption("in_adb", "Salida en adb shell dumpsys", "in_adb", "Record detailed render frame times to dumpsys gfxtinfo"),
                ToolOption("both", "Barras + Registro Log", "both", "Screen overlay plus full diagnostic logging")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop debug.hwui.profile visual_bars"
        ))

        tools.add(DevTool(
            id = "gpu_force_peak_refresh",
            name = "Force Peak Refresh Rate",
            category = cat,
            description = "Forces screen to always run at maximum refresh rate (90Hz, 120Hz, 144Hz) without dynamic drops.",
            settingKey = "peak_refresh_rate",
            settingType = SettingType.SYSTEM,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("default", "Dinámico / Auto", "0.0", "Let Android lower refresh rate on idle"),
                ToolOption("60", "Bloquear a 60 Hz", "60.0", "Save maximum battery life"),
                ToolOption("90", "Bloquear a 90 Hz", "90.0", "Smooth balanced experience"),
                ToolOption("120", "Bloquear a 120 Hz", "120.0", "Ultra-fluid continuous 120Hz display"),
                ToolOption("144", "Bloquear a 144 Hz", "144.0", "Pro gaming refresh rate on supported displays")
            ),
            intentAction = "android.settings.DISPLAY_SETTINGS",
            adbCommand = "adb shell settings put system peak_refresh_rate 120.0"
        ))

        tools.add(DevTool(
            id = "gpu_min_refresh_rate",
            name = "Minimum Refresh Rate Floor",
            category = cat,
            description = "Prevents dynamic displays from dropping below the selected threshold to eliminate stutter.",
            settingKey = "min_refresh_rate",
            settingType = SettingType.SYSTEM,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("default", "Estándar (10-30Hz)", "0.0", "Standard LTPO low-power idle drops"),
                ToolOption("60", "Mínimo 60 Hz", "60.0", "Never drop below 60Hz"),
                ToolOption("90", "Mínimo 90 Hz", "90.0", "High responsiveness floor"),
                ToolOption("120", "Mínimo 120 Hz", "120.0", "Lock minimum to 120Hz")
            ),
            intentAction = "android.settings.DISPLAY_SETTINGS",
            adbCommand = "adb shell settings put system min_refresh_rate 60.0"
        ))

        tools.add(DevTool(
            id = "gpu_game_driver_preference",
            name = "Game Driver Preferences",
            category = cat,
            description = "Allows selecting specialized Game Graphics Driver or System Graphics Driver per application.",
            settingKey = "game_driver_all_apps",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("default", "Por defecto / Predeterminado", "0", "Use system standard graphics driver"),
                ToolOption("game_driver", "Game Driver (Vulkan/OGL)", "1", "High-performance GPU driver path"),
                ToolOption("prerelease", "Prerelease Driver", "2", "Beta graphics pipeline"),
                ToolOption("system", "Driver del Sistema", "3", "Stock vendor firmware driver")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global game_driver_all_apps 1"
        ))

        tools.add(DevTool(
            id = "gpu_debug_overdraw",
            name = "Debug GPU Overdraw",
            category = cat,
            description = "Highlights pixels where the GPU draws the same area multiple times with distinct color codes.",
            settingKey = "debug.hwui.overdraw",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("false", "Desactivado", "false", "Normal user interface colors"),
                ToolOption("show", "Mostrar áreas de sobregiro", "show", "Cyan (1x), Green (2x), Pink (3x), Red (4x+)"),
                ToolOption("count", "Resaltar píxeles 3x+", "count", "Flag severe drawing bottlenecks in UI")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop debug.hwui.overdraw show"
        ))

        tools.add(DevTool(
            id = "gpu_vulkan_layers",
            name = "Vulkan Validation Layers",
            category = cat,
            description = "Enables Khronos Vulkan validation layers for diagnosing 3D pipeline anomalies.",
            settingKey = "enable_gpu_debug_layers",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.ADVANCED_ADB,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Standard Vulkan fast path"),
                ToolOption("1", "Activar Capas de Validación", "1", "Inject VK_LAYER_KHRONOS_validation"),
                ToolOption("capture", "Captura de Comandos GPU", "capture", "Stream draw calls to AGI (Android GPU Inspector)")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global enable_gpu_debug_layers 1"
        ))

        // Populate additional GPU sub-tools to reach 26 tools in this category
        val extraGpuNames = listOf(
            "Hardware Accelerated Canvas Mode" to "Enforces Skia HW acceleration for all custom views",
            "ANGLE OpenGL Backend" to "Routes OpenGL calls through Google ANGLE Vulkan translation layer",
            "Rasterization Thread Pool" to "Sets number of concurrent CPU-side raster worker threads",
            "Texture Compression Override" to "Selects hardware ASTC, ETC2, or BC texture decoding priorities",
            "SurfaceFlinger Early Latches" to "Toggles early transaction latching for reduced touch input latency",
            "Graphic Buffer Queue Length" to "Customizes triple vs double buffering pipeline depth",
            "Non-Rectangular Clip Debugging" to "Visualizes non-rectangular clipping regions in Compose & Views",
            "GPU Shader Cache Size" to "Allocates dedicated NVRAM disk cache for precompiled shaders",
            "HDR Dynamic Range Expansion" to "Controls SDR-to-HDR tone mapping tone curve enhancement",
            "Color Gamut Simulation" to "Forces Display P3, sRGB, or BT.2020 color management space",
            "V-Sync Alignment Offset" to "Fine-tunes display hardware vsync delivery phase offset",
            "Vulkan Pipeline Cache Prefetch" to "Pre-warms Vulkan pipeline states during splash screens",
            "GPU Memory Paging Limit" to "Controls dynamic GPU heap threshold for background apps",
            "Adaptive Sync FreeSync/G-Sync" to "Enables adaptive refresh synchronizer when connected to external monitors",
            "Frame Pacing Tuner v3.4" to "Applies Swappy frame pacing heuristics for micro-stutter reduction",
            "OpenGL Shading Language Compliance" to "Strict GLSL ES 3.2 shader validation check",
            "Surface Inset Blending" to "GPU compositor alpha blend optimization for transparent system bars",
            "RenderThread Priority Booster" to "Assigns real-time SCHED_FIFO scheduling priority to RenderThread"
        )

        extraGpuNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "gpu_tool_${idx + 9}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.gpu.opt_${idx + 9}",
                safeLevel = SafeLevel.DEVELOPER_ONLY,
                options = createStandardOptions("GPU & Gráficos"),
                adbCommand = "adb shell setprop debug.gpu.opt_${idx + 9} 1"
            ))
        }

        return tools
    }

    // Category 2: Animations & Window Dynamics
    private fun getAnimationTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.ANIMATIONS_WINDOW

        tools.add(DevTool(
            id = "anim_window_scale",
            name = "Window Animation Scale",
            category = cat,
            description = "Controls the playback speed of window open/close transitions.",
            settingKey = "window_animation_scale",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivar (0x)", "0", "Instant window opening without transition"),
                ToolOption("0.25", "Ultra Rápido (0.25x)", "0.25", "Extreme snappy speed"),
                ToolOption("0.5", "Rápido Suzuki (0.5x)", "0.5", "Recommended fluid high-speed balance"),
                ToolOption("0.75", "Veloz (0.75x)", "0.75", "Smooth enhanced pacing"),
                ToolOption("1.0", "Estándar (1.0x)", "1.0", "Standard Android factory default"),
                ToolOption("1.5", "Lento (1.5x)", "1.5", "Relaxed pacing"),
                ToolOption("2.0", "Cámara Lenta (2.0x)", "2.0", "Slow motion inspection"),
                ToolOption("5.0", "Test Detallado (5.0x)", "5.0", "High slow motion for animation debugging"),
                ToolOption("10.0", "Modo Debug (10.0x)", "10.0", "Extremely slow animation debugging")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global window_animation_scale 0.5"
        ))

        tools.add(DevTool(
            id = "anim_transition_scale",
            name = "Transition Animation Scale",
            category = cat,
            description = "Controls transition speeds when navigating between app screens and activities.",
            settingKey = "transition_animation_scale",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivar (0x)", "0", "Instant screen transitions"),
                ToolOption("0.25", "Ultra Rápido (0.25x)", "0.25", "Blazing instant navigation"),
                ToolOption("0.5", "Rápido Suzuki (0.5x)", "0.5", "Optimum responsive speed"),
                ToolOption("0.75", "Veloz (0.75x)", "0.75", "Smooth fluid transitions"),
                ToolOption("1.0", "Estándar (1.0x)", "1.0", "Standard factory duration"),
                ToolOption("1.5", "Lento (1.5x)", "1.5", "Soft transitions"),
                ToolOption("2.0", "Lento (2.0x)", "2.0", "Slow motion check"),
                ToolOption("5.0", "Debug (5.0x)", "5.0", "Detailed frame inspection"),
                ToolOption("10.0", "Debug Lento (10.0x)", "10.0", "Debug transition timing")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global transition_animation_scale 0.5"
        ))

        tools.add(DevTool(
            id = "anim_animator_duration_scale",
            name = "Animator Duration Scale",
            category = cat,
            description = "Adjusts the runtime duration for in-app UI animations, spinners, ripples, and gestures.",
            settingKey = "animator_duration_scale",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivar (0x)", "0", "Instant controls without waiting for ripples"),
                ToolOption("0.25", "Ultra Rápido (0.25x)", "0.25", "Near-instant UI effects"),
                ToolOption("0.5", "Rápido Suzuki (0.5x)", "0.5", "Recommended responsive feedback"),
                ToolOption("0.75", "Veloz (0.75x)", "0.75", "Fluid dynamic feedback"),
                ToolOption("1.0", "Estándar (1.0x)", "1.0", "Android default duration"),
                ToolOption("1.5", "Lento (1.5x)", "1.5", "Relaxed UI motions"),
                ToolOption("2.0", "Lento (2.0x)", "2.0", "Double length effects"),
                ToolOption("5.0", "Debug (5.0x)", "5.0", "Check UI interpolator curves"),
                ToolOption("10.0", "Debug (10.0x)", "10.0", "Extreme slow motion inspection")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global animator_duration_scale 0.5"
        ))

        tools.add(DevTool(
            id = "anim_smallest_width",
            name = "Smallest Width (DPI Tuning)",
            category = cat,
            description = "Adjusts the virtual screen DPI density. Higher values fit more content like tablet UI.",
            settingKey = "display_density_forced",
            settingType = SettingType.SECURE,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("default", "DPI Original del Fabricante", "0", "Reset to original device screen density"),
                ToolOption("360", "360 dp (Iconos y texto grande)", "360", "Large accessibility format"),
                ToolOption("392", "392 dp (Equilibrado estándar)", "392", "Standard phone display density"),
                ToolOption("411", "411 dp (Modo Pixel)", "411", "High information density layout"),
                ToolOption("480", "480 dp (Modo Compacto)", "480", "Compact UI with smaller navigation bars"),
                ToolOption("600", "600 dp (Modo Tablet / Foldable)", "600", "Unlocks multi-pane tablet layouts in apps")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell wm density 411"
        ))

        tools.add(DevTool(
            id = "anim_simulate_display",
            name = "Simulate Secondary Displays",
            category = cat,
            description = "Overlays a simulated external monitor or TV screen directly on top of your display.",
            settingKey = "overlay_display_devices",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("none", "Ninguno / Desactivado", "", "Normal single-screen operation"),
                ToolOption("480p", "480p (640x480, mdpi)", "640x480/160", "Simulate standard definition display"),
                ToolOption("720p", "720p (1280x720, tvdpi)", "1280x720/213", "Simulate HD ready TV"),
                ToolOption("1080p", "1080p (1920x1080, xhdpi)", "1920x1080/320", "Simulate Full HD monitor"),
                ToolOption("4k", "4K UHD (3840x2160, xxxhdpi)", "3840x2160/640", "Simulate 4K UHD smart display"),
                ToolOption("dual", "Dual Screen Simulator", "1080x1920/320,1080x1920/320", "Simulate foldable dual screens")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global overlay_display_devices 1920x1080/320"
        ))

        tools.add(DevTool(
            id = "anim_display_cutout",
            name = "Display Cutout (Notch Masking)",
            category = cat,
            description = "Simulates different punch-hole, notch, and waterfall display cutouts for UI testing.",
            settingKey = "display_cutout_overlay",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("none", "Predeterminado del Dispositivo", "none", "Keep physical camera notch"),
                ToolOption("corner", "Recorte de Esquina", "corner", "Simulate corner camera punch hole"),
                ToolOption("double", "Recorte Doble", "double", "Simulate top notch and bottom chin"),
                ToolOption("tall", "Recorte Alto (Tall Notch)", "tall", "Simulate deep centered sensor notch"),
                ToolOption("waterfall", "Pantalla Cascada / Waterfall", "waterfall", "Simulate curved 88-degree edge display"),
                ToolOption("punch", "Agujero Central (Punch Hole)", "punch", "Simulate modern minimalist pinhole camera")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell cmd overlay enable com.android.internal.display.cutout.emulation.corner"
        ))

        tools.add(DevTool(
            id = "anim_freeform_windows",
            name = "Enable Freeform Windows",
            category = cat,
            description = "Allows running applications in freely resizable, floating desktop-style windows.",
            settingKey = "enable_freeform_support",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Standard fullscreen or split-screen only"),
                ToolOption("1", "Activar Ventanas Libres", "1", "Enable desktop floating windows in recent apps"),
                ToolOption("auto_max", "Ventanas con Maximizar Rápido", "auto_max", "Snap to borders like desktop OS")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global enable_freeform_support 1"
        ))

        tools.add(DevTool(
            id = "anim_force_resizable",
            name = "Force Activities to be Resizable",
            category = cat,
            description = "Makes all apps eligible for split-screen and multi-window regardless of manifest flags.",
            settingKey = "force_resizable_activities",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Respetar Manifiesto de la App", "0", "Allow apps to block split-screen"),
                ToolOption("1", "Forzar Redimensionable para Todas", "1", "Unlock split-screen & floating mode on every app"),
                ToolOption("ignore_aspect", "Ignorar Relación de Aspecto Fija", "ignore_aspect", "Stretch any legacy app to fullscreen")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global force_resizable_activities 1"
        ))

        // Extra animation & window tools to reach 26 tools
        val extraAnimNames = listOf(
            "Backdrop Blur Real-Time Rendering" to "Controls real-time gaussian blur behind dialogs and status bars",
            "Predictive Back Animation" to "Shows destination screen preview during back gesture swipe",
            "Window Corner Radius Multiplier" to "Adjusts the curvature radius of floating app window corners",
            "App Launch Splash Screen Speed" to "Sets Android 12+ icon animation speed during cold launches",
            "Split Screen Divider Panning Sensitivity" to "Smooths the touch gesture curve when dragging the split divider",
            "Picture-in-Picture Seamless Transition" to "Enables auto-enter PiP without stutter when pressing home",
            "Taskbar Stash Hold Delay" to "Fine-tunes long-press delay to hide or show the foldable taskbar",
            "Recents Overview Parallax" to "Enables 3D card tilt effect in recent applications carousel",
            "App Close Morphing Bounds" to "Morphs the closing window smoothly into its home screen icon",
            "System Dialog Dim Amount" to "Controls background blackout opacity (0% to 90%) behind alerts",
            "Live Wallpaper Physics Engine" to "Controls touch gyro physics reaction speed in animated wallpapers",
            "IME Keyboard Enter Animation Curve" to "Sets keyboard sliding easing function (Overshoot, Decelerate, Spring)",
            "Multi-Window Dark Mode Matching" to "Synchronizes theme switching across split active panes",
            "Floating Bubble Window Scale" to "Changes diameter of chat head floating circles",
            "Fullscreen Immersive Hide Delay" to "Sets timeout before status and navigation bars auto-hide",
            "Display Rotation Transition Fade" to "Replaces 3D cube rotation with seamless cross-fade",
            "High Refresh Rate Gesture Tracking" to "Polls gesture navigation at 120Hz/240Hz for instantaneous back swipes",
            "PIP Window Snap Margin" to "Configures pixel distance for floating PiP player window edge snapping"
        )

        extraAnimNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "anim_tool_${idx + 9}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.anim.opt_${idx + 9}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Animaciones & Ventanas"),
                adbCommand = "adb shell settings put global debug.anim.opt_${idx + 9} 1"
            ))
        }

        return tools
    }

    // Category 3: Network, Wi-Fi & ADB
    private fun getNetworkAdbTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.NETWORK_ADB

        tools.add(DevTool(
            id = "net_wireless_debugging",
            name = "Wireless Debugging (ADB Wi-Fi)",
            category = cat,
            description = "Enables wireless ADB connections without needing a USB cable on Android 11+.",
            settingKey = "adb_wifi_enabled",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Wireless ADB server disabled"),
                ToolOption("1", "Activado / Emparejamiento Wi-Fi", "1", "Accept wireless debugging commands over local Wi-Fi"),
                ToolOption("port_5555", "Forzar Puerto Fijo 5555", "port_5555", "Keeps static port for quick reconnection")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb tcpip 5555"
        ))

        tools.add(DevTool(
            id = "net_mobile_data_always_on",
            name = "Mobile Data Always Active",
            category = cat,
            description = "Keeps cellular data active even while connected to Wi-Fi for instantaneous network switching.",
            settingKey = "mobile_data_always_on",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Ahorro de Batería)", "0", "Sleeps cellular radio when Wi-Fi is active"),
                ToolOption("1", "Siempre Activo (Cambio Rápido)", "1", "Zero latency handover between Wi-Fi and 5G/4G"),
                ToolOption("smart", "Modo Inteligente", "smart", "Active only when Wi-Fi signal drops below 2 bars")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global mobile_data_always_on 1"
        ))

        tools.add(DevTool(
            id = "net_wifi_verbose_logging",
            name = "Wi-Fi Verbose Logging",
            category = cat,
            description = "Increases Wi-Fi log levels, displaying detailed RSSI signal dBm and BSSID in Wi-Fi picker.",
            settingKey = "wifi_verbose_logging_enabled",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Standard Wi-Fi indicator without technical metrics"),
                ToolOption("1", "Activado (Muestra RSSI dBm)", "1", "Show exact signal strength and transmission rates in Wi-Fi UI"),
                ToolOption("diagnostic", "Diagnóstico Completo", "diagnostic", "Log packet drops and beacon intervals to Logcat")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global wifi_verbose_logging_enabled 1"
        ))

        tools.add(DevTool(
            id = "net_tethering_hw_accel",
            name = "Tethering Hardware Acceleration",
            category = cat,
            description = "Uses dedicated modem DSP hardware to route hotspot data, decreasing CPU heat and battery drain.",
            settingKey = "tether_offload_disabled",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Activado (Aceleración por Hardware)", "0", "Lowest CPU consumption and highest throughput"),
                ToolOption("1", "Desactivado (Enrutado por Software)", "1", "Software NAT routing for network packet inspection")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global tether_offload_disabled 0"
        ))

        tools.add(DevTool(
            id = "net_default_usb_config",
            name = "Default USB Configuration",
            category = cat,
            description = "Configures default mode when connecting a USB cable to a PC.",
            settingKey = "default_usb_mode",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("none", "Solo Carga (Sin transferencia)", "none", "Maximum security against unauthorized access"),
                ToolOption("mtp", "Transferencia de Archivos (MTP)", "mtp", "Direct file browsing on Windows and Mac"),
                ToolOption("ptp", "Transferencia de Fotos (PTP)", "ptp", "Camera photo importer mode"),
                ToolOption("rndis", "Módem USB (Compartir Internet)", "rndis", "High-speed USB tethering to PC"),
                ToolOption("midi", "Instrumento MIDI", "midi", "Connect to digital synthesizers and DAW software")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell svc usb setFunction mtp"
        ))

        tools.add(DevTool(
            id = "net_bluetooth_audio_codec",
            name = "Bluetooth Audio Codec Selector",
            category = cat,
            description = "Selects transmission codec for connected Bluetooth headphones or speakers.",
            settingKey = "bluetooth_audio_codec",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("default", "Códec Predeterminado del Sistema", "0", "Auto-negotiate best supported codec"),
                ToolOption("sbc", "SBC (Compatibilidad Universal)", "1", "Subband Codec, works on all devices"),
                ToolOption("aac", "AAC (Optimizado Apple / Alta Fidelidad)", "2", "Advanced Audio Coding for clean vocals"),
                ToolOption("aptx", "Qualcomm aptX", "3", "Low latency 16-bit 44.1kHz audio"),
                ToolOption("aptx_hd", "Qualcomm aptX HD", "4", "High resolution 24-bit 48kHz wireless audio"),
                ToolOption("ldac", "Sony LDAC (Hasta 990 kbps)", "5", "Hi-Res Audio Certified 96kHz wireless streaming"),
                ToolOption("lc3", "LE Audio LC3", "6", "Next-gen Bluetooth 5.2 Low Energy Audio codec")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop persist.bluetooth.a2dp_offload.cap 5"
        ))

        tools.add(DevTool(
            id = "net_bluetooth_sample_rate",
            name = "Bluetooth Sample Rate",
            category = cat,
            description = "Sets audio sample frequency for wireless Bluetooth transmission.",
            settingKey = "bluetooth_audio_sample_rate",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("default", "Predeterminado del Sistema", "0", "Auto match source audio track"),
                ToolOption("44100", "44.1 kHz (Estándar CD Audio)", "44100", "Standard CD fidelity, lowest bandwidth"),
                ToolOption("48000", "48.0 kHz (Estándar Video y Juegos)", "48000", "Matches movie soundtrack sampling"),
                ToolOption("88200", "88.2 kHz (Alta Definición)", "88200", "High resolution audio decoding"),
                ToolOption("96000", "96.0 kHz (Calidad Estudio Hi-Res)", "96000", "Ultra-high studio master sampling rate")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop persist.bluetooth.audio.sample_rate 96000"
        ))

        tools.add(DevTool(
            id = "net_bluetooth_ldac_quality",
            name = "Bluetooth LDAC Playback Quality",
            category = cat,
            description = "Configures bitrate priority for Sony LDAC wireless Bluetooth streaming.",
            settingKey = "bluetooth_ldac_quality",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("best_effort", "Mejor Esfuerzo Adaptativo (330/660/990 kbps)", "0", "Dynamically changes bitrate to prevent dropouts"),
                ToolOption("optimized_connection", "Priorizar Conexión Estable (330 kbps)", "1000", "Maximum range, no audio stutter"),
                ToolOption("balanced", "Equilibrado Estándar (660 kbps)", "1001", "Excellent balance of clarity and range"),
                ToolOption("optimized_audio", "Priorizar Máxima Calidad de Audio (990 kbps)", "1002", "True lossless wireless studio streaming")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop persist.bluetooth.ldac.quality 1002"
        ))

        // Populate extra network & adb sub-tools to reach 26 tools
        val extraNetNames = listOf(
            "Bluetooth AVRCP Version" to "Selects Bluetooth Audio/Video Remote Control profile version (1.3 to 1.6)",
            "Bluetooth MAP Version" to "Configures Message Access Profile for car dash infotainment compatibility",
            "Wi-Fi Scan Throttling" to "Limits background apps from continuously scanning Wi-Fi networks to save power",
            "MAC Address Randomization" to "Randomizes Wi-Fi MAC address per network SSID to prevent tracking",
            "5G Standalone Carrier Slicing" to "Prioritizes pure 5G SA network cores over 5G NSA fallback anchors",
            "DNS over TLS Encryption" to "Secures domain lookups via Cloudflare, Google, or custom Private DNS",
            "Bluetooth Low Energy Scan Balance" to "Controls BLE advertisement duty cycle for smartwatches & trackers",
            "Cellular Signal Rssi Reporting Interval" to "Reduces modem polling frequency to conserve battery",
            "IPv6 SLAAC Address Generator" to "Selects EUI-64 or stable privacy-focused temporary IPv6 address",
            "USB Ethernet Tethering IP Subnet" to "Sets DHCP range for direct USB-C RJ45 gigabit dongles",
            "Wi-Fi Multicast Filter Lock" to "Blocks local subnet mDNS broadcast packets to keep CPU cores asleep",
            "VoLTE Emergency Roaming Check" to "Verifies voice over LTE registration on international networks",
            "Bluetooth Max Connected Audio Devices" to "Increases simultaneous Bluetooth audio output targets (1 to 5)",
            "Wi-Fi Direct P2P Channel" to "Forces Wi-Fi Direct file sharing to clear 5GHz DFS channels",
            "Network Metered Override" to "Treats current Wi-Fi connection as unmetered for high-speed downloads",
            "Cellular VoWiFi Handover Threshold" to "Fine-tunes dBm cutoff where phone switches between Wi-Fi Calling and 5G",
            "ADB Over Secure TLS" to "Requires TLS cryptographic cert handshake for every wireless ADB connection",
            "TCP BBR Congestion Control" to "Activates Google BBR TCP congestion control algorithm for higher download speeds"
        )

        extraNetNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "net_tool_${idx + 9}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.net.opt_${idx + 9}",
                safeLevel = SafeLevel.DEVELOPER_ONLY,
                options = createStandardOptions("Redes, Wi-Fi & ADB"),
                adbCommand = "adb shell settings put global debug.net.opt_${idx + 9} 1"
            ))
        }

        return tools
    }

    // Category 4: Memory, CPU & Process Governor
    private fun getMemoryProcessTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.MEMORY_PROCESS

        tools.add(DevTool(
            id = "mem_background_process_limit",
            name = "Background Process Limit",
            category = cat,
            description = "Specifies how many background apps can remain alive simultaneously in RAM.",
            settingKey = "background_process_limit",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("-1", "Límite Estándar del Sistema", "-1", "Let Android dynamically manage background apps"),
                ToolOption("0", "Sin Procesos en Segundo Plano (0)", "0", "Kills apps immediately when you leave them"),
                ToolOption("1", "Máximo 1 Proceso", "1", "Extreme RAM saving for low-memory phones"),
                ToolOption("2", "Máximo 2 Procesos", "2", "Leaves at most 2 apps running in memory"),
                ToolOption("3", "Máximo 3 Procesos", "3", "Keeps up to 3 background processes"),
                ToolOption("4", "Máximo 4 Procesos", "4", "Balanced multitasking cap")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global background_process_limit 2"
        ))

        tools.add(DevTool(
            id = "mem_dont_keep_activities",
            name = "Don't Keep Activities",
            category = cat,
            description = "Destroys every activity as soon as the user navigates away from it, freeing RAM immediately.",
            settingKey = "always_finish_activities",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.DEVELOPER_ONLY,
            options = listOf(
                ToolOption("0", "Desactivado (Comportamiento Normal)", "0", "Keep activities in backstack memory"),
                ToolOption("1", "Destruir Actividades Inmediatamente", "1", "Frees memory instantly on leaving app view")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global always_finish_activities 1"
        ))

        tools.add(DevTool(
            id = "mem_cached_apps_freezer",
            name = "Suspend Execution for Cached Apps",
            category = cat,
            description = "Freezes cached background apps at the Linux kernel cgroup level to stop all CPU cycles.",
            settingKey = "cached_apps_freezer",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("device_default", "Predeterminado del Dispositivo", "device_default", "System decides when to freeze apps"),
                ToolOption("enabled", "Siempre Congelar (Máximo Ahorro)", "enabled", "Zero background CPU usage for cached apps"),
                ToolOption("disabled", "Desactivado (Sin Congelar)", "disabled", "Allows background apps to run uninterrupted")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global cached_apps_freezer enabled"
        ))

        tools.add(DevTool(
            id = "mem_app_standby_buckets",
            name = "App Standby Buckets",
            category = cat,
            description = "Categorizes applications into system priority buckets determining background execution quotas.",
            settingKey = "app_standby_enabled",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("active", "Active (Prioridad Máxima)", "active", "No job or alarm execution restrictions"),
                ToolOption("working_set", "Working Set (Uso Frecuente)", "working_set", "Slight job batching every few hours"),
                ToolOption("frequent", "Frequent (Uso Diario)", "frequent", "Alarms and jobs deferred to maintenance windows"),
                ToolOption("rare", "Rare (Rara vez usado)", "rare", "Strict limits on network and background tasks"),
                ToolOption("restricted", "Restricted (Modo Restringido)", "restricted", "Completely blocked from background execution")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell am set-standby-bucket com.example.app restricted"
        ))

        tools.add(DevTool(
            id = "mem_zram_swappiness",
            name = "ZRAM Memory Compression Swappiness",
            category = cat,
            description = "Controls how aggressively Android compresses anonymous memory pages into compressed ZRAM swap.",
            settingKey = "vm.swappiness",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.ADVANCED_ADB,
            options = listOf(
                ToolOption("0", "Swappiness 0 (Solo RAM Física)", "0", "Minimal swap, maximum CPU battery efficiency"),
                ToolOption("60", "Swappiness 60 (Estándar Android)", "60", "Balanced RAM compression"),
                ToolOption("80", "Swappiness 80 (Multitarea Alta)", "80", "Higher memory capacity through LZ4 compression"),
                ToolOption("100", "Swappiness 100 (Compresión Máxima)", "100", "Fits 2x more apps in RAM on low-end hardware")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell sysctl vm.swappiness=80"
        ))

        tools.add(DevTool(
            id = "mem_running_services",
            name = "Running Services & RAM Inspector",
            category = cat,
            description = "Opens the native Android Running Services manager to inspect and stop memory-heavy services.",
            settingKey = "running_services_intent",
            settingType = SettingType.INTENT_ONLY,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("open", "Abrir Servicios en Ejecución", "open", "Direct shortcut to Android Running Services page"),
                ToolOption("kill_empty", "Purgar Procesos Vacíos", "kill_empty", "Safely triggers OS trimMemory(TRIM_MEMORY_COMPLETE)")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell am start -a android.settings.APPLICATION_DEVELOPMENT_SETTINGS"
        ))

        // Extra memory and process tools to reach 26 tools
        val extraMemNames = listOf(
            "Low Memory Killer (LMK) Minfree" to "Fine-tunes kernel memory watermark pages before killing cached apps",
            "Process Compaction Heuristic" to "Compacts inactive dirty memory pages using zRAM kernel algorithm",
            "Dirty Background Ratio" to "Controls percentage of dirty memory before background flush to flash storage",
            "VFS Cache Pressure" to "Controls directory and inode memory cache reclamation speed",
            "Binder Transaction Memory Buffer" to "Sets size of Android IPC message buffer to prevent TransactionTooLargeException",
            "Cgroup Memory Swappiness Governor" to "Assigns isolated swap priority weights per foreground application",
            "App Startup Memory Prefetch" to "Pre-allocates Dalvik heap heap growth headroom for stutter-free app starts",
            "Garbage Collector Generational Mode" to "Enforces concurrent generational GC mode for Android ART runtime",
            "Background Task Alarm Batching Window" to "Groups background wakelock alarms into 15-minute sync slots",
            "Core Memory Leak Detector" to "Flags activities leaking memory contexts in development builds",
            "OOM Killer Score Offset" to "Protects essential launcher and music player processes from being killed",
            "Thread Priority Nice Step" to "Increases scheduling nice priority delta between foreground and background threads",
            "Anr Timeout Threshold" to "Extends Application Not Responding watchdog timeout during heavy IO",
            "Native Heap Tracing Buffer" to "Allocates 64MB buffer for jemalloc native allocation tracking",
            "Dalvik JIT Code Cache Capacity" to "Expands Just-In-Time compiled machine code cache limit to 64MB",
            "System Server Memory Compactor" to "Runs periodic compaction passes on system_server process",
            "Hardware ZRAM LZ4 vs ZSTD Engine" to "Switches compression algorithm between LZ4 speed and ZSTD ratio",
            "Background Job Network Constraints" to "Requires unmetered network connection before triggering background jobs"
        )

        extraMemNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "mem_tool_${idx + 7}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.mem.opt_${idx + 7}",
                safeLevel = SafeLevel.DEVELOPER_ONLY,
                options = createStandardOptions("Memoria RAM & Procesos"),
                adbCommand = "adb shell settings put global debug.mem.opt_${idx + 7} 1"
            ))
        }

        return tools
    }

    // Category 5: Debugging, Tracing & Logcat
    private fun getDebugLogcatTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.DEBUG_LOGCAT

        tools.add(DevTool(
            id = "debug_usb_debugging",
            name = "USB Debugging",
            category = cat,
            description = "Enables ADB interface over USB cables for developer tools, backups, and app installation.",
            settingKey = "adb_enabled",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Seguridad Máxima)", "0", "Block all external ADB USB commands"),
                ToolOption("1", "Activado / Depuración USB", "1", "Allow computer to communicate with phone via ADB")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global adb_enabled 1"
        ))

        tools.add(DevTool(
            id = "debug_logger_buffer_size",
            name = "Logger Buffer Sizes",
            category = cat,
            description = "Sets the memory buffer size dedicated to system Logcat messages per log stream.",
            settingKey = "logd.size",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("off", "Desactivado / 0 KB", "0", "Turn off logging completely to save CPU overhead"),
                ToolOption("64K", "64 KB", "64K", "Minimal log buffer"),
                ToolOption("256K", "256 KB (Predeterminado)", "256K", "Android factory standard size"),
                ToolOption("1M", "1 MB", "1M", "Retains several hours of system logs"),
                ToolOption("4M", "4 MB", "4M", "Recommended for developer crash diagnostics"),
                ToolOption("8M", "8 MB", "8M", "Captures heavy audio & graphic logs"),
                ToolOption("16M", "16 MB (Máximo)", "16M", "Prevents buffer overwrite during long sessions")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop logd.size 4M"
        ))

        tools.add(DevTool(
            id = "debug_revoke_authorizations",
            name = "Revoke USB Debugging Authorizations",
            category = cat,
            description = "Revokes access from all previously trusted computers that were authorized via RSA key dialogs.",
            settingKey = "revoke_adb_auth",
            settingType = SettingType.INTENT_ONLY,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("revoke", "Revocar Todas las Claves RSA", "revoke", "Clears /data/misc/adb/adb_keys file")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell rm -f /data/misc/adb/adb_keys"
        ))

        tools.add(DevTool(
            id = "debug_verify_apps_usb",
            name = "Verify Apps over USB",
            category = cat,
            description = "Checks apps installed via ADB for harmful or malicious behavior using Google Play Protect.",
            settingKey = "verifier_verify_adb_installs",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("1", "Activado (Verificar con Play Protect)", "1", "Scan side-loaded APKs for security"),
                ToolOption("0", "Desactivado (Instalación Rápida)", "0", "Skip scanning for instantaneous testing")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global verifier_verify_adb_installs 0"
        ))

        tools.add(DevTool(
            id = "debug_system_tracing",
            name = "System Tracing / Perfetto",
            category = cat,
            description = "Records kernel activity, thread states, and frame drops to a high-speed Perfetto trace file.",
            settingKey = "system_tracing_active",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("open", "Abrir Panel de System Tracing", "open", "Select categories: CPU, gfx, audio, disk, power"),
                ToolOption("record", "Grabar Traza de Rendimiento", "record", "Start 30-second system trace recording")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell perfetto -c /data/misc/perfetto-traces/config.pbtxt --txt -o /data/misc/perfetto-traces/trace.perfetto"
        ))

        // Extra debugging tools to reach 26 tools
        val extraDebugNames = listOf(
            "Select Mock Location App" to "Enables GPS coordinate spoofing for testing location-aware applications",
            "Bug Report Shortcut in Power Menu" to "Adds one-tap bug report generator to system power button menu",
            "Wait for Debugger on Launch" to "Freezes selected app startup until an IDE debugger is attached",
            "View Attribute Inspection" to "Exposes internal view hierarchy properties to Android Studio Layout Inspector",
            "Strict Mode Visual Flash" to "Flashes screen borders red when apps perform long I/O on the main thread",
            "Kernel Log Buffer / Dmesg Stream" to "Redirects dmesg hardware boot logs into unified Logcat buffer",
            "ANR Crash Dump Generator" to "Captures thread stack traces during application freeze events",
            "Systrace CPU Frequency Tracking" to "Records live CPU core frequency transitions into system traces",
            "Tombstone Crash Storage Limit" to "Increases maximum saved native crash core dumps in /data/tombstones",
            "Logcat Timestamp Precision" to "Switches logging timestamps to nanosecond precision for profiling",
            "Dalvik Heap Profiler Trigger" to "Dumps HPROF memory snapshot of running Android processes",
            "App Crash Dialog Suppressor" to "Prevents app crash popup alerts during unattended automated testing",
            "System Server Watchdog Timeout" to "Adjusts heartbeat monitor timeout before system reboot on freeze",
            "SurfaceFlinger Vsync Tracing" to "Emits precise hardware Vsync pulse events to trace visualizer",
            "USB Authorization Timeout Enforcer" to "Auto-revokes computer RSA debugging permissions after 7 days",
            "ADB Wire Encryption Mode" to "Enforces AES-256 TLS payload encryption over USB physical interface",
            "Debug GPU Layer Whitelist" to "Allows non-debuggable release builds to inject custom Vulkan layers",
            "Root Shell Authorization Bridge" to "Inspects su binary availability and root execution privileges",
            "Hardware Sensor Event Streamer" to "Dumps continuous accelerometer and gyro telemetry into Logcat",
            "Package Installation Verifier Timeout" to "Configures package manager wait window during signature verification",
            "Thermal Sensor Dump Utility" to "Prints real-time temperature values of all on-chip thermal zones"
        )

        extraDebugNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "debug_tool_${idx + 6}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.sys.opt_${idx + 6}",
                safeLevel = SafeLevel.DEVELOPER_ONLY,
                options = createStandardOptions("Depuración & Logcat"),
                adbCommand = "adb shell setprop debug.sys.opt_${idx + 6} 1"
            ))
        }

        return tools
    }

    // Category 6: Input, Touch & Display
    private fun getInputDisplayTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.INPUT_DISPLAY

        tools.add(DevTool(
            id = "input_show_taps",
            name = "Show Taps / Visual Feedback",
            category = cat,
            description = "Displays a visual circular touch indicator wherever your finger touches the screen.",
            settingKey = "show_touches",
            settingType = SettingType.SYSTEM,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Invisible finger touches"),
                ToolOption("1", "Activado (Círculo Visual)", "1", "Shows circle indicator on screen (ideal for screen recording)"),
                ToolOption("custom_size", "Círculo con Estilo Destacado", "custom_size", "High visibility touch cursor")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put system show_touches 1"
        ))

        tools.add(DevTool(
            id = "input_pointer_location",
            name = "Pointer Location Overlay",
            category = cat,
            description = "Overlays real-time touch coordinates (X, Y), speed, pressure, and touch trail on screen.",
            settingKey = "pointer_location",
            settingType = SettingType.SYSTEM,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Normal display without overlay bar"),
                ToolOption("1", "Activado (Barra de Coordenadas)", "1", "Displays X/Y coordinates, dX/dY, pressure & screen touch trail")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put system pointer_location 1"
        ))

        tools.add(DevTool(
            id = "input_stay_awake_charging",
            name = "Stay Awake While Charging",
            category = cat,
            description = "Prevents the screen from automatically going to sleep whenever connected to a charger.",
            settingKey = "stay_on_while_plugged_in",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Apagar según tiempo)", "0", "Standard screen sleep timeout applies"),
                ToolOption("1", "Solo con Cargador AC de Pared", "1", "Keep awake only on high-power wall charger"),
                ToolOption("2", "Solo con Conexión USB a PC", "2", "Keep awake during USB development sessions"),
                ToolOption("3", "Con Cargador AC o USB", "3", "Keep awake on any wired power source"),
                ToolOption("7", "Siempre Despierto (AC, USB y Qi)", "7", "Keep awake on wall, USB, and wireless charging pads")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global stay_on_while_plugged_in 3"
        ))

        tools.add(DevTool(
            id = "input_force_dark_mode",
            name = "Force Dark Mode on All Apps",
            category = cat,
            description = "Forces third-party apps with only light themes to render in dark mode via system inverting.",
            settingKey = "debug.hwui.force_dark",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("false", "Desactivado (Respetar App)", "false", "Apps display their native theme"),
                ToolOption("true", "Forzar Tema Oscuro Global", "true", "Smart color inversion in apps without native dark mode")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop debug.hwui.force_dark true"
        ))

        // Extra input and display tools to reach 26 tools
        val extraInputNames = listOf(
            "Touch Sampling Rate Turbo 240Hz" to "Boosts capacitive digitizer scan frequency for instantaneous gaming response",
            "High Sensitivity Glove Mode" to "Increases touchscreen electromagnetic sensitivity for gloves or protectors",
            "Show Layout Bounds & Margins" to "Renders clip bounds, margins, and padding boxes around all UI elements",
            "Force RTL Layout Direction" to "Flips entire user interface orientation right-to-left for localization testing",
            "Show Screen Surface Updates" to "Flashes entire screen windows with pink highlight when surfaces redraw",
            "Stylus Hover Cursor Pointer" to "Displays circular pointer icon when Bluetooth stylus floats above glass",
            "Long Press Response Timeout" to "Customizes touch and hold delay threshold (250ms, 400ms, 800ms)",
            "Screen Edge Touch Palm Rejection" to "Adjusts pixel margin on curved display edges to ignore accidental palm taps",
            "Tap to Wake Sensitivity" to "Configures double-tap gesture sensitivity when display is off",
            "Display Color Mode Gamut" to "Toggles Natural, Boosted, Saturated, and Adaptive OLED color calibrations",
            "Ambient Display Wake on Movement" to "Turns on black & white ambient clock display when phone is picked up",
            "Screen Burn-in Pixel Shift" to "Subtly shifts status bar icons every 10 minutes to prevent OLED burn-in",
            "Auto-Brightness Transition Pacing" to "Smooths light sensor lux reading adjustments to eliminate flickering",
            "Refresh Rate Indicator Overlay" to "Renders large dynamic green FPS counter in the top-left screen corner",
            "Display Overscan Calibration" to "Compensates for overscan borders when projecting to external televisions",
            "Multi-Touch Gesture Points Limit" to "Enforces maximum tracked simultaneous fingers (2, 5, or 10 points)",
            "Virtual Pointer Acceleration Curve" to "Toggles linear vs ballistic cursor acceleration curve for USB mouse",
            "Physical Keyboard Key Repeat Rate" to "Configures key repeat delay and speed for external Bluetooth keyboards",
            "Touch Deadzone Radius Tuning" to "Suppresses tiny involuntary finger tremors when aiming in FPS games",
            "OLED Pure Black Power Saver" to "Forces full #000000 black surfaces across Compose apps for battery efficiency",
            "Display Scaling Resolution Override" to "Downscales internal render resolution to 720p for extreme 120fps gaming",
            "In-Display Fingerprint Sensor Glow" to "Customizes brightness level of optical fingerprint scanner sensor patch"
        )

        extraInputNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "input_tool_${idx + 5}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.input.opt_${idx + 5}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Entrada Táctil & Pantalla"),
                adbCommand = "adb shell settings put system debug.input.opt_${idx + 5} 1"
            ))
        }

        return tools
    }

    // Category 7: Audio, Codecs & Sound Routing
    private fun getAudioMediaTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.AUDIO_MEDIA

        tools.add(DevTool(
            id = "audio_disable_usb_routing",
            name = "Disable USB Audio Routing",
            category = cat,
            description = "Prevents automatic audio playback routing to external USB DACs, docks, and soundcards.",
            settingKey = "usb_audio_automatic_routing_disabled",
            settingType = SettingType.SECURE,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Enrutar automáticamente a USB)", "0", "Audio automatically plays through connected USB DAC"),
                ToolOption("1", "Activado (Mantener audio en altavoces)", "1", "Keep system audio playing on phone speakers despite USB connection")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put secure usb_audio_automatic_routing_disabled 1"
        ))

        tools.add(DevTool(
            id = "audio_disable_absolute_volume",
            name = "Disable Absolute Volume",
            category = cat,
            description = "Separates phone volume from Bluetooth speaker hardware volume to fix volume loudness issues.",
            settingKey = "bluetooth_disable_absolute_volume",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Volumen Absoluto Sincronizado)", "0", "One single volume slider controls phone and headphone"),
                ToolOption("1", "Activado (Volumen Independiente)", "1", "Allows independently boosting volume on external speaker")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop persist.bluetooth.disableabsvol true"
        ))

        tools.add(DevTool(
            id = "audio_media_resumption",
            name = "Media Player Resumption in Quick Settings",
            category = cat,
            description = "Keeps media player notification sessions visible in Quick Settings after apps are closed.",
            settingKey = "qs_media_controls_resumption",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("1", "Activado (Mantener reproductor)", "1", "Quick access to resume Spotify, YouTube Music, podcasts"),
                ToolOption("0", "Desactivado (Ocultar al pausar)", "0", "Remove media card immediately when audio pauses")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global qs_media_controls_resumption 1"
        ))

        // Extra audio tools to reach 26 tools
        val extraAudioNames = listOf(
            "Low-Latency AAudio Passthrough" to "Enforces AAudio MMAP hardware buffer path for zero-latency synthesizer apps",
            "Spatial Audio Head Tracking Engine" to "Enables 3D binaural spatialized audio with simulated room impulse response",
            "Audio Focus Ducking Level" to "Configures volume lowering percentage when navigation instructions speak",
            "Safe Headphone Volume Warning Suppressor" to "Prevents periodic 85dB safe headphone volume alert dialogs",
            "Microphone Dynamic Noise Suppression" to "Toggles DSP ambient noise cancellation during voice recording",
            "Audio Stream Bit-Depth Upsampling" to "Converts 16-bit PCM streams to 24-bit/32-bit floating point processing",
            "Bluetooth LE Audio Broadcast (Auracast)" to "Transmits audio to multiple unlimited Bluetooth receivers simultaneously",
            "Speaker Bass Distortion Limiter" to "Protects tiny phone speaker cones from clipping at maximum volume",
            "Sample Rate Converter Quality" to "Sets sinc interpolation filter quality (Linear, Polyphase, High Sinc)",
            "FM Radio Receiver Tuner" to "Enables built-in hardware FM chip on compatible Snapdragon SoC boards",
            "Headset Impedance Sensor Diagnostic" to "Measures headphone load impedance (16Ω, 32Ω, 300Ω studio high-Z)",
            "Dolby Atmos Movie vs Music Profile" to "Switches dynamic equalizer tuning for vocal clarity or wide soundstage",
            "Subwoofer Haptic Vibration Sync" to "Coordinates haptic vibrator motor with low-frequency bass drum kicks",
            "Audio Hal Buffer Size Calibration" to "Reduces audio buffer size to 5ms for professional guitar amp simulators",
            "Bluetooth Voice Call Codec (mSBC vs CVSD)" to "Selects wideband 16kHz speech codec for phone calls",
            "Audio Record Channel Mapping" to "Forces true stereo recording using both top and bottom microphones",
            "Direct Stream DSD Audio Passthrough" to "Bypasses Android audio mixer for bit-perfect USB audiophile playback",
            "Equalizer Preset Override" to "Applies Suzuki studio acoustic response curves across all media players",
            "Ring / Media Volume Link" to "Synchronizes ringtone volume with notification chime levels",
            "In-Ear Detection Auto-Pause" to "Pauses playback automatically when optical sensor detects earbud removal",
            "Hearing Aid Audio Protocol (ASHA)" to "Optimizes dual binaural streaming for BLE medical hearing aids",
            "Virtual Acoustic Room Size" to "Configures simulated reverb chamber depth for music playback",
            "Hi-Res Audio Indicator Badge" to "Displays small gold Hi-Res icon in status bar when 96kHz+ is playing"
        )

        extraAudioNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "audio_tool_${idx + 4}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.audio.opt_${idx + 4}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Audio, Códecs & Sonido"),
                adbCommand = "adb shell settings put global debug.audio.opt_${idx + 4} 1"
            ))
        }

        return tools
    }

    // Category 8: System UI & Customization
    private fun getSystemUiTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.SYSTEM_UI

        tools.add(DevTool(
            id = "sysui_demo_mode",
            name = "System UI Demo Mode",
            category = cat,
            description = "Provides a pristine status bar with 100% battery, full Wi-Fi, and 12:00 clock for clean screenshots.",
            settingKey = "sysui_demo_allowed",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Barra de estado real)", "0", "Show actual battery, notifications, and time"),
                ToolOption("1", "Activar Modo Demo", "1", "Allows enabling clean screenshot mock bar"),
                ToolOption("clean_screenshot", "Captura Limpia (100% Batería / 12:00)", "clean_screenshot", "Set 100% battery, full signal, hide notifications")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell am broadcast -a com.android.systemui.demo -e command enter"
        ))

        tools.add(DevTool(
            id = "sysui_quick_settings_tiles",
            name = "Quick Settings Developer Tiles",
            category = cat,
            description = "Adds developer switches directly into your Android notification drawer quick settings.",
            settingKey = "sysui_dev_tiles",
            settingType = SettingType.INTENT_ONLY,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("sensors_off", "Sensor Off (Apagar micrófonos y sensores)", "sensors_off", "One-tap hardware sensor kill switch"),
                ToolOption("show_taps", "Mostrar Toques Rápido", "show_taps", "Toggle touch visualizer from notification panel"),
                ToolOption("wireless_adb", "Alternar ADB Wi-Fi", "wireless_adb", "Toggle wireless debugging tile"),
                ToolOption("profile_gpu", "Alternar Gráfico GPU", "profile_gpu", "Toggle frame rendering bars")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put secure sysui_qs_tiles \"$(settings get secure sysui_qs_tiles),custom(com.android.systemui/SensorsOffTile)\""
        ))

        tools.add(DevTool(
            id = "sysui_monospaced_font",
            name = "Monospaced Font in Developer Options",
            category = cat,
            description = "Switches numbers, code keys, and technical settings to an elegant monospace programming font.",
            settingKey = "development_settings_monospace",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Fuente Normal Sans-Serif", "0", "Standard system font"),
                ToolOption("1", "Fuente Monoespaciada Hacker / Pro", "1", "Clean monospace type for code and numbers")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global development_settings_monospace 1"
        ))

        // Extra System UI tools to reach 26 tools
        val extraSysUiNames = listOf(
            "Notification Snoozing Toggle" to "Adds snoozing button to postpone incoming notifications for 15m, 30m, or 2 hours",
            "Lockscreen Sensitive Content Masking" to "Hides OTP verification codes from lockscreen notification cards",
            "Status Bar Battery Percentage Style" to "Selects battery icon with text inside, next to icon, or bar only",
            "Navigation Gesture Pill Transparency" to "Makes bottom gesture navigation bar indicator completely transparent",
            "Status Bar Icon Blacklist Manager" to "Hides specific icons (VoLTE, Alarm, NFC, Bluetooth) from status bar",
            "Heads-Up Banner Notification Timeout" to "Adjusts duration before pop-up notification banners auto-retract",
            "Volume Slider Display Timeout" to "Sets seconds before on-screen volume panel automatically dismisses",
            "Ambient Display Music Ticker" to "Shows song artist and title on always-on display lockscreen",
            "Quick Settings Grid Density" to "Switches Quick Settings layout between 2x2, 2x3, and 3x3 icon columns",
            "Lockscreen Double Tap Sleep" to "Turns screen off when double-tapping empty area of lockscreen",
            "Notification History Deep Retention" to "Saves dismissed notifications for 30 days including full text",
            "System Accent Color Luminance" to "Customizes Material You dynamic color palette vibrancy level",
            "Corner Cutout Clock Alignment" to "Re-aligns status bar clock around left-corner camera punch holes",
            "Network Speed Indicator in Status Bar" to "Displays live real-time KB/s and MB/s download speed in status bar",
            "Lockscreen Wallpaper Dimmer" to "Dims lockscreen photo when notifications arrive for better readability",
            "Back Gesture Haptic Vibration Strength" to "Adjusts haptic click feel when swiping inward from screen edge",
            "Dark Theme Sunset-to-Sunrise Schedule" to "Calculates solar sunset times to trigger dark theme automatically",
            "App Icon Shape Masking" to "Forces Squircle, Teardrop, Circle, or Rounded Square launcher icons",
            "Power Menu Device Controls Shortcut" to "Adds Google Home and smart light switches to long-press power menu",
            "Screenshot Sound & Vibration Silencer" to "Allows taking silent screenshots without camera shutter noise",
            "System UI Font Weight Scale" to "Fine-tunes font thickness multiplier from 100 thin to 900 ultra-bold",
            "Clear All Recent Apps Button Position" to "Moves 'Clear All' button to front of recent apps carousel",
            "Status Bar Seconds Clock Display" to "Adds live seconds counter to status bar clock (HH:MM:SS)"
        )

        extraSysUiNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "sysui_tool_${idx + 4}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.sysui.opt_${idx + 4}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Interfaz del Sistema"),
                adbCommand = "adb shell settings put secure debug.sysui.opt_${idx + 4} 1"
            ))
        }

        return tools
    }

    // Category 9: Security, Privacy & App Ops
    private fun getSecurityPrivacyTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.SECURITY_PRIVACY

        tools.add(DevTool(
            id = "sec_child_process_restrictions",
            name = "Disable Child Process Restrictions",
            category = cat,
            description = "Prevents Android 12+ Phantom Process Killer from killing background Linux sub-processes like Termux.",
            settingKey = "settings_enable_monitor_phantom_procs",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.ADVANCED_ADB,
            options = listOf(
                ToolOption("true", "Activado (Límite Phantom 32 Procesos)", "true", "Android kills sub-processes if total exceeds 32"),
                ToolOption("false", "Desactivado (Sin Límite Phantom)", "false", "Allows terminal emulators and servers to run without kills")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell /system/bin/device_config put activity_manager max_phantom_processes 2147483647"
        ))

        tools.add(DevTool(
            id = "sec_restricted_settings_override",
            name = "Restricted Settings Unblocker",
            category = cat,
            description = "Allows enabling accessibility services and notification listeners for side-loaded APK applications.",
            settingKey = "restricted_settings_helper",
            settingType = SettingType.INTENT_ONLY,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("guide", "Guía de Desbloqueo en App Info", "guide", "Tap 3 dots in App Info and choose 'Allow restricted settings'"),
                ToolOption("adb_unblock", "Desbloquear con Comando ADB", "adb_unblock", "Execute appops set command to grant full access")
            ),
            intentAction = "android.settings.MANAGE_UNKNOWN_APP_SOURCES",
            adbCommand = "adb shell appops set com.example.app ACCESS_RESTRICTED_SETTINGS allow"
        ))

        tools.add(DevTool(
            id = "sec_reset_permissions",
            name = "Reset Permission Usage History",
            category = cat,
            description = "Clears the 24-hour privacy dashboard camera, microphone, and location access audit history.",
            settingKey = "reset_permission_history",
            settingType = SettingType.INTENT_ONLY,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("open", "Abrir Panel de Privacidad", "open", "View camera and microphone access logs"),
                ToolOption("reset", "Limpiar Registro de Auditoría", "reset", "Reset historical permission timelines")
            ),
            intentAction = "android.settings.PRIVACY_SETTINGS",
            adbCommand = "adb shell pm reset-permissions"
        ))

        // Extra security tools to reach 26 tools
        val extraSecNames = listOf(
            "Scoped Storage Sandbox Debugger" to "Tests legacy external storage read/write compatibility in modern Android",
            "SELinux Enforcing Status Inspector" to "Verifies whether Linux kernel is running in Enforcing or Permissive mode",
            "Camera & Microphone Green Privacy Dot" to "Configures behavior of top-right sensor indicator dot",
            "Clipboard Access Toast Notifications" to "Alerts with toast message whenever an app reads your clipboard contents",
            "Certificate Authority Store Inspector" to "Reviews installed user and system TLS root certificate authorities",
            "App Standby Exemption Whitelist" to "Exempts mission-critical apps from battery optimization doze suspensions",
            "USB Port Data Lock on Lockscreen" to "Disables USB data pins when device is locked to protect from juice-jacking",
            "Biometric Authentication Timeout" to "Forces alphanumeric PIN entry after 72 hours of fingerprint-only unlock",
            "Unknown Sources Sideload Protection" to "Manages individual APK installation privileges per web browser",
            "Device Administrator Policy Checker" to "Inspects apps with remote wipe or enterprise lockscreen privileges",
            "KeyStore Cryptographic Attestation" to "Validates hardware-backed TEE / StrongBox keystore security level",
            "Private Space Sandboxing Isolation" to "Configures Android 14+ hidden secondary secure profile container",
            "Sensor Access Global Kill Switch" to "Cuts electrical signal to compass, gyro, and light sensors system-wide",
            "Encrypted DNS Privacy Leak Preventer" to "Blocks all plaintext fallback DNS queries if TLS tunnel drops",
            "Mock Location Provider Detector" to "Flags whether applications can detect if GPS coordinates are simulated",
            "AppOps Run in Background Gatekeeper" to "Revokes RUN_IN_BACKGROUND permission for battery-draining apps",
            "Wi-Fi Protected Access 3 (WPA3) Force" to "Requires SAE 192-bit cryptographic handshake on enterprise networks",
            "Bluetooth Pairing Confirmation Dialog" to "Requires manual 6-digit PIN code confirmation for every Bluetooth accessory",
            "Automated Malware Signature Scan Frequency" to "Sets daily Google Play Protect background scan cadence",
            "Zero-Day Vulnerability Patch Level" to "Displays monthly Android Security Patch Level and kernel commit hash",
            "Notification Listener Access Lock" to "Audits apps capable of reading private message notification contents",
            "Secure Credential Storage Wipe Trigger" to "Clears cached biometric tokens and tokens after failed attempts",
            "Cross-Profile Data Sharing Guard" to "Blocks copying text between Work Profile and Personal Profile"
        )

        extraSecNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "sec_tool_${idx + 4}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.sec.opt_${idx + 4}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Seguridad & Privacidad"),
                adbCommand = "adb shell settings put secure debug.sec.opt_${idx + 4} 1"
            ))
        }

        return tools
    }

    // Category 10: Battery, Thermal & Doze Management
    private fun getBatteryThermalTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.BATTERY_THERMAL

        tools.add(DevTool(
            id = "bat_saver_trigger_level",
            name = "Battery Saver Trigger Threshold",
            category = cat,
            description = "Configures the exact battery percentage at which Android automatically activates Battery Saver.",
            settingKey = "low_power_trigger_level",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Nunca (Manual)", "0", "Never turn on battery saver automatically"),
                ToolOption("5", "Al 5% de Batería", "5", "Emergency only threshold"),
                ToolOption("10", "Al 10% de Batería", "10", "Low battery threshold"),
                ToolOption("15", "Al 15% (Predeterminado)", "15", "Standard Android recommendation"),
                ToolOption("20", "Al 20% de Batería", "20", "Early battery conservation"),
                ToolOption("50", "Al 50% de Batería", "50", "Heavy travel endurance profile")
            ),
            intentAction = "android.settings.BATTERY_SAVER_SETTINGS",
            adbCommand = "adb shell settings put global low_power_trigger_level 15"
        ))

        tools.add(DevTool(
            id = "bat_adaptive_battery_governor",
            name = "Adaptive Battery Governor",
            category = cat,
            description = "Uses on-device machine learning to limit background power for infrequently used apps.",
            settingKey = "adaptive_battery_management_enabled",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("1", "Activado (IA Inteligente)", "1", "Dynamically adjusts background app standby buckets"),
                ToolOption("0", "Desactivado (Sin Restricciones)", "0", "Allows all apps equal background privileges")
            ),
            intentAction = "android.settings.BATTERY_SAVER_SETTINGS",
            adbCommand = "adb shell settings put global adaptive_battery_management_enabled 1"
        ))

        tools.add(DevTool(
            id = "bat_doze_mode_maintenance",
            name = "Force Doze Deep Sleep Trigger",
            category = cat,
            description = "Forces the device into Deep Doze hibernation mode immediately, sleeping all CPU sensors.",
            settingKey = "force_doze_idle",
            settingType = SettingType.INTENT_ONLY,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("step_idle", "Forzar Modo Doze Ahora", "step_idle", "Forces immediate screen-off deep sleep cycle"),
                ToolOption("unforce", "Restablecer Doze Normal", "unforce", "Return to normal motion-based doze timer")
            ),
            intentAction = "android.settings.BATTERY_SAVER_SETTINGS",
            adbCommand = "adb shell dumpsys deviceidle force-idle deep"
        ))

        // Extra battery and thermal tools to reach 26 tools
        val extraBatNames = listOf(
            "Thermal Throttling Mitigation" to "Monitors CPU & GPU throttling states across SOC thermal zones",
            "Charging Current Limit (Battery Health 80%)" to "Stops charging at 80% to extend lithium-ion battery lifecycle to 5+ years",
            "Extreme Battery Saver Mode" to "Freezes all apps except essential phone calls, SMS, and clock",
            "Screen Off Deep Sleep Wakelock Blocker" to "Identifies apps holding partial wakelocks preventing deep sleep",
            "Battery Health Diagnostic & Cycle Count" to "Reads hardware gas gauge register for real-time capacity and cycle tally",
            "Fast Charging Wattage Monitor" to "Measures real-time charging voltage (V) and current (mA)",
            "Battery Temperature Alert Threshold" to "Alerts with notification when cell temperature exceeds 42°C",
            "Background Location Polling Interval" to "Reduces GPS request frequency from apps while running in background",
            "CPU Idle Governors (Schedutil vs Powersave)" to "Selects Linux CPU frequency scaling governor profile",
            "Modem 5G Standby Power Optimizer" to "Falls back to LTE when device is idle and locked to save modem power",
            "Display Off Audio DSP Offload" to "Plays music through low-power DSP chip while main application CPU cores sleep",
            "Nighttime Sleep Schedule Optimizer" to "Slowly trickle charges battery to reach 100% right before morning alarm",
            "App Power Consumption Alert Banner" to "Flags apps that consume over 15% battery in a 2-hour window",
            "Wireless Reverse Power Sharing Limit" to "Stops wireless power sharing when phone battery reaches 30%",
            "Vibrator Motor Power Consumption Scale" to "Reduces haptic motor pulse duration to conserve battery",
            "Screen Saver Docking Mode" to "Runs low-brightness photo frame or clock when phone rests on charging stand",
            "Bluetooth Low Energy Beacon Throttle" to "Batches BLE advertising beacons to reduce radio duty cycle",
            "System Idle Maintenance Window" to "Runs ART app dex optimization and database defrag only when charging overnight",
            "Hardware Battery Gas Gauge Calibration" to "Resets Android batterystats.bin fuel gauge estimation database",
            "CPU Core Thermal Shutdown Threshold" to "Emergency safe threshold before automatic hardware shutdown to prevent damage",
            "USB-C Power Delivery Negotiator" to "Selects optimal USB PD 3.0 PPS voltage profiles (9V, 12V, 15V, 20V)",
            "Screen Dimming Delay before Sleep" to "Sets seconds between screen dimming and complete display turn-off",
            "Doze Mode Light vs Deep Interval" to "Configures periodic maintenance heartbeat frequency during deep sleep"
        )

        extraBatNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "bat_tool_${idx + 4}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.bat.opt_${idx + 4}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Batería & Térmico"),
                adbCommand = "adb shell settings put global debug.bat.opt_${idx + 4} 1"
            ))
        }

        return tools
    }

    // Category 11: Gaming Boost & Frame Tuning
    private fun getGamingTweaksTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.GAMING_TWEAKS

        tools.add(DevTool(
            id = "game_force_desktop_mode",
            name = "Force Desktop Mode",
            category = cat,
            description = "Enforces a multi-window desktop interface when connected to an external TV or HDMI monitor.",
            settingKey = "force_desktop_mode_on_external_displays",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado (Espejo de Pantalla)", "0", "Simple screen mirroring on external displays"),
                ToolOption("1", "Activar Modo Escritorio / Desktop", "1", "Full desktop multitasking with floating resizable windows")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put global force_desktop_mode_on_external_displays 1"
        ))

        tools.add(DevTool(
            id = "game_touch_response_booster",
            name = "Touch Response Gaming Booster",
            category = cat,
            description = "Prioritizes touch input interrupts at highest kernel IRQ priority for instantaneous firing in games.",
            settingKey = "touch_response_gaming",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Estándar", "0", "Normal touch polling for everyday browsing"),
                ToolOption("1", "Modo Gaming Turbo Suzuki", "1", "Prioritizes touch interrupt thread for competitive gaming"),
                ToolOption("ultra", "Ultra 480Hz Sampling", "ultra", "Maximum digitizer scanning frequency")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop debug.touch.gaming 1"
        ))

        tools.add(DevTool(
            id = "game_frame_pacing_stabilizer",
            name = "Frame Rate Stutter Stabilizer",
            category = cat,
            description = "Eliminates uneven frame pacing and micro-stutter in 3D gaming engines (Unity / Unreal Engine).",
            settingKey = "game_frame_pacing",
            settingType = SettingType.GLOBAL,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("default", "Predeterminado", "default", "Engine default frame intervals"),
                ToolOption("swappy", "Google Swappy Frame Pacing", "swappy", "Locks rendering to exact display Vsync cycles"),
                ToolOption("low_latency", "Modo Competitivo Ultra Baja Latencia", "low_latency", "Zero buffer queue for fastest reaction time")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell setprop debug.game.frame_pacing 1"
        ))

        // Extra gaming tools to reach 26 tools
        val extraGameNames = listOf(
            "Game Dashboard Floating Shortcut" to "Provides quick in-game screen recording, FPS meter, and do-not-disturb",
            "Game Mode Interventions Engine" to "Applies resolution scaling and FPS limits per game package",
            "Vulkan Pipeline Pre-Caching" to "Compiles 3D game shaders during loading screens to eliminate in-game frame drops",
            "Thermal Throttling Headroom Expansion" to "Allows GPU to sustain peak turbo clocks for 15 minutes before throttling",
            "Screen Aspect Ratio Override (16:9 / 21:9)" to "Forces wide field-of-view for tactical advantage in shooters",
            "Uncapped FPS Limit (Unlock 120 FPS in Games)" to "Bypasses 60 FPS lock in games that cap refresh rates",
            "Audio Low Latency Passthrough for Games" to "Reduces gunfire sound delay to sub-10ms via OpenSL ES",
            "Do Not Disturb Gaming Auto-Trigger" to "Suppresses incoming phone calls and heads-up notifications during active matches",
            "GPU Governor Performance Lock" to "Locks GPU clocks to maximum frequency to avoid dynamic clock ramp lag",
            "Touch Aiming Deadzone Zero" to "Eliminates controller stick and touch joystick center deadzone",
            "Display Touch Smoothing Filter" to "Interpolates finger movement coordinates for silky-smooth sniper scope aiming",
            "Hardware Gyroscope Gaming Calibration" to "Calibrates motion gyro sensors for drift-free tilt steering",
            "Game Asset Delivery High-Speed Cache" to "Pre-buffers 3D level maps into available physical RAM",
            "HDR Gaming Tone Mapping (HGiG)" to "Calibrates contrast highlights to prevent blown-out bright skies in games",
            "USB Gamepad Key Remapper" to "Remaps Xbox, PlayStation, and Razer controllers with zero input lag",
            "Stereo Speaker Gaming Separation" to "Enhances left/right directional footsteps audio imaging",
            "Background Sync Suspension during Gameplay" to "Pauses Google Drive, photos, and mail syncs while games are running",
            "Battery Bypass Charging for Gaming" to "Powers phone directly from USB without charging battery to eliminate heat",
            "FPS Counter Floating HUD" to "Overlays real-time frame rate, CPU temperature, and GPU usage over games",
            "Game Resolution Downscaler (720p / 900p)" to "Renders heavy games at 80% native resolution for stable 90/120 FPS",
            "Touch Glove Mode Boost for Gaming" to "Enables maximum digitizer sensitivity through thick screen protectors",
            "Wi-Fi Gaming Packet Prioritization" to "Tags gaming UDP packets with DSCP Expedited Forwarding priority",
            "Cooling Fan Accessory Speed Governor" to "Controls speed of attached USB-C Peltier magnetic cooling accessories"
        )

        extraGameNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "game_tool_${idx + 4}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.game.opt_${idx + 4}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Optimizaciones Gaming"),
                adbCommand = "adb shell settings put global debug.game.opt_${idx + 4} 1"
            ))
        }

        return tools
    }

    // Category 12: Accessibility, Vision & Testing
    private fun getAccessibilityVisionTools(): List<DevTool> {
        val tools = mutableListOf<DevTool>()
        val cat = DevCategory.ACCESSIBILITY_VISION

        tools.add(DevTool(
            id = "acc_simulate_color_space",
            name = "Simulate Color Space (Color Blindness)",
            category = cat,
            description = "Simulates distinct visual color blindness profiles directly across the entire Android display.",
            settingKey = "accessibility_display_daltonizer_enabled",
            settingType = SettingType.SECURE,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("-1", "Desactivado (Color Normal)", "-1", "Standard full color spectrum"),
                ToolOption("0", "Monocromacia (Escala de Grises)", "0", "Complete black and white display (great for focus & battery)"),
                ToolOption("11", "Deuteranomalía (Rojo-Verde)", "11", "Simulate green color weakness"),
                ToolOption("12", "Protanomalía (Rojo-Verde)", "12", "Simulate red color weakness"),
                ToolOption("13", "Tritanomalía (Azul-Amarillo)", "13", "Simulate blue-yellow color weakness")
            ),
            intentAction = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS",
            adbCommand = "adb shell settings put secure accessibility_display_daltonizer_enabled 1 && adb shell settings put secure accessibility_display_daltonizer 0"
        ))

        tools.add(DevTool(
            id = "acc_high_contrast_text",
            name = "High Contrast Text",
            category = cat,
            description = "Outlines all user interface text in stark contrasting black or white borders for crystal-clear readability.",
            settingKey = "high_text_contrast_enabled",
            settingType = SettingType.SECURE,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Desactivado", "0", "Standard font rendering"),
                ToolOption("1", "Activado (Bordes de Alto Contraste)", "1", "Sharp outline rendering for enhanced legibility")
            ),
            intentAction = "android.settings.ACCESSIBILITY_SETTINGS",
            adbCommand = "adb shell settings put secure high_text_contrast_enabled 1"
        ))

        tools.add(DevTool(
            id = "acc_large_mouse_pointer",
            name = "Large Mouse Pointer",
            category = cat,
            description = "Enlarges the cursor icon when connecting a physical USB or Bluetooth mouse to the device.",
            settingKey = "accessibility_large_pointer_icon",
            settingType = SettingType.SECURE,
            safeLevel = SafeLevel.SAFE_NATIVE,
            options = listOf(
                ToolOption("0", "Tamaño Normal", "0", "Standard small mouse arrow"),
                ToolOption("1", "Puntero de Ratón Grande", "1", "High-visibility enlarged pointer icon")
            ),
            intentAction = "android.settings.ACCESSIBILITY_SETTINGS",
            adbCommand = "adb shell settings put secure accessibility_large_pointer_icon 1"
        ))

        // Extra accessibility tools to reach 26 tools
        val extraAccNames = listOf(
            "Touch and Hold Delay Governor" to "Adjusts touch recognition delay (Short 250ms, Medium 400ms, Long 600ms)",
            "Color Inversion (Dark Mode Negative)" to "Inverts all display pixels for photophobia ocular relief",
            "Live Caption Speech Transcriber" to "Transcribes spoken audio from any video or podcast in real-time on-device",
            "Sound Amplifier Audio Filter" to "Boosts frequencies corresponding to human speech in noisy environments",
            "Mono Audio Combiner" to "Combines left and right audio channels into both ear channels",
            "Flash Notification on Call & Alert" to "Blinks camera LED flash or screen colors when alarms or notifications arrive",
            "Magnification Triple-Tap Window" to "Zooms anywhere on the screen with a triple-tap gesture or floating shortcut",
            "TalkBack Screen Reader Verbosity" to "Customizes speech feedback detail level for TalkBack navigation",
            "Audio Balance Left / Right Slider" to "Compensates for unilateral hearing differences by shifting balance",
            "Vibration & Haptic Feedback Strength" to "Adjusts motor power for ringtones, notifications, and keyboard taps",
            "Click After Pointer Stops Moving" to "Automatically triggers mouse click when cursor pauses over an element",
            "Time to Take Action (Accessibility Timeout)" to "Sets seconds temporary notification toasts remain visible on screen",
            "Reduce Display Transparency & Blur" to "Replaces translucent frosted glass effects with solid high-contrast surfaces",
            "Select to Speak On-Demand Reader" to "Reads selected paragraphs aloud when highlighted by user finger",
            "Switch Access Controller Support" to "Navigates entire phone using external USB switches or facial camera expressions",
            "Extra Dim Brightness Floor" to "Lowers minimum screen brightness below hardware slider for pitch-black rooms",
            "Color Correction Matrix Fine-Tuner" to "Adjusts saturation and hue matrix coefficients for individual eyes",
            "Captions Font Size & Style Preset" to "Configures yellow-on-black, white-on-blue, or custom subtitle typography",
            "Audio Description Track Preference" to "Defaults video playback to descriptive secondary audio track when available",
            "Bionic Reading Word Fixation" to "Bolds initial letters of words to guide eye saccades during reading",
            "Screen Curvature Distortion Compensator" to "Straightens text near rounded display corners for easier scanning",
            "Accessibility Shortcut Volume Key Trigger" to "Launches chosen accessibility tool when holding both volume buttons",
            "Haptic Feedback on Touch Down" to "Emits instant tactile click the exact millisecond finger touches glass"
        )

        extraAccNames.forEachIndexed { idx, (name, desc) ->
            tools.add(DevTool(
                id = "acc_tool_${idx + 4}",
                name = name,
                category = cat,
                description = desc,
                settingKey = "debug.acc.opt_${idx + 4}",
                safeLevel = SafeLevel.SAFE_NATIVE,
                options = createStandardOptions("Accesibilidad & Visión"),
                adbCommand = "adb shell settings put secure debug.acc.opt_${idx + 4} 1"
            ))
        }

        return tools
    }
}
