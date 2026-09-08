package com.example.data

import com.example.model.DevCategory

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ES("es", "Español", "🇪🇸"),
    EN("en", "English", "🇺🇸"),
    PT("pt", "Português", "🇧🇷"),
    FR("fr", "Français", "🇫🇷"),
    DE("de", "Deutsch", "🇩🇪"),
    ZH("zh", "中文 (简体)", "🇨🇳"),
    JA("ja", "日本語", "🇯🇵")
}

object LanguageManager {
    fun getCategoryName(category: DevCategory, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.ES -> when (category) {
                DevCategory.GPU_GRAPHICS -> "Gráficos y GPU"
                DevCategory.ANIMATIONS_WINDOW -> "Animaciones y Ventanas"
                DevCategory.NETWORK_ADB -> "Redes, Wi-Fi y ADB"
                DevCategory.MEMORY_PROCESS -> "Memoria y Procesos"
                DevCategory.DEBUG_LOGCAT -> "Depuración y Logcat"
                DevCategory.INPUT_DISPLAY -> "Entrada y Pantalla"
                DevCategory.AUDIO_MEDIA -> "Audio y Códecs"
                DevCategory.SYSTEM_UI -> "Interfaz del Sistema"
                DevCategory.SECURITY_PRIVACY -> "Seguridad y Privacidad"
                DevCategory.BATTERY_THERMAL -> "Batería y Térmica"
                DevCategory.GAMING_TWEAKS -> "Optimización Gaming"
                DevCategory.ACCESSIBILITY_VISION -> "Accesibilidad y Visión"
            }
            AppLanguage.EN -> when (category) {
                DevCategory.GPU_GRAPHICS -> "Graphics & GPU"
                DevCategory.ANIMATIONS_WINDOW -> "Animations & Windows"
                DevCategory.NETWORK_ADB -> "Networks, Wi-Fi & ADB"
                DevCategory.MEMORY_PROCESS -> "Memory & Processes"
                DevCategory.DEBUG_LOGCAT -> "Debugging & Logcat"
                DevCategory.INPUT_DISPLAY -> "Input & Display"
                DevCategory.AUDIO_MEDIA -> "Audio & Codecs"
                DevCategory.SYSTEM_UI -> "System UI & Custom"
                DevCategory.SECURITY_PRIVACY -> "Security & Privacy"
                DevCategory.BATTERY_THERMAL -> "Battery & Thermal"
                DevCategory.GAMING_TWEAKS -> "Gaming Optimization"
                DevCategory.ACCESSIBILITY_VISION -> "Accessibility & Vision"
            }
            AppLanguage.PT -> when (category) {
                DevCategory.GPU_GRAPHICS -> "Gráficos e GPU"
                DevCategory.ANIMATIONS_WINDOW -> "Animações e Janelas"
                DevCategory.NETWORK_ADB -> "Redes, Wi-Fi e ADB"
                DevCategory.MEMORY_PROCESS -> "Memória e Processos"
                DevCategory.DEBUG_LOGCAT -> "Depuração e Logcat"
                DevCategory.INPUT_DISPLAY -> "Entrada e Tela"
                DevCategory.AUDIO_MEDIA -> "Áudio e Codecs"
                DevCategory.SYSTEM_UI -> "Interface do Sistema"
                DevCategory.SECURITY_PRIVACY -> "Segurança e Privacidade"
                DevCategory.BATTERY_THERMAL -> "Bateria e Térmica"
                DevCategory.GAMING_TWEAKS -> "Otimização para Jogos"
                DevCategory.ACCESSIBILITY_VISION -> "Acessibilidade e Visão"
            }
            AppLanguage.FR -> when (category) {
                DevCategory.GPU_GRAPHICS -> "Graphismes & GPU"
                DevCategory.ANIMATIONS_WINDOW -> "Animations & Fenêtres"
                DevCategory.NETWORK_ADB -> "Réseaux, Wi-Fi & ADB"
                DevCategory.MEMORY_PROCESS -> "Mémoire & Processus"
                DevCategory.DEBUG_LOGCAT -> "Débogage & Logcat"
                DevCategory.INPUT_DISPLAY -> "Entrée & Affichage"
                DevCategory.AUDIO_MEDIA -> "Audio & Codecs"
                DevCategory.SYSTEM_UI -> "Interface Système"
                DevCategory.SECURITY_PRIVACY -> "Sécurité & Confidentialité"
                DevCategory.BATTERY_THERMAL -> "Batterie & Thermique"
                DevCategory.GAMING_TWEAKS -> "Optimisation Gaming"
                DevCategory.ACCESSIBILITY_VISION -> "Accessibilité & Vision"
            }
            AppLanguage.DE -> when (category) {
                DevCategory.GPU_GRAPHICS -> "Grafik & GPU"
                DevCategory.ANIMATIONS_WINDOW -> "Animationen & Fenster"
                DevCategory.NETWORK_ADB -> "Netzwerk, WLAN & ADB"
                DevCategory.MEMORY_PROCESS -> "Speicher & Prozesse"
                DevCategory.DEBUG_LOGCAT -> "Debugging & Logcat"
                DevCategory.INPUT_DISPLAY -> "Eingabe & Anzeige"
                DevCategory.AUDIO_MEDIA -> "Audio & Codecs"
                DevCategory.SYSTEM_UI -> "Systemoberfläche"
                DevCategory.SECURITY_PRIVACY -> "Sicherheit & Datenschutz"
                DevCategory.BATTERY_THERMAL -> "Akku & Temperatur"
                DevCategory.GAMING_TWEAKS -> "Gaming-Optimierung"
                DevCategory.ACCESSIBILITY_VISION -> "Bedienungshilfen"
            }
            AppLanguage.ZH -> when (category) {
                DevCategory.GPU_GRAPHICS -> "图形与 GPU"
                DevCategory.ANIMATIONS_WINDOW -> "动画与窗口"
                DevCategory.NETWORK_ADB -> "网络、Wi-Fi 与 ADB"
                DevCategory.MEMORY_PROCESS -> "内存与后台进程"
                DevCategory.DEBUG_LOGCAT -> "调试与日志"
                DevCategory.INPUT_DISPLAY -> "输入与屏幕显示"
                DevCategory.AUDIO_MEDIA -> "音频与编解码器"
                DevCategory.SYSTEM_UI -> "系统界面"
                DevCategory.SECURITY_PRIVACY -> "安全与隐私"
                DevCategory.BATTERY_THERMAL -> "电池与温度"
                DevCategory.GAMING_TWEAKS -> "游戏加速调优"
                DevCategory.ACCESSIBILITY_VISION -> "无障碍与色彩模拟"
            }
            AppLanguage.JA -> when (category) {
                DevCategory.GPU_GRAPHICS -> "グラフィックスとGPU"
                DevCategory.ANIMATIONS_WINDOW -> "アニメーションとウィンドウ"
                DevCategory.NETWORK_ADB -> "ネットワーク、Wi-FiとADB"
                DevCategory.MEMORY_PROCESS -> "メモリとプロセス"
                DevCategory.DEBUG_LOGCAT -> "デバッグとログ"
                DevCategory.INPUT_DISPLAY -> "入力とディスプレイ"
                DevCategory.AUDIO_MEDIA -> "オーディオとコーデック"
                DevCategory.SYSTEM_UI -> "システムUI"
                DevCategory.SECURITY_PRIVACY -> "セキュリティとプライバシー"
                DevCategory.BATTERY_THERMAL -> "バッテリーと温度"
                DevCategory.GAMING_TWEAKS -> "ゲーム最適化"
                DevCategory.ACCESSIBILITY_VISION -> "アクセシビリティ"
            }
        }
    }

    fun getUiString(key: String, lang: AppLanguage): String {
        return when (key) {
            "app_title" -> "Suzuki DevUnlocker"
            "app_version" -> "v3.4 PRO"
            "unlock_title" -> when (lang) {
                AppLanguage.ES -> "Desbloquear Opciones de Desarrollador"
                AppLanguage.EN -> "Unlock Developer Options"
                AppLanguage.PT -> "Desbloquear Opções do Desenvolvedor"
                AppLanguage.FR -> "Déverrouiller Options Développeur"
                AppLanguage.DE -> "Entwickleroptionen entsperren"
                AppLanguage.ZH -> "解锁开发者选项"
                AppLanguage.JA -> "開発者向けオプションを解除"
            }
            "unlock_desc" -> when (lang) {
                AppLanguage.ES -> "Toca 'Información del Teléfono' y pulsa el 'Número de compilación' 7 veces consecutivas."
                AppLanguage.EN -> "Go to 'About Phone' and tap 'Build Number' 7 consecutive times to enable."
                AppLanguage.PT -> "Acesse 'Sobre o Telefone' e toque no 'Número da versão' 7 vezes seguidas."
                AppLanguage.FR -> "Accédez à 'À propos du téléphone' et appuyez 7 fois sur le 'Numéro de build'."
                AppLanguage.DE -> "Tippen Sie unter 'Über das Telefon' 7 Mal auf die 'Build-Nummer'."
                AppLanguage.ZH -> "前往“关于手机”，连续点击“版本号”7次即可开启。"
                AppLanguage.JA -> "「端末情報」の「ビルド番号」を7回連続でタップして有効化します。"
            }
            "open_device_info" -> when (lang) {
                AppLanguage.ES -> "Abrir Info del Teléfono"
                AppLanguage.EN -> "Open About Phone"
                AppLanguage.PT -> "Abrir Sobre o Telefone"
                AppLanguage.FR -> "Ouvrir Infos Téléphone"
                AppLanguage.DE -> "Telefoninfo öffnen"
                AppLanguage.ZH -> "打开系统信息"
                AppLanguage.JA -> "端末情報を開く"
            }
            "open_dev_settings" -> when (lang) {
                AppLanguage.ES -> "Abrir Opciones Desarrollador"
                AppLanguage.EN -> "Open Developer Settings"
                AppLanguage.PT -> "Abrir Opções de Dev"
                AppLanguage.FR -> "Ouvrir Paramètres Développeur"
                AppLanguage.DE -> "Entwickleroptionen öffnen"
                AppLanguage.ZH -> "打开开发者设置"
                AppLanguage.JA -> "開発者設定を開く"
            }
            "realtime_telemetry" -> when (lang) {
                AppLanguage.ES -> "Telemetría Suzuki en Tiempo Real"
                AppLanguage.EN -> "Suzuki Real-Time Telemetry"
                AppLanguage.PT -> "Telemetria Suzuki em Tempo Real"
                AppLanguage.FR -> "Télémétrie Suzuki en Temps Réel"
                AppLanguage.DE -> "Suzuki Echtzeit-Telemetrie"
                AppLanguage.ZH -> "Suzuki 实时遥测监控"
                AppLanguage.JA -> "Suzuki リアルタイムテレメトリ"
            }
            "search_hint" -> when (lang) {
                AppLanguage.ES -> "Buscar entre 300+ herramientas y 1500+ opciones..."
                AppLanguage.EN -> "Search across 300+ tools and 1,500+ options..."
                AppLanguage.PT -> "Pesquisar entre 300+ ferramentas e 1500+ opções..."
                AppLanguage.FR -> "Rechercher parmi 300+ outils et 1500+ options..."
                AppLanguage.DE -> "Suche in über 300 Tools und 1500 Optionen..."
                AppLanguage.ZH -> "在 300+ 开发者工具与 1500+ 项设置中搜索..."
                AppLanguage.JA -> "300以上のツールと1500以上の設定から検索..."
            }
            "all_categories" -> when (lang) {
                AppLanguage.ES -> "Todas las Herramientas"
                AppLanguage.EN -> "All Tools"
                AppLanguage.PT -> "Todas as Ferramentas"
                AppLanguage.FR -> "Tous les Outils"
                AppLanguage.DE -> "Alle Werkzeuge"
                AppLanguage.ZH -> "全部工具"
                AppLanguage.JA -> "すべてのツール"
            }
            "tools_count_badge" -> when (lang) {
                AppLanguage.ES -> "318 Herramientas | 1,540 Opciones"
                AppLanguage.EN -> "318 Tools | 1,540 Options"
                AppLanguage.PT -> "318 Ferramentas | 1.540 Opções"
                AppLanguage.FR -> "318 Outils | 1 540 Options"
                AppLanguage.DE -> "318 Tools | 1.540 Optionen"
                AppLanguage.ZH -> "318 个工具 | 1,540 项选项"
                AppLanguage.JA -> "318 ツール | 1,540 設定"
            }
            "adb_status" -> when (lang) {
                AppLanguage.ES -> "Estado ADB"
                AppLanguage.EN -> "ADB Status"
                AppLanguage.PT -> "Status ADB"
                AppLanguage.FR -> "Statut ADB"
                AppLanguage.DE -> "ADB-Status"
                AppLanguage.ZH -> "ADB 状态"
                AppLanguage.JA -> "ADB ステータス"
            }
            "dev_mode" -> when (lang) {
                AppLanguage.ES -> "Modo Dev"
                AppLanguage.EN -> "Dev Mode"
                AppLanguage.PT -> "Modo Dev"
                AppLanguage.FR -> "Mode Dév"
                AppLanguage.DE -> "Entwickler"
                AppLanguage.ZH -> "开发者模式"
                AppLanguage.JA -> "開発者モード"
            }
            "active" -> when (lang) {
                AppLanguage.ES -> "ACTIVO"
                AppLanguage.EN -> "ACTIVE"
                AppLanguage.PT -> "ATIVO"
                AppLanguage.FR -> "ACTIF"
                AppLanguage.DE -> "AKTIV"
                AppLanguage.ZH -> "已激活"
                AppLanguage.JA -> "有効"
            }
            "inactive" -> when (lang) {
                AppLanguage.ES -> "BLOQUEADO"
                AppLanguage.EN -> "LOCKED"
                AppLanguage.PT -> "BLOQUEADO"
                AppLanguage.FR -> "VERROUILLÉ"
                AppLanguage.DE -> "GESPERRT"
                AppLanguage.ZH -> "未开启"
                AppLanguage.JA -> "ロック中"
            }
            "copy_adb" -> when (lang) {
                AppLanguage.ES -> "Copiar Comando ADB"
                AppLanguage.EN -> "Copy ADB Command"
                AppLanguage.PT -> "Copiar Comando ADB"
                AppLanguage.FR -> "Copier Commande ADB"
                AppLanguage.DE -> "ADB-Befehl kopieren"
                AppLanguage.ZH -> "复制 ADB 指令"
                AppLanguage.JA -> "ADBコマンドをコピー"
            }
            "safe_android_notice" -> when (lang) {
                AppLanguage.ES -> "Modifica estrictamente lo permitido por Android sin riesgo al sistema."
                AppLanguage.EN -> "Modifies strictly what Android allows safely without system risk."
                AppLanguage.PT -> "Modifica estritamente o permitido pelo Android com segurança."
                AppLanguage.FR -> "Modifie uniquement ce qui est autorisé par Android en toute sécurité."
                AppLanguage.DE -> "Ändert nur was Android sicher erlaubt ohne Systemrisiko."
                AppLanguage.ZH -> "严格在 Android 官方安全权限范围内修改，确保系统稳定。"
                AppLanguage.JA -> "Androidが許可する範囲内でのみ安全に変更します。"
            }
            "presets_title" -> when (lang) {
                AppLanguage.ES -> "Ajustes Rápidos Suzuki"
                AppLanguage.EN -> "Suzuki Quick Presets"
                AppLanguage.PT -> "Predefinições Suzuki"
                AppLanguage.FR -> "Préréglages Rapides Suzuki"
                AppLanguage.DE -> "Suzuki Schnell-Voreinstellungen"
                AppLanguage.ZH -> "Suzuki 快速调优预设"
                AppLanguage.JA -> "Suzuki クイックプリセット"
            }
            else -> key
        }
    }
}
