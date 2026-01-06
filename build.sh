#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

KEYSTORE="lexicore-key.jks"
KEY_ALIAS="lexicore"
OUTPUT_DIR="release"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

[ -f "/usr/lib/jvm/java-25-openjdk/bin/java" ] && export JAVA_HOME="/usr/lib/jvm/java-25-openjdk" || { echo "Java 25 not found"; exit 1; }
[ -f "$KEYSTORE" ] || { echo "Keystore not found"; exit 1; }

SDK_PATH="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Android/Sdk}}"
[ -d "$SDK_PATH" ] || { echo "SDK not found"; exit 1; }

if [ -t 0 ]; then
    stty -echo
    printf "Password: "
    IFS= read -r PW
    stty echo
    echo ""
else
    read -r PW
fi

./gradlew clean lintRelease bundleRelease assembleRelease \
    --no-daemon \
    --warning-mode all \
    -Dorg.gradle.java.home="$JAVA_HOME" \
    -Dandroid.sdk.dir="$SDK_PATH" \
    -Pandroid.experimental.sdk.test.enableTargetSdkCheck=false \
    -Pandroid.injected.signing.store.file="$(pwd)/$KEYSTORE" \
    -Pandroid.injected.signing.store.password="${PW}" \
    -Pandroid.injected.signing.key.alias="$KEY_ALIAS" \
    -Pandroid.injected.signing.key.password="${PW}"

unset PW

mkdir -p "$OUTPUT_DIR"
find app/build/outputs/bundle/release/ -name "*.aab" -exec cp {} "$OUTPUT_DIR/LexiCore_${TIMESTAMP}.aab" \;
find app/build/outputs/apk/release/ -name "*-release.apk" -exec cp {} "$OUTPUT_DIR/LexiCore_${TIMESTAMP}.apk" \;

echo "OK: $OUTPUT_DIR/LexiCore_${TIMESTAMP}.{aab,apk}"
