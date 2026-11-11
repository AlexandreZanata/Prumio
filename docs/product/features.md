# Feature catalog

Every user-facing capability with a stable id (`F-<AREA>-<NN>`), its release and the plan phase
that builds it. Business behavior is specified in [business-rules.md](../domain/business-rules.md);
screens in [screens.md](screens.md).

Releases: **v0.1** organize essentials · **v0.2** automation and peace of mind · **v0.3–v0.9**
Android completeness · **v1.0** Android release (project complete).

## Capture and inbox

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-CAP-01 | "Add anything" quick capture dialog from every top-level screen | v0.1 | 3 | BR-INBOX-001, 002 |
| F-CAP-02 | Inbox list with count badge, edit, discard with undo | v0.1 | 3 | BR-INBOX-003, 005, 006 |
| F-CAP-03 | Organize an inbox item into task / appointment / matter | v0.1 | 3 | BR-INBOX-004 |
| F-CAP-04 | Share text from any app into the inbox | v0.3 | 6 | BR-INBOX-001 |
| F-CAP-05 | Home screen quick-capture widget | v0.3 | 6 | BR-INBOX-001 |
| F-CAP-06 | Launcher shortcuts and Quick Settings tile for capture | v0.3 | 6 | BR-INBOX-002 |
| F-CAP-07 | Voice capture with the system speech recognizer | v0.4 | 6 | BR-INBOX-001 |

## Tasks

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-TASK-01 | Create/edit tasks: title, notes, due date, due time | v0.1 | 3 | BR-TASK-001, 002 |
| F-TASK-02 | Complete, dismiss, reopen, delete with undo | v0.1 | 3 | BR-TASK-003, BR-GEN-005 |
| F-TASK-03 | Postpone (tomorrow, next week, pick a date) | v0.1 | 3 | BR-TASK-007 |
| F-TASK-04 | All tasks: anytime, upcoming, overdue, completed | v0.1 | 3 | BR-TASK-009 |
| F-TASK-05 | Task reminders (at time + advance notices) | v0.1 | 4 | BR-TASK-006, BR-REM-* |
| F-TASK-06 | Recurring tasks (from due date or from completion) | v0.2 | 5 | BR-REC-*, BR-TASK-005 |

## Agenda

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-AGD-01 | Agenda day list with month header | v0.1 | 3 | BR-APPT-005, 007 |
| F-AGD-02 | Create/edit appointments: timed or all-day, location, notes | v0.1 | 3 | BR-APPT-001…003 |
| F-AGD-03 | Advance notices for appointments | v0.1 | 4 | BR-APPT-004 |
| F-AGD-04 | Recurring appointments with exceptions | v0.2 | 5 | BR-REC-008 |
| F-AGD-05 | Export/import appointments as .ics | v0.5 | 6 | — |

## Matters

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-MAT-01 | Matters list grouped by attention/waiting/open/resolved | v0.1 | 3 | BR-MATTER-010, 013 |
| F-MAT-02 | Matter detail: status, waiting for, next action, history | v0.1 | 3 | BR-MATTER-002…007 |
| F-MAT-03 | Notes in the history (backdatable) | v0.1 | 3 | BR-MATTER-008 |
| F-MAT-04 | Follow-up date with reminder | v0.2 | 5 | BR-MATTER-009 |
| F-MAT-05 | Link appointments to a matter | v0.2 | 5 | — |

## Today and review

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-TOD-01 | Greeting and date | v0.1 | 0 ✅ | BR-TODAY-001 |
| F-TOD-02 | Day summary counts, what to do today, next appointment, matters needing attention, inbox nudge | v0.1 | 3 | BR-TODAY-002…007 |
| F-TOD-03 | Weekly life review | v0.3 | 6 | BR-REVIEW-* |
| F-TOD-04 | Today home screen widget | v0.3 | 6 | — |

## Reminders

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-REM-01 | Offline scheduled notifications, survive reboot | v0.1 | 4 | BR-REM-001…005 |
| F-REM-02 | Snooze (10 min, 1 h, tomorrow) and complete from the notification | v0.1 | 4 | BR-REM-007, 008 |
| F-REM-03 | Reminder reliability screen (permissions, exact alarms, battery) | v0.1 | 4 | BR-REM-013, 014 |
| F-REM-04 | Morning summary | v0.2 | 5 | BR-REM-012 |
| F-REM-05 | Quiet hours | v0.2 | 5 | BR-REM-010, 011 |

## Data and settings

| Id | Feature | Release | Phase | Rules |
|---|---|---|---|---|
| F-DAT-01 | Search (accent-insensitive, offline) | v0.1 | 3 | BR-SEARCH-* |
| F-DAT-02 | Settings: theme, defaults, about, privacy | v0.1 | 3 | BR-SET-* |
| F-DAT-03 | Backup export/import to a file | v0.2 | 5 | BR-BACKUP-001…003 |
| F-DAT-04 | Automatic daily backups to a chosen folder | v0.2 | 5 | BR-BACKUP-004 |
| F-DAT-05 | Encrypted backups | v0.5 | 6 | BR-BACKUP-005 |
| F-DAT-06 | Labels (areas) and filters | v0.4 | 6 | — |

## Platform quality

| Id | Feature | Release | Phase |
|---|---|---|---|
| F-PLT-01 | English and Brazilian Portuguese, per-app language | v0.1 | 3 |
| F-PLT-02 | Dark theme | v0.1 | 2 |
| F-PLT-03 | Accessibility: TalkBack, 200 % font, contrast | v0.6 | 6 |
| F-PLT-04 | Tablet/foldable list-detail layouts | v0.7 | 6 |
| F-PLT-05 | Onboarding (philosophy, permissions, summary choice) | v0.6 | 6 |
| F-PLT-06 | Store release (F-Droid, Google Play) | v1.0 | 7 |

## Not planned (needs an explicit owner decision)

iOS, desktop and web clients · sync between devices · sharing and collaboration · attachments ·
location-based reminders · AI features · integrations that require network access.
