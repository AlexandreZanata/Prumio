# ADR-0022: Reconcile offline reminders through a serialized projection

- **Status:** Accepted
- **Date:** 2026-11-12
- **Deciders:** Project owner (Phase 4 implementation request), Codex

## Context

P4.07 explicitly calls for WorkManager as a daily safety net. Receivers, app changes and notification
actions can overlap; persisted scheduling state must also survive OS resets.

## Decision

Use the planned WorkManager dependency, pinned in the version catalog. The current stable version
is documented in the [official release notes](https://developer.android.com/jetpack/androidx/releases/work).
Remove the unused transitive ACCESS_NETWORK_STATE permission. Use no network constraints and no work-analytics dependency. Keep all planning/validation in domain.
Share ReminderCoordinator mutexes across use-case instances. Force reconciliation after process
start, boot, update, time/zone and exact-access changes. Preserve persisted request codes, with
URI identity per alarm. A denied permission retains wishes/future alarms but defers catch-up OS
alarms until access returns, avoiding an immediate self-rescheduling loop.

## Consequences

Features never schedule alarms. Atomic owner writes include reminder wishes, including delete/Undo.
WorkManager's merged manifest remains subject to the no-INTERNET privacy gate. Release signing
is configured separately; the owner has requested preparation without generating a release key.

## Alternatives considered

- Periodic alarms alone — no durable background job safety net.
- Per-feature scheduling — duplicates rules and permits stale notifications.
