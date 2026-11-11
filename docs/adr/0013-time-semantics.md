# ADR-0013: Use floating times for personal items and zoned times for appointments

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

"Take medicine at 08:00" should stay at 08:00 local time when travelling; "Call with São Paulo at
15:00" is a fixed instant. Mixing the two causes the classic organizer bugs.

## Decision

- Floating (local date/time, no zone): task due dates/times, matter follow-ups, all-day
  appointments, note dates.
- Zoned (local date-time + IANA zone): timed appointments.
- Instants (UTC) for audit timestamps and alarm triggers.
- `TimeProvider` is the only source of "now" and of the current zone.
Specification: domain/time-semantics.md.

## Consequences

- Clear, testable behavior on travel and DST; `FakeTimeProvider` simulates both.
- Two representations to map in storage and backups.

## Alternatives considered

- **Everything as instants** — personal tasks shift when travelling.
- **Everything floating** — appointments with remote people shift when travelling.
