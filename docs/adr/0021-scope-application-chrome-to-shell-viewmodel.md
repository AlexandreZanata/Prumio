# ADR-0021: Scope application chrome to ShellViewModel

- **Status:** Accepted
- **Date:** 2026-11-12
- **Deciders:** Project owner (explicit approval in the Phase 3 session), Codex

## Context

The app must observe appearance preferences and the Inbox badge independently of the current
feature. Phase 3 calls for a ShellViewModel, while the original naming gate required all
ViewModels to live in feature modules. The owner explicitly authorized a single exception.

## Decision

Allow only `app/shell/src/commonMain/kotlin/app/prumio/app/shell/ShellViewModel.kt` outside features.
It remains internal, extends PrumioViewModel and calls domain use cases. ShellRoute scopes it to
the root ViewModelStore. All other ViewModels remain subject to the existing placement rule.
Reuse the lifecycle and Koin Compose dependencies already in the version catalog and used by
features; no new external library, module or module dependency rule is introduced.

## Consequences

The theme and Inbox count update across every destination. NamingConventionTest checks the
exact exception and continues enforcing the base class and visibility for it. Business rules
remain in the domain; the shell only renders chrome.

## Alternatives considered

- A plain state holder — avoids the exception but duplicates lifecycle ownership supplied by ViewModel.
- A feature-owned global ViewModel — makes shell chrome depend on a feature's internal state.
