# ADR-0002: Use Compose Multiplatform for UI and Navigation 3 for navigation

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

UI should be shared with a future iOS app and be easy for AI agents to read and generate.
Declarative Kotlin UI with previews fits both. Navigation must be testable and multiplatform.

## Decision

- UI: Compose Multiplatform 1.12 (Material 3 1.9 wrapped by `:core:designsystem`), resources via
  Compose Multiplatform resources.
- Navigation: Navigation 3 (JetBrains multiplatform build). The back stack is a list of
  `@Serializable` keys owned by `:app:shell`; features contribute entries.
- ViewModels: JetBrains multiplatform `lifecycle-viewmodel`, scoped per navigation entry.

## Consequences

- Back stack logic is plain list manipulation, unit-tested without UI (`PrumioNavigatorTest`).
- Every key must be registered for serialization (enforced by `ModuleStructureTest`).
- Material 3 multiplatform versions lag Jetpack slightly; acceptable.

## Alternatives considered

- **Jetpack Compose (Android only)** — no UI sharing with iOS.
- **Navigation Compose 2 (type-safe routes)** — mature, but the back stack is opaque; Navigation 3
  is the recommended direction and simpler to reason about.
- **Voyager / Decompose** — third-party, more concepts to learn for agents.
