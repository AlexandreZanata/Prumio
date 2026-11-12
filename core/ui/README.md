# `:core:ui`

> Shared presentation layer: domain-aware composables (how a Task or a Matter looks everywhere),
> labels and formatters for model types, shared strings and the `PrumioViewModel` base class.

| | |
|---|---|
| **Layer** | Presentation (shared) |
| **Plugin** | `prumio.kmp.compose` (public `Res`) |
| **Package** | `app.prumio.core.ui` |
| **May depend on** | `:core:common`, `:core:designsystem`, `:core:domain` (types only, ADR-0019), `:core:model` |
| **Used by** | `:feature:*`, `:app:shell` |

## Responsibility

- `mvi/`: `PrumioViewModel`, `CollectEffects` and the platform-neutral `OnTimeChange` callback (docs/architecture/presentation-pattern.md).
- `label/`: localized text for model enums and values (`DayPeriod.greeting()`, `MatterStatus.label()`,
  `reminderOffsetText()`).
- `format/`: localized date/time formatting (`relativeDateText()`, `timeText()`, `ageText()`…),
  expect/actual per platform, including compact weekday and month/year headers.
- `component/`: `TaskRow`, `AppointmentRow`, `MatterCard`, `MatterStatusChip`, `InboxItemRow`,
  `DueLabel`, `ReminderSummary`, `TimelineEntryRow`, `SummaryTile`, `DateField`, `TimeField` — every way a domain object is
  drawn, defined once.
- `message/`: `UiMessage`, `rememberMessageSender()`, `DomainError.toUiMessage()` (snackbars and Undo,
  docs/architecture/presentation-pattern.md#messages-and-undo). Undo callbacks suspend in the app scope and survive the originating screen.
- `composeResources/`: shared strings (actions such as Save/Cancel, destination names, labels).
  `Res` is public so features reuse these strings instead of redefining them.

## Rules

- A composable that renders a domain type and is used by two or more features lives here.
- Components receive models or UI models and callbacks; they never load data or hold ViewModels.
- Same component rules as the design system: `modifier` parameter, one per file, listed in
  [docs/design-system/components.md](../../docs/design-system/components.md).
- Never import Material directly: build on `:core:designsystem`.

## Tests

`./gradlew :core:ui:testAndroidHostTest` — formatter and message unit tests, `DomainComponentScreenshotTest`
(light/dark/200 % font). Re-record with `-PrecordScreenshots` and review the PNG diff.

## Phase 4 reminders

ReminderField and ReminderPermissionDialog are shared by editors. rememberNotificationPermissionRequest is an explicit expect/actual launcher used by Routes. ReminderSummary displays persisted offsets; UI never schedules alarms.
