# ADR-0015: Use `app.prumio` as application id and package root

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Android needs a globally unique, permanent application id once published. Kotlin packages need a
root. The name "Prumio" is a working brand pending trademark and domain checks.

## Decision

- Application id `app.prumio` (debug: `app.prumio.debug`).
- Package root `app.prumio`; each module's package/namespace is derived from its Gradle path
  (`:core:domain` → `app.prumio.core.domain`) by `prumio.kmp.library`.

## Consequences

- Short, brand-aligned and systematic; enforced by `CodeHygieneTest` (package matches module).
- Changing it is cheap **before** the first store release (one property and a package rename) and
  impossible afterwards for the application id. Confirm before task P7.02.

## Alternatives considered

- **`io.github.alexandrezanata.prumio`** — always available but long and tied to a personal account.
