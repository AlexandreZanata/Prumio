# Changelog

All notable changes to Prumio are documented here. Format: [Keep a Changelog](https://keepachangelog.com/en/1.1.0/);
versions follow [Semantic Versioning](https://semver.org/).

## [Unreleased]

### Added
- Phase 2 design system (P2.01–P2.09): screenshot tests with Roborazzi + Robolectric
  (`:core:screenshot-testing`, ADR-0020) in light, dark and 200 % font; Material Symbols icon set
  replacing `material-icons-core`; shape, elevation and motion tokens; generic components
  (`PrumioTextField`, `PrumioCheckbox`, `PrumioChip`, `PrumioTag`, `PrumioListItem`, `PrumioSwitch`,
  `PrumioBadge`, `PrumioDivider`, `PrumioBottomSheet`, `PrumioConfirmDialog`,
  `PrumioLoadingIndicator`, date/time pickers and quick date chips); domain components in `:core:ui`
  (`TaskRow`, `AppointmentRow`, `MatterCard`, `MatterStatusChip`, `InboxItemRow`, `DueLabel`,
  `ReminderSummary`, `TimelineEntryRow`, `SummaryTile`); localized date, time, age and reminder
  formatters; snackbar messages with Undo hosted by the app (`UiMessage`, ADR-0019); heading and
  row semantics for TalkBack.
- Phase 1 domain and persistence (P1.02–P1.16): model types; input validation; SQLite schema v1
  (inbox items, tasks, appointments, matters, history); `:core:database-testing`; repositories for
  inbox, tasks, appointments, matters, search and settings; 43 use cases covering capture and
  conversion, task lifecycle and postpone, appointments across time zones, matters (waiting,
  resolve, notes, follow-up, next action, attention), Today snapshot, accent-insensitive search
  and typed settings. 44 business rules implemented with tests.
- Coverage gate (P1.01): Kover on every shared module, `:core:domain` ≥ 90 % and `:core:data` ≥ 80 %
  lines enforced in `check`; merged report with `./gradlew koverHtmlReport`.
- `DomainError.Validation` and `InvalidTransition` require a business rule id (`BR-AREA-NNN`).
- Brand identity (ADR-0018): vectorized sprout logo (SVG/PNG/Android adaptive icon), `PrumioLogo`,
  Prumio Green `#88C29A` tonal color system with WCAG contrast tests, brand usage guide.
- Device testing rule: manual checks run on the physical phone over adb.
- Phase 0 foundation: Kotlin Multiplatform (Android-only) project with 20 modules, convention
  plugins, enforced module dependency rules, version catalog.
- App shell: bottom navigation (Today, Agenda, Matters, Inbox), "Add anything" dialog placeholder,
  Search and Settings placeholders, light/dark theme, English and Brazilian Portuguese strings.
- Reference slice: Today greeting and localized date (BR-TODAY-001).
- Quality gates: ktlint + Compose rules, Konsist architecture tests, business-rule traceability,
  privacy gate (no INTERNET permission), SQLDelight migration verification, `scripts/verify.sh`, CI.
- Documentation: product vision, domain model and business rules, architecture, design system,
  AI agent manual and recipes, execution plan with validation gates, ADR-0001 … ADR-0017.

### Changed
- Cards use `surfaceContainerHigh` and the light status colors are deeper, so status tags inside
  cards keep 4.5:1 contrast (the contrast test now covers tags, cards, errors and snackbars).
