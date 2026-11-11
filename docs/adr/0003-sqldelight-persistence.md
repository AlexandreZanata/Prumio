# ADR-0003: Persist data in SQLite through SQLDelight

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

All data is local (offline-first, no backend). The store must be reliable, queryable (agenda
ranges, search), reactive, multiplatform and easy to evolve with verified migrations.

## Decision

- SQLite via SQLDelight 2: schema and queries written as SQL in `.sq` files, typed Kotlin generated.
- Migrations as `.sqm` files with committed schema snapshots, verified in `check`.
- Settings stored in the same database (key-value `setting` table) so one backup covers everything.

## Consequences

- SQL is explicit and readable for agents; generated APIs are type-safe.
- Reactive reads via `asFlow()`; multi-table atomicity via one transaction per port method.
- SQL must target SQLite 3.18 (minSdk 26) — no UPSERT/RETURNING/window functions.
- Tests use a real in-memory SQLite on the JVM.

## Alternatives considered

- **Room KMP** — good, but annotation-driven DAOs hide SQL; SQLDelight's SQL-first model is
  clearer for reviews and agents.
- **DataStore for settings** — a second storage technology and a second backup path.
- **Realm / ObjectBox** — heavier, proprietary data formats, less portable backups.
