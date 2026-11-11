# Phase 2 — Design system and shared UI

**Goal:** every visual building block the features need exists once, documented, previewed and
screenshot-tested, in light and dark, accessible. **Gate:** G2. **Can run in parallel with:** phase 1
(tasks P2.06 and P2.07 need the phase-1 model types from P1.02).

### P2.01 — Screenshot testing
- **Status:** done
- **Depends on:** —
- **Goal:** visual regression tests runnable on the JVM and in CI.
- **Inputs:** testing-strategy.md, recipe add-library.md (Roborazzi, Robolectric need approval/ADR).
- **Deliverables:** Roborazzi + Robolectric in host tests of `:core:designsystem` and `:core:ui`
  (`isIncludeAndroidResources = true`), record/verify Gradle tasks, baseline images committed,
  CI verification, a helper `screenshotTest { }` that renders light, dark and 200 % font.
- **Validation:** change a token locally → verification fails with a diff image; revert.
- **Result:** `:core:screenshot-testing` with `ScreenshotTest.screenshot(name) { }` (light, dark,
  200 % font); Roborazzi library + Robolectric native graphics; `-PrecordScreenshots` records,
  every `testAndroidHostTest` verifies. Validated: changing `primary` failed `FoundationScreenshotTest`
  with a `_compare.png` diff.

### P2.02 — Icon set
- **Status:** done
- **Depends on:** —
- **Goal:** replace the frozen `material-icons-core` with Material Symbols vectors (Apache-2.0)
  as Compose resources, keeping `PrumioIcons` as the only entry point.
- **Deliverables:** icons for today, calendar_month, pending_actions, inbox, add, search, settings,
  arrow_back, close, check, notifications, schedule, repeat, event, label, mic, share, more_vert,
  delete, edit, undo; NOTICE attribution; dependency removed.
- **Validation:** screenshots of the catalog; `./scripts/verify.sh`.
- **Result:** 36 Material Symbols vectors (outlined + filled for tabs) in `composeResources/drawable`;
  `PrumioIcons` getters are `@Composable`; `compose-material-icons` removed; NOTICE updated.

### P2.03 — Token review and contrast test
- **Status:** done
- **Depends on:** P2.01
- **Deliverables:** final type scale, shapes, elevation and motion tokens in tokens.md; a unit test
  computing WCAG contrast for all text/background pairs (≥ 4.5:1, large ≥ 3:1).
- **Validation:** contrast test green in light and dark.
- **Result:** shape, elevation and motion tokens (`PrumioTokens.kt`, tokens.md); the existing
  `PrumioColorsContrastTest` now covers cards, snackbars, errors and status tags. It found tags in
  cards below 4.5:1: cards moved to `surfaceContainerHigh` and light status colors deepened.

### P2.04 — Generic components
- **Status:** done
- **Depends on:** P2.01
- **Deliverables:** `PrumioTextField`, `PrumioCheckbox`, `PrumioChip`, `PrumioListItem`,
  `PrumioSwitch`, `PrumioBadge`, `PrumioDivider`, `PrumioBottomSheet`, `PrumioConfirmDialog`,
  `PrumioLoadingIndicator` — catalog entries, previews, screenshots.
- **Validation:** `DesignSystemTest`, screenshot tests.
- **Result:** all listed components plus `PrumioTag`; catalog updated; screenshots in
  `ComponentScreenshotTest` (dialogs and sheets are checked on the device: they open their own window).

### P2.05 — Date and time selection
- **Status:** done
- **Depends on:** P2.04
- **Deliverables:** `PrumioDatePicker`, `PrumioTimePicker` (dialogs wrapping Material 3, locale week
  start, 12/24 h), `PrumioQuickDateChips` (Today, Tomorrow, Next week, Pick…).
- **Validation:** screenshots; ViewModel-free state tests where possible.
- **Result:** as specified; `QuickDate(label, date)` options are built by the caller so the
  feature decides which shortcuts it offers.

### P2.06 — Domain components in `:core:ui`
- **Status:** done
- **Depends on:** P1.02, P2.04
- **Deliverables:** `TaskRow`, `AppointmentRow`, `MatterCard`, `MatterStatusChip`, `InboxItemRow`,
  `DueLabel`, `ReminderSummary`, `TimelineEntryRow`, `SummaryTile`; labels for `MatterStatus`,
  `TaskStatus`; UI models where needed; strings en + pt-BR.
- **Validation:** screenshots of each in typical, long-text, overdue/waiting states.
- **Result:** all nine components in `:core:ui/component`, status labels, strings en + pt-BR;
  `:core:ui` may use domain types (ADR-0019). Screenshots in `DomainComponentScreenshotTest`.

### P2.07 — Formatters
- **Status:** done
- **Depends on:** P1.02
- **Deliverables:** in `:core:ui/format`: relative date ("Today", "Tomorrow", "In 3 days",
  "Mon, 17 Nov"), time (system 12/24 h), duration/offset ("1 hour before", "At time"), age
  ("2 days ago"), plurals; expect/actual for Android.
- **Validation:** unit tests in en and pt-BR locales.
- **Result:** `relativeDateText` (Today / Tomorrow / Yesterday / weekday within 6 days / short date —
  weekday names instead of "In 3 days", clearer for planning), `timeText`, `dateTimeText`, `ageText`,
  `reminderOffsetText`, plurals. `RelativeDayTest` (pure) and `DateFormattingTest` (en-US, pt-BR).

### P2.08 — Messages and Undo
- **Status:** done
- **Depends on:** P2.04
- **Deliverables:** `UiMessage` model, `PrumioSnackbarHost` hosted by `PrumioApp`, a `MessageBus`
  or effect convention for features, `DomainError.toUiMessage()`, Undo pattern (BR-GEN-005 UI part)
  documented in presentation-pattern.md.
- **Validation:** ViewModel test example + screenshot.
- **Result:** `UiMessage`, `PrumioMessenger`/`PrumioSnackbarHost` hosted by `PrumioApp`, no bus:
  ViewModel effect → Route → `rememberMessageSender()`; `toUiMessage()`/`fieldMessage()` tested in
  `UiMessageTest`; Undo pattern in presentation-pattern.md#messages-and-undo.

### P2.09 — Accessibility baseline
- **Status:** done
- **Depends on:** P2.04, P2.06
- **Deliverables:** headings semantics in `PrumioSectionHeader`, merged semantics for rows
  ("Complete task …"), 200 % font screenshots for all components, accessibility.md updated.
- **Validation:** screenshots at 200 %; TalkBack spot-check on the catalog.
- **Result:** heading semantics in `PrumioSectionHeader`, labelled task checkbox, `SummaryTile` read
  as one phrase, `_font200` screenshots for every component, accessibility.md updated. TalkBack
  pass on real screens stays in P6.11 (no screen uses the components yet).

## Gate record

| Date | Result | Notes |
|---|---|---|
| 2026-11-11 | Automated part passed | 36 screenshots (12 tests × light/dark/200 %) recorded and verified by `./scripts/verify.sh` (also run by CI); `PrumioColorsContrastTest` and `DesignSystemTest` green. Waiting for the owner's review of the screenshot gallery (manual item of G2). |
