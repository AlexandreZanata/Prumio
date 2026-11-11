# ADR-0004: Use Koin for dependency injection

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Classes receive dependencies through constructors. Something must assemble the graph on every
platform. The assembly should be easy to read and modify by agents and fail early when incomplete.

## Decision

- Koin 4 (multiplatform). One Koin module per Gradle module (`di/` package or `XxxFeatureModule.kt`),
  all listed in `prumioModules` (`:app:shell`).
- Constructor injection only; Koin APIs are confined to DI files, Routes (`koinViewModel()`) and the
  Android entry point (enforced by `LayerDependencyTest`).
- `KoinModulesTest` runs `verify()` so missing bindings fail the build, not the app.

## Consequences

- No code generation, fast builds, the graph is plain Kotlin.
- Resolution errors are runtime by nature — mitigated by the verification test.

## Alternatives considered

- **Manual DI** — verbose wiring code that grows with every use case.
- **Hilt/Dagger** — Android only.
- **kotlin-inject / Metro** — compile-time safety, but more annotations and generated code to understand.
