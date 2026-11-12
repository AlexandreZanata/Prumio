#!/usr/bin/env bash
# The single local quality gate (AGENTS.md, rule 10). Run it before every commit.
# It runs exactly what CI runs:
#   - spotlessCheck   formatting + ktlint + Compose rules
#   - check           unit tests of every module, coverage thresholds (Kover), architecture tests (Konsist), Android lint,
#                     SQLDelight migration verification and the privacy gate (no INTERNET permission)
#   - assembleDebug   the app still builds
# Extra Gradle arguments are forwarded, e.g. ./scripts/verify.sh --offline
set -euo pipefail

cd "$(dirname "$0")/.."

./gradlew --console=plain spotlessCheck check :app:android:assembleDebug "$@"

echo "verify: all gates passed"
