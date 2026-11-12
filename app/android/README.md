# `:app:android`

> The Android application: the APK entry point. Intentionally thin.

| | |
|---|---|
| **Layer** | Platform entry point |
| **Plugin** | `prumio.android.application` |
| **Package** | `app.prumio.app.android` (application id `app.prumio`, debug `app.prumio.debug`) |
| **May depend on** | `:app:shell`, `:core:*` |

## Responsibility

- `PrumioApplication`: starts Koin with `prumioModules` from `:app:shell`.
- `MainActivity`: enables edge-to-edge and calls `PrumioApp()` and forwards
  initial/new notification deep links to the shell for domain validation and navigation.
- `AndroidManifest.xml`: removes `INTERNET`, declares backup rules, per-app languages, launcher icon.
- Platform resources only: launcher icon, window theme, backup and locale XML.

## Rules

- No screens, ViewModels or business logic here. UI belongs to features; wiring belongs to `:app:shell`.
- Never add the `INTERNET` permission. `check` runs `verifyDebugNoNetworkPermission` and
  `verifyReleaseNoNetworkPermission`, which fail the build if any dependency adds it (PRIVACY.md).
- Manifest components owned by a core module (receivers, workers) are declared in that module's
  manifest and merged automatically.
- `applicationId` is permanent once published (ADR-0015).

## Commands

```bash
./gradlew :app:android:assembleDebug
./gradlew :app:android:installDebug
```

## Phase 4 reminders

Application starts ReminderRuntime after Koin. MainActivity forwards initial/new intents and requests forced reconciliation on resume. Version 0.1.0; debug remains app.prumio.debug. Release signing is optional local configuration; without a key assembleRelease produces an unsigned APK (see docs/architecture/release-signing.md). The custom Android instrumentation tests a real alarm/notification/Complete without clearing data.

Run `./scripts/verify-reminders-device.sh <physical-adb-serial>` with the debug app's notification
permission enabled and the phone unlocked. The test waits at most 60 seconds for delivery, asserts
Complete closes the task and removes its notification, and removes only its own test task in finally.
Test exact access allowed and denied separately using the system reliability screen. Restore the
original permission/app-op state afterward. No emulator is selected and no release APK is installed.
