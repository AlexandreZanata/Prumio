# ADR-0006: Derive notifications from data instead of scheduling them imperatively

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Reminders must survive reboots, time zone changes, edits, deletions, snoozes, backups and app
updates. Imperative scheduling ("call schedule() when X happens") is fragile: every code path must
remember to schedule and cancel, and drift accumulates. Agents are especially likely to miss a path.

## Decision

The database is the only source of truth. A pure `ReminderPlanner` computes the desired alarms
from data; `ReconcileRemindersUseCase` diffs them against what the OS has and applies the
difference. Reconciliation runs on many triggers (BR-REM-002). Features never touch alarms.
Specification: domain/reminders-and-notifications.md.

## Consequences

- Idempotent and self-healing; any trigger fixes any drift.
- Planner fully unit-testable (DST, quiet hours, snooze) without Android.
- iOS later only needs new adapters.
- Requires persisting scheduling state (scheduled alarms, snoozes, deliveries).

## Alternatives considered

- **WorkManager per reminder** — imprecise timing, still imperative.
- **Imperative AlarmManager calls from use cases** — fragile as described.
