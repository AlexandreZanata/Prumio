# Platform strategy

## Today: Android only

- The only compilation target is Android (`kotlin { android { … } }` in `prumio.kmp.library`).
- The project is **complete when the Android app reaches v1.0** (docs/plan/phase-7-release.md),
  with or without any other platform.
- **iOS, desktop and web are added only when the owner explicitly asks for them.** Agents must not
  add targets, `iosMain` source sets, Xcode projects or iOS-only dependencies on their own (AGENTS.md).

## Why Kotlin Multiplatform anyway

Shared modules are KMP modules with a single target so that adding iOS later is additive, not a
rewrite (ADR-0001). The price is small and paid now through a few rules.

## iOS-readiness rules (enforced today)

1. 🔒 `commonMain` never imports `android.*`, `java.*`, `javax.*` or Android-only AndroidX/libraries
   (`LayerDependencyTest`).
2. Use multiplatform libraries only in `commonMain`: kotlinx-coroutines, kotlinx-datetime,
   kotlinx-serialization, SQLDelight, Koin, Compose Multiplatform, JetBrains lifecycle/navigation3.
3. Platform behavior goes behind an interface (domain port) or `expect`/`actual`, with the Android
   implementation in `androidMain`. Current examples: `databaseDriverModule`, `formatDayHeader`.
4. Resources are Compose Multiplatform resources (`composeResources/`), not Android `res/`
   (exception: launcher icon, window theme and system XML in `:app:android`).
5. Time is kotlinx-datetime + `kotlin.time`; ids are `kotlin.uuid`; no `java.time`/`java.util.UUID`.
6. Tests of shared logic live in `commonTest` so they will run on iOS too.

## What adding iOS will involve (for reference only)

| Step | Work |
|---|---|
| Targets | Add `iosArm64()`, `iosSimulatorArm64()` (and `iosX64()` if needed) to `prumio.kmp.library`; framework export in `:app:shell` |
| App | `app/ios` Xcode project calling `PrumioApp()` via `ComposeUIViewController` |
| Database | `NativeSqliteDriver` actual for `databaseDriverModule` |
| Formatting | `formatDayHeader` actual with `NSDateFormatter` |
| Reminders | `AlarmScheduler`/`NotificationPresenter` with `UNUserNotificationCenter`, 64-pending window (reminders-and-notifications.md#ios-only-when-explicitly-requested) |
| Backup | `BackupFileGateway` with `UIDocumentPickerViewController` |
| Widgets, share, shortcuts | WidgetKit, share extension, App Intents (separate tasks) |
| CI | macOS job for iOS tests |

A full iOS plan is drafted in [plan/future-ios.md](../plan/future-ios.md) and stays dormant until requested.
