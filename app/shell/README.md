# `:app:shell`

> The shared root of the app: `PrumioApp()`, the navigation host, the bottom bar, the
> "Add anything" button and the list of all DI modules. Identical on every platform.

| | |
|---|---|
| **Layer** | Application assembly |
| **Plugin** | `prumio.kmp.compose` |
| **Package** | `app.prumio.app.shell` |
| **May depend on** | `:core:*`, `:feature:*` |
| **Used by** | `:app:android` (and a future iOS app) |

## Public API

| Symbol | File | Contract |
|---|---|---|
| `PrumioApp(modifier)` | `PrumioApp.kt` | Root composable. Platform entry points only call this |
| `prumioModules` | `di/PrumioModules.kt` | Every Koin module of the app |

Internal: `PrumioNavigator` (the only `Navigator`), `PrumioNavDisplay` (Navigation 3 host),
`PrumioBottomBar` + `TopLevelDestination` (tab order), `navSavedStateConfiguration`.

## Rules

- When a feature module is added, register **both** its `xxxEntries(navigator)` call in
  `PrumioNavDisplay.kt` and its `xxxFeatureModule` in `PrumioModules.kt` (architecture test enforces it).
- Back stack rules live only in `PrumioNavigator` and are unit tested
  (docs/architecture/navigation.md#back-stack-rules).
- No feature UI here — only the chrome around features.

## Tests

`./gradlew :app:shell:testAndroidHostTest`:
- `PrumioNavigatorTest` — back stack rules.
- `KoinModulesTest` — every Koin definition can be resolved (catches missing bindings).
