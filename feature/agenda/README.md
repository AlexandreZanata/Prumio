# `:feature:agenda`

> Where you need to be: appointments and dated tasks day by day, plus the appointment editor and detail.

| | |
|---|---|
| **Layer** | Feature |
| **Plugin** | `prumio.feature` (wires every allowed dependency) |
| **Package** | `app.prumio.feature.agenda` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain`, `:core:model`, `:core:navigation`, `:core:ui` |
| **Status** | Implemented in Phase 3. Reactive agenda with week/date browsing, day ordering, timed/all-day appointment editor and detail, deletion with Undo and date rollover. |

## Contract

| | |
|---|---|
| Navigation keys | AgendaKey, AppointmentEditorKey, AppointmentDetailKey |
| Business rules | BR-APPT-*, BR-TIME-* (docs/domain/business-rules.md) |
| Screen specification | [docs/product/screens.md](../../docs/product/screens.md#agenda) |
| Plan tasks | P3.08, P3.09, P5.05 (docs/plan/) |
| Screen files | Each screen uses `XxxContract.kt`, `XxxViewModel.kt`, `XxxRoute.kt`, `XxxScreen.kt`; editor/detail subpackages for multi-screen features |

## Public API

Only two symbols are public; everything else is `internal` or `private` (architecture tests enforce it):

- `fun EntryProviderScope<NavKey>.agendaEntries(navigator: Navigator)` — registers this feature's
  screens. Called from `app/shell/.../navigation/PrumioNavDisplay.kt`.
- `val agendaFeatureModule` — Koin definitions (ViewModels). Listed in `app/shell/.../di/PrumioModules.kt`.

## Rules

- Follow the screen pattern of `:feature:today` (the reference implementation):
  `XxxContract.kt` → `XxxViewModel.kt` → `XxxRoute.kt` → `XxxScreen.kt` (docs/architecture/presentation-pattern.md).
- No business rule is decided here: call use cases from `:core:domain`.
- Build UI only from `Prumio*` components (`:core:designsystem`) and shared domain components (`:core:ui`).
  If you need something that does not exist, add it there first (docs/ai/recipes/add-design-system-component.md).
- Strings: reuse `:core:ui` shared strings; feature-specific strings go in this module's
  `composeResources` in **English and Brazilian Portuguese**.

## Tests

`./gradlew :feature:agenda:testAndroidHostTest` — ViewModel tests in `src/commonTest` with fakes from `:core:testing`.

## Phase 3 validation

ViewModel tests cover state, actions, navigation and error effects. Kover enforces ≥ 80 % lines
for ViewModel classes (including their coroutine/lambda classes). Android host screenshot tests
use `:core:screenshot-testing` for empty, typical and relevant long/error/status states in light,
dark and 200 % font. Sheets, dialogs and the keyboard are checked on the physical phone.

## Phase 4 reminders

Appointment editor uses domain default notice/all-day reminder preferences, supports up to five offsets and contextual permission. Details render stored reminder summaries.
