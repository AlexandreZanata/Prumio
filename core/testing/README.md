# `:core:testing`

> The single set of test doubles and helpers, shared by every module's tests.

| | |
|---|---|
| **Layer** | Test support (never shipped: only used from test source sets) |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.testing` |
| **May depend on** | `:core:common`, `:core:domain`, `:core:model` |
| **Used by** | test source sets of any module |

## Public API

| Symbol | Purpose |
|---|---|
| `FakeTimeProvider` | Controllable clock and time zone: `advanceBy`, `setLocal`, `moveTo` |
| `FakeIdGenerator` | Deterministic UUID-shaped ids |
| `runViewModelTest { }` | `runTest` with `Dispatchers.Main` replaced, for ViewModel tests |

Phase 1 adds `Fake<Aggregate>Repository` for every domain port and entity builders
(`aTask(title = …)`) used across tests.

## Rules

- Fakes implement domain ports in memory and behave like the real thing (including `Flow` updates);
  prefer them over mocking libraries (none are used in Prumio).
- Classes named `Fake*` live only here (architecture test enforces it).
- Builders give every field a sensible default so tests only state what matters.
