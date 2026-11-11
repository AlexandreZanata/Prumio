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
| `FakeRepositories` / `FakeStore` | One coherent set of in-memory fakes (`inbox`, `tasks`, `appointments`, `matters`, `search`, `settings`) over shared state, so cross-aggregate effects behave like SQLite |
| `Fake<Aggregate>Repository` | In-memory implementation of each domain port, same ordering and `Flow` emissions as the adapter |
| `TestDispatcherProvider` | Runs repository work on a test dispatcher |
| `anInboxItem()`, `aTask()`, `anAppointment()`, `timedAt()`, `allDay()`, `aMatter()`, `aMatterEntry()`, `TestInstant` | Builders with defaults |
| `assertSuccess()`, `assertFailure()`, `assertValidation(ruleId)`, `assertInvalidTransition(ruleId)`, `assertNotFound()` | Outcome assertions |

## Rules

- Fakes implement domain ports in memory and behave like the real thing (including `Flow` updates);
  prefer them over mocking libraries (none are used in Prumio).
- Classes named `Fake*` live only here (architecture test enforces it).
- Builders give every field a sensible default so tests only state what matters.
