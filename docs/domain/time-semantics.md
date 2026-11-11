# Time semantics

Time bugs are the most common bugs in organizers. This document fixes, once, how Prumio
represents and interprets time. Rules: BR-TIME-001 … BR-TIME-003.

## The two kinds of time

| Kind | Used by | Stored as | Meaning |
|---|---|---|---|
| **Floating** | task due date/time, matter follow-up, all-day appointments, notes' `occurredOn` | `LocalDate` / `LocalTime` (ISO text) | A wall-clock moment interpreted in the device's *current* time zone. "09:00" is 09:00 wherever you are |
| **Zoned** | timed appointments | `LocalDateTime` + IANA `TimeZone` id | A fixed instant. Shown converted to the device's current zone |
| **Instant** | `createdAt`, `updatedAt`, `completedAt`, `resolvedAt`, `lastActivityAt`, alarm triggers | epoch milliseconds UTC | A point on the timeline, for ordering and auditing |

Why floating for tasks: personal tasks follow the person ("take medicine at 08:00").
Why zoned for appointments: a call at 15:00 in São Paulo stays at the same instant if you fly to Lisbon.

## The one source of "now"

- Code gets the current instant and zone **only** from `TimeProvider` (`:core:common`).
- `TimeProvider.today()` is the device's local date now (BR-TIME-003).
- `Clock.System` / `TimeZone.currentSystemDefault()` appear only in `SystemTimeProvider`
  (architecture test enforces it).
- Tests use `FakeTimeProvider` to move time (`advanceBy`, `setLocal`) and travel (`moveTo(zone)`).

## Converting

| Need | How |
|---|---|
| Floating date+time → instant (for alarms) | `LocalDateTime(date, time).toInstant(timeProvider.timeZone())` |
| Zoned appointment → instant | `start.toInstant(appointment.timeZone)` |
| Instant → what to display | `instant.toLocalDateTime(timeProvider.timeZone())` |
| Does an appointment occur on local day D? | convert start/end to the device zone, test overlap with `[D 00:00, D+1 00:00)` (BR-APPT-005) |

## Daylight saving time

- A floating local time that does not exist on a day (spring-forward gap, e.g. 02:30) resolves
  **forward** by the length of the gap (kotlinx-datetime's default `toInstant` behavior).
- An ambiguous local time (fall-back overlap) resolves to the **earlier** instant.
- Recurrence uses the same rules for every occurrence (BR-REC-004).
- Tests for DST use `America/Sao_Paulo` (historical DST), `Europe/Lisbon` and `America/New_York`
  with known transition dates.

## Device time zone or clock changes

- Floating items keep their wall-clock values; their instants change → reminders are re-planned
  (BR-REM-002 trigger "time or time zone change").
- Zoned appointments keep their instants; their displayed local time changes.
- Today recomputes on resume and on date change (BR-TODAY-007).

## Storage formats (SQLite)

| Kotlin type | Column type | Format | Example |
|---|---|---|---|
| `Instant` | `INTEGER` | epoch milliseconds | `1762862400000` |
| `LocalDate` | `TEXT` | ISO-8601 `yyyy-MM-dd` (sorts correctly) | `2026-11-12` |
| `LocalTime` | `TEXT` | `HH:mm` (seconds never stored) | `09:00` |
| `LocalDateTime` | two columns `*_date` + `*_time` | as above | `2026-11-12`, `16:00` |
| `TimeZone` | `TEXT` | IANA id | `America/Sao_Paulo` |

Range queries on zoned appointments fetch one extra day on each side using stored local dates, then
filter precisely in the domain after converting to the device zone.

## Display

Formatting is localized and lives in `:core:ui` (`format/`): day headers, relative dates
("Tomorrow", "In 3 days"), 12/24-hour time following the system setting. Domain code never formats
for display.
