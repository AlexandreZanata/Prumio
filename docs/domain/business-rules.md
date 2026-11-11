# Business rules

The complete, numbered list of Prumio's business rules. This is the **source of truth** for
behavior: code implements these rules, tests prove them, UI copy explains them.

## How to use this document

- Every rule has a stable id `BR-<AREA>-<NNN>`. Ids are never reused or renumbered; a removed rule
  keeps its heading with `Status: planned` and a "Withdrawn" note, or is replaced by a new id.
- **Status** is `planned` (specified, not yet enforced by code) or `implemented` (enforced by code
  **and** proven by at least one test whose name or comment cites the id).
  `BusinessRuleTraceabilityTest` checks this.
- **Phase** is the plan phase where the rule is implemented (docs/plan/). An implemented rule whose
  screen is still pending has a **Screen:** line naming the task that builds it.
- Code that enforces a rule cites it in KDoc (`Rule: BR-TASK-003`); the test that proves it starts
  its name with the id: ``fun `BR-TASK-003 completing an open task sets completedAt`()``.
- To add or change a rule follow [ai/recipes/add-business-rule.md](../ai/recipes/add-business-rule.md).
- Terms are defined in [glossary.md](glossary.md); fields in [model.md](model.md); transitions in
  [state-machines.md](state-machines.md).

