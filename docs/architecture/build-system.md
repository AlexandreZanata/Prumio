# Build system

Gradle (Kotlin DSL) with convention plugins, a version catalog and configuration cache.

## Toolchain (November 2026)

| Tool | Version | Where |
|---|---|---|
| Gradle | 9.8.2 | `gradle/wrapper/gradle-wrapper.properties` (checksum-verified) |
| JDK | 21 | local / CI `setup-java` |
| Android Gradle Plugin | 9.5.1 | catalog `agp` |
| Kotlin | 2.4.21 | catalog `kotlin` |
| Compose Multiplatform | 1.12.2 (Material 3 1.9.0) | catalog |
| compileSdk / targetSdk / minSdk | 37 / 36 / 26 | catalog `android-*` |
| JVM bytecode | 17 | catalog `jvm-target` |

`targetSdk` is raised to the latest API after reviewing behavior changes (task P7.01).

## Layout

```
settings.gradle.kts         modules list (include), repositories, typesafe project accessors
build.gradle.kts            plugins declared once (apply false) + Spotless formatting
gradle/libs.versions.toml   every version and dependency
gradle.properties           JVM args, caching, configuration cache
build-logic/convention/     convention plugins and module rules
```

## Convention plugins

| Plugin id | Applies to | Does |
|---|---|---|
| `prumio.kmp.library` | every shared module | KMP + Android KMP library plugin, Android target (namespace from path, SDK levels, host tests), coroutines, test deps, **module rules**, **Kover coverage** |
| `prumio.kmp.compose` | modules with Compose UI/resources | + Compose Multiplatform, compiler plugin, Android resources, Compose deps, internal `Res` in `<package>.resources` |
| `prumio.feature` | `:feature:*` | + every allowed core module, lifecycle/navigation3, Koin; `:core:testing` for tests |
| `prumio.android.application` | `:app:android` | Android app config, Compose, **privacy gate** (`verify<Variant>NoNetworkPermission` in `check`) |
| `prumio.jvm.library` | `:tooling:*` | Kotlin JVM |

A module's `build.gradle.kts` applies exactly one of them plus only module-specific additions.
Feature build files are three lines.

## Module rules

`ModuleRules.kt` checks every project dependency as it is declared and fails configuration with an
explanation when it breaks [modules.md](modules.md#dependency-rules).

## Quality gates

| Gate | Task | Fails when |
|---|---|---|
| Formatting | `spotlessCheck` (ktlint + Compose rules) | Code is not formatted (`spotlessApply` fixes most) |
| Unit tests | `check` → `testAndroidHostTest` per module | Any test fails |
| Architecture | `check` → `:tooling:architecture-tests:test` | Any rule in AGENTS.md is broken |
| Coverage | `check` → `koverVerify` per module | Line coverage below `CoverageRules.kt` thresholds (domain 90 %, data 80 %) |
| Migrations | `check` → `verifySqlDelightMigration` | Schema changed without migration/snapshot |
| Privacy | `check` → `verifyDebugNoNetworkPermission`, `verifyReleaseNoNetworkPermission` | Merged manifest requests `INTERNET` |
| Android lint | `check` → `lint` (app) | Lint errors |
| Assemble | `:app:android:assembleDebug` | App does not build |

`./scripts/verify.sh` runs all of them; CI runs the same script.

## CI

`.github/workflows/ci.yml` on every push and pull request: JDK 21, Gradle cache, `./scripts/verify.sh`,
upload test reports on failure and the debug APK as an artifact. Dependabot proposes weekly updates
for Gradle dependencies and GitHub Actions; toolchain upgrades are done together (AGP + Kotlin +
Compose) by a plan task, never piecemeal.

## Performance tips

- Configuration cache and build cache are on. Keep plugins and build logic compatible (no
  `project` access at execution time, no `afterEvaluate`).
- Run a single module's tests while iterating; run `verify.sh` before committing.
