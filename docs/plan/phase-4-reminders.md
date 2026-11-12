# Phase 4 — Reminders and notifications (release v0.1)

**Goal:** Prumio reminds you, offline, reliably and honestly. **Gate:** G4 → release **v0.1**.
**Depends on:** phase 3. Specification: docs/domain/reminders-and-notifications.md.

Schema note: v0.1 is not released yet, so the reminder tables are added to schema version 1
(regenerate `1.db`). From v0.1 on, every schema change is a migration.

### P4.01 — Reminder storage and scheduling state
- **Status:** done
- **Deliverables:** tables `reminder`, `scheduled_alarm`, `snooze`, `delivered_alarm`; ports
  `ReminderRepository`, `ScheduledAlarmStore`, `SnoozeStore`, `DeliveredAlarmStore`; adapters and fakes.
- **Validation:** data tests; `verifySqlDelightMigration`.

- **Result:** Reminder wishes and projection/snooze/delivery state are persisted with adapters/fakes; aggregate writes and delete/Undo are atomic. Schema 1 snapshot regenerated; additive Android bridge preserves installed pre-release data.

### P4.02 — Reminders in the domain and editors
- **Status:** done
- **Depends on:** P4.01
- **Deliverables:** `Reminder` model, default reminder rules in create/update use cases, settings
  keys `default_reminder_time`, `default_appointment_notice_minutes`; reminders field (presets,
  max 5) in task and appointment editors; `ReminderSummary` shown in details.
- **Rules:** BR-TASK-006, BR-APPT-004.

- **Result:** Domain defaults and stable reminder identities; shared max-five offset editors, contextual explanation and summaries; editable default time/advance notice. Appointment conversion also applies defaults atomically.

### P4.03 — Reminder planner
- **Status:** done
- **Depends on:** P4.02
- **Deliverables:** `ReminderPlanner` (pure) and `PlanningInput`/`PlannedAlarm`/`AlarmKey`; table
  tests per rule: anchors, offsets, 14-day window, 200 limit, snooze, catch-up < 12 h, closed items,
  DST, travel.
- **Rules:** BR-REM-003, BR-REM-004, BR-REM-005, BR-REM-009.

- **Result:** Pure deterministic planner, 14-day/200 limit, floating/zoned anchors, snooze, delivered-key deduplication, <12-hour catch-up and DST/travel tests.

### P4.04 — Reconciliation
- **Status:** done
- **Depends on:** P4.03
- **Deliverables:** `ReconcileRemindersUseCase` (diff, idempotent, mutex), `AlarmScheduler` and
  `ReminderReconciliationTrigger` ports, fakes; idempotency tests.
- **Rules:** BR-REM-001.

- **Result:** Shared mutex, persisted request-code identities, idempotent diff and forced restoration after OS drift. Tests cover concurrent calls, stale snoozes and setting changes.

### P4.05 — Android alarm scheduler
- **Status:** done
- **Depends on:** P4.04
- **Deliverables:** `AndroidAlarmScheduler` (exact when permitted, inexact otherwise), PendingIntent
  identity per key, `SCHEDULE_EXACT_ALARM` declared (ADR-0014).
- **Rules:** BR-REM-013.

- **Result:** Exact allow-while-idle scheduler with denied/racing-permission inexact fallback and immutable URI-specific PendingIntents; API 26/36 adapter tests.

### P4.06 — Notifications
- **Status:** done
- **Depends on:** P4.04
- **Deliverables:** channels, `AndroidNotificationPresenter`, `NotificationContentFactory` with
  strings en + pt-BR, one notification per item, `DeliverAlarmUseCase`.
- **Rules:** BR-REM-015, BR-REM-016, BR-GEN-006, BR-REM-006.

- **Result:** Localized native channels/body/actions, item-tag replacement and current-data validation before delivery. Concurrent duplicate delivery and permission-denied recovery covered.

### P4.07 — Receivers and background safety net
- **Status:** done
- **Depends on:** P4.05, P4.06
- **Deliverables:** alarm, boot, time/time zone, package replaced, exact-alarm-permission receivers
  (manifest in `:core:notifications`), daily `ReconcileWorker` (WorkManager; check merged manifest
  permissions with the privacy gate).
