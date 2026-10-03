#!/bin/bash
set -euo pipefail

cd "$(dirname "$0")"

KEYSTORE="lexicore-key.jks"
KEY_ALIAS="lexicore"
OUTPUT_DIR="release"
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")

[ -f "/usr/lib/jvm/java-25-openjdk/bin/java" ] && export JAVA_HOME="/usr/lib/jvm/java-25-openjdk" || { echo "Java 25 not found"; exit 1; }
[ -f "$KEYSTORE" ] || { echo "Keystore not found"; exit 1; }

export ANDROID_HOME="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-$HOME/Android/Sdk}}"
[ -d "$ANDROID_HOME" ] || { echo "SDK not found"; exit 1; }

if [ -t 0 ]; then
    trap 'stty echo' EXIT
    stty -echo
    printf "Password: "
    IFS= read -r PW
    stty echo
    echo ""
else
    IFS= read -r PW || [ -n "$PW" ]
fi

env "ORG_GRADLE_PROJECT_android.injected.signing.store.password=$PW" \
    "ORG_GRADLE_PROJECT_android.injected.signing.key.password=$PW" \
    ./gradlew clean lintRelease bundleRelease assembleRelease \
    --no-daemon \
    --warning-mode all \
    -Dorg.gradle.java.home="$JAVA_HOME" \
    -Pandroid.injected.signing.store.file="$(pwd)/$KEYSTORE" \
    -Pandroid.injected.signing.key.alias="$KEY_ALIAS"

unset PW

mkdir -p "$OUTPUT_DIR"
find app/build/outputs/bundle/release/ -name "*.aab" -exec cp {} "$OUTPUT_DIR/LexiCore_${TIMESTAMP}.aab" \;
find app/build/outputs/apk/release/ -name "*-release.apk" -exec cp {} "$OUTPUT_DIR/LexiCore_${TIMESTAMP}.apk" \;

echo "OK: $OUTPUT_DIR/LexiCore_${TIMESTAMP}.{aab,apk}"
