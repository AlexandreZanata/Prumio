# `:feature:today`

> Your day at a glance: greeting, summary counts, what to do today, next appointment, matters that need attention and the inbox nudge.

| | |
|---|---|
| **Layer** | Feature |
| **Plugin** | `prumio.feature` (wires every allowed dependency) |
| **Package** | `app.prumio.feature.today` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain`, `:core:model`, `:core:navigation`, `:core:ui` |
| **Status** | Reference implementation of the presentation pattern (greeting + date, BR-TODAY-001). Full screen in task P3.07. |

## Contract

| | |
|---|---|
| Navigation keys | `TodayKey` (top-level) |
| Business rules | BR-TODAY-001 … BR-TODAY-007 (docs/domain/business-rules.md) |
| Screen specification | [docs/product/screens.md](../../docs/product/screens.md#today) |
| Plan tasks | P3.07 (docs/plan/) |
| Files today | `TodayContract.kt`, `TodayViewModel.kt`, `TodayRoute.kt`, `TodayScreen.kt`, `TodayNavigation.kt`, `TodayFeatureModule.kt` |

## Public API

Only two symbols are public; everything else is `internal` or `private` (architecture tests enforce it):

- `fun EntryProviderScope<NavKey>.todayEntries(navigator: Navigator)` — registers this feature's
  screens. Called from `app/shell/.../navigation/PrumioNavDisplay.kt`.
- `val todayFeatureModule` — Koin definitions (ViewModels). Listed in `app/shell/.../di/PrumioModules.kt`.

## Rules

- Follow the screen pattern of `:feature:today` (the reference implementation):
  `XxxContract.kt` → `XxxViewModel.kt` → `XxxRoute.kt` → `XxxScreen.kt` (docs/architecture/presentation-pattern.md).
- No business rule is decided here: call use cases from `:core:domain`.
- Build UI only from `Prumio*` components (`:core:designsystem`) and shared domain components (`:core:ui`).
  If you need something that does not exist, add it there first (docs/ai/recipes/add-design-system-component.md).
- Strings: reuse `:core:ui` shared strings; feature-specific strings go in this module's
  `composeResources` in **English and Brazilian Portuguese**.

## Tests

`./gradlew :feature:today:testAndroidHostTest` — ViewModel tests in `src/commonTest` with fakes from `:core:testing`.
