# Design system

Prumio should feel **calm, clear and trustworthy** — a quiet assistant, not a productivity
dashboard. The design system makes that feeling consistent by construction: every pixel comes from
`:core:designsystem` tokens and components, so a change in one place changes the whole app.

## Principles

1. **Calm over loud.** Soft, natural palette (deep sage green); color is used for meaning (status),
   not decoration. No red badges for productivity, no confetti.
2. **Clarity over density.** Generous spacing on a 4 dp grid, one primary action per screen,
   readable type sizes.
3. **Consistency over novelty.** One component per concept; one look per domain object (`TaskRow`
   everywhere a task appears).
4. **Accessible by default.** Contrast, touch targets and labels are built into components
   (docs/architecture/accessibility.md).
5. **Light and dark are equal citizens.** Every token has both values; previews and screenshots
   cover both.

## Structure

| Part | Where | Doc |
|---|---|---|
| Tokens: color, typography, spacing, shape | `core/designsystem/.../theme/` | [tokens.md](tokens.md) |
| Generic components (`Prumio*`) | `core/designsystem/.../component/` | [components.md](components.md) |
| Icons | `core/designsystem/.../icon/PrumioIcons.kt` | [components.md](components.md#tokens-and-registries-not-composables) |
| Domain components (`TaskRow`, `MatterCard`…) | `core/ui/.../component/` (phase 2) | [components.md](components.md#domain-aware-components-and-helpers--coreui) |

## Rules (enforced where marked 🔒)

- 🔒 Only `:core:designsystem` imports Material 3; everything else uses `Prumio*` components and
  `PrumioTheme` tokens.
- 🔒 Every public composable of `:core:designsystem` and `:core:ui` is listed in the catalog.
- 🔒 Feature modules have no public composables.
- No literal colors, font sizes or dp values in features — use tokens (`PrumioTheme.spacing.lg`).
  Layout-only sizes (an image ratio) are acceptable when no token fits; prefer adding a token.
- Components take text from callers; they never contain user-facing strings.

## Dynamic color

Android 12+ "Material You" dynamic color is **off**: Prumio keeps its own calm identity and
predictable status colors. Revisit only with an ADR.
