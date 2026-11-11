# Future — iOS (dormant)

> **Do not start any task in this file unless the owner explicitly asks for iOS.** The project is
> complete with Android alone (docs/plan/README.md). This draft exists so the architecture keeps
> iOS cheap to add.

## Preconditions

- A macOS machine or CI runner with Xcode.
- Apple developer account (owner).
- Android v1.0 shipped or explicitly deprioritized by the owner.

## Draft tasks

| Id | Task |
|---|---|
| I.01 | Add iOS targets to `prumio.kmp.library` (`iosArm64`, `iosSimulatorArm64`), run all `commonTest` on iOS, fix any platform leaks |
| I.02 | `app/ios` Xcode project hosting `PrumioApp()` via `ComposeUIViewController`; framework export from `:app:shell` |
| I.03 | `NativeSqliteDriver` actual for `databaseDriverModule`; database tests on iOS |
| I.04 | iOS actuals for formatters (`NSDateFormatter`), locale and per-app language |
| I.05 | `AlarmScheduler` + `NotificationPresenter` with `UNUserNotificationCenter`, 64-pending window, background refresh reconciliation |
| I.06 | Backup file gateway with the document picker |
| I.07 | Share extension and App Intents/Shortcuts for capture |
| I.08 | WidgetKit widgets (Today, capture) |
| I.09 | iOS accessibility (VoiceOver) and Dynamic Type pass |
| I.10 | macOS CI job; TestFlight; App Store release |

Each task will be expanded into the standard task format when iOS is requested.
