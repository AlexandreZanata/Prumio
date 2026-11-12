# ADR-0023: Preserve installed pre-release debug data while extending schema 1

- **Status:** Accepted
- **Date:** 2026-11-12
- **Deciders:** Phase 4 schema instructions, Codex

## Context

P4.01 explicitly adds four tables to unreleased schema 1 and regenerates 1.db. An installed Phase 3
debug database already reports version 1. SQLite would not call onUpgrade for it. Clearing data
would lose the owner's existing organizer entries.

## Decision

For schema version 1 only, the Android driver onOpen reads the four reminder table DDL blocks
from the packaged SQLDelight sources and executes CREATE TABLE/INDEX IF NOT EXISTS. This reuses
the schema source rather than maintaining duplicate DDL. It changes no existing rows/tables and
is idempotent for a fresh Phase 4 database. SQLDelight's committed 1.db contains the complete schema.

## Consequences

Existing debug data survives installation. Robolectric tests on API 26 and 36 verify preservation
and repeated opens with the real DDL. From the first release onward all schema changes use numbered
migrations. Remove this version-1-only compatibility bridge when moving to schema 2; never retrofit
a released schema snapshot.
