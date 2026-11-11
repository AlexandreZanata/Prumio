# Design tokens

The only place where visual constants are defined. Code: `core/designsystem/src/commonMain/kotlin/app/prumio/core/designsystem/theme/`.

## Color

Access: `PrumioTheme.colors.<token>`. Defined in `PrumioColors.kt` for light and dark; Material 3's
color scheme is derived from these values.

| Token | Light | Dark | Use |
|---|---|---|---|
| `primary` | `#2E6B5E` | `#96D3C3` | Primary actions, selected states |
| `onPrimary` | `#FFFFFF` | `#00382E` | Content on primary |
| `primaryContainer` | `#B2EFDF` | `#105145` | FAB, highlighted surfaces |
| `onPrimaryContainer` | `#00201A` | `#B2EFDF` | Content on primaryContainer |
| `secondary` | `#4B635C` | `#B1CCC3` | Secondary emphasis |
| `secondaryContainer` | `#CDE8DF` | `#344C45` | Chips, tonal surfaces |
| `background` / `surface` | `#F7FAF8` | `#101413` | Screen background |
| `onBackground` / `onSurface` | `#181C1B` | `#DFE4E1` | Main text |
| `surfaceVariant` | `#DBE5E0` | `#3F4945` | Cards |
| `onSurfaceVariant` | `#3F4945` | `#BFC9C4` | Secondary text |
| `outline` / `outlineVariant` | `#6F7975` / `#BFC9C4` | `#89938F` / `#3F4945` | Borders, dividers, empty-state icons |
| `error` / `onError` | `#BA1A1A` / `#FFFFFF` | `#FFB4AB` / `#690005` | Destructive actions, errors |
| `statusOpen` | `#2B5F8E` | `#9CCBFA` | Matter Open |
| `statusWaiting` | `#8A5A00` | `#FFB951` | Matter Waiting |
| `statusResolved` | `#2E6B3A` | `#94D69B` | Matter Resolved, completed |
| `statusOverdue` | `#B3261E` | `#FFB4AB` | Overdue labels (always with text) |

The window background in `app/android/src/main/res/values*/colors.xml` mirrors `background`.

## Typography

Access: `PrumioText(style = PrumioTextStyle.X)`. Mapped onto the Material 3 type scale in
`PrumioTypography.kt`; system font (no bundled fonts, smaller APK).

| Style | Based on | Use |
|---|---|---|
| `Display` | displaySmall, semibold | Summary counts |
| `Headline` | headlineMedium, semibold | Greeting, hero titles |
| `Title` | titleLarge | Top bar and card titles |
| `Subtitle` | titleMedium, semibold | Section headers |
| `Body` | bodyLarge | Default text |
| `BodyStrong` | bodyLarge, medium | List item titles |
| `Label` | labelLarge | Buttons, chips |
| `Caption` | bodySmall | Metadata (times, ages) |
| `Overline` | labelMedium, semibold, +1 sp tracking, uppercase | Date line on Today |

## Spacing

Access: `PrumioTheme.spacing.<token>` (4 dp grid).

| Token | dp | Typical use |
|---|---|---|
| `xxs` | 2 | Hairline gaps |
| `xs` | 4 | Icon-to-text |
| `sm` | 8 | Between related elements |
| `md` | 12 | Inside list rows |
| `lg` | 16 | Screen padding (`screenHorizontal`), card padding |
| `xl` | 24 | Between sections |
| `xxl` | 32 | Large separations |
| `xxxl` | 48 | Empty-state icons; `minTouchTarget` (48) |

## Shape

Material shapes: extraSmall 4, small 8, medium 12, large 16, extraLarge 28 dp (rounded corners).
Cards use medium; dialogs and sheets extraLarge.

## Changing tokens

Tokens are global: a change affects every screen. Update this page and both themes, check light,
dark and contrast (≥ 4.5:1 for text), and re-record screenshots (from phase 2).
