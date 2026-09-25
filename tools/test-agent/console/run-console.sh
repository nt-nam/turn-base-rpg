#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "$0")/../../.." && pwd)"
api_port="${PXWORLD_API_PORT:-18080}"
web_port="${PXWORLD_CONSOLE_PORT:-4173}"
cd "$root"

./gradlew :server:app:installDist --console=plain -q
(cd web && if [ -n "${CI:-}" ] || [ ! -d node_modules ]; then npm ci --no-audit --no-fund; fi && npm run console:build --silent)

content_copy="$(mktemp -d)"
cp -r content/. "$content_copy"

PXWORLD_DB_URL="jdbc:h2:mem:console-agent;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1" \
PXWORLD_PORT="$api_port" PXWORLD_CONTENT_DIR="$content_copy" PXWORLD_CONTENT_WRITABLE=true \
  server/app/build/install/app/bin/app > "${TMPDIR:-/tmp}/pxworld-console-server.log" 2>&1 &
server_pid=$!
PXWORLD_API="http://localhost:$api_port" node web/node_modules/vite/bin/vite.js preview web/apps/console --port "$web_port" --strictPort > "${TMPDIR:-/tmp}/pxworld-console-web.log" 2>&1 &
web_pid=$!
trap 'kill $server_pid $web_pid 2>/dev/null || true; rm -rf "$content_copy"' EXIT

for _ in $(seq 1 60); do curl -sf "http://localhost:$api_port/health" >/dev/null && curl -sf "http://localhost:$web_port" >/dev/null && break; sleep 1; done

node tools/test-agent/console/console-agent.mjs --api="http://localhost:$api_port" --url="http://localhost:$web_port" "$@"
