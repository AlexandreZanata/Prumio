# Phase 2 — Design system and shared UI

**Goal:** every visual building block the features need exists once, documented, previewed and
screenshot-tested, in light and dark, accessible. **Gate:** G2. **Can run in parallel with:** phase 1
(tasks P2.06 and P2.07 need the phase-1 model types from P1.02).

### P2.01 — Screenshot testing
- **Status:** todo
- **Depends on:** —
- **Goal:** visual regression tests runnable on the JVM and in CI.
- **Inputs:** testing-strategy.md, recipe add-library.md (Roborazzi, Robolectric need approval/ADR).
- **Deliverables:** Roborazzi + Robolectric in host tests of `:core:designsystem` and `:core:ui`
  (`isIncludeAndroidResources = true`), record/verify Gradle tasks, baseline images committed,
  CI verification, a helper `screenshotTest { }` that renders light, dark and 200 % font.
- **Validation:** change a token locally → verification fails with a diff image; revert.

### P2.02 — Icon set
- **Status:** todo
- **Depends on:** —
- **Goal:** replace the frozen `material-icons-core` with Material Symbols vectors (Apache-2.0)
  as Compose resources, keeping `PrumioIcons` as the only entry point.
- **Deliverables:** icons for today, calendar_month, pending_actions, inbox, add, search, settings,
  arrow_back, close, check, notifications, schedule, repeat, event, label, mic, share, more_vert,
  delete, edit, undo; NOTICE attribution; dependency removed.
- **Validation:** screenshots of the catalog; `./scripts/verify.sh`.

### P2.03 — Token review and contrast test
- **Status:** todo
- **Depends on:** P2.01
- **Deliverables:** final type scale, shapes, elevation and motion tokens in tokens.md; a unit test
  computing WCAG contrast for all text/background pairs (≥ 4.5:1, large ≥ 3:1).
- **Validation:** contrast test green in light and dark.

### P2.04 — Generic components
- **Status:** todo
- **Depends on:** P2.01
- **Deliverables:** `PrumioTextField`, `PrumioCheckbox`, `PrumioChip`, `PrumioListItem`,
  `PrumioSwitch`, `PrumioBadge`, `PrumioDivider`, `PrumioBottomSheet`, `PrumioConfirmDialog`,
  `PrumioLoadingIndicator` — catalog entries, previews, screenshots.
- **Validation:** `DesignSystemTest`, screenshot tests.

### P2.05 — Date and time selection
- **Status:** todo
- **Depends on:** P2.04
- **Deliverables:** `PrumioDatePicker`, `PrumioTimePicker` (dialogs wrapping Material 3, locale week
  start, 12/24 h), `PrumioQuickDateChips` (Today, Tomorrow, Next week, Pick…).
- **Validation:** screenshots; ViewModel-free state tests where possible.

### P2.06 — Domain components in `:core:ui`
- **Status:** todo
- **Depends on:** P1.02, P2.04
- **Deliverables:** `TaskRow`, `AppointmentRow`, `MatterCard`, `MatterStatusChip`, `InboxItemRow`,
  `DueLabel`, `ReminderSummary`, `TimelineEntryRow`, `SummaryTile`; labels for `MatterStatus`,
  `TaskStatus`; UI models where needed; strings en + pt-BR.
- **Validation:** screenshots of each in typical, long-text, overdue/waiting states.

### P2.07 — Formatters
- **Status:** todo
- **Depends on:** P1.02
- **Deliverables:** in `:core:ui/format`: relative date ("Today", "Tomorrow", "In 3 days",
  "Mon, 17 Nov"), time (system 12/24 h), duration/offset ("1 hour before", "At time"), age
  ("2 days ago"), plurals; expect/actual for Android.
- **Validation:** unit tests in en and pt-BR locales.

### P2.08 — Messages and Undo
- **Status:** todo
- **Depends on:** P2.04
- **Deliverables:** `UiMessage` model, `PrumioSnackbarHost` hosted by `PrumioApp`, a `MessageBus`
  or effect convention for features, `DomainError.toUiMessage()`, Undo pattern (BR-GEN-005 UI part)
  documented in presentation-pattern.md.
- **Validation:** ViewModel test example + screenshot.

### P2.09 — Accessibility baseline
- **Status:** todo
- **Depends on:** P2.04, P2.06
- **Deliverables:** headings semantics in `PrumioSectionHeader`, merged semantics for rows
  ("Complete task …"), 200 % font screenshots for all components, accessibility.md updated.
- **Validation:** screenshots at 200 %; TalkBack spot-check on the catalog.

## Gate record

| Date | Result | Notes |
|---|---|---|
| | | |
