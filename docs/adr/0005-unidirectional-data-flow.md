# ADR-0005: Use unidirectional data flow with Contract / ViewModel / Route / Screen

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Screens built by many different agent sessions must look and behave the same way internally, so
any agent can read, test and modify any screen.

## Decision

Every screen has exactly four files: `XxxContract.kt` (UiState, Action, Effect), `XxxViewModel.kt`
(extends `PrumioViewModel`), `XxxRoute.kt` (stateful glue), `XxxScreen.kt` (stateless UI with
previews). Navigation decisions are effects. Specification: architecture/presentation-pattern.md.

## Consequences

- Screens are previewable and screenshot-testable; ViewModels are unit-testable with fakes.
- A little boilerplate for trivial screens, accepted for uniformity.
- Enforced by `NamingConventionTest` (base class, visibility, file set).

## Alternatives considered

- **MVVM with multiple StateFlows and callbacks** — less uniform; harder to review.
- **Redux-like store library** — more concepts and dependencies than needed.
