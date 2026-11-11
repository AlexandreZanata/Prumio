# `:feature:settings`

> Preferences: appearance, reminders and quiet hours, morning summary, weekly review, backup and restore, privacy statement and about.

| | |
|---|---|
| **Layer** | Feature |
| **Plugin** | `prumio.feature` (wires every allowed dependency) |
| **Package** | `app.prumio.feature.settings` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain`, `:core:model`, `:core:navigation`, `:core:ui` |
| **Status** | Placeholder screen. Grows in tasks P3.13 (base), P4.10 (reminder reliability), P5.08–P5.12 (summary, quiet hours, backup). |

## Contract

| | |
|---|---|
| Navigation keys | `SettingsKey`; planned: `ReminderReliabilityKey`, `BackupKey`, `AboutKey` |
| Business rules | BR-SET-*, BR-BACKUP-*, BR-REM-010 … BR-REM-014 (docs/domain/business-rules.md) |
| Screen specification | [docs/product/screens.md](../../docs/product/screens.md#settings) |
| Plan tasks | P3.13, P4.10, P5.08–P5.12 (docs/plan/) |
| Files today | `SettingsRoute.kt`, `SettingsScreen.kt`, `SettingsNavigation.kt`, `SettingsFeatureModule.kt` |

## Public API

Only two symbols are public; everything else is `internal` or `private` (architecture tests enforce it):

- `fun EntryProviderScope<NavKey>.settingsEntries(navigator: Navigator)` — registers this feature's
  screens. Called from `app/shell/.../navigation/PrumioNavDisplay.kt`.
- `val settingsFeatureModule` — Koin definitions (ViewModels). Listed in `app/shell/.../di/PrumioModules.kt`.

## Rules

- Follow the screen pattern of `:feature:today` (the reference implementation):
  `XxxContract.kt` → `XxxViewModel.kt` → `XxxRoute.kt` → `XxxScreen.kt` (docs/architecture/presentation-pattern.md).
- No business rule is decided here: call use cases from `:core:domain`.
- Build UI only from `Prumio*` components (`:core:designsystem`) and shared domain components (`:core:ui`).
  If you need something that does not exist, add it there first (docs/ai/recipes/add-design-system-component.md).
- Strings: reuse `:core:ui` shared strings; feature-specific strings go in this module's
  `composeResources` in **English and Brazilian Portuguese**.

## Tests

`./gradlew :feature:settings:testAndroidHostTest` — ViewModel tests in `src/commonTest` with fakes from `:core:testing`.
