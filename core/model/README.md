# `:core:model`

> The nouns of Prumio: entities, value objects, typed ids and enums. Plain immutable data.

| | |
|---|---|
| **Layer** | Domain model |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.model` |
| **May depend on** | `:core:common` |
| **Used by** | `:core:domain`, `:core:data`, `:core:ui`, `:core:testing`, `:feature:*` |

## Responsibility

Hold the types defined in [docs/domain/model.md](../../docs/domain/model.md), field by field.
The model is the shared vocabulary between domain, data and UI.

## Public API

| Symbol | File | Notes |
|---|---|---|
| `InboxItemId`, `TaskId`, `AppointmentId`, `MatterId`, `MatterEntryId`, `ReminderId` | `Ids.kt` | Typed ids (value classes over UUID strings) |
| `InboxItem` | `InboxItem.kt` | |
| `Task`, `TaskStatus`, `TaskRecurrence`, `RecurrenceBasis` | `Task.kt` | Derived: `isOpen`, `hasDueTime`, `isRecurring` |
| `RecurrenceRule`, `Frequency`, `MonthlyPattern`, `RecurrenceEnd` | `RecurrenceRule.kt` | Data only; engine in phase 5 |
| `Appointment`, `AppointmentTiming` (`Timed`, `AllDay`) | `Appointment.kt` | Derived: `isAllDay` |
| `Matter`, `MatterStatus`, `MatterEntry`, `MatterEntryKind` | `Matter.kt` | Derived: `isResolved`, `isEditable` |
| `Settings`, `ThemePreference` | `Settings.kt` | Defaults = BR-SET-001 |
| `DayPeriod` | `DayPeriod.kt` | Morning / Afternoon / Evening (BR-TODAY-001) |

`Reminder` and `ReminderOwner` are defined in `Reminder.kt` / `ReminderOwner.kt` (phase 4).

## Rules

- Only `data class`, `value class`, `enum class`, `sealed interface` and `object`. All properties are `val`.
- No behavior beyond trivial derived properties of a single entity (e.g. `Task.hasDueTime`).
  Rules that need the clock, settings or other entities belong to `:core:domain`.
- No serialization or database annotations: mapping is done in `:core:data`.
- Names follow [docs/domain/glossary.md](../../docs/domain/glossary.md) exactly.

## How to extend

Add a field → update docs/domain/model.md first, then the type here, then the schema
(docs/ai/recipes/add-database-table.md) and the mappers in `:core:data`.

## Tests

`./gradlew :core:model:testAndroidHostTest`

## Phase 4 reminders

`Reminder` and typed `ReminderOwner` are part of Task/Appointment aggregates. Settings adds 09:00 date-only time, 30-minute appointment notice, device permission denial count and banner dismissal.
