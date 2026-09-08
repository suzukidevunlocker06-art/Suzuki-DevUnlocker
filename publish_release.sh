#!/usr/bin/env bash
set -e

# ==============================================================================
# Suzuki-DevUnlocker v3.4 — GitHub Release Publisher
# ==============================================================================

REPO="${1:-$GITHUB_REPOSITORY}"
TOKEN="${2:-$GITHUB_TOKEN}"
TAG="v3.4"
NAME="Suzuki DevUnlocker v3.4"
APK_PATH="app/build/outputs/apk/release/app-release.apk"
APK_RELEASE_NAME="Suzuki-DevUnlocker-v3.4.apk"

echo "=== 🐺 Suzuki-DevUnlocker GitHub Release Publisher ==="

if [ ! -f "$APK_PATH" ]; then
    echo "⚠️ APK de release no encontrado. Compilando proyecto..."
    STORE_PASSWORD=android KEY_PASSWORD=android gradle :app:assembleRelease
fi

if [ ! -f "$APK_PATH" ]; then
    echo "❌ Error: No se pudo generar el APK de release en $APK_PATH."
    exit 1
fi

APK_SIZE=$(ls -lh "$APK_PATH" | awk '{print $5}')
echo "✅ APK de Release listo: $APK_PATH ($APK_SIZE)"

# Check if repo and token are provided
if [ -z "$REPO" ] || [ -z "$TOKEN" ]; then
    echo ""
    echo "ℹ️ Para publicar automáticamente en GitHub, ejecuta este comando con tu repositorio y token:"
    echo "   ./publish_release.sh <USUARIO/REPOSITORIO> <GITHUB_TOKEN>"
    echo ""
    echo "Ejemplo:"
    echo "   ./publish_release.sh suzukidevunlocker06/Suzuki-DevUnlocker ghp_xxxxxxxxxxxx"
    echo ""
    echo "O define las variables de entorno:"
    echo "   export GITHUB_REPOSITORY=\"tu_usuario/Suzuki-DevUnlocker\""
    echo "   export GITHUB_TOKEN=\"tu_personal_access_token\""
    echo "   ./publish_release.sh"
    exit 0
fi

echo "🚀 Publicando Release $TAG en GitHub: $REPO..."

# Release notes
RELEASE_NOTES=$(cat <<'EOF'
## 🐺 Suzuki DevUnlocker v3.4 — Official Release

### 🚀 Novedades y Optimizaciones
- **312+ Herramientas Avanzadas**: GPU, Animaciones, Redes/ADB, Memoria RAM, Depuración/Logcat, Entrada Táctil, Audio, Sistema, Privacidad, Batería, Gaming y Accesibilidad.
- **1,800+ Opciones de Ajuste Fino**: Perfiles Suzuki Turbo, Ahorro de Energía, Baja Latencia y Diagnóstico.
- **Telemetría en Vivo**: Monitor continuo de FPS / Tasa de Refresco, memoria RAM disponible, temperatura y nivel de batería, estado de Developer Mode y ADB.
- **Presets Rápidos (1-Clic)**:
  - ⚡ **0.5x Speed**: Aceleración inmediata de transiciones a 0.5x.
  - 🎮 **Gaming Turbo**: 4x MSAA y máxima tasa de sondeo táctil.
  - 🔋 **Eco Batería**: Hibernación activa de procesos de fondo.
  - 📸 **Demo Screenshot**: Barra de estado impecable con batería al 100%.
- **Asistente de Activación**: Pasos guiados y enlace nativo a Ajustes del Sistema y Opciones de Desarrollador.
- **Soporte Multi-Idioma**: Español 🇪🇸, English 🇺🇸, Português 🇧🇷, Français 🇫🇷, Deutsch 🇩🇪, 中文 🇨🇳, 日本語 🇯🇵.

### 📱 Compatibilidad
- Android 7.0 (API 24) a Android 16 (API 36).
- Arquitecturas: ARM64-v8a, ARMeabi-v7a, x86_64.

### 📥 Descarga e Instalación
Descarga `Suzuki-DevUnlocker-v3.4.apk` adjunto e instálalo en tu dispositivo Android.
EOF
)

# 1. Create or get release
echo "📦 Creando Release en GitHub API..."
RELEASE_PAYLOAD=$(node -e "console.log(JSON.stringify({
  tag_name: '$TAG',
  name: '$NAME',
  body: process.argv[1],
  draft: false,
  prerelease: false
}))" "$RELEASE_NOTES")

RELEASE_RESPONSE=$(curl -s -X POST \
  -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  https://api.github.com/repos/$REPO/releases \
  -d "$RELEASE_PAYLOAD")

RELEASE_ID=$(echo "$RELEASE_RESPONSE" | node -e "
  let d = '';
  process.stdin.on('data', c => d += c);
  process.stdin.on('end', () => {
    try {
      const j = JSON.parse(d);
      if (j.id) console.log(j.id);
      else if (j.errors || j.message) console.error(j.message);
    } catch(e) {}
  });
")

# If release already exists, fetch it
if [ -z "$RELEASE_ID" ]; then
    echo "Buscando release existente con tag $TAG..."
    RELEASE_RESPONSE=$(curl -s -X GET \
      -H "Authorization: token $TOKEN" \
      -H "Accept: application/vnd.github.v3+json" \
      https://api.github.com/repos/$REPO/releases/tags/$TAG)
    RELEASE_ID=$(echo "$RELEASE_RESPONSE" | node -e "
      let d = '';
      process.stdin.on('data', c => d += c);
      process.stdin.on('end', () => {
        try {
          const j = JSON.parse(d);
          if (j.id) console.log(j.id);
        } catch(e) {}
      });
    ")
fi

if [ -z "$RELEASE_ID" ]; then
    echo "❌ Error al crear/obtener la Release en GitHub:"
    echo "$RELEASE_RESPONSE"
    exit 1
fi

echo "✅ Release ID obtenido: $RELEASE_ID"

# 2. Upload APK Asset
echo "📤 Subiendo $APK_RELEASE_NAME ($APK_SIZE) a los Assets de la Release..."
UPLOAD_URL="https://uploads.github.com/repos/$REPO/releases/$RELEASE_ID/assets?name=$APK_RELEASE_NAME"

UPLOAD_RESPONSE=$(curl -s -X POST \
  -H "Authorization: token $TOKEN" \
  -H "Accept: application/vnd.github.v3+json" \
  -H "Content-Type: application/vnd.android.package-archive" \
  --data-binary @"$APK_PATH" \
  "$UPLOAD_URL")

BROWSER_DOWNLOAD_URL=$(echo "$UPLOAD_RESPONSE" | node -e "
  let d = '';
  process.stdin.on('data', c => d += c);
  process.stdin.on('end', () => {
    try {
      const j = JSON.parse(d);
      if (j.browser_download_url) console.log(j.browser_download_url);
    } catch(e) {}
  });
")

if [ -n "$BROWSER_DOWNLOAD_URL" ]; then
    echo ""
    echo "🎉 ¡Release publicada exitosamente!"
    echo "🔗 Enlace de descarga directa del APK:"
    echo "   $BROWSER_DOWNLOAD_URL"
    echo "🔗 Ver Release en GitHub:"
    echo "   https://github.com/$REPO/releases/tag/$TAG"
else
    echo "⚠️ Respuesta de subida de asset:"
    echo "$UPLOAD_RESPONSE"
fi
