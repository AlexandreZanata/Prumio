# ADR-0020: Screenshot tests with Roborazzi and Robolectric

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner (approved as task P2.01 of the execution plan)

## Context

Agents change UI code they cannot see. A visual regression net must catch unintended changes to
any component in light, dark and 200 % font, run on every `./scripts/verify.sh` and in CI, and work
without an emulator or a device (manual checks use the physical phone, not CI).

## Decision

- **Robolectric 4.17** (native graphics) renders Compose on the JVM inside `androidHostTest`.
- **Roborazzi 1.78.0** (library only, no Gradle plugin) captures and compares images; recording is
  switched on by the Gradle property `-PrecordScreenshots` (`KmpComposeConventionPlugin`).
- **androidx.compose.ui:ui-test** drives the composition.
- All three live only in the test-only module `:core:screenshot-testing`, which exposes one base
  class, `ScreenshotTest`, and is allowed only in `androidHostTest` (`ModuleRules.kt`). Nothing
  reaches the APK; no network, no tracking.
- Baselines are committed next to the tests (`src/androidHostTest/screenshots/`); a change above
  0.5 % of pixels fails.

## Consequences

- Visual changes become reviewable PNG diffs in pull requests.
- Host tests of UI modules are slower (~30 s per module) and need extra JVM flags for Robolectric.
- Upgrading Robolectric, Compose or the fonts can shift pixels: re-record in a dedicated commit.
- Dialogs and bottom sheets (separate windows) are not captured; they are checked on the phone.

## Alternatives considered

- **Paparazzi** — its layoutlib renderer is tied to specific AGP/compileSdk versions and its Gradle
  plugin targets `com.android.library`, not the KMP library plugin used here.
- **Compose Preview Screenshot Testing (Google)** — experimental, tied to the `com.android.library`
  plugin, not the KMP one.
- **Instrumented screenshots on a device** — slow, needs hardware in CI.
