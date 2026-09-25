#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../.." && pwd)"
out="${1:-$root/tools/test-agent/reports/$(date +%Y%m%d-%H%M%S)}"
mode="${MODE:-scenario}"
mkdir -p "$out"
cd "$root"
with_server="${WITH_SERVER:-}"
[ "${SCENARIO:-}" = "cloud" ] && with_server=1
tasks=(:game:platform-desktop:installDist)
[ -n "$with_server" ] && tasks+=(:server:app:installDist)
./gradlew --settings-file settings-test.gradle "${tasks[@]}" -q

server=""
export PXWORLD_API_URL=off
if [ -n "$with_server" ]; then
  api_port="${PXWORLD_API_PORT:-18080}"
  PXWORLD_DB_URL="jdbc:h2:mem:desktop-agent;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1" \
  PXWORLD_PORT="$api_port" PXWORLD_CONTENT_WRITABLE=false \
    "$root/server/app/build/install/app/bin/app" > "$out/server.log" 2>&1 &
  server=$!
  export PXWORLD_API_URL="http://localhost:$api_port" PXWORLD_TEST_API="http://localhost:$api_port"
  for _ in $(seq 1 60); do curl -sf "$PXWORLD_TEST_API/health" >/dev/null && break; sleep 1; done
fi

launcher="$root/game/platform-desktop/build/install/platform-desktop/bin/platform-desktop"
[ -f "$launcher.bat" ] && [ "${OS:-}" = "Windows_NT" ] && launcher="$launcher.bat"
export PXWORLD_SAVE_DIR="$out/saves" PXWORLD_FLAVOR="${PXWORLD_FLAVOR:-DEV}" PXWORLD_ENV=qa
"$launcher" > "$out/game.log" 2>&1 &
game=$!
set +e
node "$root/tools/test-agent/run.mjs" --mode="$mode" --out="$out" ${SCENARIO:+--scenario=$SCENARIO}
status=$?
wait "$game" 2>/dev/null
[ -n "$server" ] && kill "$server" 2>/dev/null
exit $status
