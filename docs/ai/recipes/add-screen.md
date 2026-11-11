# Recipe: add a screen to a feature

Reference implementation: `feature/today/src/commonMain/kotlin/app/prumio/feature/today/`.
Pattern: [presentation-pattern.md](../../architecture/presentation-pattern.md).

## Steps

1. Read the screen spec in [screens.md](../../product/screens.md) and its rules. Write the spec there
   first if it is missing.
2. Add the navigation key if needed ([add-navigation-key.md](add-navigation-key.md)).
3. Create the four files in `feature/<feature>/src/commonMain/kotlin/app/prumio/feature/<feature>/`
   (sub-package allowed for multi-screen features, e.g. `.detail`):
   - `XxxContract.kt` — `XxxUiState` (immutable, defaults), `XxxAction` (sealed), `XxxEffect` (sealed).
   - `XxxViewModel.kt` — `internal class XxxViewModel(useCases…) : PrumioViewModel<…>(XxxUiState())`.
   - `XxxRoute.kt` — collects state with `collectAsStateWithLifecycle()`, effects with `CollectEffects`,
     gets the ViewModel with `koinViewModel()` as a default parameter.
   - `XxxScreen.kt` — stateless; `PrumioScaffold` + `Prumio*`/`:core:ui` components; private `@Preview`s.
4. Register:
   - `XxxNavigation.kt`: `entry<XxxKey> { key -> XxxRoute(navigator = navigator, …) }` inside the
     feature's `xxxEntries` function.
   - `XxxFeatureModule.kt`: `viewModelOf(::XxxViewModel)` (or `viewModel { params -> … }` for arguments).
5. Strings: shared ones from `:core:ui`; new ones in the feature's `composeResources/values` **and**
   `values-pt-rBR` ([add-string.md](add-string.md)).
6. Missing UI piece? Add it to the design system or `:core:ui` first
   ([add-design-system-component.md](add-design-system-component.md)). Never build it privately if
   another feature could need it.
7. Tests: `feature/<feature>/src/commonTest/.../XxxViewModelTest.kt` — initial state, each action,
   each effect, error mapping; rule ids in names.

## Validation

```bash
./gradlew :feature:<feature>:testAndroidHostTest :tooling:architecture-tests:test
./gradlew :app:android:installDebug   # then open the screen on a device/emulator
```
