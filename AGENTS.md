# AGENTS.md — Operating manual for AI agents

> Prumio is built and maintained mostly by AI agents. This file is the entry point for every agent
> (Claude Code, Codex, Gemini, Cursor, Copilot…). Read it fully before changing anything.
> Humans: this is also the shortest accurate description of how the codebase works.

## 1. What Prumio is

An offline-first, open-source personal organizer that **remembers things for you**: tasks
(what to do), appointments (where to be) and **matters** (life's unfinished business, with a next
action and a history). Private by design: no network, no account, no telemetry.

- Language: **Kotlin only**. UI: **Compose Multiplatform**. Persistence: **SQLDelight**. DI: **Koin**.
- Targets: **Android only**. The architecture is Kotlin Multiplatform so iOS can be added later,
  **but never add iOS (or any other target) unless the owner explicitly asks for it** in the task.
  The project is complete when the Android app reaches v1.0 (docs/plan/README.md).

## 2. Golden rules

1. **Read before you write.** For any task, read in this order: this file → the plan task
   (`docs/plan/phase-*.md`) → the README.md of every module you will touch → the docs the task links.
2. **Never duplicate. Reuse by reference.** Before creating a composable, use case, repository,
   string, formatter or helper, search for an existing one:
   - UI components: `docs/design-system/components.md` (catalog of everything in `:core:designsystem` and `:core:ui`).
   - Business operations: `core/domain/src/commonMain/.../<aggregate>/`.
   - Strings: `core/ui/src/commonMain/composeResources/values/strings.xml` (shared) before adding to a feature.
   If something is needed by two features, it moves down to a core module — it is never copied.
3. **Respect the module graph.** Allowed dependencies are in `docs/architecture/modules.md` and
   enforced by `build-logic/.../ModuleRules.kt`: a forbidden dependency fails the build.
   Features never depend on other features, `:core:data`, `:core:database` or `:core:notifications`.
4. **Business logic lives only in `:core:domain`.** ViewModels orchestrate use cases and map to UI
   state; composables only render state and emit actions. No rule is ever implemented in UI code.
5. **Every business rule has an ID** (`BR-AREA-NNN`) in `docs/domain/business-rules.md`. Cite it in
   the KDoc of the code that enforces it and in the name of the test that proves it. When you
   implement a rule, change its status to `implemented` in the same change.
6. **Privacy is non-negotiable.** Never add network access, analytics, crash reporters, ads or
   accounts. The build fails if the merged manifest requests `INTERNET` (PRIVACY.md).
7. **Keep `commonMain` platform-neutral.** No `android.*`, `java.*` or Android-only libraries in
   `commonMain`. Platform code goes to `androidMain` behind an interface or `expect`.
8. **Small, predictable files.** One public component/class per file, file named after it,
   ≤ 400 lines. Screens always use the file set `XxxContract.kt`, `XxxViewModel.kt`, `XxxRoute.kt`, `XxxScreen.kt`.
9. **Docs are part of the change.** If you change behavior, structure or a contract, update the
   matching doc (module README, business rules, component catalog, ADR) in the same commit.
10. **Prove it.** Run `./scripts/verify.sh` before saying a task is done. Never weaken a test, a
    rule or a gate to make it pass; fix the code or ask.

## 3. Commands

| Purpose | Command |
|---|---|
| Full local gate (run before every commit) | `./scripts/verify.sh` |
| Auto-format Kotlin | `./gradlew spotlessApply` |
| Unit tests of one module | `./gradlew :core:domain:testAndroidHostTest` |
| Architecture tests | `./gradlew :tooling:architecture-tests:test` |
| Build the debug APK | `./gradlew :app:android:assembleDebug` |
| Install on a running emulator/device | `./gradlew :app:android:installDebug` |

Requirements: JDK 21, Android SDK with platform 36 (`ANDROID_HOME` set).

## 4. Repository map

```
app/android      Android entry point (Application, MainActivity, manifest). Thin.
app/shell        Shared root UI: PrumioApp(), navigation host, bottom bar, DI module list.
core/common      Outcome, TimeProvider, IdGenerator, dispatchers. Bottom of the graph.
core/model       Entities, typed ids, enums. Plain immutable data.
core/domain      Business rules: use cases, repository interfaces (ports), pure engines.
core/database    SQLDelight schema (.sq), migrations (.sqm), drivers.
core/data        Repository implementations (adapters), mappers, backup serializer.
core/notifications  Alarm scheduling and notifications (platform adapters of domain ports).
core/designsystem   PrumioTheme tokens + generic Prumio* components. The only Material user.
core/ui          Domain-aware shared UI (TaskRow, labels, formatters), PrumioViewModel base.
core/navigation  Navigation keys and the Navigator contract.
core/testing     Fakes and test helpers shared by all tests.
feature/*        One user-facing feature each (today, agenda, matters, inbox, capture, tasks, search, settings).
tooling/architecture-tests  Konsist tests that enforce this file.
build-logic      Gradle convention plugins and module rules.
docs/            Product, domain, architecture, design system, AI recipes, plan, ADRs.
```

Start at `docs/README.md` for the documentation map.

## 5. How to execute a task from the plan

1. Open `docs/plan/README.md`, find the first task whose status is `todo` and whose
   dependencies are `done` (or the task the owner assigned you).
2. Read the task's **Inputs**. Follow the linked recipe in `docs/ai/recipes/` if there is one.
3. Implement in small commits. Write tests first for business rules.
4. Run the task's **Validation** commands and `./scripts/verify.sh`.
5. Self-review with `docs/ai/review-checklist.md`.
6. Update the task status to `done` in the phase file and the status board in `docs/plan/README.md`,
   add a line to `CHANGELOG.md` under *Unreleased*.
7. Report: what changed, how it was validated, what is left or uncertain.

Full workflow: `docs/ai/workflow.md`. Definition of done: `docs/ai/definition-of-done.md`.

## 6. When to stop and ask the owner

- The task needs a new dependency, a new module, or a change to module rules.
- A business rule is ambiguous, contradicts another, or is missing.
- A test or gate seems wrong.
- The change touches privacy, data deletion, backup format or the database schema of a released version.
- Anything about iOS or other platforms.

Record every architectural decision as an ADR in `docs/adr/` (template included).
