# `:tooling:architecture-tests`

> Executable architecture: tests that read the whole repository (sources, build files, docs)
> and fail with an actionable list when a rule from AGENTS.md is broken.

| | |
|---|---|
| **Layer** | Tooling (JVM, never shipped) |
| **Plugin** | `prumio.jvm.library` |
| **Libraries** | [Konsist](https://docs.konsist.lemonappdev.com/), kotlin-test, JUnit 4 |
| **May depend on** | no project modules (it inspects files, it does not link them) |

## What is checked

| Test class | Rules |
|---|---|
| `LayerDependencyTest` | Features import only allowed layers; only the design system imports Material; common/model/domain are pure Kotlin; `commonMain` is platform neutral; Koin only in DI files and Routes |
| `NamingConventionTest` | Use cases (location, single `operator fun invoke`); ViewModels (extend `PrumioViewModel`, internal, in features; exact ShellViewModel exception in ADR-0021); screen file set; repositories (ports in domain, adapters in data); fakes in `:core:testing` |
| `DesignSystemTest` | `Prumio*` prefix; every shared composable listed in docs/design-system/components.md; no public composables in features |
| `CodeHygieneTest` | No `GlobalScope`, `runBlocking`, `println`, `!!`, direct clocks/UUIDs; no `removeFirst/removeLast`; files ≤ 400 lines; package mirrors module and folders |
| `ModuleStructureTest` | Every module has README.md + CLAUDE.md, is listed in docs/architecture/modules.md and wired in the shell; every navigation key is serializable |
| `BusinessRuleTraceabilityTest` | Rule ids unique with valid status; code cites only existing rules; every `implemented` rule is cited by a test |
| `PrivacyTest` | Manifest removes `INTERNET` (BR-PRIV-001); no tracking/crash/ads/account SDKs in build files (BR-PRIV-002) |
| `StringResourcesTest` | Same string keys in English and pt-BR; no Android-style escaping in Compose resources |
| `DocumentationLinksTest` | Every relative link and heading anchor in every Markdown file resolves |

Module-to-module dependencies are enforced earlier, at Gradle configuration time, by
`build-logic/convention/src/main/kotlin/app/prumio/buildlogic/ModuleRules.kt`.

## Run

```bash
./gradlew :tooling:architecture-tests:test
```

## Changing a rule

Rules encode decisions. To change one: update the doc that states it (AGENTS.md or the linked
architecture doc), write or update an ADR, then change the test — in the same commit.
Never relax a rule just to make a change pass.
