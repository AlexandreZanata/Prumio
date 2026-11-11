# ADR-0010: Give every business rule an id and trace it to code and tests

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Agents need to know exactly which behavior is required, where it is implemented and which test
proves it — and to find all of that with a text search when a rule changes.

## Decision

- Rules live in docs/domain/business-rules.md with ids `BR-<AREA>-<NNN>`, a status
  (`planned`/`implemented`) and a phase.
- Enforcing code cites the id in KDoc; proving tests start their names with it.
- `BusinessRuleTraceabilityTest` fails when code cites unknown ids, when ids are duplicated, or
  when an `implemented` rule has no test citing it.

## Consequences

- `rg BR-TASK-004` finds the spec, the implementation and the tests.
- Status in the doc always reflects reality.

## Alternatives considered

- **Specs in tests only (BDD)** — less readable for product discussions.
- **Issue tracker as source of truth** — not versioned with code; not available offline to agents.
