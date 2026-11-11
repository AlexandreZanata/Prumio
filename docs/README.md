# Documentation map

Everything about Prumio that is not code lives here. Documents are written to be read by AI agents
first: each one has a single purpose, stable headings you can link to, and explicit contracts.

> Reading order for a new agent or contributor: [AGENTS.md](../AGENTS.md) → this page →
> [architecture/overview.md](architecture/overview.md) → [domain/model.md](domain/model.md) →
> the plan task you were given.

## Product — *what* and *why*

| Document | Purpose |
|---|---|
| [product/vision.md](product/vision.md) | Philosophy, principles, target user, non-goals, differentiation |
| [product/features.md](product/features.md) | Feature catalog (`F-*` ids) mapped to releases and phases |
| [product/screens.md](product/screens.md) | Every screen, its content, actions and navigation map |
| [product/ux-principles.md](product/ux-principles.md) | Interaction rules every screen must follow |

## Domain — the logical contracts

| Document | Purpose |
|---|---|
| [domain/glossary.md](domain/glossary.md) | Ubiquitous language: product term ↔ pt-BR ↔ code name. One name per concept |
| [domain/model.md](domain/model.md) | Entities, fields, types, invariants and relationships |
| [domain/state-machines.md](domain/state-machines.md) | Allowed state transitions and their side effects |
| [domain/business-rules.md](domain/business-rules.md) | Every rule with a stable `BR-*` id, status and phase |
| [domain/time-semantics.md](domain/time-semantics.md) | Floating vs zoned times, "today", DST |
| [domain/recurrence.md](domain/recurrence.md) | Supported repeat rules and the occurrence algorithm |
| [domain/reminders-and-notifications.md](domain/reminders-and-notifications.md) | How reminders are planned, reconciled and delivered |
| [domain/backup-format.md](domain/backup-format.md) | The backup file contract (JSON, versioned) |

## Architecture — *how* the code is organized

| Document | Purpose |
|---|---|
| [architecture/overview.md](architecture/overview.md) | Layers, data flow, diagrams. Start here |
| [architecture/modules.md](architecture/modules.md) | Module catalog and the dependency rules enforced by the build |
| [architecture/conventions.md](architecture/conventions.md) | Naming, packages, file layout, Kotlin style |
| [architecture/presentation-pattern.md](architecture/presentation-pattern.md) | Contract / ViewModel / Route / Screen pattern |
| [architecture/contracts.md](architecture/contracts.md) | Every domain port (interface) and its semantics |
| [architecture/data-layer.md](architecture/data-layer.md) | Repositories, transactions, flows, mapping |
| [architecture/database-schema.md](architecture/database-schema.md) | Tables, indices, migrations policy |
| [architecture/error-handling.md](architecture/error-handling.md) | `Outcome`, `DomainError`, user-facing messages |
| [architecture/navigation.md](architecture/navigation.md) | Navigation keys, back stack rules, deep links |
| [architecture/dependency-injection.md](architecture/dependency-injection.md) | Koin modules and registration rules |
| [architecture/platform-strategy.md](architecture/platform-strategy.md) | Android today, iOS-ready rules, how iOS will be added |
| [architecture/i18n.md](architecture/i18n.md) | Strings, plurals, dates, supported languages |
| [architecture/accessibility.md](architecture/accessibility.md) | Accessibility requirements |
| [architecture/testing-strategy.md](architecture/testing-strategy.md) | Test pyramid, what to test where, fakes |
| [architecture/build-system.md](architecture/build-system.md) | Gradle, convention plugins, version catalog, CI |

## Design system

| Document | Purpose |
|---|---|
| [brand/README.md](brand/README.md) | Logo files, Prumio Green and logo usage rules |
| [design-system/README.md](design-system/README.md) | Visual principles and how to use the design system |
| [design-system/tokens.md](design-system/tokens.md) | Colors, typography, spacing, shapes |
| [design-system/components.md](design-system/components.md) | **Catalog of every shared component.** Search it before creating UI |

## Working with AI agents

| Document | Purpose |
|---|---|
| [ai/workflow.md](ai/workflow.md) | Step-by-step loop for executing a task |
| [ai/definition-of-done.md](ai/definition-of-done.md) | What "done" means for any change |
| [ai/review-checklist.md](ai/review-checklist.md) | Self-review before reporting |
| [ai/recipes/](ai/recipes/README.md) | One recipe per kind of change (new screen, use case, table…) |

## Plan and decisions

| Document | Purpose |
|---|---|
| [plan/README.md](plan/README.md) | Roadmap, status board and how tasks are executed |
| [plan/validation-gates.md](plan/validation-gates.md) | Gates G0–G7 that close each phase |
| [plan/qa/device-qa-checklist.md](plan/qa/device-qa-checklist.md) | Manual device test scripts |
| [adr/](adr/README.md) | Architecture Decision Records |

## Rules for writing docs

1. One topic per file; link instead of repeating. If two docs disagree, the more specific one wins
   (business-rules.md over overview prose) and the conflict must be fixed in the same change.
2. Use stable ids for anything code refers to: `BR-*` rules, `F-*` features, `P<phase>.<nn>` tasks, `ADR-NNNN`.
3. Write contracts as tables and Kotlin signatures, not prose.
4. English only in docs and code. User-facing strings exist in English and Brazilian Portuguese.
