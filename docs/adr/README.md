# Architecture Decision Records

Short documents that record **why** an important decision was made, so nobody (human or AI)
re-litigates it by accident or breaks it unknowingly. Format: [template.md](template.md).

| ADR | Decision | Status |
|---|---|---|
| [0001](0001-kotlin-multiplatform-android-first.md) | Kotlin Multiplatform architecture, Android as the only target until iOS is explicitly requested | Accepted |
| [0002](0002-compose-multiplatform-ui.md) | Compose Multiplatform UI with Navigation 3 | Accepted |
| [0003](0003-sqldelight-persistence.md) | SQLite through SQLDelight, offline only | Accepted |
| [0004](0004-koin-dependency-injection.md) | Koin for dependency injection | Accepted |
| [0005](0005-unidirectional-data-flow.md) | Unidirectional data flow with Contract / ViewModel / Route / Screen | Accepted |
| [0006](0006-notifications-derived-from-data.md) | Notifications are a projection of the database | Accepted |
| [0007](0007-modular-architecture-enforced-rules.md) | Small modules with dependency rules enforced by the build | Accepted |
| [0008](0008-design-system-single-source.md) | The design system is the single source of UI components | Accepted |
| [0009](0009-no-network-access.md) | No network access at all | Accepted |
| [0010](0010-business-rule-traceability.md) | Business rules have ids, cited by code and tests | Accepted |
| [0011](0011-uuid-identifiers.md) | UUID identifiers generated on device | Accepted |
| [0012](0012-next-actions-are-linked-tasks.md) | A matter's next actions are tasks linked to it | Accepted |
| [0013](0013-time-semantics.md) | Floating times for tasks, zoned times for appointments | Accepted |
| [0014](0014-exact-alarm-strategy.md) | Exact alarms when permitted, honest fallback otherwise | Accepted |
| [0015](0015-application-id-and-namespace.md) | `app.prumio` application id and package root | Accepted |
| [0016](0016-android-system-backup.md) | Allow Android system backup of the database | Accepted |
| [0017](0017-ai-first-repository.md) | Repository and documentation designed for AI agents | Accepted |

## When to write an ADR

- Choosing or replacing a library, a pattern or a module boundary.
- Changing a rule enforced by build-logic or architecture tests.
- Any decision where a reasonable person could have chosen differently.

Number sequentially; never delete an ADR — supersede it (`Status: Superseded by ADR-00NN`).
