# ADR-0001: Use a Kotlin Multiplatform architecture with Android as the only target

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

The owner wants a pure-Kotlin app for Android now, with full iOS support possible later — but only
when explicitly requested. The project is complete when Android reaches v1.0, even with no iOS.
Rewriting shared logic for iOS later would be expensive; maintaining two platforms now is unwanted.

## Decision

- All shared modules are Kotlin Multiplatform modules (`prumio.kmp.library`) with **one target:
  Android** (`com.android.kotlin.multiplatform.library`).
- Business logic, data access, UI (Compose Multiplatform) and resources live in `commonMain`.
- Platform specifics go to `androidMain` behind interfaces or `expect`/`actual`.
- No iOS (or other) target, source set or dependency is added until the owner explicitly asks.

## Consequences

- Adding iOS later is additive (targets + `iosMain` actuals + Xcode app), see platform-strategy.md.
- `commonMain` must stay platform neutral; `LayerDependencyTest` enforces it today.
- Only multiplatform libraries can be used in shared code.
- Slight build complexity versus a plain Android project, absorbed by convention plugins.

## Alternatives considered

- **Plain Android (Jetpack Compose) project** — simplest now, but an iOS version would require
  porting or rewriting most code.
- **Flutter / React Native** — not Kotlin; contradicts the owner's requirement.
- **KMP with iOS targets from day one** — doubles CI and testing effort before it is needed.