- **Rules:** BR-REM-002.

- **Result:** Asynchronous alarm/action/recovery receivers, unique unconstrained daily WorkManager job, boot/update/time/zone/exact-permission handling. Merged manifests contain neither INTERNET nor the unused transitive ACCESS_NETWORK_STATE permission.

### P4.08 — Notification actions
- **Status:** done
- **Depends on:** P4.07
- **Deliverables:** Complete (task), Snooze 10 min / 1 h / Tomorrow, using use cases; snooze rules.
- **Rules:** BR-REM-007, BR-REM-008.

- **Result:** Complete uses the normal task operation; Snooze dialog delegates 10 minutes/1 hour/Tomorrow to domain (matter Tomorrow only). Physical Complete action validated.

### P4.09 — Deep links
- **Status:** done
- **Depends on:** P4.06
- **Deliverables:** `prumio://…` URIs, `MainActivity` builds the back stack, deleted-item handling.
- **Rules:** BR-REM-017.

- **Result:** Initial/onNewIntent links resolve against current repositories; Today + target back stack, safe missing-item message and consumed-intent clearing. Physical notification content opens MainActivity.

### P4.10 — Reactive reconciliation
- **Status:** done
- **Depends on:** P4.04
- **Deliverables:** app-scoped observer of reminder-relevant data and settings (debounced 1 s)
  started at app start.
- **Rules:** BR-REM-002, BR-SET-002.

- **Result:** App-lifetime one-second debounce and observer started after DI; resume/OS changes force restoration. Reconciliation and reactive settings tests pass.

### P4.11 — Permissions and reliability screen
- **Status:** done
- **Depends on:** P4.05, P4.06
- **Deliverables:** contextual `POST_NOTIFICATIONS` request with explanation, denial handling,
  Settings → Reminder reliability (notifications, exact alarms, battery info, fix buttons).
- **Rules:** BR-REM-013, BR-REM-014.

- **Result:** Contextual explanation in task/appointment editors and explicit matter follow-up flows, persistent two-denial limit, dismissible Settings banner and reliability/system-fix screen. Physical two-denial/no-third-prompt flow validated.

### P4.12 — Device validation and release v0.1
- **Status:** in progress (implementation and unsigned artifact ready; G4/release sign-off pending)
- **Depends on:** P4.01 … P4.11
- **Deliverables:** instrumented tests (schedule → fire → notification) on API 26 and latest
  emulators; device QA section G executed; version 0.1.0; CHANGELOG; signed APK on a GitHub release
  (signing setup documented, keystore never committed).
- **Validation:** G4 checklist; 7 days of owner dogfooding.

- **Owner scope clarification (2026-11-12):** no keystore exists; prepare local signing configuration
  and the release APK for later signing. No key was generated and no unsigned APK was published as
  a signed release. See [release-signing.md](../architecture/release-signing.md).
- **Implementation result:** version 0.1.0; R8/resource-shrunk unsigned release APK and custom Android
  instrumentation runner/script. Physical API 36 schedule → receiver → notification → content intent →
  Complete passed, with exact access allowed and denied. Permission explanation/refusal persistence,
  two-denial limit and removal of QA fixtures verified on the physical phone. Host adapters and
  pre-release-v1 preservation tested on API 26 and 36.
- **Still required for G4/release:** API 26 physical-device instrumentation, broader reboot/Doze/time-zone/
  update and snooze-duration QA, seven days of owner dogfooding, owner key/signature and publication.
  No emulator was started (AGENTS.md owner instruction takes precedence over the older emulator wording).

## Gate record

| Date | Result | Notes |
|---|---|---|
| 2026-11-12 | Implementation validated; release gate pending | Full verify green: 331 tests, 0 failures. Domain 99.19%, data 96.01%; changed feature ViewModels 94–98%. API 26/36 host adapters and API 36 physical exact/inexact alarm, notification/content intent/Complete. Physical contextual denial flow. Signing setup + unsigned 0.1.0 APK prepared per owner clarification. Remaining device matrix and seven-day owner dogfooding/sign-off remain open. |
