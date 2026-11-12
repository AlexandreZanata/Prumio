# `:feature:capture`

> "Add anything": the quick capture dialog reachable from every top-level screen. Saves text to the inbox in seconds.

| | |
|---|---|
| **Layer** | Feature |
| **Plugin** | `prumio.feature` (wires every allowed dependency) |
| **Package** | `app.prumio.feature.capture` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain`, `:core:model`, `:core:navigation`, `:core:ui` |
| **Status** | Implemented in Phase 3. Quick capture with a focused multiline field, Save/IME Done, validation and confirmation. |

## Contract

| | |
|---|---|
| Navigation keys | CaptureKey |
| Business rules | BR-INBOX-001, BR-INBOX-002 (docs/domain/business-rules.md) |
| Screen specification | [docs/product/screens.md](../../docs/product/screens.md#quick-capture) |
| Plan tasks | P3.01, P6.02–P6.05 (docs/plan/) |
| Screen files | Each screen uses `XxxContract.kt`, `XxxViewModel.kt`, `XxxRoute.kt`, `XxxScreen.kt`; editor/detail subpackages for multi-screen features |

## Public API

Only two symbols are public; everything else is `internal` or `private` (architecture tests enforce it):

- `fun EntryProviderScope<NavKey>.captureEntries(navigator: Navigator)` — registers this feature's
  screens. Called from `app/shell/.../navigation/PrumioNavDisplay.kt`.
- `val captureFeatureModule` — Koin definitions (ViewModels). Listed in `app/shell/.../di/PrumioModules.kt`.

## Rules

- Follow the screen pattern of `:feature:today` (the reference implementation):
  `XxxContract.kt` → `XxxViewModel.kt` → `XxxRoute.kt` → `XxxScreen.kt` (docs/architecture/presentation-pattern.md).
- No business rule is decided here: call use cases from `:core:domain`.
- Build UI only from `Prumio*` components (`:core:designsystem`) and shared domain components (`:core:ui`).
  If you need something that does not exist, add it there first (docs/ai/recipes/add-design-system-component.md).
- Strings: reuse `:core:ui` shared strings; feature-specific strings go in this module's
  `composeResources` in **English and Brazilian Portuguese**.

## Tests

`./gradlew :feature:capture:testAndroidHostTest` — ViewModel tests in `src/commonTest` with fakes from `:core:testing`.

## Phase 3 validation

ViewModel tests cover state, actions, navigation and error effects. Kover enforces ≥ 80 % lines
for ViewModel classes (including their coroutine/lambda classes). Android host screenshot tests
use `:core:screenshot-testing` for empty, typical and relevant long/error/status states in light,
dark and 200 % font. Sheets, dialogs and the keyboard are checked on the physical phone.
