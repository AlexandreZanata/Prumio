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
- `MainActivity`: enables edge-to-edge and calls `PrumioApp()`. Later (phase 4) it also turns
  notification deep links into navigation keys.
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
