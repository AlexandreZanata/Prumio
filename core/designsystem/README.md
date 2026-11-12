# `:core:designsystem`

> Prumio's visual language: theme tokens and generic `Prumio*` components. The **only** module
> allowed to use Material 3 directly.

| | |
|---|---|
| **Layer** | UI foundation |
| **Plugin** | `prumio.kmp.compose` |
| **Package** | `app.prumio.core.designsystem` |
| **May depend on** | `:core:common` |
| **Used by** | `:core:ui`, `:feature:*`, `:app:shell` |

## Responsibility

- `theme/`: `PrumioTheme`, `PrumioColors`, `PrumioTextStyle`, `PrumioSpacing`, `PrumioShape`,
  `PrumioElevation`, `PrumioMotion` (docs/design-system/tokens.md).
- `component/`: generic components with no knowledge of tasks, matters or any domain concept.
- `icon/`: `PrumioIcons`, the single icon registry (Material Symbols vectors in
  `composeResources/drawable/ic_*.xml`, Apache-2.0, see NOTICE).
- `component/PrumioLogo.kt` + `composeResources/drawable/prumio_mark.xml`: the brand mark
  (generated; rules in [docs/brand/README.md](../../docs/brand/README.md)). Prumio Green `#88C29A`
  is the accent of every token.
- `preview/`: the visual component catalog preview.

The full catalog with signatures and usage is [docs/design-system/components.md](../../docs/design-system/components.md).
**Search it before writing any UI.**

## Rules

- Material 3 is an `implementation` dependency: it does not leak. Everything public is expressed
  with Prumio types (`PrumioColors`, `PrumioTextStyle`, `PrumioButtonVariant`) or Compose foundation types.
- Every public composable: name starts with `Prumio`, has a `modifier: Modifier = Modifier`
  parameter, lives in its own file named after it, and is listed in the catalog (tests enforce it).
- No hard-coded colors, sizes or font sizes outside `theme/`.
- No user-facing strings: components receive text from callers.
- Accessibility: 48 dp touch targets, content descriptions required for actionable icons.

## How to extend

New component → [docs/ai/recipes/add-design-system-component.md](../../docs/ai/recipes/add-design-system-component.md).

## Tests

- `./gradlew :core:designsystem:testAndroidHostTest` — `FoundationScreenshotTest` (type, buttons) and
  `ComponentScreenshotTest` (every component, light/dark/200 % font; baselines in
  `src/androidHostTest/screenshots/`), `PrumioColorsContrastTest` (WCAG), `PrumioBadgeTest`.
- Re-record after an intended visual change: add `-PrecordScreenshots`, then review the PNG diff
  (docs/architecture/testing-strategy.md#screenshot-tests).

Phase 3 extends PrumioText with an AnnotatedString overload, PrumioNavigationBarItem with an
optional badgeCount and PrumioMessenger with app-scope post. Action messages stay 5 seconds
(or the longer accessibility timeout); these contracts are in the component catalog.
