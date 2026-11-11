# Recipe: add a feature module

Needs a plan task or owner approval (new modules change the architecture map).

## Steps (example: `:feature:review`)

1. `settings.gradle.kts`: `include(":feature:review")` (alphabetical within features).
2. `feature/review/build.gradle.kts`:
   ```kotlin
   plugins {
       alias(libs.plugins.prumio.feature)
   }
   ```
3. Sources in `feature/review/src/commonMain/kotlin/app/prumio/feature/review/`:
   - `ReviewNavigation.kt` — `fun EntryProviderScope<NavKey>.reviewEntries(navigator: Navigator) { entry<ReviewKey> { ReviewRoute(navigator) } }`
   - `ReviewFeatureModule.kt` — `val reviewFeatureModule = module { viewModelOf(::ReviewViewModel) }`
   - The screen files ([add-screen.md](add-screen.md)).
4. Strings: `feature/review/src/commonMain/composeResources/values/strings.xml` and `values-pt-rBR/strings.xml`.
5. Navigation key in `:core:navigation` ([add-navigation-key.md](add-navigation-key.md)).
6. Wire into the shell:
   - `app/shell/build.gradle.kts`: `implementation(projects.feature.review)`
   - `app/shell/.../navigation/PrumioNavDisplay.kt`: `reviewEntries(navigator)`
   - `app/shell/.../di/PrumioModules.kt`: `reviewFeatureModule`
   - root `build.gradle.kts`: `kover(projects.feature.review)` (merged coverage report)
7. Contract docs:
   - `feature/review/README.md` (copy another feature's README and adapt every row).
   - `feature/review/CLAUDE.md` containing `@README.md`.
   - Add `` `:feature:review` `` to the catalog in [modules.md](../../architecture/modules.md).
8. Tests in `feature/review/src/commonTest`.

`ModuleStructureTest` fails if steps 6–7 are incomplete; `ModuleRules` fails if the feature depends
on something it should not.

## Validation

```bash
./gradlew :feature:review:testAndroidHostTest :tooling:architecture-tests:test :app:android:assembleDebug
```
