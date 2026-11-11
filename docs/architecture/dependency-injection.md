# Dependency injection

Koin (multiplatform, no code generation). Chosen for readability: the whole object graph is a few
plain Kotlin files an agent can read in one pass (ADR-0004).

## Where definitions live

| Module | Koin module | Contents |
|---|---|---|
| `:core:common` | `coreCommonModule` | `TimeProvider`, `IdGenerator`, `DispatcherProvider` (singletons) |
| `:core:database` | `coreDatabaseModule` | `SqlDriver` (platform, via `databaseDriverModule`), `PrumioDatabase` |
| `:core:data` | `coreDataModule` | Every repository/store bound to its domain port |
| `:core:domain` | `coreDomainModule` | Every use case and policy (`factoryOf`) |
| `:core:notifications` | `coreNotificationsModule` | `AlarmScheduler`, `NotificationPresenter`, reconciliation trigger |
| `:feature:x` | `xFeatureModule` | The feature's ViewModels (`viewModelOf`) |
| `:app:shell` | `prumioModules` | The list of all modules above |

`:app:android` starts Koin once: `startKoin { androidContext(app); modules(prumioModules) }`.

## Rules

1. 🔒 Koin imports only in `di/` packages, `*FeatureModule.kt`, `*Route.kt` (for `koinViewModel()`)
   and `:app:android`. Business classes use constructor injection and never call `get()` or `inject()`.
2. Scope: stateless things (use cases, policies) → `factoryOf`; stateful or expensive (database,
   repositories, providers) → `singleOf`/`single`; ViewModels → `viewModelOf`.
3. Bind to interfaces: `singleOf(::SqlDelightTaskRepository) bind TaskRepository::class`.
4. Platform-specific bindings: `expect val xxxPlatformModule: Module` in `commonMain`,
   `actual` in `androidMain` (see `databaseDriverModule`).
5. A new Gradle module that declares a Koin module must be added to `prumioModules` 🔒
   (`ModuleStructureTest` for features, `KoinModulesTest` for missing bindings).

## Verification

`KoinModulesTest` (`:app:shell`, host test) calls Koin's `verify()` on all modules: every
constructor parameter of every definition must be resolvable. A missing binding fails `check`,
not the user's device.

## Arguments for ViewModels

```kotlin
// FeatureModule
viewModel { params -> TaskDetailViewModel(taskId = TaskId(params.get()), observeTask = get(), completeTask = get()) }
// Route
val viewModel: TaskDetailViewModel = koinViewModel { parametersOf(key.taskId) }
```
