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
| `PrumioText` | `(text: String or AnnotatedString, modifier, style: PrumioTextStyle = Body, color, maxLines; plain text also textAlign/textDecoration)` | All text. Pick a semantic `PrumioTextStyle`; `Overline` uppercases; long text ends in an ellipsis | ready |
| `PrumioLogo` | `(contentDescription: String?, modifier, size: Dp = 32.dp, color = brand)` | The Prumio mark — the only way to show the logo ([brand rules](../brand/README.md)) | ready |
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
| `PrumioScaffold` | `(modifier, topBar, bottomBar, floatingActionButton, snackbarHost, content: (PaddingValues) -> Unit)` | Skeleton of every screen | ready |
| `PrumioTopBar` | `(title, modifier, onNavigateBack: (() -> Unit)?, navigateBackContentDescription, showBrandMark: Boolean = false, actions)` | Screen title; back arrow when `onNavigateBack` is set; brand mark before the title on root screens (Today) | ready |
| `PrumioNavigationBar` | `(modifier, content: RowScope.() -> Unit)` | Bottom bar container (used only by `:app:shell`) | ready |
| `PrumioNavigationBarItem` | `RowScope.(selected, onClick, icon, selectedIcon, label, modifier, badgeCount: Int = 0)` | One bottom bar destination | ready |
| `PrumioCard` | `(modifier, onClick: (() -> Unit)?, content: ColumnScope.() -> Unit)` | Grouped content on `surfaceContainerHigh`; clickable when `onClick` is set. No inner padding: add it | ready |
| `PrumioSectionHeader` | `(title, modifier, action: @Composable () -> Unit)` | Title of a group inside a screen ("What to do today"); announced as a heading | ready |
| `PrumioListItem` | `(headline, modifier, supporting: String?, overline: String?, onClick: (() -> Unit)?, leading, trailing)` | Generic rows (settings, inbox, search results) | ready |
| `PrumioDivider` | `(modifier)` | Hairline separator between rows or groups | ready |
| `PrumioLoadingIndicator` | `(modifier)` | Rare loading states (import/export); never for local reads | ready |
| `PrumioEmptyState` | `(title, modifier, description: String?, icon: ImageVector?)` | Empty lists and screens. Calm tone, suggests what comes next | ready |

### Inputs and selection

| Component | Signature | Use for | Status |
|---|---|---|---|
| `PrumioTextField` | `(value, onValueChange, label, modifier, placeholder, errorText, maxLength, singleLine, minLines, keyboardOptions, keyboardActions, focusRequester)` | All text input. `errorText` shows the error state; `maxLength` adds a counter and flags excessive input | ready |
| `PrumioCheckbox` | `(checked, onCheckedChange, contentDescription: String, modifier, enabled)` | Round completion control of tasks (`TaskOpen`/`TaskDone` icons); 48 dp target | ready |
| `PrumioChip` | `(label, selected, onClick, modifier, leadingIcon: ImageVector?)` | Filters and single choices | ready |
| `PrumioSwitch` | `(checked, onCheckedChange, modifier, enabled)` | Boolean settings | ready |
| `PrumioQuickDateChips` | `(options: List<QuickDate>, selected: LocalDate?, onSelect, pickLabel, onPick, modifier)` | Today / Tomorrow / Next week / Pick… row; `QuickDate(label, date)` is built by the caller | ready |
| `PrumioDatePicker` | `(initialDate: LocalDate?, confirmLabel, dismissLabel, onConfirm: (LocalDate) -> Unit, onDismiss, modifier)` | Date dialog (locale week start) | ready |
| `PrumioTimePicker` | `(initialTime: LocalTime, is24Hour, confirmLabel, dismissLabel, onConfirm: (LocalTime) -> Unit, onDismiss, modifier)` | Time dialog; pass `rememberIs24HourFormat()` | ready |

### Labels, feedback and overlays

