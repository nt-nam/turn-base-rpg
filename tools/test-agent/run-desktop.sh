#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
out="${1:-$root/tools/test-agent/reports/$(date +%Y%m%d-%H%M%S)}"
mode="${MODE:-scenario}"
mkdir -p "$out"
cd "$root"
./gradlew --settings-file settings-test.gradle :game:platform-desktop:installDist -q
launcher="$root/game/platform-desktop/build/install/platform-desktop/bin/platform-desktop"
[ -f "$launcher.bat" ] && [ "${OS:-}" = "Windows_NT" ] && launcher="$launcher.bat"
export PXWORLD_SAVE_DIR="$out/saves" PXWORLD_FLAVOR="${PXWORLD_FLAVOR:-DEV}" PXWORLD_ENV=qa
"$launcher" > "$out/game.log" 2>&1 &
game=$!
set +e
node "$root/tools/test-agent/run.mjs" --mode="$mode" --out="$out" ${SCENARIO:+--scenario=$SCENARIO}
status=$?
wait "$game" 2>/dev/null
exit $status
