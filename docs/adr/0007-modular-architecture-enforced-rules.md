# ADR-0007: Split the app into small modules and enforce dependency rules in the build

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

The app is built and maintained mostly by AI agents. Agents optimize locally: under pressure they
import whatever compiles. Documentation alone does not prevent architectural erosion.

## Decision

- Modules by layer and feature (architecture/modules.md), each with a README contract.
- `ModuleRules.kt` (build-logic) fails Gradle configuration on any forbidden project dependency,
  with a message explaining the rule and pointing to the doc.
- Konsist tests enforce code-level rules (imports, naming, catalog, visibility).
- Material 3 is an `implementation` dependency of the design system, so features cannot even
  compile against it.

## Consequences

- Violations are caught in seconds, locally and in CI, with actionable messages.
- Changing a rule requires changing code, docs and an ADR together — deliberate by design.
- More modules mean more build files, minimized by convention plugins.

## Alternatives considered

- **Single module with packages** — no enforcement possible; erosion guaranteed.
- **Third-party module-graph plugins** — less transparent than a 100-line rule table.
