# `:core:common`

> Tiny, dependency-free building blocks used by every other module.

| | |
|---|---|
| **Layer** | Foundation (bottom of the graph) |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.common` |
| **May depend on** | nothing (only kotlinx-coroutines, kotlinx-datetime, Koin core) |
| **Used by** | every module |

## Responsibility

Provide cross-cutting primitives that have no business meaning: results, time, ids, dispatchers.
If it mentions a Task, Matter or any product concept, it does **not** belong here (→ `:core:model` / `:core:domain`).

## Public API

| Symbol | File | Contract |
|---|---|---|
| `Outcome<T, E>` + `map`, `flatMap`, `onSuccess`, `onFailure`, `asSuccess`, `asFailure` | `Outcome.kt` | Expected failures are values, not exceptions (docs/architecture/error-handling.md) |
| `TimeProvider`, `localNow()`, `today()`, `SystemTimeProvider` | `time/TimeProvider.kt` | The only source of "now" and of the device time zone |
| `IdGenerator`, `UuidIdGenerator` | `id/IdGenerator.kt` | UUID strings for new entities (ADR-0011) |
| `DispatcherProvider`, `DefaultDispatcherProvider` | `coroutines/DispatcherProvider.kt` | `io` for storage, `default` for CPU work |
| `coreCommonModule` | `di/CoreCommonModule.kt` | Koin bindings of the three providers above |

## Rules

- No Android, Compose, SQLDelight or `java.*` imports (architecture tests enforce it).
- `Clock.System`, `TimeZone.currentSystemDefault()` and `Uuid.random()` may only appear in this module's providers.
- Adding something here requires that at least two unrelated modules need it.

## Tests

`./gradlew :core:common:testAndroidHostTest` — tests live in `src/commonTest`.