Areas: [GEN](#general) · [TIME](#time) · [TODAY](#today) · [INBOX](#inbox) · [TASK](#tasks) ·
[APPT](#appointments) · [MATTER](#matters) · [REM](#reminders-and-notifications) ·
[REC](#recurrence) · [SEARCH](#search) · [SET](#settings) · [BACKUP](#backup) ·
[REVIEW](#weekly-review) · [PRIV](#privacy)

---

## General

### BR-GEN-001 — A title is the only required field
- **Status:** implemented · **Phase:** 1
- **Rule:** Creating a task, appointment (plus its start), matter or inbox item never requires more
  than a title/text. Every other field is optional and has a defined default.
- **Why:** capturing must take seconds; organization can come later (docs/product/vision.md).

### BR-GEN-002 — Text normalization and limits
- **Status:** implemented · **Phase:** 1
- **Rule:** Titles and texts are trimmed; a value that is blank after trimming is rejected
  (`DomainError.Validation`). Limits: title 500 characters, inbox text 2 000, notes/description
  10 000, location 500, waiting-for 500, matter note 5 000. Optional fields that are blank after
  trimming are stored as null.

### BR-GEN-003 — Stable identifiers
- **Status:** implemented · **Phase:** 1
- **Rule:** Every entity gets a random UUID (`IdGenerator`) when created on the device. Ids never
  change and are never reused, including after deletion and across backup/restore.

### BR-GEN-004 — Timestamps
- **Status:** implemented · **Phase:** 1
- **Rule:** `createdAt` is set once at creation. `updatedAt` changes on every modification of the
  entity's own fields. Both are UTC instants from `TimeProvider`.

### BR-GEN-005 — Deleting is explicit and undoable for a moment
- **Status:** planned · **Phase:** 3
- **Rule:** Deleting an item is permanent and only happens through an explicit Delete action. After
  deleting, an Undo action is offered for 5 seconds; Undo restores the item with the same id and
  fields. Dismissing (tasks) and resolving (matters) are the non-destructive alternatives and are
  offered first in the UI.

### BR-GEN-006 — Nothing changes state by being ignored
- **Status:** planned · **Phase:** 4
- **Rule:** Ignoring or swiping away a notification, letting a date pass or not opening the app
  never completes, dismisses, resolves or deletes anything. Overdue items stay visible until the
  user acts on them.

---

## Time

### BR-TIME-001 — Floating dates and times
- **Status:** implemented · **Phase:** 1
- **Rule:** Task due dates/times, matter follow-ups and all-day appointments are *floating*: they
  are stored as local date (and time) without a time zone and always interpreted in the device's
  current time zone. "Pay rent on the 10th at 09:00" stays at 09:00 wherever you are.

### BR-TIME-002 — Timed appointments keep their time zone
- **Status:** implemented · **Phase:** 1
- **Rule:** A timed appointment stores its local start/end and the IANA time zone in which it was
  created or last edited. It represents a fixed instant and is displayed converted to the device's
  current time zone.

### BR-TIME-003 — "Today" is the device's local date
- **Status:** implemented · **Phase:** 1
- **Rule:** "Today", "tomorrow", "overdue" and day boundaries are computed from `TimeProvider.today()`
  (device time zone) at the moment of evaluation. Details: [time-semantics.md](time-semantics.md).

---

## Today

### BR-TODAY-001 — Greeting by time of day
- **Status:** implemented · **Phase:** 0
- **Rule:** Local time 05:00–11:59 → "Good morning"; 12:00–17:59 → "Good afternoon";
  18:00–04:59 → "Good evening".
- **Enforced by:** `GetDayPeriodUseCase` (`:core:domain`), label `DayPeriod.greeting()` (`:core:ui`).

### BR-TODAY-002 — Summary counts
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.07
- **Rule:** The day summary shows three counts: **Tasks** = open tasks due today or overdue;
  **Appointments** = appointments occurring today (BR-APPT-005), including ones already over;
  **Matters** = unresolved matters that need attention (BR-MATTER-010).

### BR-TODAY-003 — Order of "What to do today"
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.07
- **Rule:** Open tasks due today or overdue, in this order: overdue tasks (oldest due first), then
  today's timed tasks by time, then today's untimed tasks by creation time. Completed-today tasks
  are shown collapsed at the end ("Done today", count).

### BR-TODAY-004 — Next appointment
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.07
- **Rule:** Shows the first appointment occurrence whose end (or start, when it has no end) is
  after now, looking at today first; if none today, the first within the next 7 days with its date;
  if none, the section is hidden. All-day appointments of today are listed above it as a line.

### BR-TODAY-005 — Matters that deserve attention
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.07
- **Rule:** Shows up to 5 matters that need attention (BR-MATTER-010) in that rule's order, each
  with its status and next action. "See all" opens the Matters tab.

### BR-TODAY-006 — Inbox nudge
- **Status:** planned · **Phase:** 3
- **Rule:** When the inbox is not empty, Today shows "N items to organize" linking to the Inbox.
  Hidden when the inbox is empty.

### BR-TODAY-007 — Day rollover
- **Status:** planned · **Phase:** 3
- **Rule:** When the local date changes while the app is open or resumed (midnight, time zone or
  clock change), Today recomputes greeting, date and lists without user action.

---

## Inbox

### BR-INBOX-001 — Capture needs only text
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.01 (dialog behavior)
- **Rule:** Capturing saves a non-blank text (≤ 2 000 characters, BR-GEN-002) to the inbox
  immediately, asking nothing else. Saving closes the capture dialog and confirms with a short message.

### BR-INBOX-002 — Capture is one tap away
- **Status:** planned · **Phase:** 3
- **Rule:** "Add anything" is visible on every top-level screen. From any of them, a thought is saved
  with: tap Add → type → tap Save (or the keyboard's done key). The text field is focused and the
  keyboard open as soon as the dialog appears.

### BR-INBOX-003 — Inbox order
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.02
- **Rule:** Inbox items are listed newest first (by `createdAt`).

### BR-INBOX-004 — Converting an item
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.03 (editor opens pre-filled)
- **Rule:** An inbox item can be converted into a task, an appointment or a matter. The first line
  of the text (truncated to 500 characters) becomes the title; remaining lines, if any, become the
  notes/description. The new entity is created and the inbox item removed in one transaction. The
  editor of the target type opens pre-filled so the user can add details, but saving it is optional:
  canceling the editor after conversion keeps the converted entity with just its title.

### BR-INBOX-005 — Inbox badge
- **Status:** planned · **Phase:** 3
- **Rule:** The Inbox tab shows the number of items as a badge; hidden at 0, "99+" above 99.

### BR-INBOX-006 — Discarding an item
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.02
- **Rule:** Discarding deletes the item (BR-GEN-005, with Undo). Editing changes only its text.

---

## Tasks

### BR-TASK-001 — Task title
- **Status:** implemented · **Phase:** 1
- **Rule:** A task requires a non-blank title (≤ 500). Status starts as `Open`.

### BR-TASK-002 — Time and recurrence require a date
- **Status:** implemented · **Phase:** 1
- **Rule:** `dueTime`, `recurrence` and reminders require `dueDate`. Removing the due date removes
  the time, the recurrence and the reminders in the same change.

### BR-TASK-003 — Task states
- **Status:** implemented · **Phase:** 1
- **Rule:** Allowed transitions: Open → Completed (complete), Open → Dismissed (dismiss),
  Completed → Open and Dismissed → Open (reopen). `completedAt`/`dismissedAt` are set on entering
  the state and cleared on leaving it. Any other transition returns `DomainError.InvalidTransition`.
  See [state-machines.md](state-machines.md#task).

### BR-TASK-004 — Overdue
- **Status:** implemented · **Phase:** 1
- **Rule:** An open task is overdue when its due date is before today, or when its due date is today
  and its due time is before the current local time. Tasks without due date are never overdue.

### BR-TASK-005 — Completing a recurring task
- **Status:** planned · **Phase:** 5
- **Rule:** Completing a task that has a recurrence does not close it: the completion is recorded
  (BR-REC-007) and the due date moves to the next occurrence (BR-REC-005). The task stays `Open`,
  unless the series has ended (BR-REC-006).

### BR-TASK-006 — Default reminder
- **Status:** planned · **Phase:** 4
- **Rule:** When a due time is set on a task that has no reminders, an "at time" reminder (offset 0)
  is added; the user may remove it. Tasks with only a due date get no reminder by default (they
  appear in Today and in the morning summary).

### BR-TASK-007 — Postpone versus snooze
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.05
- **Rule:** *Postpone* changes the due date (Tomorrow, Next week = same weekday + 7 days, or a picked
  date); due time and reminder offsets are kept relative to the new date. *Snooze* (BR-REM-007) only
  delays a notification and never changes the task.

### BR-TASK-008 — Link to a matter
- **Status:** implemented · **Phase:** 1
- **Rule:** A task may belong to at most one matter (`matterId`). Linking or unlinking appends an
  entry to that matter's history (BR-MATTER-003) and updates its `lastActivityAt`.

### BR-TASK-009 — Task lists
- **Status:** implemented · **Phase:** 3
- **Screen:** P3.06
- **Rule:** "All tasks" offers: **Anytime** (open, no due date, newest first), **Upcoming** (open,
  due after today, by due date-time), **Overdue** (by due date-time), **Completed** (completed in the
  last 30 days, most recent first). Dismissed tasks are reachable through a filter.

---

## Appointments

### BR-APPT-001 — Required fields
- **Status:** implemented · **Phase:** 1
- **Rule:** An appointment requires a title and a start date; a start time unless it is all-day.
  Defaults when converting from the inbox or quick-adding: start = today at the next full hour,
  timed, no end time.

### BR-APPT-002 — Valid ranges
- **Status:** implemented · **Phase:** 1
- **Rule:** The end must not be before the start. All-day: `endDate ≥ startDate` (default
  `endDate = startDate`). Timed: end date-time ≥ start date-time; an appointment without end time is
  a point in time.

### BR-APPT-003 — Time zone anchoring
- **Status:** implemented · **Phase:** 1
- **Rule:** Saving a timed appointment stores the device time zone at that moment (BR-TIME-002).
  Editing the time re-anchors it to the current device time zone.

### BR-APPT-004 — Default advance notice
- **Status:** planned · **Phase:** 4
- **Rule:** A new timed appointment gets one reminder with the default advance notice from settings
  (default 30 minutes). A new all-day appointment gets one reminder on its first day at the default
  reminder time (BR-REM-004). The user may change or remove them.

### BR-APPT-005 — When an appointment occurs on a day
- **Status:** implemented · **Phase:** 1
- **Rule:** An appointment (or occurrence) occurs on a local day if any part of it overlaps that
  day in the device time zone. A point-in-time appointment occurs on the day of its start.

### BR-APPT-006 — Past appointments have no state
- **Status:** implemented · **Phase:** 1
- **Rule:** Appointments are never "done" or "missed". Past ones simply leave Today and the upcoming
  agenda and remain visible when browsing past days and in search.

### BR-APPT-007 — Agenda ordering
- **Status:** implemented · **Phase:** 3
- **Screen:** P3.08
- **Rule:** The agenda shows days in chronological order starting today. Within a day: all-day
  appointments first, then timed appointments and timed open tasks merged by start time, then
  untimed open tasks due that day. Overdue tasks are not repeated in the agenda (they live in Today).

---

## Matters

### BR-MATTER-001 — Matter title
- **Status:** implemented · **Phase:** 1
- **Rule:** A matter requires a non-blank title (≤ 500). It starts `Open` with a `Created` history entry.

### BR-MATTER-002 — Matter states
- **Status:** implemented · **Phase:** 1
- **Rule:** States are `Open`, `Waiting`, `Resolved`. Allowed: Open ↔ Waiting, Open → Resolved,
  Waiting → Resolved, Resolved → Open (reopen). See [state-machines.md](state-machines.md#matter).

### BR-MATTER-003 — History is written with every change
- **Status:** implemented · **Phase:** 1
- **Rule:** Each state change, follow-up change, action linked/completed/dismissed/unlinked and note
  appends a `MatterEntry` in the same transaction as the change. History is shown newest first.

### BR-MATTER-004 — Entering Waiting
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.11 (waiting sheet)
- **Rule:** When a matter becomes `Waiting`, the app offers two optional questions: "Waiting for
  whom or what?" (`waitingFor`) and "When should I check again?" (follow-up date, quick choices:
  in 3 days, in 1 week, in 2 weeks, pick a date). Skipping both is allowed. Leaving `Waiting`
  clears `waitingFor`.

### BR-MATTER-005 — Next action
- **Status:** implemented · **Phase:** 1
- **Rule:** Next actions are tasks linked to the matter. The matter's *next action* is its open
  linked task with the earliest due date-time; undated tasks come after dated ones, oldest created
  first. A matter can have several open actions; only the next one is highlighted.

### BR-MATTER-006 — Completing the next action
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.11 (next-action prompt)
- **Rule:** Completing or dismissing a linked task appends an entry. If the matter is `Open` and no
  open linked task remains, the app asks "What's the next action?" (optional; can also choose
  "Mark as waiting" or "Resolve").

### BR-MATTER-007 — Resolving
- **Status:** implemented · **Phase:** 1
- **Rule:** Resolving sets `resolvedAt`, clears the follow-up and `waitingFor`. If open linked tasks
  exist, the user chooses: dismiss them (default) or keep them as standalone tasks (unlinked).
  Reopening clears `resolvedAt` and returns to `Open`.

### BR-MATTER-008 — Notes
- **Status:** implemented · **Phase:** 1
- **Rule:** The user can add notes to the history with a date (default today; past dates allowed,
  future dates rejected). Notes can be edited or deleted; system entries cannot.

### BR-MATTER-009 — Follow-up
- **Status:** planned · **Phase:** 1 (logic), 5 (reminder)
- **Rule:** An `Open` or `Waiting` matter may have a follow-up date with an optional time. On that
  date a follow-up reminder is delivered (BR-REM-004). The follow-up is cleared when resolved and
  can be cleared or moved at any time; each change appends an entry.

### BR-MATTER-010 — Needs attention
- **Status:** implemented · **Phase:** 1
- **Rule:** An unresolved matter needs attention when at least one is true: (a) its follow-up date
  is today or earlier; (b) it is `Open` and has no open linked task; (c) its next action is overdue
  or due today. Order: follow-up date ascending (none last), then `lastActivityAt` ascending.

### BR-MATTER-011 — Stalled
- **Status:** planned · **Phase:** 6
- **Rule:** An unresolved matter is stalled when `lastActivityAt` is at least N days ago
  (setting `stalled_matter_days`, default 14). Used by the weekly review only — never shown as
  a warning elsewhere.

### BR-MATTER-012 — Activity
- **Status:** implemented · **Phase:** 1
- **Rule:** `lastActivityAt` is updated on creation, edits of title/description, every history entry,
  and any change to a linked task's status or link.

### BR-MATTER-013 — Matters list groups
- **Status:** implemented · **Phase:** 3
- **Screen:** P3.10
- **Rule:** The Matters tab groups: **Needs attention** (BR-MATTER-010 order), **Waiting** (by
  follow-up date, none last), **Open** (by `lastActivityAt` descending), **Resolved** (collapsed;
  resolved in the last 90 days, most recent first; older ones via search).

---

## Reminders and notifications

Full design: [reminders-and-notifications.md](reminders-and-notifications.md).

### BR-REM-001 — Notifications are derived from data
- **Status:** planned · **Phase:** 4
- **Rule:** The set of scheduled alarms is always the output of `ReminderPlanner` applied to the
  database. No feature, ViewModel or use case schedules or cancels an alarm directly.

### BR-REM-002 — Reconciliation triggers
- **Status:** planned · **Phase:** 4
- **Rule:** Alarms are reconciled on: app start; any change to reminder-relevant data or settings;
  device boot; app update; time or time zone change; exact-alarm permission change; after each alarm
  fires; and at least once every 24 hours by a background job.

### BR-REM-003 — Planning window
- **Status:** planned · **Phase:** 4
- **Rule:** Alarms are planned for the next 14 days, at most 200 at a time (earliest first).
  Reconciliation keeps the window moving.

### BR-REM-004 — Anchors and trigger times
- **Status:** planned · **Phase:** 4
- **Rule:** Anchor = task due date at due time (or the default reminder time when undated in time);
  appointment occurrence start instant (all-day: first day at the default reminder time); matter
  follow-up date at follow-up time (or the default reminder time). Trigger = anchor − offset.
  Matters have exactly one implicit follow-up reminder at offset 0.

### BR-REM-005 — What produces reminders
- **Status:** planned · **Phase:** 4
- **Rule:** Only open tasks, unresolved matters with a follow-up, and appointment occurrences whose
  trigger is in the future produce alarms. Completing, dismissing, resolving or deleting an item
  removes its alarms at the next reconciliation.

### BR-REM-006 — Ignored notifications change nothing
- **Status:** planned · **Phase:** 4
- **Rule:** See BR-GEN-006. A delivered notification that the user ignores stays in the shade until
  dismissed by the user or replaced; the item is untouched.

### BR-REM-007 — Snooze
- **Status:** planned · **Phase:** 4
- **Rule:** Snooze options: 10 minutes, 1 hour, Tomorrow (at the default reminder time). Snoozing
  reschedules that notification only; a new snooze replaces the previous one; the item is unchanged.
  A snooze is discarded when the item is completed, dismissed, resolved, deleted or its anchor changes.

### BR-REM-008 — Notification actions
- **Status:** planned · **Phase:** 4
- **Rule:** Task reminder: Complete, Snooze, open on tap. Appointment reminder: Snooze, open on tap.
  Matter follow-up: Snooze (Tomorrow only), open on tap. Complete from a notification follows
  BR-TASK-003/BR-TASK-005 exactly as in the app.

### BR-REM-009 — Missed reminders
- **Status:** planned · **Phase:** 4
- **Rule:** A reminder whose trigger passed while the device was off or the alarm could not fire is
  delivered once at the next reconciliation if it is less than 12 hours late; older ones are skipped
  (the item is still visible in Today). A reminder is never delivered twice.

### BR-REM-010 — Quiet hours
- **Status:** planned · **Phase:** 5
- **Rule:** When quiet hours are enabled (default off; 22:00–07:00), *deferrable* notifications
  (BR-REM-011) whose trigger falls inside quiet hours are delivered at the end of quiet hours.
  Time-critical notifications are never delayed.

### BR-REM-011 — Time-critical versus deferrable
- **Status:** planned · **Phase:** 5
- **Rule:** Time-critical: anchored to a time explicitly set on the item (task due time, timed
  appointment start, follow-up time). Deferrable: anchored to the default reminder time, catch-up
  deliveries (BR-REM-009), the morning summary and the weekly review reminder.

### BR-REM-012 — Morning summary
- **Status:** planned · **Phase:** 5
- **Rule:** Optional, default off. At the chosen time (default 08:00) one notification summarizes:
  open tasks due today or overdue, today's appointments (count and first start time), matters that
  need attention. Not sent when all three counts are zero.

### BR-REM-013 — Exact alarms
- **Status:** planned · **Phase:** 4
- **Rule:** With the exact-alarm permission granted, alarms fire at the exact minute; otherwise
  inexact alarms are used and Settings → Reminder reliability explains that reminders may be late by
  several minutes, with a button to grant the permission.

### BR-REM-014 — Asking for notification permission
- **Status:** planned · **Phase:** 4
- **Rule:** The notification permission (Android 13+) is requested only when the user first creates
  a reminder or enables a summary, after a one-sentence explanation. If denied, reminders are still
  planned and a calm, dismissible banner in Settings explains how to enable them. Never ask again
  automatically after two denials.

### BR-REM-015 — One notification per item
- **Status:** planned · **Phase:** 4
- **Rule:** Each item has at most one visible notification; a newer reminder for the same item
  replaces the previous one.

### BR-REM-016 — Channels
- **Status:** planned · **Phase:** 4
- **Rule:** Notification channels: **Reminders** (tasks and appointments, high importance),
  **Follow-ups** (matters, default importance), **Summaries** (morning summary, weekly review, low
  importance). Users control them in system settings.

### BR-REM-017 — Opening a notification
- **Status:** planned · **Phase:** 4
- **Rule:** Tapping a notification opens the item's detail screen (task, appointment or matter) with
  Today underneath; summaries open Today. If the item no longer exists, Today opens with a short message.

---

## Recurrence

Full specification: [recurrence.md](recurrence.md).

### BR-REC-001 — Supported patterns
- **Status:** planned · **Phase:** 5
- **Rule:** Daily, weekly (one or more weekdays), monthly (day of month, or nth/last weekday),
  yearly; every N (interval ≥ 1); ending never, after N occurrences, or on a date.

### BR-REC-002 — Storage format
- **Status:** planned · **Phase:** 5
- **Rule:** Stored as an RFC 5545 RRULE subset (`FREQ`, `INTERVAL`, `BYDAY`, `BYMONTHDAY`,
  `BYSETPOS`, `COUNT`, `UNTIL`). Parsing rejects anything outside the subset.

### BR-REC-003 — Short months
- **Status:** planned · **Phase:** 5
- **Rule:** Monthly on day 29, 30 or 31 falls on the last day of shorter months
  (e.g. "every month on the 31st" → Feb 28/29, Apr 30).

### BR-REC-004 — Time zones and DST
- **Status:** planned · **Phase:** 5
- **Rule:** Occurrences of floating items are computed in local time; of timed appointments in their
  stored zone. A local time that does not exist (DST gap) moves forward by the gap; an ambiguous one
  (DST overlap) uses the earlier instant.

### BR-REC-005 — Next due date of a recurring task
- **Status:** planned · **Phase:** 5
- **Rule:** From due date (default): the next due date is the first occurrence strictly after the
  current due date **and** on or after today — missed occurrences are skipped, never piled up.
  From completion: next due date = completion date + interval of the frequency.

### BR-REC-006 — End of a series
- **Status:** planned · **Phase:** 5
- **Rule:** When no next occurrence exists (COUNT or UNTIL reached), completing the task sets it to
  `Completed` like a normal task.

### BR-REC-007 — Completion log and undo
- **Status:** planned · **Phase:** 5
- **Rule:** Each completion of a recurring task is recorded with the occurrence date and the
  completion instant. Undo restores the previous due date and removes that record.

### BR-REC-008 — Editing recurring appointments
- **Status:** planned · **Phase:** 5
- **Rule:** Editing or deleting an occurrence asks for the scope: *This occurrence* (stored as an
  exception), *This and following* (series split at that occurrence), *All occurrences*.

---

## Search

### BR-SEARCH-001 — Scope
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.12
- **Rule:** Search covers task titles and notes, appointment titles, notes and locations, matter
  titles, descriptions, waiting-for and notes, and inbox texts. Fully offline.

### BR-SEARCH-002 — Matching
- **Status:** implemented · **Phase:** 1
- **Rule:** Case- and accent-insensitive ("documentacao" finds "Documentação"); every word of the
  query must appear (in any order) as a substring of the searchable text.

### BR-SEARCH-003 — Results
- **Status:** implemented · **Phase:** 1 (logic), 3 (screen)
- **Screen:** P3.12
- **Rule:** Results are grouped by type (Tasks, Appointments, Matters, Inbox); open/unresolved before
  closed; most recently updated first within each group.

### BR-SEARCH-004 — Minimum query
- **Status:** implemented · **Phase:** 1
- **Rule:** Queries shorter than 2 characters (after trimming) return no results and show a hint.

---

## Settings

### BR-SET-001 — Everything has a default
- **Status:** implemented · **Phase:** 1
- **Rule:** Every setting has a default (docs/domain/model.md#settings). The app is fully usable
  without opening Settings. Unknown or invalid stored values fall back to the default.

### BR-SET-002 — Settings affect reminders immediately
- **Status:** planned · **Phase:** 4
- **Rule:** Changing a reminder-related setting (default reminder time, advance notice, quiet hours,
  summary, review) triggers reconciliation (BR-REM-002).

### BR-SET-003 — Settings are backed up
- **Status:** planned · **Phase:** 5
- **Rule:** Backups include all settings except device-specific ones (backup folder, permissions state).

---

## Backup

Format: [backup-format.md](backup-format.md).

### BR-BACKUP-001 — Export
- **Status:** planned · **Phase:** 5
- **Rule:** Export writes one JSON file (format version 1) with all user data and settings to a
  location chosen by the user through the system file picker.

### BR-BACKUP-002 — Validate before import
- **Status:** planned · **Phase:** 5
- **Rule:** Import parses and validates the whole file before changing anything. Invalid files and
  files with a newer format version are rejected with a clear message and no data change.

### BR-BACKUP-003 — Import replaces, with a safety net
- **Status:** planned · **Phase:** 5
- **Rule:** Importing replaces all current data after an explicit confirmation that states the
  counts on both sides. Before replacing, a safety backup of the current data is written to app
  storage and kept until the next successful import.

### BR-BACKUP-004 — Automatic backups
- **Status:** planned · **Phase:** 5
- **Rule:** Optional (default off): once a day, export to a folder the user picked, keeping the 7 most
  recent automatic files. Failures are shown in Settings, never as alarming notifications.

### BR-BACKUP-005 — Encrypted backups
- **Status:** planned · **Phase:** 6
- **Rule:** The user may protect a backup with a password (AES-256-GCM with a key derived by
  PBKDF2-HMAC-SHA256, ≥ 310 000 iterations, random salt and nonce). A forgotten password cannot be recovered.

### BR-BACKUP-006 — The user controls the destination
- **Status:** planned · **Phase:** 5
- **Rule:** Prumio never sends a backup anywhere by itself; files go only where the user chooses.

---

## Weekly review

### BR-REVIEW-001 — Content
- **Status:** planned · **Phase:** 6
- **Rule:** The review lists, each as a short section hidden when empty: stalled matters
  (BR-MATTER-011), open matters without next action, waiting matters whose follow-up passed or is
  missing, appointments in the next 7 days, overdue tasks, inbox items older than 3 days.

### BR-REVIEW-002 — No judgement
- **Status:** planned · **Phase:** 6
- **Rule:** The review offers actions (set next action, follow up, resolve, reschedule, organize) and
  a "Done for this week" button that stores `lastReviewedAt`. No scores, streaks, percentages or
  guilt-inducing copy anywhere in the app.

### BR-REVIEW-003 — Optional weekly reminder
- **Status:** planned · **Phase:** 6
- **Rule:** Off by default. When enabled, one reminder at the chosen weekday and time (default
  Sunday 18:00) on the Summaries channel.

---

## Privacy

### BR-PRIV-001 — No network access
- **Status:** implemented · **Phase:** 0
- **Rule:** The app never requests the `INTERNET` permission. The manifest removes it even if a
  library adds it, and `check` fails if the merged manifest of any variant contains it.
- **Enforced by:** `app/android/src/main/AndroidManifest.xml`, `VerifyNoNetworkPermissionTask`
  (build-logic), `PrivacyTest` (tooling).

### BR-PRIV-002 — No tracking or account SDKs
- **Status:** implemented · **Phase:** 0
- **Rule:** No analytics, telemetry, advertising, remote crash reporting or account/authentication
  SDKs may be added (Firebase, Crashlytics, Sentry, Play Services auth, ad SDKs and similar).
- **Enforced by:** `PrivacyTest` (denylist over gradle/libs.versions.toml and build files).

### BR-PRIV-003 — Data stays on the device
- **Status:** planned · **Phase:** 5
- **Rule:** User data lives only in app-private storage. It leaves the device only through an export
  or backup the user starts (BR-BACKUP-006) or the Android system backup the user enabled (ADR-0016).
