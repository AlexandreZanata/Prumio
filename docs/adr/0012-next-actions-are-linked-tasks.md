# ADR-0012: Model a matter's next actions as tasks linked to it

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

A matter ("Sort out the motorcycle documents") has a next action ("Call the dispatcher tomorrow at
9"). The original idea listed a separate `PendingAction` entity. A next action needs a due date,
time, reminders and completion — everything a task already has.

## Decision

- A next action is a regular `Task` with `matterId` set. No separate action entity.
- The matter's *next action* is computed (BR-MATTER-005): earliest open linked task.
- Linking, completing and dismissing linked tasks append history entries (BR-MATTER-003).

## Consequences

- Reminders, recurrence, Today, Agenda and search work for next actions with no extra code
  (no duplication).
- A matter may have several open actions; only the first is highlighted.

## Alternatives considered

- **Separate `PendingAction` entity** — duplicates task fields, reminders and UI.
- **Single `nextAction` text field on the matter** — no date, no reminder, no history.
