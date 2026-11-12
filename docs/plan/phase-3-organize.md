# Phase 3 — Organize

**Goal:** the app is useful every day without reminders: capture, inbox, tasks, Today, agenda,
matters, search and basic settings, in English and Brazilian Portuguese. **Gate:** G3.
**Depends on:** phases 1 and 2. Every task follows docs/ai/recipes/add-screen.md and the specs in
docs/product/screens.md.

### P3.01 — Quick capture
- **Status:** done
- **Depends on:** P1.10, P2.04, P2.08
- **Deliverables:** real `CaptureScreen` (focused multi-line field, Save, keyboard done), ViewModel
  calling `CaptureInboxItemUseCase`, "Saved to Inbox" message.
- **Rules:** BR-INBOX-001, BR-INBOX-002.
- **Validation:** ViewModel tests; device: capture from each tab in two taps.
- **Result:** Focused multiline capture, Save/IME Done, validation and confirmation; reachable from every tab.

### P3.02 — Inbox list
- **Status:** done
- **Depends on:** P1.10, P2.06, P2.08
- **Deliverables:** list newest first with ages, edit text, discard with Undo, empty state, badge on
  the Inbox tab (shell observes `observeCount`).
- **Rules:** BR-INBOX-003, BR-INBOX-005, BR-INBOX-006, BR-GEN-005.
- **Result:** Live newest-first Inbox, ages, editing, explicit discard with app-scope Undo and a reactive shell badge.

### P3.03 — Organize an inbox item
- **Status:** done
- **Depends on:** P3.02, P3.04, P3.09, P3.11
- **Deliverables:** organize sheet (Task / Appointment / Matter) → conversion → target editor pre-filled.
- **Rules:** BR-INBOX-004.
- **Result:** Atomic conversion to task/appointment/matter followed by the persisted target editor; cancel preserves conversion.

### P3.04 — Task editor
- **Status:** done
- **Depends on:** P1.11, P2.05, P2.06
- **Deliverables:** `TaskEditorKey`; title, notes, quick date chips, time, matter link; save with
  title only; validation messages from `DomainError.Validation`.
- **Rules:** BR-TASK-001, BR-TASK-002, BR-GEN-001.
- **Result:** Title-only task editor, notes, quick dates, time, matter selection and domain field errors.

### P3.05 — Task detail and actions
- **Status:** done
- **Depends on:** P3.04
- **Deliverables:** `TaskDetailKey`; complete/reopen, postpone sheet, dismiss, delete with undo, edit.
- **Rules:** BR-TASK-003, BR-TASK-007, BR-GEN-005.
- **Result:** Task detail, complete/reopen/dismiss, postpone, edit and delete/Undo; preserves the last-action prompt.

### P3.06 — All tasks
- **Status:** done
- **Depends on:** P3.05
- **Deliverables:** `AllTasksKey`; segments Anytime / Upcoming / Overdue / Completed; dismissed filter.
- **Rules:** BR-TASK-009, BR-TASK-004.
- **Result:** Anytime/Upcoming/Overdue/Completed segments and Dismissed filter with inline completion.

### P3.07 — Today
- **Status:** done
- **Depends on:** P1.14, P2.06, P3.05
- **Deliverables:** full Today per screens.md (summary tiles, what to do today, done today,
  next appointment, attention matters, inbox nudge); completing tasks inline.
- **Rules:** BR-TODAY-002 … BR-TODAY-006.
- **Result:** Live Today counts, task sections/completion/Undo, done link, next/all-day appointments, attention matters and pluralized Inbox nudge.

### P3.08 — Agenda
- **Status:** done
- **Depends on:** P1.12, P2.05, P2.06
- **Deliverables:** week strip + month label, continuous day list with sticky headers, ordering,
  jump to date, add appointment for a day.
- **Rules:** BR-APPT-005, BR-APPT-007.
- **Result:** Week/date navigation, localized headers, day list with sticky headers and domain-ordered task/appointment rows.

### P3.09 — Appointment editor and detail
- **Status:** done
- **Depends on:** P3.08
- **Deliverables:** `AppointmentEditorKey`, `AppointmentDetailKey`; timed/all-day, range validation,
  location, notes, delete with undo.
- **Rules:** BR-APPT-001 … BR-APPT-003.
- **Result:** Timed/all-day appointment editor, range validation, device-zone display and detail/edit/delete/Undo.

