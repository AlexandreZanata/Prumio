#!/usr/bin/env bash
# Physical-device-only integration gate; permission prerequisites are documented in app/android/README.md.
set -euo pipefail
cd "$(dirname "$0")/.."
serial="${1:?Pass a physical adb serial}"
if [[ "$serial" == emulator-* ]]; then
    echo "Use the owner's physical phone (AGENTS.md)." >&2
    exit 1
fi
adb -s "$serial" get-state >/dev/null
./gradlew :app:android:assembleDebug :app:android:assembleDebugAndroidTest
adb -s "$serial" install -r app/android/build/outputs/apk/debug/android-debug.apk
adb -s "$serial" install -r -t app/android/build/outputs/apk/androidTest/debug/android-debug-androidTest.apk
result="$(adb -s "$serial" shell am instrument -w app.prumio.debug.test/app.prumio.app.android.di.ReminderInstrumentation)"
printf '%s\n' "$result"
[[ "$result" == *"PASS: real alarm, receiver, notification and Complete action"* ]]
