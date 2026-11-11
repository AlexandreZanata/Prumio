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

## Public API (today)

| Symbol | File | Notes |
|---|---|---|
| `InboxItemId`, `TaskId`, `AppointmentId`, `MatterId`, `MatterEntryId`, `ReminderId` | `Ids.kt` | Typed ids (value classes over UUID strings) |
| `DayPeriod` | `DayPeriod.kt` | Morning / Afternoon / Evening (BR-TODAY-001) |

Phase 1 adds `InboxItem`, `Task`, `Appointment`, `Matter`, `MatterEntry`, `Reminder`,
`RecurrenceRule`, `Settings` and their enums (task P1.02).

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