### P3.10 — Matters list
- **Status:** done
- **Depends on:** P1.13, P2.06
- **Deliverables:** groups Needs attention / Waiting / Open / Resolved (collapsed), new matter.
- **Rules:** BR-MATTER-010, BR-MATTER-013.
- **Result:** Needs attention/Waiting/Open/Resolved groups, collapsed resolved section and create/detail navigation.

### P3.11 — Matter detail
- **Status:** done
- **Depends on:** P3.10, P3.04
- **Deliverables:** `MatterDetailKey`, `MatterEditorKey`; status actions, waiting sheet, resolve
  dialog with open-actions choice, next action card + add action, follow-up date, history timeline,
  notes (add/edit/delete, backdate).
- **Rules:** BR-MATTER-002 … BR-MATTER-009 (UI parts).
- **Result:** Matter editor/detail, waiting presets, follow-up date/time, next-action question from all completion entry points, resolve choice and dated note CRUD/Undo.

### P3.12 — Search
- **Status:** done
- **Depends on:** P1.15, P2.06
- **Deliverables:** focused field, debounce, grouped results with highlight, empty/no-result states.
- **Rules:** BR-SEARCH-001 … BR-SEARCH-004.
- **Result:** Focused offline search with 200 ms cancellation/debounce, minimum-query hint, grouped results, accent-insensitive highlights, detail links and refresh after returning from a changed detail.

### P3.13 — Settings base
- **Status:** done
- **Depends on:** P1.09, P2.04
- **Deliverables:** theme (applied by `PrumioApp`), language link to system settings, About
  (version, licenses, source link, privacy statement).
- **Rules:** BR-SET-001.
- **Result:** Root-applied theme preferences, Android 13+ language intent and About section (version, source, licenses, privacy).

### P3.14 — Day rollover and polish
- **Status:** done
- **Depends on:** P3.07
- **Deliverables:** Today/Agenda recompute on date change and resume; empty states reviewed against
  ux-principles.md; all strings reviewed in pt-BR.
- **Rules:** BR-TODAY-007, BR-TIME-003.
- **Validation:** G3 checklist; owner dogfooding for 3 days.
- **Result:** Midnight/resume/live device-time refresh, calm empty states, en/pt-BR plurals, compact week layout and adaptive large-font summary tiles. Owner dogfooding remains a G3 sign-off item.

## Gate record

| Date | Result | Notes |
|---|---|---|
| 2026-11-12 | Automated checks passed; device smoke passed; owner sign-off pending | `./scripts/verify.sh`: all gates passed (284 tests, 0 failures), 34 feature screen states × light/dark/200 % font, shared date/time field snapshots. Physical phone 2311DRK48G, Android 16 / API 36; debug package only. Capture on all tabs; convert all three types; task complete/reopen/postpone/delete+Undo; matter waiting/follow-up/note edit/delete+Undo/resolve/reopen/keep actions; grouped search; theme and per-app language in en/pt-BR. See evidence below. |

### Validation evidence

- Commands: `./gradlew spotlessApply`, module `testAndroidHostTest`, `./scripts/verify.sh`, per-module `koverLog`; debug APK installed with `adb -s <physical-phone> install -r` so no emulator was targeted.
- Feature ViewModel line coverage: Capture 95 %, Inbox 98.28 %, Tasks 99.35 %, Agenda 99.37 %, Matters 99.41 %, Today/Search/Settings 100 %. Domain 99.55 % (≥ 90 %), Data 95.12 % (≥ 80 %). Features enforce ≥ 80 %, including coroutine/lambda classes.
- Screenshot baselines reviewed: empty, typical, relevant long/error/status states; 34 feature cases (102 images), plus shared date/time fields. Corrected compact week overflow, search highlight contrast and large-font summary labels.
- Physical capture confirmed focused field and visible IME. The Android per-app language page opened and pt-BR applied; light/dark/system preferences were exercised. Undo was exercised after a detail closed.
- Crash inspection: no Prumio crash entries in `adb logcat -b crash -d`; the global buffer contained an older unrelated application crash.
- Midnight, time-zone travel and date changes use deterministic domain/ViewModel tests; live Android clock/date/zone broadcasts and receiver cleanup are covered by Robolectric tests.
- No dependency version, module graph, database schema, backup format, release install or network permission changed. ADR-0021 records the owner-approved exact ShellViewModel exception.
- Pending: owner dogfooding for at least 3 days, broader manual device QA and final G3 sign-off. Reminder delivery (including BR-MATTER-009’s reminder portion) belongs to later phases.
