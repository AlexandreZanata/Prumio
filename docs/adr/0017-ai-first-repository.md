# ADR-0017: Design the repository and documentation for AI agents

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

The whole app will be built and maintained with AI agents. Agents start every session without
memory, read files literally, follow explicit instructions well, and fail on implicit conventions,
ambiguity and large files.

## Decision

- **Single entry point**: AGENTS.md (tool-agnostic) + CLAUDE.md importing it; each module has a
  README contract and a CLAUDE.md that imports it (auto-loaded by Claude Code).
- **Progressive disclosure**: AGENTS.md → docs/README.md → specific docs; each doc has one purpose.
- **Explicit contracts**: business rules with ids, ports with signatures, component catalog,
  glossary with one name per concept.
- **Recipes** for every recurring change, with exact paths and templates.
- **Executable rules**: module rules in the build, Konsist tests, privacy gate, traceability test —
  with failure messages that say how to fix.
- **Small files** (≤ 400 lines) and a fixed file set per screen.
- **One command** to validate everything (`./scripts/verify.sh`).
- **Plan with small tasks**, each with inputs, deliverables and validation (docs/plan).

## Consequences

- Any agent can pick up a task with no prior context and produce consistent code.
- Docs must be kept in sync; the definition of done and tests make that part of every change.

## Alternatives considered

- **Conventional human-oriented docs** (a README and tribal knowledge) — insufficient for agents.
