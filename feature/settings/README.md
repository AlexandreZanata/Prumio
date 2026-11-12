# `:feature:settings`

> Preferences: appearance, reminders and quiet hours, morning summary, weekly review, backup and restore, privacy statement and about.

| | |
|---|---|
| **Layer** | Feature |
| **Plugin** | `prumio.feature` (wires every allowed dependency) |
| **Package** | `app.prumio.feature.settings` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain`, `:core:model`, `:core:navigation`, `:core:ui` |
| **Status** | Implemented in Phase 3. Appearance preferences applied app-wide, Android 13+ per-app language settings, version, source, licenses and privacy statement. Reminder/backup settings arrive in later phases. |

## Contract

| | |
|---|---|
| Navigation keys | SettingsKey |
| Business rules | BR-SET-*, BR-BACKUP-*, BR-REM-010 … BR-REM-014 (docs/domain/business-rules.md) |
| Screen specification | [docs/product/screens.md](../../docs/product/screens.md#settings) |
| Plan tasks | P3.13, P4.10, P5.08–P5.12 (docs/plan/) |
| Screen files | Each screen uses `XxxContract.kt`, `XxxViewModel.kt`, `XxxRoute.kt`, `XxxScreen.kt`; editor/detail subpackages for multi-screen features |

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

## Phase 3 validation

ViewModel tests cover state, actions, navigation and error effects. Kover enforces ≥ 80 % lines
for ViewModel classes (including their coroutine/lambda classes). Android host screenshot tests
use `:core:screenshot-testing` for empty, typical and relevant long/error/status states in light,
dark and 200 % font. Sheets, dialogs and the keyboard are checked on the physical phone.

`SettingsPlatform` keeps package/version and language intents in `androidMain`; `commonMain`
receives only platform-neutral values and callbacks. About is a section of Settings.

## Phase 4 reminders

Default reminder time/appointment notice controls, dismissible denied-notification banner, Reminder reliability system-status/fix screen and notification Snooze dialog. Platform links stay in androidMain; policies stay in domain.
