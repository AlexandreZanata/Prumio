# Component catalog

**Search this page before writing any UI.** It lists every public composable of
`:core:designsystem` (generic) and `:core:ui` (domain-aware). If what you need exists, use it by
reference; if it almost exists, extend it (new parameter or variant); only if nothing fits, create a
new component with [the recipe](../ai/recipes/add-design-system-component.md) and add it here.

`DesignSystemTest` fails when a public composable of those two modules is missing from this page.

Legend — **Status**: `ready` (implemented), `planned` (task id).

## Generic components — `:core:designsystem`

Package `app.prumio.core.designsystem.component` unless noted.

### Foundations

| Component | Signature (essential parameters) | Use for | Status |
|---|---|---|---|
| `PrumioTheme` | `(darkTheme: Boolean = isSystemInDarkTheme(), content)` — package `theme` | Root of every screen, preview and screenshot test. Tokens via `PrumioTheme.colors`, `PrumioTheme.spacing` | ready |
| `PrumioText` | `(text, modifier, style: PrumioTextStyle = Body, color, textAlign, maxLines)` | All text. Pick a semantic `PrumioTextStyle`; `Overline` uppercases | ready |
| `PrumioIcon` | `(icon: ImageVector, contentDescription: String?, modifier, tint)` | Non-clickable icons from `PrumioIcons`. `null` description only when decorative | ready |

### Actions

| Component | Signature | Use for | Status |
|---|---|---|---|
| `PrumioButton` | `(text, onClick, modifier, variant: PrumioButtonVariant = Primary, enabled)` | Buttons. Variants: `Primary` (one per screen), `Secondary`, `Text`, `Destructive` | ready |
| `PrumioIconButton` | `(icon, contentDescription: String, onClick, modifier, enabled)` | Toolbar and inline icon actions; 48 dp target | ready |
| `PrumioFloatingActionButton` | `(icon, contentDescription, onClick, modifier)` | Only the global "Add anything" action | ready |

### Layout and structure

| Component | Signature | Use for | Status |
|---|---|---|---|
| `PrumioScaffold` | `(modifier, topBar, bottomBar, floatingActionButton, content: (PaddingValues) -> Unit)` | Skeleton of every screen | ready |
| `PrumioTopBar` | `(title, modifier, onNavigateBack: (() -> Unit)?, navigateBackContentDescription, actions)` | Screen title; back arrow when `onNavigateBack` is set | ready |
| `PrumioNavigationBar` | `(modifier, content: RowScope.() -> Unit)` | Bottom bar container (used only by `:app:shell`) | ready |
| `PrumioNavigationBarItem` | `RowScope.(selected, onClick, icon, selectedIcon, label, modifier)` | One bottom bar destination | ready |
| `PrumioCard` | `(modifier, onClick: (() -> Unit)?, content: ColumnScope.() -> Unit)` | Grouped content; clickable when `onClick` is set | ready |
| `PrumioSectionHeader` | `(title, modifier, action: @Composable () -> Unit)` | Title of a group inside a screen ("What to do today") | ready |
| `PrumioEmptyState` | `(title, modifier, description: String?, icon: ImageVector?)` | Empty lists and screens. Calm tone, suggests what comes next | ready |

### Planned (phase 2, see docs/plan/phase-2-design-system.md)

| Component | Use for | Task |
|---|---|---|
| `PrumioTextField` | Single and multi-line text input with label, error and character counter | P2.04 |
| `PrumioCheckbox` | Round completion control for tasks | P2.04 |
| `PrumioChip` | Filter and status chips | P2.04 |
| `PrumioListItem` | Rows with leading/trailing slots and supporting text | P2.04 |
| `PrumioSwitch` | Boolean settings | P2.04 |
| `PrumioBadge` | Counts on tabs (inbox) | P2.04 |
| `PrumioDivider` | Separators | P2.04 |
| `PrumioSnackbarHost` | Messages with Undo | P2.08 |
| `PrumioBottomSheet` | Contextual choices (postpone, snooze, convert) | P2.04 |
| `PrumioConfirmDialog` | Confirmation of destructive actions | P2.04 |
| `PrumioLoadingIndicator` | Rare loading states (import/export) | P2.04 |
| `PrumioDatePicker`, `PrumioTimePicker` | Date and time selection | P2.05 |
| `PrumioQuickDateChips` | Today / Tomorrow / Next week / Pick | P2.05 |

### Tokens and registries (not composables)

| Symbol | Where | Notes |
|---|---|---|
| `PrumioColors` | `theme/PrumioColors.kt` | Includes status colors `statusOpen`, `statusWaiting`, `statusResolved`, `statusOverdue` |
| `PrumioTextStyle` | `theme/PrumioTypography.kt` | `Display`, `Headline`, `Title`, `Subtitle`, `Body`, `BodyStrong`, `Label`, `Caption`, `Overline` |
| `PrumioSpacing` | `theme/PrumioSpacing.kt` | `xxs`=2, `xs`=4, `sm`=8, `md`=12, `lg`=16, `xl`=24, `xxl`=32, `xxxl`=48 dp; `minTouchTarget`, `screenHorizontal` |
| `PrumioIcons` | `icon/PrumioIcons.kt` | The only icon registry |
| `PrumioButtonVariant` | `component/PrumioButton.kt` | Button styles |

## Domain-aware components and helpers — `:core:ui`

### Presentation helpers

| Component | Signature | Use for | Status |
|---|---|---|---|
| `CollectEffects` | `(effects: Flow<E>, onEffect: (E) -> Unit)` — package `mvi` | Collect one-off ViewModel effects in a `XxxRoute` | ready |
| `greeting` | `DayPeriod.greeting(): String` — package `label` | "Good morning/afternoon/evening" (BR-TODAY-001) | ready |
| `dayHeaderText` | `LocalDate.dayHeaderText(): String` — package `format` | Localized "Wednesday, 11 Nov" | ready |

`PrumioViewModel` (package `mvi`) is the base class of every screen ViewModel (not a composable).

### Planned (phase 2)

| Component | Renders | Task |
|---|---|---|
| `TaskRow` | A task in any list: completion control, title, due label, matter link, overdue state | P2.06 |
| `AppointmentRow` | An appointment: time range or "All day", title, location | P2.06 |
| `MatterCard` | A matter: status chip, title, next action, follow-up | P2.06 |
| `MatterStatusChip` | Open / Waiting / Resolved with color **and** text | P2.06 |
| `InboxItemRow` | An inbox item with age ("2 days ago") | P2.06 |
| `DueLabel` | "Today 15:30", "Tomorrow", "Overdue · Mon" | P2.06 |
| `ReminderSummary` | "1 hour before" / "At time" | P2.06 |
| `TimelineEntryRow` | One entry of a matter's history | P2.06 |
| `SummaryTile` | Count + label tiles of Today's summary | P2.06 |
| `relativeDateText`, `timeText`, `durationText` | Localized formatting helpers | P2.07 |
| `label` for `MatterStatus`, `TaskStatus`, `RecurrenceRule` | Localized labels | P2.06 |

## How to choose where a new component goes

1. Does it know about a Prumio concept (task, matter, status, reminder)? → `:core:ui`.
2. Is it a generic visual element (input, chip, dialog)? → `:core:designsystem`, prefixed `Prumio`.
3. Is it used by exactly one screen and unlikely to be reused? → `private` inside that feature's
   `XxxScreen.kt` (or a `component/` file in the feature, still `internal`).
4. The moment a second feature needs it → move it to 1 or 2 and replace the original. Never copy.
