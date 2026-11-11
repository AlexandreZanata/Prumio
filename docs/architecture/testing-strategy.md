# Testing strategy

Tests are the safety net that lets AI agents change code confidently. They must be fast,
deterministic, named after what they prove, and runnable with one command (`./scripts/verify.sh`).

## Pyramid

| Level | What | Where | Tools | Phase |
|---|---|---|---|---|
| Domain unit | Use cases, policies, engines (recurrence, planner, search) | `core/domain/src/commonTest` | kotlin-test, coroutines-test, Turbine, fakes | 0+ |
| ViewModel | State and effects for each action | `feature/*/src/commonTest` | `runViewModelTest`, Turbine, fakes | 0+ |
| Data integration | Repositories and mappers on real SQLite | `core/data/src/androidHostTest` | `JdbcSqliteDriver` in memory | 1 |
| Database | Queries, constraints, migrations | `core/database/src/androidHostTest`, `verifySqlDelightMigration` | SQLDelight | 0+ |
| DI graph | Every binding resolvable | `app/shell/src/androidHostTest` | Koin `verify()` | 0 |
| Navigation | Back stack rules | `app/shell/src/commonTest` | kotlin-test | 0 |
| Architecture | Layering, naming, catalog, traceability, privacy | `tooling/architecture-tests` | Konsist | 0 |
| Screenshot | Components and screens, light/dark, 200 % font | `*/src/androidHostTest` of `core/designsystem`, `core/ui`, features | Roborazzi + Robolectric (`:core:screenshot-testing`) | 2 |
| Contrast | WCAG ratio of every text/background pair, both themes | `core/designsystem/src/commonTest` (`PrumioColorsContrastTest`) | kotlin-test | 2 |
| Instrumented | Alarms fire, notifications, receivers, SAF | `core/notifications/src/androidDeviceTest`, `app/android/src/androidTest` | AndroidX Test on emulator | 4 |
| Manual QA | Real devices, reboot, Doze, permissions, TalkBack | docs/plan/qa/device-qa-checklist.md | humans | 3+ |

## Rules

1. **Business rules are tested in the domain**, at the lowest level that can prove them, with test
   names starting with the rule id (`BR-TASK-004 …`). `BusinessRuleTraceabilityTest` requires every
   `implemented` rule to be cited by a test.
2. **Fakes, not mocks.** `:core:testing` provides in-memory fakes of every port; no mocking library.
3. **Deterministic time and ids**: `FakeTimeProvider`, `FakeIdGenerator`. No `delay`-based waiting;
   use the test dispatcher's virtual time.
4. **One behavior per test**, arrange-act-assert, readable without comments.
5. **Builders** (`aTask(...)`) in `:core:testing` keep tests short and resistant to model changes.
6. **Never weaken a test to make a change pass.** If a test is wrong, fix it in a separate,
   explained commit.
7. Flaky tests are bugs: fix or quarantine with an issue, never ignore silently.

## Screenshot tests

Visual regression tests run on the JVM (Robolectric, native graphics) in every module's
`androidHostTest`, so they are part of `./scripts/verify.sh` and CI. No emulator is involved.

```kotlin
class MyComponentScreenshotTest : ScreenshotTest() {      // :core:screenshot-testing
    @Test
    fun states() = screenshot("states") {                  // one PNG per variant
        Column { MyComponent(/* typical */); MyComponent(/* long text */); MyComponent(/* error */) }
    }
}
```

- `screenshot(name) { }` renders the content three times — `_light`, `_dark` and `_font200`
  (200 % font scale) — on a 360 dp wide phone, inside `PrumioTheme`, and compares each with the
  baseline in `src/androidHostTest/screenshots/<TestClass>/`. A difference above 0.5 % of pixels
  fails the test and writes a `_compare.png` diff under `build/outputs/roborazzi`.
- Group the states of one component (or a family) per test: typical, long text, empty/error,
  overdue/waiting. Use builders from `:core:testing` and fixed dates (never "now").
- Dialogs and bottom sheets open their own window and are not captured; check them on the phone.
- **Changing visuals on purpose**: run
  `./gradlew :core:ui:testAndroidHostTest -PrecordScreenshots` (or the module you changed), open
  the changed PNGs in the diff and confirm each change is intended before committing them. Never
  re-record to silence a failure you did not expect.
- Baselines depend on the Robolectric SDK and fonts pinned in `ScreenshotTest`; bumping
  Robolectric or Compose may require a deliberate re-record in its own commit.

## Coverage

Kover instruments every shared module (`prumio.kmp.library` → `CoverageRules.kt` in build-logic).

| Module | Minimum line coverage | Enforced by |
|---|---|---|
| `:core:domain` | 90 % | `:core:domain:koverVerify` (part of `check`) |
| `:core:data` | 80 % | `:core:data:koverVerify` (part of `check`) |
| Features (ViewModels) | 80 % — enforced from phase 3 | planned |

- Excluded (wiring or generated, not logic): `app.prumio.*.di.*`, generated `*.resources.*`,
  `ComposableSingletons*`.
- Merged HTML report of all shared modules: `./gradlew koverHtmlReport` →
  `build/reports/kover/html/index.html`. Per module: `./gradlew :core:domain:koverLog`.
- Thresholds live only in `CoverageRules.kt`. Raising one is welcome; lowering one needs the
  owner's approval. Never exclude real logic to pass the gate — write the test.

Coverage is a signal, not the goal: rules must be proven by intent-named tests.

## Commands

```bash
./scripts/verify.sh                                  # everything (what CI runs)
./gradlew :core:domain:testAndroidHostTest           # one module (KMP host tests)
./gradlew :tooling:architecture-tests:test           # architecture
./gradlew :core:ui:testAndroidHostTest -PrecordScreenshots   # re-record screenshots (review the diff!)
./gradlew :core:database:verifySqlDelightMigration   # migrations
./gradlew koverHtmlReport                            # merged coverage report
```
