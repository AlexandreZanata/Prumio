# ADR-0014: Use exact alarms when permitted and degrade honestly otherwise

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Android restricts exact alarms: `SCHEDULE_EXACT_ALARM` is user-granted and denied by default on
recent versions; `USE_EXACT_ALARM` is reserved by Google Play policy for alarm-clock and calendar
apps. Doze and OEM battery savers can delay inexact alarms.

## Decision

- Declare `SCHEDULE_EXACT_ALARM`. Use `setExactAndAllowWhileIdle` when `canScheduleExactAlarms()`,
  otherwise `setAndAllowWhileIdle`.
- Settings → Reminder reliability shows the state and a button to grant the permission (BR-REM-013).
- Do not request `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`; explain battery settings instead.
- Re-evaluate `USE_EXACT_ALARM` (Prumio is calendar-like) when preparing the Play Store release
  (task P7.04) and for the F-Droid build.

## Consequences

- Reminders are exact for users who grant the permission; others get a clear explanation.
- No Play policy risk before the store review.

## Alternatives considered

- **`USE_EXACT_ALARM` from day one** — may be rejected by Play review.
- **Inexact only** — reminders minutes late; breaks the core promise.