| Component | Signature | Use for | Status |
|---|---|---|---|
| `PrumioTag` | `(label, color: Color, modifier, icon: ImageVector?)` | Non-interactive colored label (statuses). Color always comes with text | ready |
| `PrumioBadge` | `(count: Int, modifier)` | Counts (inbox tab). Hidden at 0, "99+" above 99 | ready |
| `PrumioSnackbarHost` | `(messenger: PrumioMessenger, modifier)` | Where messages appear; placed once by `PrumioApp`. Features send messages with `rememberMessageSender()` (`:core:ui`) | ready |
| `rememberPrumioMessenger` | `(): PrumioMessenger` | Creates the app's messenger (`suspend show(text, actionLabel): Boolean` — true when the action was tapped). Only `PrumioApp` calls it | ready |
| `PrumioBottomSheet` | `(onDismissRequest, modifier, content: ColumnScope.() -> Unit)` | Contextual choices (postpone, snooze, convert) | ready |
| `PrumioConfirmDialog` | `(title, text, confirmLabel, dismissLabel, onConfirm, onDismiss, modifier, destructive)` | Confirmation of destructive actions | ready |

`PrumioMessenger.post(block: suspend PrumioMessenger.() -> Unit)` launches messages/actions in the
app scope so confirmation and Undo survive the originating screen. Messages with an action remain
for 5 seconds, extended by the accessibility timeout preference. `UiMessage.onAction` calls the
restore use case directly; it must not dispatch back into a disposed ViewModel.

### Tokens and registries (not composables)

| Symbol | Where | Notes |
|---|---|---|
| `PrumioColors` | `theme/PrumioColors.kt` | Prumio Green tonal palette; `brand`/`onBrand` for identity; status colors `statusOpen`, `statusWaiting`, `statusResolved`, `statusOverdue` |
| `PrumioTextStyle` | `theme/PrumioTypography.kt` | `Display`, `Headline`, `Title`, `Subtitle`, `Body`, `BodyStrong`, `Label`, `Caption`, `Overline` |
| `PrumioSpacing` | `theme/PrumioSpacing.kt` | `xxs`=2, `xs`=4, `sm`=8, `md`=12, `lg`=16, `xl`=24, `xxl`=32, `xxxl`=48 dp; `minTouchTarget`, `screenHorizontal` |
| `PrumioIcons` | `icon/PrumioIcons.kt` | The only icon registry |
| `PrumioButtonVariant` | `component/PrumioButton.kt` | Button styles |
| `PrumioShape`, `PrumioElevation`, `PrumioMotion` | `theme/PrumioTokens.kt` | Corners, depth, durations ([tokens](tokens.md)); `PrumioTheme.shape`, `PrumioTheme.elevation` |

## Domain-aware components and helpers — `:core:ui`

### Presentation helpers

| Component | Signature | Use for | Status |
|---|---|---|---|
| `OnTimeChange` | `(onChange: () -> Unit)` — package `mvi` | Re-subscribe to domain observations after live device clock/date/zone changes (BR-TODAY-007); Android receiver unregisters on disposal | ready |
| `CollectEffects` | `(effects: Flow<E>, onEffect: (E) -> Unit)` — package `mvi` | Collect one-off ViewModel effects in a `XxxRoute` | ready |
| `greeting` | `DayPeriod.greeting(): String` — package `label` | "Good morning/afternoon/evening" (BR-TODAY-001) | ready |
| `dayHeaderText` | `LocalDate.dayHeaderText(): String` — package `format` | Localized "Wednesday, 11 Nov" | ready |

`PrumioViewModel` (package `mvi`) is the base class of every screen ViewModel (not a composable).

### Domain components

Package `app.prumio.core.ui.component`. Each receives models and callbacks; time-dependent state
(`today`, `now`, `isOverdue`) is passed in by the caller, never read inside.

