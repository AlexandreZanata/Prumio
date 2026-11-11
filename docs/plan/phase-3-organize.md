# Phase 3 — Organize

**Goal:** the app is useful every day without reminders: capture, inbox, tasks, Today, agenda,
matters, search and basic settings, in English and Brazilian Portuguese. **Gate:** G3.
**Depends on:** phases 1 and 2. Every task follows docs/ai/recipes/add-screen.md and the specs in
docs/product/screens.md.

### P3.01 — Quick capture
- **Status:** todo
- **Depends on:** P1.10, P2.04, P2.08
- **Deliverables:** real `CaptureScreen` (focused multi-line field, Save, keyboard done), ViewModel
  calling `CaptureInboxItemUseCase`, "Saved to Inbox" message.
- **Rules:** BR-INBOX-001, BR-INBOX-002.
- **Validation:** ViewModel tests; device: capture from each tab in two taps.

### P3.02 — Inbox list
- **Status:** todo
- **Depends on:** P1.10, P2.06, P2.08
- **Deliverables:** list newest first with ages, edit text, discard with Undo, empty state, badge on
  the Inbox tab (shell observes `observeCount`).
- **Rules:** BR-INBOX-003, BR-INBOX-005, BR-INBOX-006, BR-GEN-005.

### P3.03 — Organize an inbox item
- **Status:** todo
- **Depends on:** P3.02, P3.04, P3.09, P3.11
- **Deliverables:** organize sheet (Task / Appointment / Matter) → conversion → target editor pre-filled.
- **Rules:** BR-INBOX-004.

### P3.04 — Task editor
- **Status:** todo
- **Depends on:** P1.11, P2.05, P2.06
- **Deliverables:** `TaskEditorKey`; title, notes, quick date chips, time, matter link; save with
  title only; validation messages from `DomainError.Validation`.
- **Rules:** BR-TASK-001, BR-TASK-002, BR-GEN-001.

### P3.05 — Task detail and actions
- **Status:** todo
- **Depends on:** P3.04
- **Deliverables:** `TaskDetailKey`; complete/reopen, postpone sheet, dismiss, delete with undo, edit.
- **Rules:** BR-TASK-003, BR-TASK-007, BR-GEN-005.

### P3.06 — All tasks
- **Status:** todo
- **Depends on:** P3.05
- **Deliverables:** `AllTasksKey`; segments Anytime / Upcoming / Overdue / Completed; dismissed filter.
- **Rules:** BR-TASK-009, BR-TASK-004.

### P3.07 — Today
- **Status:** todo
- **Depends on:** P1.14, P2.06, P3.05
- **Deliverables:** full Today per screens.md (summary tiles, what to do today, done today,
  next appointment, attention matters, inbox nudge); completing tasks inline.
- **Rules:** BR-TODAY-002 … BR-TODAY-006.

### P3.08 — Agenda
- **Status:** todo
- **Depends on:** P1.12, P2.05, P2.06
- **Deliverables:** week strip + month label, continuous day list with sticky headers, ordering,
  jump to date, add appointment for a day.
- **Rules:** BR-APPT-005, BR-APPT-007.

### P3.09 — Appointment editor and detail
- **Status:** todo
- **Depends on:** P3.08
- **Deliverables:** `AppointmentEditorKey`, `AppointmentDetailKey`; timed/all-day, range validation,
  location, notes, delete with undo.
- **Rules:** BR-APPT-001 … BR-APPT-003.

### P3.10 — Matters list
- **Status:** todo
- **Depends on:** P1.13, P2.06
- **Deliverables:** groups Needs attention / Waiting / Open / Resolved (collapsed), new matter.
- **Rules:** BR-MATTER-010, BR-MATTER-013.

### P3.11 — Matter detail
- **Status:** todo
- **Depends on:** P3.10, P3.04
- **Deliverables:** `MatterDetailKey`, `MatterEditorKey`; status actions, waiting sheet, resolve
  dialog with open-actions choice, next action card + add action, follow-up date, history timeline,
  notes (add/edit/delete, backdate).
- **Rules:** BR-MATTER-002 … BR-MATTER-009 (UI parts).

### P3.12 — Search
- **Status:** todo
- **Depends on:** P1.15, P2.06
- **Deliverables:** focused field, debounce, grouped results with highlight, empty/no-result states.
- **Rules:** BR-SEARCH-001 … BR-SEARCH-004.

### P3.13 — Settings base
- **Status:** todo
- **Depends on:** P1.09, P2.04
- **Deliverables:** theme (applied by `PrumioApp`), language link to system settings, About
  (version, licenses, source link, privacy statement).
- **Rules:** BR-SET-001.

### P3.14 — Day rollover and polish
- **Status:** todo
- **Depends on:** P3.07
- **Deliverables:** Today/Agenda recompute on date change and resume; empty states reviewed against
  ux-principles.md; all strings reviewed in pt-BR.
- **Rules:** BR-TODAY-007, BR-TIME-003.
- **Validation:** G3 checklist; owner dogfooding for 3 days.

## Gate record

| Date | Result | Notes |
|---|---|---|
| | | |
