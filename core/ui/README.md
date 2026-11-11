# `:core:ui`

> Shared presentation layer: domain-aware composables (how a Task or a Matter looks everywhere),
> labels and formatters for model types, shared strings and the `PrumioViewModel` base class.

| | |
|---|---|
| **Layer** | Presentation (shared) |
| **Plugin** | `prumio.kmp.compose` (public `Res`) |
| **Package** | `app.prumio.core.ui` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:model` |
| **Used by** | `:feature:*`, `:app:shell` |

## Responsibility

- `mvi/`: `PrumioViewModel` and `CollectEffects` (docs/architecture/presentation-pattern.md).
- `label/`: localized text for model enums and values (`DayPeriod.greeting()`, later `MatterStatus.label()`).
- `format/`: localized date/time formatting (`dayHeaderText()`), expect/actual per platform.
- `component/` (phase 2): `TaskRow`, `AppointmentRow`, `MatterCard`, `MatterStatusChip`,
  `InboxItemRow`, `DueLabel`… — every way a domain object is drawn, defined once.
- `composeResources/`: shared strings (actions such as Save/Cancel, destination names, labels).
  `Res` is public so features reuse these strings instead of redefining them.

## Rules

- A composable that renders a domain type and is used by two or more features lives here.
- Components receive models or UI models and callbacks; they never load data or hold ViewModels.
- Same component rules as the design system: `modifier` parameter, one per file, listed in
  [docs/design-system/components.md](../../docs/design-system/components.md).
- Never import Material directly: build on `:core:designsystem`.

## Tests

`./gradlew :core:ui:testAndroidHostTest`; screenshot tests from phase 2.
