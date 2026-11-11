# Phase 1 — Domain and persistence

**Goal:** every entity, rule and storage operation needed by v0.1 exists in pure Kotlin and SQL,
fully tested, with no UI. **Gate:** G1. **Can run in parallel with:** phase 2.

### P1.01 — Coverage measurement with thresholds
- **Status:** done
- **Depends on:** —
- **Goal:** measure test coverage and fail the build below agreed thresholds.
- **Inputs:** docs/architecture/testing-strategy.md#coverage, docs/ai/recipes/add-library.md.
- **Deliverables:** Kover in the catalog and in `prumio.kmp.library` (or the root build); merged
  report; verification rules `:core:domain` ≥ 90 %, `:core:data` ≥ 80 % lines; `koverVerify` wired
  into `scripts/verify.sh` and CI; docs updated. If Kover cannot instrument the Android KMP host
  tests, write an ADR and use the documented alternative.
- **Validation:** `./scripts/verify.sh`; temporarily add an untested function to the domain and
  confirm verification fails (revert).
- **Result:** Kover 0.9.11 works with the Android KMP host tests (no ADR needed). Thresholds in
  `build-logic/.../CoverageRules.kt`; `koverVerify` runs in `check` (so in `verify.sh` and CI);
  merged report via `./gradlew koverHtmlReport`. The gate was proven: domain at 60 % failed the
  90 % rule; `DomainError` gained a rule-id invariant with tests, bringing the domain to 100 %.
  `ModuleStructureTest` requires every shared module in the merged report.

### P1.02 — Domain model types
- **Status:** todo
- **Depends on:** —
- **Goal:** implement the phase-1 types of model.md exactly.
- **Inputs:** docs/domain/model.md, glossary.md, core/model/README.md.
- **Deliverables:** in `:core:model`: `InboxItem`, `Task`, `TaskStatus`, `TaskRecurrence`,
  `RecurrenceBasis`, `RecurrenceRule` (+ `Frequency`, `MonthlyPattern`, `RecurrenceEnd`; data only),
  `Appointment`, `AppointmentTiming`, `Matter`, `MatterStatus`, `MatterEntry`, `MatterEntryKind`;
  trivial derived properties with tests; README "Public API" table updated.
- **Validation:** `./gradlew :core:model:testAndroidHostTest`; field-by-field comparison with model.md.

### P1.03 — Validation and creation policies
- **Status:** todo
- **Depends on:** P1.02
- **Goal:** one place that normalizes and validates input for every entity.
- **Inputs:** business-rules.md (GEN, TASK, APPT, MATTER, INBOX), error-handling.md.
- **Deliverables:** `TextRules` (trim, blank → null, limits), `TaskDraft`/`AppointmentDraft`/
  `MatterDraft` validated into models with `Outcome<…, DomainError.Validation>`; tests per rule.
- **Rules:** BR-GEN-001, BR-GEN-002, BR-TASK-001, BR-TASK-002, BR-APPT-001, BR-APPT-002, BR-MATTER-001, BR-INBOX-001 (validation part).
- **Validation:** `./gradlew :core:domain:testAndroidHostTest`.

### P1.04 — Database schema version 1
- **Status:** todo
- **Depends on:** P1.02
- **Goal:** create the phase-1 tables, indices, constraints and named queries.
- **Inputs:** docs/architecture/database-schema.md, data-layer.md, recipe add-database-table.md.
- **Deliverables:** `inbox_item.sq`, `task.sq`, `appointment.sq`, `matter.sq`, `matter_entry.sq` with
  queries needed by contracts.md; regenerated `databases/1.db`; query tests (ordering, CHECK
  constraints, FK cascade/SET NULL).
- **Validation:** `./gradlew :core:database:testAndroidHostTest :core:database:verifySqlDelightMigration`.

### P1.05 — Shared in-memory database for tests
- **Status:** todo
- **Depends on:** P1.04
- **Goal:** one reusable way to create a fresh in-memory `PrumioDatabase` in JVM tests (no copies).
- **Inputs:** docs/architecture/modules.md, ADR-0007.
- **Deliverables:** module `:core:database-testing` (consumable only from test source sets, like
  `:core:testing`) exposing `inMemoryPrumioDatabase()`; ModuleRules, modules.md, settings and
  READMEs updated; ADR addendum. Database and data tests use it.
- **Validation:** `./scripts/verify.sh` (module rules and structure tests must accept the module).

### P1.06 — Ports, fakes and builders
- **Status:** todo
- **Depends on:** P1.02
- **Goal:** declare the domain ports of contracts.md and their in-memory fakes.
- **Inputs:** docs/architecture/contracts.md, recipe add-repository.md.
- **Deliverables:** `InboxRepository`, `TaskRepository`, `AppointmentRepository`, `MatterRepository`,
  `SearchRepository`, `SettingsRepository` interfaces; `Fake*` in `:core:testing` with real `Flow`
  behavior; builders `anInboxItem()`, `aTask()`, `anAppointment()`, `aMatter()`, `aMatterEntry()`.
- **Validation:** fakes have their own tests proving ordering and emissions.

### P1.07 — Inbox and task repositories
- **Status:** todo
- **Depends on:** P1.04, P1.05, P1.06
- **Goal:** SQLDelight adapters for inbox items and tasks.
- **Inputs:** data-layer.md, contracts.md, recipe add-repository.md.
- **Deliverables:** `SqlDelightInboxRepository`, `SqlDelightTaskRepository`, mappers,
  `search_text` maintenance, Koin bindings; tests for every method and `Flow` re-emission.
