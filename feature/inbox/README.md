# `:feature:inbox`

> Thoughts captured but not yet organized: the inbox list and the processing flow that turns an item into a task, appointment or matter.

| | |
|---|---|
| **Layer** | Feature |
| **Plugin** | `prumio.feature` (wires every allowed dependency) |
| **Package** | `app.prumio.feature.inbox` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain`, `:core:model`, `:core:navigation`, `:core:ui` |
| **Status** | Placeholder screen. Implemented in tasks P3.02–P3.03. |

## Contract

| | |
|---|---|
| Navigation keys | `InboxKey` (top-level) |
| Business rules | BR-INBOX-003 … BR-INBOX-006 (docs/domain/business-rules.md) |
| Screen specification | [docs/product/screens.md](../../docs/product/screens.md#inbox) |
| Plan tasks | P3.02, P3.03 (docs/plan/) |
| Files today | `InboxRoute.kt`, `InboxScreen.kt`, `InboxNavigation.kt`, `InboxFeatureModule.kt` |

## Public API

Only two symbols are public; everything else is `internal` or `private` (architecture tests enforce it):

- `fun EntryProviderScope<NavKey>.inboxEntries(navigator: Navigator)` — registers this feature's
  screens. Called from `app/shell/.../navigation/PrumioNavDisplay.kt`.
- `val inboxFeatureModule` — Koin definitions (ViewModels). Listed in `app/shell/.../di/PrumioModules.kt`.

## Rules

- Follow the screen pattern of `:feature:today` (the reference implementation):
  `XxxContract.kt` → `XxxViewModel.kt` → `XxxRoute.kt` → `XxxScreen.kt` (docs/architecture/presentation-pattern.md).
- No business rule is decided here: call use cases from `:core:domain`.
- Build UI only from `Prumio*` components (`:core:designsystem`) and shared domain components (`:core:ui`).
  If you need something that does not exist, add it there first (docs/ai/recipes/add-design-system-component.md).
- Strings: reuse `:core:ui` shared strings; feature-specific strings go in this module's
  `composeResources` in **English and Brazilian Portuguese**.

## Tests

`./gradlew :feature:inbox:testAndroidHostTest` — ViewModel tests in `src/commonTest` with fakes from `:core:testing`.
