# `:core:notifications`

> Delivers reminders on the device: schedules alarms and shows notifications. Implements the
> domain scheduling ports; decides nothing on its own.

| | |
|---|---|
| **Layer** | Platform adapter |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.notifications` |
| **May depend on** | `:core:common`, `:core:domain`, `:core:model` |
| **Used by** | `:app:shell` (DI assembly only) |

## Responsibility (phase 4)

- `AndroidAlarmScheduler`: implements `AlarmScheduler` with `AlarmManager` (exact when permitted, inexact otherwise).
- `AndroidNotificationPresenter`: channels, notification content, actions (Complete, Snooze, Open).
- Broadcast receivers: alarm fired, boot completed, time/time zone changed, app updated, exact-alarm permission changed.
- A daily WorkManager job that re-runs reconciliation as a safety net.

**What is *not* here:** deciding *which* alarms exist. That is `ReminderPlanner` in `:core:domain`
(BR-REM-001). This module only executes the plan it receives.

Specification: [docs/domain/reminders-and-notifications.md](../../docs/domain/reminders-and-notifications.md).

## Rules

- No feature module may reach this module; features change data and reconciliation follows.
- Android code lives in `src/androidMain`; `commonMain` holds only DI declarations.
- Every receiver does its work through `goAsync()` + domain use cases, never with business logic inline.

## Tests

`./gradlew :core:notifications:testAndroidHostTest` and the instrumented alarm tests added in
phase 4 (docs/plan/phase-4-reminders.md). Manual: docs/plan/qa/device-qa-checklist.md.