- **Validation:** `./gradlew :core:data:testAndroidHostTest`.

### P1.08 — Appointment and matter repositories
- **Status:** todo
- **Depends on:** P1.04, P1.05, P1.06
- **Goal:** SQLDelight adapters for appointments, matters and history (atomic `save`/`resolve`).
- **Deliverables:** `SqlDelightAppointmentRepository`, `SqlDelightMatterRepository`, mappers, tests
  (including zoned/all-day round-trips and history ordering).
- **Validation:** `./gradlew :core:data:testAndroidHostTest`.

### P1.09 — Settings
- **Status:** todo
- **Depends on:** P1.06
- **Goal:** typed settings with defaults over the `setting` table.
- **Inputs:** model.md#settings, recipe add-setting.md.
- **Deliverables:** `SettingKey` registry (phase-1 keys: `theme`, `week_start`), `Settings`,
  `SettingsRepository` + SQLDelight adapter + fake; invalid values fall back to defaults.
- **Rules:** BR-SET-001.
- **Validation:** domain + data tests.

### P1.10 — Inbox use cases
- **Status:** todo
- **Depends on:** P1.03, P1.06
- **Deliverables:** `CaptureInboxItemUseCase`, `EditInboxItemUseCase`, `DiscardInboxItemUseCase`,
  `RestoreInboxItemUseCase` (undo), `ConvertInboxItemUseCase`, `ObserveInboxUseCase`.
- **Rules:** BR-INBOX-001, BR-INBOX-003, BR-INBOX-004, BR-INBOX-006, BR-GEN-003, BR-GEN-004.
- **Validation:** `./gradlew :core:domain:testAndroidHostTest`.

### P1.11 — Task use cases
- **Status:** todo
- **Depends on:** P1.03, P1.06
- **Deliverables:** `CreateTaskUseCase`, `UpdateTaskUseCase`, `CompleteTaskUseCase`,
  `DismissTaskUseCase`, `ReopenTaskUseCase`, `PostponeTaskUseCase`, `DeleteTaskUseCase`,
  `RestoreTaskUseCase`, `LinkTaskToMatterUseCase`, `TaskOverduePolicy`, observation use cases for lists.
- **Rules:** BR-TASK-001 … BR-TASK-004, BR-TASK-007, BR-TASK-008, BR-TIME-001, BR-TIME-003.
- **Validation:** domain tests including midnight and time zone change (`FakeTimeProvider.moveTo`).

### P1.12 — Appointment use cases
- **Status:** todo
- **Depends on:** P1.03, P1.06
- **Deliverables:** `CreateAppointmentUseCase`, `UpdateAppointmentUseCase`, `DeleteAppointmentUseCase`,
  `RestoreAppointmentUseCase`, `ObserveAppointmentsOnDaysUseCase` (non-recurring; occurrence on day per
  BR-APPT-005, zone conversion).
- **Rules:** BR-APPT-001 … BR-APPT-003, BR-APPT-005, BR-APPT-006, BR-TIME-002.
- **Validation:** domain tests with travel across zones and DST days.

### P1.13 — Matter use cases and policies
- **Status:** todo
- **Depends on:** P1.03, P1.06, P1.11
- **Deliverables:** `CreateMatterUseCase`, `UpdateMatterUseCase`, `WaitOnMatterUseCase`,
  `ResumeMatterUseCase`, `ResolveMatterUseCase`, `ReopenMatterUseCase`, `AddMatterNoteUseCase`,
  `EditMatterNoteUseCase`, `DeleteMatterNoteUseCase`, `SetMatterFollowUpUseCase`,
  `NextActionPolicy`, `MatterAttentionPolicy`, `ObserveMattersUseCase`, `ObserveMatterDetailUseCase`.
- **Rules:** BR-MATTER-001 … BR-MATTER-010, BR-MATTER-012 (follow-up reminder part of 009 is phase 5).
- **Validation:** state machine tests for every allowed and forbidden transition (state-machines.md).

### P1.14 — Today aggregation
- **Status:** todo
- **Depends on:** P1.11, P1.12, P1.13
- **Deliverables:** `ObserveTodayUseCase` emitting a `TodaySnapshot` (counts, ordered tasks, next
  appointment, attention matters, inbox count) that recomputes on data change.
- **Rules:** BR-TODAY-002 … BR-TODAY-005.
- **Validation:** domain tests with mixed fixtures; Turbine tests for re-emission.

### P1.15 — Search
- **Status:** todo
- **Depends on:** P1.07, P1.08
- **Deliverables:** `SearchTextNormalizer` (accent folding for Latin scripts, lowercase), search
  queries per type, `SearchUseCase` with grouping and ordering.
- **Rules:** BR-SEARCH-001 … BR-SEARCH-004.
- **Validation:** tests with Portuguese accents ("Documentação", "ação", "Pendência"), multi-word queries.

### P1.16 — Wiring and gate review
- **Status:** todo
- **Depends on:** P1.07 … P1.15
- **Goal:** everything bound in Koin; docs reflect reality; gate G1 executed.
- **Deliverables:** `coreDataModule`/`coreDomainModule` complete; contracts.md, model.md, schema,
  module READMEs updated; rule statuses flipped; gate record filled.
- **Validation:** `./scripts/verify.sh`; G1 checklist in validation-gates.md.

## Gate record

| Date | Result | Notes |
|---|---|---|
| | | |
