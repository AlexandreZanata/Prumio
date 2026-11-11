# ADR-0008: Make the design system the single source of UI components

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

The owner requires that a component used in more than one place is always referenced, never
duplicated. AI agents tend to re-create similar UI in each feature.

## Decision

- Generic components live only in `:core:designsystem` (`Prumio*`); domain-aware ones only in `:core:ui`.
- Only `:core:designsystem` may use Material 3 (dependency is `implementation`; `LayerDependencyTest`).
- Every shared composable is listed in docs/design-system/components.md (`DesignSystemTest`), the
  index agents search before creating UI.
- Feature modules have no public composables (`DesignSystemTest`); anything reusable moves down.

## Consequences

- One visual definition per concept; global changes in one place.
- Creating a component has a small fixed cost (catalog entry, preview).

## Alternatives considered

- **Use Material 3 directly in features** — fast but inconsistent and duplicative.
- **Convention-only reuse** — not enforceable; erodes under agent-driven development.