| Component | Signature | Renders | Status |
|---|---|---|---|
| `TaskRow` | `(task, today, isOverdue, onToggle: (Boolean) -> Unit, onClick, modifier, matterTitle: String?)` | A task in any list: completion control ("Complete task …" for TalkBack), title (struck through when done), `DueLabel`, matter link | ready |
| `AppointmentRow` | `(appointment, start: LocalTime?, end: LocalTime?, onClick, modifier)` | Time range, single time or "All day", title, location. Times already in the device zone | ready |
| `MatterCard` | `(matter, nextAction: Task?, today, onClick, modifier)` | Status chip, title, next action or "No next action yet", waiting for, follow-up | ready |
| `MatterStatusChip` | `(status: MatterStatus, modifier)` | Open / Waiting / Resolved with color, icon **and** text | ready |
| `InboxItemRow` | `(item, now: Instant, onClick, modifier, trailing)` | An inbox item with its age ("2 days ago") | ready |
| `DateField` | `(label, date: LocalDate?, today, onDateChange, modifier, clearable = true)` | Shared labelled date picker with optional clear action | ready |
| `TimeField` | `(label, time: LocalTime?, onTimeChange, modifier, clearable = true, suggestedTime = 09:00)` | Shared labelled device-format time picker and clear action | ready |
| `DueLabel` | `(date, time: LocalTime?, today, isOverdue, modifier)` | "Today, 15:30", "Tomorrow", "Overdue · Mon, Jan 12" | ready |
| `ReminderSummary` | `(offsetsMinutes: List<Int>, modifier)` | "🔔 1 day before · At time"; nothing when empty | ready |
| `TimelineEntryRow` | `(entry: MatterEntry, today, modifier)` | One entry of a matter's history: icon, sentence, day | ready |
| `SummaryTile` | `(count, label, modifier, onClick: (() -> Unit)?)` | Count + label tile of Today's summary; read as one phrase | ready |

`SummaryTile` uses the caption token for its label so three summary tiles fit compact phones.

### Formatters and labels

| Helper | Signature | Output | Status |
|---|---|---|---|
| `relativeDateText` | `LocalDate.relativeDateText(today): String` — `format` | "Today", "Tomorrow", "Yesterday", weekday within 6 days, else "Mon, Jan 26" | ready |
| `relativeDayOf` | `(date, today): RelativeDay` — `format` | The pure rule behind `relativeDateText` (unit-tested) | ready |
| `shortWeekdayText` / `formatShortWeekday` | `LocalDate.shortWeekdayText()` / `(date, languageTag): String` — `format` | Compact weekday in Agenda | ready |
| `monthYearText` / `formatMonthYear` | `LocalDate.monthYearText()` / `(date, languageTag): String` — `format` | Localized month and year | ready |
| `timeText` | `LocalTime.timeText(): String` — `format` | "15:30" or "3:30 PM" per the device setting | ready |
| `dateTimeText` | `(date, time: LocalTime?, today): String` — `format` | "Tomorrow, 15:30" | ready |
| `rememberIs24HourFormat` | `(): Boolean` — `format` | Device 12/24 h preference (for `PrumioTimePicker`) | ready |
| `ageText` | `(since: Instant, now: Instant): String` — `format` | "Just now", "5 minutes ago", "2 days ago" | ready |
| `reminderOffsetText` | `(offsetMinutes: Int): String` — `label` | "At time", "1 hour before", "2 days before" | ready |
| `label` | `MatterStatus.label()`, `TaskStatus.label()` — `label` | Localized status names | ready |

### Messages

Package `app.prumio.core.ui.message` ([presentation pattern](../architecture/presentation-pattern.md#messages-and-undo)).

| Helper | Signature | Use for | Status |
|---|---|---|---|
| `UiMessage` | `data class (text: StringResource, args, actionLabel: StringResource?, onAction: (suspend () -> Unit)?)` | A message a ViewModel asks to show (in an effect) | ready |
| `rememberMessageSender` | `(): (UiMessage) -> Unit` | In a Route: shows a `UiMessage` and runs `onAction` when tapped (Undo) | ready |
| `toUiMessage` | `DomainError.toUiMessage(): UiMessage` | Standard text for any domain error | ready |
| `fieldMessage` | `DomainError.Validation.fieldMessage(): StringResource` | Text under a form field (`PrumioTextField(errorText = …)`) | ready |
| `LocalMessenger` | `ProvidableCompositionLocal<PrumioMessenger?>` | Provided once by `PrumioApp`; read only through `rememberMessageSender` | ready |

## How to choose where a new component goes

1. Does it know about a Prumio concept (task, matter, status, reminder)? → `:core:ui`.
2. Is it a generic visual element (input, chip, dialog)? → `:core:designsystem`, prefixed `Prumio`.
3. Is it used by exactly one screen and unlikely to be reused? → `private` inside that feature's
   `XxxScreen.kt` (or a `component/` file in the feature, still `internal`).
4. The moment a second feature needs it → move it to 1 or 2 and replace the original. Never copy.
