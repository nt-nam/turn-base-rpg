#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
out="${1:-$root/tools/test-agent/reports/android-$(date +%Y%m%d-%H%M%S)}"
mode="${MODE:-scenario}"
host_port="${HOST_PORT:-47117}"
device_port=47017
package=com.game.pxworld
activity=com.pxworld.android.AndroidLauncher
apk=game/platform-android/build/outputs/apk/debug/platform-android-debug.apk
boot_timeout="${BOOT_TIMEOUT:-600}"
start_timeout="${GAME_START_TIMEOUT:-180}"
mkdir -p "$out"
cd "$root"

sdk_dir() {
  local dir="${ANDROID_SDK_ROOT:-${ANDROID_HOME:-}}"
  if [ -z "$dir" ] && [ -f local.properties ]; then
    dir="$(sed -n 's/^sdk\.dir=//p' local.properties | tr -d '\r' | sed 's/\\:/:/g; s/\\\\/\//g')"
  fi
  echo "$dir"
}

sdk="$(sdk_dir)"
adb_bin="${ADB:-}"
if [ -z "$adb_bin" ] && [ -n "$sdk" ] && [ -e "$sdk/platform-tools/adb" ]; then adb_bin="$sdk/platform-tools/adb"; fi
if [ -z "$adb_bin" ]; then adb_bin="$(command -v adb || true)"; fi
if [ -z "$adb_bin" ]; then echo "adb not found: set ANDROID_SDK_ROOT, ANDROID_HOME or ADB" >&2; exit 1; fi

adb_cmd() { MSYS_NO_PATHCONV=1 "$adb_bin" "$@"; }
device_prop() { adb_cmd shell getprop "$1" 2>/dev/null | tr -d '\r' || true; }
has_device() { adb_cmd devices 2>/dev/null | tr -d '\r' | awk 'NR > 1 && $2 == "device"' | grep -q .; }

started_emulator=""
cleanup() {
  adb_cmd forward --remove "tcp:$host_port" >/dev/null 2>&1 || true
  if [ -n "$started_emulator" ] && [ -z "${KEEP_EMULATOR:-}" ]; then adb_cmd emu kill >/dev/null 2>&1 || true; fi
}
trap cleanup EXIT

api_url="${PXWORLD_API_URL:-}"
[ "$api_url" = off ] && api_url=""
if [ -z "${SKIP_BUILD:-}" ]; then
  ./gradlew :game:platform-android:assembleDebug -q -Ppxworld.apiUrl="$api_url"
fi
[ -f "$apk" ] || { echo "missing $apk" >&2; exit 1; }

adb_cmd start-server >/dev/null 2>&1 || true
if ! has_device; then
  emulator="$sdk/emulator/emulator"
  avd="${AVD:-$("$emulator" -list-avds 2>/dev/null | tr -d '\r' | grep -v '|' | head -n 1)}"
  [ -n "$avd" ] || { echo "no device attached and no AVD to boot" >&2; exit 1; }
  read -r -a emulator_flags <<< "${EMULATOR_FLAGS:-"-feature -Vulkan"}"
  echo "booting emulator $avd"
  "$emulator" -avd "$avd" -no-window -gpu swiftshader_indirect -no-snapshot -no-audio -no-boot-anim "${emulator_flags[@]}" > "$out/emulator.log" 2>&1 &
  started_emulator=$!
fi

deadline=$((SECONDS + boot_timeout))
until [ "$(device_prop sys.boot_completed)" = 1 ] && adb_cmd shell pm path android >/dev/null 2>&1; do
  if [ "$SECONDS" -ge "$deadline" ]; then echo "device did not finish booting within ${boot_timeout}s" >&2; exit 1; fi
  sleep 5
done
echo "device ready: $(device_prop ro.product.model), api $(device_prop ro.build.version.sdk)"

adb_cmd shell settings put secure immersive_mode_confirmations confirmed
adb_cmd shell settings put global hide_error_dialogs 1
adb_cmd shell settings put system screen_off_timeout 2147483647
adb_cmd shell svc power stayon true
adb_cmd shell input keyevent KEYCODE_WAKEUP
adb_cmd shell wm dismiss-keyguard >/dev/null 2>&1 || true

adb_cmd install -r "$apk" >/dev/null
adb_cmd shell am force-stop "$package"
adb_cmd shell pm clear "$package" >/dev/null
adb_cmd logcat -G 16M >/dev/null 2>&1 || true
adb_cmd logcat -c || true
adb_cmd shell am start -W -n "$package/$activity" >/dev/null
pid="$(adb_cmd shell pidof -s "$package" 2>/dev/null | tr -d '\r' || true)"
adb_cmd forward "tcp:$host_port" "tcp:$device_port" >/dev/null

collect_logs() {
  if [ -n "$pid" ]; then
    adb_cmd logcat -d -v threadtime --pid="$pid" > "$out/logcat.txt" 2>&1 || true
  else
    adb_cmd logcat -d -v threadtime > "$out/logcat.txt" 2>&1 || true
  fi
  adb_cmd logcat -d -v threadtime -b crash > "$out/logcat-crash.txt" 2>&1 || true
}

if ! node tools/test-agent/wait-for-game.mjs --port="$host_port" --timeout="$start_timeout"; then
  adb_cmd exec-out screencap -p > "$out/device.png" 2>/dev/null || true
  collect_logs
  exit 1
fi
adb_cmd exec-out screencap -p > "$out/device-start.png" 2>/dev/null || true

set +e
node tools/test-agent/run.mjs --port="$host_port" --mode="$mode" --out="$out" ${SCENARIO:+--scenario=$SCENARIO}
status=$?
set -e
collect_logs
exit $status
