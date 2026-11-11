# ADR-0011: Identify entities with UUIDs generated on the device

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Ids must be stable across backup/restore, safe to merge if optional sync ever exists, and never
reused after deletion.

## Decision

- Every entity id is a random UUID string from `IdGenerator` (`kotlin.uuid.Uuid.random()`),
  stored as `TEXT`.
- Typed value classes (`TaskId`, `MatterId`…) prevent mixing ids in code.
- Entities carry `createdAt`/`updatedAt` for ordering and future conflict resolution.

## Consequences

- Backups preserve identities; future sync is possible without id remapping.
- Slightly larger indices than integer keys; irrelevant at personal-data scale.

## Alternatives considered

- **Autoincrement integers** — collide across devices and backups.
- **UUIDv7** — time-ordered, nice to have; can be adopted later behind `IdGenerator` without migration.
