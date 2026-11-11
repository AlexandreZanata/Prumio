# Phase 0 — Foundation

**Goal:** a repository where any AI agent can build Prumio consistently: architecture, contracts,
build system, quality gates and an app shell that runs. **Gate:** G0.

### P0.01 — Repository, license and documentation skeleton
- **Status:** done
- **Deliverables:** README, AGENTS.md, CLAUDE.md, CONTRIBUTING, PRIVACY, SECURITY, CHANGELOG,
  LICENSE (Apache-2.0), docs/ map.

### P0.02 — Product and domain contracts
- **Status:** done
- **Deliverables:** vision, features, screens, UX principles, glossary, model, state machines,
  business rules (BR-*), time semantics, recurrence, reminders, backup format.

### P0.03 — Architecture documentation and ADRs
- **Status:** done
- **Deliverables:** overview, modules, conventions, presentation pattern, contracts, data layer,
  schema, errors, navigation, DI, platform strategy, i18n, accessibility, testing, build system;
  ADR-0001 … ADR-0017.

### P0.04 — Gradle build with convention plugins
- **Status:** done
- **Deliverables:** version catalog (Kotlin 2.4.21, AGP 9.5.1, CMP 1.12.2, Gradle 9.8.2),
  `prumio.kmp.library`, `prumio.kmp.compose`, `prumio.feature`, `prumio.android.application`,
  `prumio.jvm.library`; configuration cache on.

### P0.05 — Module skeleton with contracts
- **Status:** done
- **Deliverables:** 20 modules (app, core, feature, tooling), each with README.md and CLAUDE.md.

### P0.06 — Enforced module dependency rules
- **Status:** done
- **Deliverables:** `ModuleRules.kt` failing configuration on forbidden dependencies.

### P0.07 — Architecture tests
- **Status:** done
- **Deliverables:** Konsist suite (layers, naming, design system, hygiene, structure, rule
  traceability, privacy).

### P0.08 — App shell and placeholders
- **Status:** done
- **Deliverables:** `PrumioApp` with Navigation 3 back stack rules, bottom bar, "Add anything"
  dialog, placeholder screens, theme tokens and first `Prumio*` components, en + pt-BR strings,
  launcher icon, no-INTERNET manifest.

### P0.09 — Reference vertical slice
- **Status:** done
- **Rules:** BR-TODAY-001 (implemented).
- **Deliverables:** `DayPeriod` → `GetDayPeriodUseCase` (+ tests) → `DayPeriod.greeting()` →
  Today Contract/ViewModel/Route/Screen (+ ViewModel tests).

### P0.10 — Quality gates and CI
- **Status:** done
- **Deliverables:** Spotless (ktlint + Compose rules), privacy gate task, SQLDelight migration
  verification, `scripts/verify.sh`, GitHub Actions CI, Dependabot, issue and PR templates.

## Gate record

| Date | Result | Notes |
|---|---|---|
| 2026-11-11 | Automated checks passed locally (`./scripts/verify.sh`: 40+ tests, architecture, privacy, lint, assemble) | — |
| 2026-11-11 | Manual, emulator API 36 (Pixel 6 profile) | Launch, four tabs, back to Today, "Add anything" dialog, Search/Settings, dark theme and pt-BR verified. Found and fixed an escaped apostrophe in Compose resources (now guarded by `StringResourcesTest`). Pending: API 26 run, first CI run, owner sign-off |
