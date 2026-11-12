# Design tokens

The only place where visual constants are defined. Code: `core/designsystem/src/commonMain/kotlin/app/prumio/core/designsystem/theme/`.

## Color

Access: `PrumioTheme.colors.<token>`. Defined in `PrumioColors.kt` for light and dark; Material 3's
color scheme is derived from these values (`toMaterialColorScheme`), so no off-brand Material color
can leak into components. Contrast of every text pair is enforced by `PrumioColorsContrastTest`
(see [Contrast](#contrast)).

### Brand

**Prumio Green `#88C29A`** — the logo color ([brand guide](../brand/README.md)) and the main accent
of the app. It is a *surface* color: it fills the FAB, the selected tab indicator's family, brand
surfaces and highlights. White text on it fails contrast (2.05:1), so content on it uses
`onPrimaryContainer` (`#0C2715`). Interactive text and icons in the light theme use the deeper
`primary` tone of the same hue.

```
Prumio Green tonal scale (hue ≈ 138°)
#0C2715  #2C5A3B  #2E6E47  [#88C29A]  #C3EBCC  #D0E8D5  #F6FAF6
 on-container  container(dark)  primary(light)  BRAND  on-container(dark)  secondaryContainer  background
```

### Tokens

| Token | Light | Dark | Use |
|---|---|---|---|
| `brand` | `#88C29A` | `#88C29A` | Logo, brand surfaces. Same in both themes |
| `onBrand` | `#FFFFFF` | `#FFFFFF` | Only the logo mark on `brand` |
| `primary` | `#2E6E47` | `#88C29A` | Text buttons, links, active icons, focused fields |
| `onPrimary` | `#FFFFFF` | `#0C2715` | Content on `primary` (filled buttons) |
| `primaryContainer` | `#88C29A` | `#2C5A3B` | FAB, highlighted cards, brand accents |
| `onPrimaryContainer` | `#0C2715` | `#C3EBCC` | Content on `primaryContainer` |
| `secondary` / `onSecondary` | `#4E6354` / `#FFFFFF` | `#B4CCB8` / `#203527` | Secondary emphasis |
| `secondaryContainer` / `onSecondaryContainer` | `#D0E8D5` / `#0C1F12` | `#364B3C` / `#D0E8D5` | Selected tab indicator, chips, tonal surfaces |
| `background` = `surface` | `#F6FAF6` | `#0F1511` | Screen background |
| `onBackground` = `onSurface` | `#171D19` | `#DEE5DE` | Main text |
| `surfaceVariant` / `onSurfaceVariant` | `#DDE6DE` / `#414942` | `#414942` / `#C0C9C0` | Tonal fills (unchecked switch track); secondary text |
| `surfaceContainerLowest … Highest` | `#FFFFFF` `#F0F5F0` `#EAF0EA` `#E4EBE5` `#DEE5DF` | `#0A0F0C` `#171D19` `#1B211D` `#252B27` `#303631` | Bottom bar, sheets, menus, dialogs; **cards use `surfaceContainerHigh`** (green-tinted neutrals) |
| `inverseSurface` / `inverseOnSurface` | `#2C322D` / `#EDF2EC` | `#DEE5DE` / `#2C322D` | Snackbars |
| `inversePrimary` | `#88C29A` | `#2E6E47` | Actions inside snackbars |
| `outline` / `outlineVariant` | `#717971` / `#C0C9C0` | `#8B938B` / `#414942` | Borders, dividers, empty-state icons |
| `error` / `onError` | `#BA1A1A` / `#FFFFFF` | `#FFB4AB` / `#690005` | Destructive actions, errors |
| `statusOpen` | `#2B5F8E` | `#9CCBFA` | Matter Open (always with text) |
| `statusWaiting` | `#784E00` | `#FFB951` | Matter Waiting |
| `statusResolved` | `#235E3B` | `#94D69B` | Matter Resolved, completed |
| `statusOverdue` | `#A3221B` | `#FFB4AB` | Overdue labels |

Material's tertiary role is mapped to secondary: Prumio has a single accent hue.

### Contrast

`PrumioColorsContrastTest` (`core/designsystem/src/commonTest`) computes the WCAG 2.2 ratio of
every text color on every background it is drawn on, in both themes, and fails below **4.5:1**:
`on*` colors on their containers, secondary text on screens and cards, `primary` text, errors,
snackbars, and each status color on the screen, and as a `PrumioTag` (12 % tint of itself) on the
screen and inside a card. The light status colors are deliberately deep for that last case.
`brand`/`onBrand` are excluded: they only draw the logo, which WCAG exempts. Adding a color or a
new text/background combination means adding its pair to the test.
The Android window background and launcher icon background (`app/android/src/main/res/values/colors.xml`)
mirror `background` and `brand`.

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

Access: `PrumioTheme.shape.<token>` (`PrumioTokens.kt`); also fed to Material's `Shapes`, so Material
components inside the design system get the same corners.

| Token | Corner | Use |
|---|---|---|
| `small` | 8 dp | Chips, text fields, small buttons |
| `medium` | 12 dp | Cards, list highlights |
| `large` | 16 dp | FAB, large cards |
| `extraLarge` | 28 dp | Dialogs, bottom sheets |
| `pill` | fully rounded | Tags, badges, selected tab indicator |

## Elevation

Access: `PrumioTheme.elevation.<token>`. Prumio is mostly flat; tonal surfaces carry hierarchy.

| Token | dp | Use |
|---|---|---|
| `flat` | 0 | Everything by default, including cards |
| `raised` | 1 | Bottom bar, sticky headers |
| `floating` | 3 | FAB, menus, sheets |

## Motion

`PrumioMotion` durations (ms): `SHORT_MILLIS` 150 (state changes, ripples), `MEDIUM_MILLIS` 250
(expand/collapse, list item moves), `LONG_MILLIS` 400 (screen-level transitions). Animations are
calm and never block input; the system "remove animations" setting is respected by Compose.

## Changing tokens

Tokens are global: a change affects every screen. Update this page and both themes, check light,
dark and `PrumioColorsContrastTest`, and re-record screenshots (`./gradlew testAndroidHostTest -PrecordScreenshots`,
then review the changed PNGs in the diff).
