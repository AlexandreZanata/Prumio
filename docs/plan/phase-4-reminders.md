# Phase 4 — Reminders and notifications (release v0.1)

**Goal:** Prumio reminds you, offline, reliably and honestly. **Gate:** G4 → release **v0.1**.
**Depends on:** phase 3. Specification: docs/domain/reminders-and-notifications.md.

Schema note: v0.1 is not released yet, so the reminder tables are added to schema version 1
(regenerate `1.db`). From v0.1 on, every schema change is a migration.

### P4.01 — Reminder storage and scheduling state
- **Status:** todo
- **Deliverables:** tables `reminder`, `scheduled_alarm`, `snooze`, `delivered_alarm`; ports
  `ReminderRepository`, `ScheduledAlarmStore`, `SnoozeStore`, `DeliveredAlarmStore`; adapters and fakes.
- **Validation:** data tests; `verifySqlDelightMigration`.

### P4.02 — Reminders in the domain and editors
- **Status:** todo
- **Depends on:** P4.01
- **Deliverables:** `Reminder` model, default reminder rules in create/update use cases, settings
  keys `default_reminder_time`, `default_appointment_notice_minutes`; reminders field (presets,
  max 5) in task and appointment editors; `ReminderSummary` shown in details.
- **Rules:** BR-TASK-006, BR-APPT-004.

### P4.03 — Reminder planner
- **Status:** todo
- **Depends on:** P4.02
- **Deliverables:** `ReminderPlanner` (pure) and `PlanningInput`/`PlannedAlarm`/`AlarmKey`; table
  tests per rule: anchors, offsets, 14-day window, 200 limit, snooze, catch-up < 12 h, closed items,
  DST, travel.
- **Rules:** BR-REM-003, BR-REM-004, BR-REM-005, BR-REM-009.

### P4.04 — Reconciliation
- **Status:** todo
- **Depends on:** P4.03
- **Deliverables:** `ReconcileRemindersUseCase` (diff, idempotent, mutex), `AlarmScheduler` and
  `ReminderReconciliationTrigger` ports, fakes; idempotency tests.
- **Rules:** BR-REM-001.

### P4.05 — Android alarm scheduler
- **Status:** todo
- **Depends on:** P4.04
- **Deliverables:** `AndroidAlarmScheduler` (exact when permitted, inexact otherwise), PendingIntent
  identity per key, `SCHEDULE_EXACT_ALARM` declared (ADR-0014).
- **Rules:** BR-REM-013.

### P4.06 — Notifications
- **Status:** todo
- **Depends on:** P4.04
- **Deliverables:** channels, `AndroidNotificationPresenter`, `NotificationContentFactory` with
  strings en + pt-BR, one notification per item, `DeliverAlarmUseCase`.
- **Rules:** BR-REM-015, BR-REM-016, BR-GEN-006, BR-REM-006.

### P4.07 — Receivers and background safety net
- **Status:** todo
- **Depends on:** P4.05, P4.06
- **Deliverables:** alarm, boot, time/time zone, package replaced, exact-alarm-permission receivers
  (manifest in `:core:notifications`), daily `ReconcileWorker` (WorkManager; check merged manifest
  permissions with the privacy gate).
- **Rules:** BR-REM-002.

### P4.08 — Notification actions
- **Status:** todo
- **Depends on:** P4.07
- **Deliverables:** Complete (task), Snooze 10 min / 1 h / Tomorrow, using use cases; snooze rules.
- **Rules:** BR-REM-007, BR-REM-008.

### P4.09 — Deep links
- **Status:** todo
- **Depends on:** P4.06
- **Deliverables:** `prumio://…` URIs, `MainActivity` builds the back stack, deleted-item handling.
- **Rules:** BR-REM-017.

### P4.10 — Reactive reconciliation
- **Status:** todo
- **Depends on:** P4.04
- **Deliverables:** app-scoped observer of reminder-relevant data and settings (debounced 1 s)
  started at app start.
- **Rules:** BR-REM-002, BR-SET-002.

### P4.11 — Permissions and reliability screen
- **Status:** todo
- **Depends on:** P4.05, P4.06
- **Deliverables:** contextual `POST_NOTIFICATIONS` request with explanation, denial handling,
  Settings → Reminder reliability (notifications, exact alarms, battery info, fix buttons).
- **Rules:** BR-REM-013, BR-REM-014.

### P4.12 — Device validation and release v0.1
- **Status:** todo
- **Depends on:** P4.01 … P4.11
- **Deliverables:** instrumented tests (schedule → fire → notification) on API 26 and latest
  emulators; device QA section G executed; version 0.1.0; CHANGELOG; signed APK on a GitHub release
  (signing setup documented, keystore never committed).
- **Validation:** G4 checklist; 7 days of owner dogfooding.

## Gate record

| Date | Result | Notes |
|---|---|---|
| | | |
