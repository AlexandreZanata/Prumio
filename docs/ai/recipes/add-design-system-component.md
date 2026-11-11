# Recipe: add a UI component

## 1. Decide where it goes

| Question | Destination |
|---|---|
| Generic visual element (input, chip, dialog, list item)? | `:core:designsystem`, name `Prumio<Name>` |
| Renders a Prumio concept (task, matter, status, reminder)? | `:core:ui`, name `<Entity><Role>` (`TaskRow`) |
| Used by one screen only and unlikely to be reused? | `private` in that feature's screen file |

Search [components.md](../../design-system/components.md) first. If something similar exists,
extend it (new parameter or variant) instead of creating a sibling.

## 2. Write it

File `core/designsystem/src/commonMain/kotlin/app/prumio/core/designsystem/component/Prumio<Name>.kt`
(or `core/ui/.../component/<Name>.kt`):

```kotlin
/** One sentence: what it is for and when to use it instead of alternatives. */
@Composable
fun PrumioChip(
    label: String,                     // text always comes from the caller
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,     // first optional parameter
    leadingIcon: ImageVector? = null,
) {
    // Material 3 allowed only inside :core:designsystem. Use PrumioTheme tokens, no literals.
}
```

Checklist:
- `modifier: Modifier = Modifier` parameter; required params first.
- No user-facing strings inside; content descriptions passed in (required when actionable).
- Touch target ≥ 48 dp; works in dark theme and at 200 % font scale.
- One public component per file, file named after it.

## 3. Register and preview

- Add it to [components.md](../../design-system/components.md) with signature, use and status 🔒.
- Add it to `core/designsystem/.../preview/ComponentCatalogPreview.kt` (or a preview in the
  `:core:ui` file) with realistic states.
- Add its states to a screenshot test: `ComponentScreenshotTest` (`:core:designsystem`) or
  `DomainComponentScreenshotTest` (`:core:ui`) — typical, long text, error/overdue/waiting. Record
  with `./gradlew :core:<module>:testAndroidHostTest -PrecordScreenshots` and look at the three
  PNGs (light, dark, `font200`) before committing them.
- New color combination for text? Add the pair to `PrumioColorsContrastTest`.

## 4. Replace duplicates

If the component replaces private copies in features, migrate them in the same change.

## Validation

```bash
./gradlew :core:designsystem:testAndroidHostTest :tooling:architecture-tests:test
```
