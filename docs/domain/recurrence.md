# Recurrence

Specification of repeating tasks and appointments. Implemented in phase 5 as a pure engine in
`:core:domain/recurrence/`. Rules: BR-REC-001 … BR-REC-008, BR-TASK-005.

## Supported patterns (BR-REC-001)

| Pattern | UI example | RRULE |
|---|---|---|
| Daily | Every day / every 3 days | `FREQ=DAILY;INTERVAL=3` |
| Weekly on weekdays | Every Mon and Thu | `FREQ=WEEKLY;BYDAY=MO,TH` |
| Every N weeks | Every 2 weeks on Fri | `FREQ=WEEKLY;INTERVAL=2;BYDAY=FR` |
| Monthly by day | Every month on the 10th | `FREQ=MONTHLY;BYMONTHDAY=10` |
| Monthly by weekday | Every 2nd Tuesday | `FREQ=MONTHLY;BYDAY=TU;BYSETPOS=2` |
| Monthly last weekday | Last Friday of the month | `FREQ=MONTHLY;BYDAY=FR;BYSETPOS=-1` |
| Yearly | Every year on Nov 11 | `FREQ=YEARLY` (date from the anchor) |
| End after N | … 10 times | `;COUNT=10` |
| End on date | … until Dec 31, 2026 | `;UNTIL=20261231` |

The UI offers presets (Daily, Weekdays, Weekly, Monthly, Yearly) and a "Custom" editor.

## Storage (BR-REC-002)

- Stored as the RRULE string above in a `recurrence` `TEXT` column, plus `recurrence_basis`
  (`FROM_DUE_DATE` | `FROM_COMPLETION`) for tasks.
- `RecurrenceRuleCodec` parses and formats; `parse(format(rule)) == rule` for every valid rule
  (property test). Anything outside the subset (BYHOUR, BYWEEKNO, RDATE, EXDATE…) is rejected.
- `UNTIL` is a date (floating); it is inclusive.

## Occurrence algorithm

```kotlin
/** Occurrences of [rule] anchored at [first], within [from]..[to] (inclusive), at most [limit]. */
fun occurrences(rule: RecurrenceRule, first: LocalDate, from: LocalDate, to: LocalDate, limit: Int): List<LocalDate>
```

1. Generate candidate periods from `first`: days (DAILY), ISO weeks starting at the user's week start
   (WEEKLY), months (MONTHLY), years (YEARLY), stepping by `interval`.
2. Within each period, expand to dates:
   - WEEKLY: each weekday in `weekdays` (or the anchor's weekday when empty).
   - MONTHLY/day: that day, **clamped to the month's last day** (BR-REC-003).
   - MONTHLY/nth weekday: the nth (or last, `-1`) given weekday of the month; ordinals 1–4 only.
   - YEARLY: the anchor's month and day; Feb 29 anchors fall on Feb 28 in non-leap years.
3. Discard dates before `first`. Apply `COUNT` (counting from `first`, including it) and `UNTIL`.
4. Return the dates inside `[from, to]`, sorted, up to `limit`.

Times: the occurrence time is the item's time of day (task `dueTime`, appointment start time),
combined per BR-REC-004 (floating → device zone; timed appointment → its zone; DST gap forward,
overlap earlier).

## Recurring tasks (BR-TASK-005, BR-REC-005 … BR-REC-007)

Tasks are **one row**, not a series of rows. Completing advances the due date:

```
complete(task, now):
  record TaskCompletion(task.id, occurrenceDate = task.dueDate, completedAt = now)
  next = when (basis) {
      FromDueDate    -> first occurrence > task.dueDate AND >= today   // skip missed ones
      FromCompletion -> today + interval × frequency unit               // "every 3 months after I do it"
  }
  if (next == null) status = Completed            // series ended (BR-REC-006)
  else dueDate = next; dueTime unchanged; reminders keep their offsets; snoozes cleared
undo(task): restore dueDate = completion.occurrenceDate, delete the record, status = Open
```

Examples (today = Wed 2026-11-11):

| Rule | Due date | Basis | Next due date |
|---|---|---|---|
| Daily | 2026-11-11 | FromDueDate | 2026-11-12 |
| Daily | 2026-11-06 (5 days late) | FromDueDate | 2026-11-11 (today; missed ones skipped) |
| Weekly Mon | 2026-11-09 | FromDueDate | 2026-11-16 |
| Monthly 31st | 2026-10-31 | FromDueDate | 2026-11-30 (clamped) |
| Every 3 months | 2026-08-01 | FromCompletion | 2027-02-11 |
| Daily, COUNT=3, 3rd done | 2026-11-11 | FromDueDate | none → Completed |

## Recurring appointments (BR-REC-008)

Appointments are a **series** expanded on the fly for the visible range:

- Occurrence identity = the occurrence's original start (`originalStart`).
- Exceptions (`AppointmentException`): `Cancelled` hides an occurrence; `Modified` overrides fields
  of one occurrence.
- Edit scopes:
  - *This occurrence* → upsert a `Modified` exception (delete → `Cancelled` exception).
  - *This and following* → end the original series with `UNTIL` = day before the occurrence, and
    create a new appointment from the edited occurrence with the remaining rule (COUNT reduced accordingly).
  - *All occurrences* → edit the series; exceptions whose `originalStart` no longer exists are removed.
- Reminders apply to every occurrence (offsets relative to each occurrence's start).

## Test requirements

- Table-driven tests for every row of the tables above.
- Property tests: occurrences are strictly increasing; respect COUNT/UNTIL; codec round-trip.
- DST tests in at least three zones (time-semantics.md).
- Performance: expanding 5 years of a daily rule takes < 50 ms on the JVM.
