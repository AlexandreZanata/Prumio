# Conventions

Predictability beats cleverness. When every file of a kind looks the same, an agent can find,
read and change it without guessing. Items marked 🔒 are enforced by tests or the build.

## Packages and files

- 🔒 Package = `app.prumio.` + module path + sub-package, mirroring folders.
  `:core:domain` → `app.prumio.core.domain.task`; `:feature:today` → `app.prumio.feature.today`.
  Hyphens are dropped (`:tooling:architecture-tests` → `architecturetests`).
- 🔒 Source files ≤ 400 lines. Split by responsibility, not arbitrarily.
- One public top-level class or component per file, file named after it. Small related
  declarations (a sealed hierarchy, a contract) may share a file named after the main concept.
- Platform implementations of an `expect` declaration: `<Name>.android.kt` (later `<Name>.ios.kt`).

## Naming

| Kind | Pattern | Example |
|---|---|---|
| Use case | `<Verb><Noun>UseCase` with `operator fun invoke` 🔒 | `CompleteTaskUseCase` |
| Observation use case | `Observe<Noun>UseCase` returning `Flow` | `ObserveTodayUseCase` |
| Repository port (domain) | `<Aggregate>Repository` 🔒 | `TaskRepository` |
| Repository adapter (data) | `SqlDelight<Aggregate>Repository` 🔒 | `SqlDelightTaskRepository` |
| Other ports | role noun | `AlarmScheduler`, `NotificationPresenter`, `BackupFileWriter` |
| Pure domain policy | `<Concept>Policy` / engine noun | `MatterAttentionPolicy`, `ReminderPlanner` |
| Screen files | `XxxContract.kt`, `XxxViewModel.kt`, `XxxRoute.kt`, `XxxScreen.kt` 🔒 | `TodayScreen.kt` |
| UI state / action / effect | `XxxUiState`, `XxxAction`, `XxxEffect` | `TodayUiState` |
| Feature public API | `xxxEntries(navigator)`, `xxxFeatureModule` 🔒 | `todayEntries` |
| Navigation key | `<Destination>Key` | `MatterDetailKey` |
| Design system component | `Prumio<Name>` 🔒 | `PrumioButton` |
| Domain UI component | `<Entity><Role>` | `TaskRow`, `MatterCard` |
| Koin module | `<module>Module` / `<feature>FeatureModule` | `coreDomainModule` |
| Fake | `Fake<Port>` in `:core:testing` 🔒 | `FakeTaskRepository` |
| Test builder | `a<Entity>(…)` | `aTask(title = "Pay rent")` |
| Test | `` `BR-XXX-NNN what is proven` `` when proving a rule | `` `BR-TASK-004 date-only task is overdue the next day` `` |
| String resource | `<feature>_<screen?>_<meaning>` snake_case | `today_empty_title`, `action_save` |
| SQL table / column | singular `snake_case` | `matter_entry.occurred_on` |

## Kotlin style

- Formatting: ktlint (Android Studio style) + Compose rules, via `./gradlew spotlessApply` 🔒.
- Immutability: `val`, immutable collections in state and models. Data classes for state.
- Visibility: everything `internal` or `private` unless another module needs it. In features,
  only `xxxEntries` and `xxxFeatureModule` are public 🔒.
- Exhaustive `when` over sealed types; no `else` branch when all cases are known.
- 🔒 No `!!`, `GlobalScope`, `runBlocking`, `println`, `Clock.System`, `TimeZone.currentSystemDefault()`
  or `Uuid.random()` outside their single sanctioned place.
- 🔒 No `List.removeFirst()` / `removeLast()` (crash on Android 14 and older): use `removeAt`.
- No `java.*` in common code 🔒; no `java.time` anywhere except Android `actual` formatters.
- Prefer named arguments for calls with more than two parameters or any boolean literal.
- KDoc on every public declaration of core modules: what it guarantees, which rule it enforces.
- Comments explain *why*; code explains *what*. No commented-out code.

## Coroutines

- Domain and data expose `suspend` functions and `Flow`; they never launch coroutines on their own
  except through an injected scope (reconciliation, phase 4).
- ViewModels launch in `viewModelScope`. Collect UI state with `collectAsStateWithLifecycle()`.
- Storage work runs on `DispatcherProvider.io` (inside repositories, never in ViewModels).

## Dependencies

- Every version lives in `gradle/libs.versions.toml` 🔒 (no versions in module build files).
- Adding a library requires an ADR or owner approval (AGENTS.md §6). Prefer KMP libraries.
- 🔒 Never add networking, analytics, crash reporting, ads or account SDKs (BR-PRIV-002).

## Commits and branches

- Conventional commits: `feat(today): …`, `fix(reminders): …`, `docs(plan): …`, `test(domain): …`,
  `build: …`, `refactor(ui): …`. Reference plan tasks and rules: `feat(tasks): complete task (P1.11, BR-TASK-003)`.
- One plan task per branch/PR when possible: `task/p1-11-complete-task`.
- Never commit secrets, keystores or `local.properties` (.gitignore covers them).
