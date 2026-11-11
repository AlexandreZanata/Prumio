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

- `theme/`: `PrumioTheme`, `PrumioColors`, `PrumioTextStyle`, `PrumioSpacing` (docs/design-system/tokens.md).
- `component/`: generic components with no knowledge of tasks, matters or any domain concept.
- `icon/`: `PrumioIcons`, the single icon registry.
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

Screenshot tests arrive in phase 2 (task P2.01). Until then, check `ComponentCatalogPreview` in the IDE.
