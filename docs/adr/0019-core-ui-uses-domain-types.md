# ADR-0019: Let `:core:ui` use domain result types

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Every feature must turn a `DomainError` into the same user-facing message ("Couldn't save. Your
data is safe…", field errors such as "Please fill this in"). Without a shared place, each feature
would copy the mapping — the duplication ADR-0008 forbids. `DomainError` lives in `:core:domain`,
which `:core:ui` could not depend on.

## Decision

`:core:ui` may depend on `:core:domain` to **use its types** (`DomainError`, result types such as
`ClosedTask`). It still never calls use cases, never touches repositories and never loads data;
ViewModels remain the only callers of use cases. `ModuleRules.kt` and docs/architecture/modules.md
are updated accordingly.

## Consequences

- One mapping `DomainError → message` (`:core:ui/message`), reused by every screen.
- A small coupling of shared UI to domain types; acceptable because those types are stable contracts.

## Alternatives considered

- **Mapping in each feature** — duplicated code and inconsistent copy.
- **Move `DomainError` to `:core:common`** — it references business rules; it belongs to the domain.
