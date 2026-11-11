# Phase 6 — Android complete (v0.3 → v0.9)

**Goal:** every remaining Android capability of the v1.0 scope, plus quality work: accessibility,
localization and performance. **Gate:** G6. Each task can ship in its own minor release.

### P6.01 — Weekly life review
- **Status:** todo
- **Deliverables:** new `:feature:review` (recipe add-feature-module.md), `ReviewKey`, review use
  case and screen, optional weekly reminder (settings + planner), "Done for this week".
- **Rules:** BR-REVIEW-001, BR-REVIEW-002, BR-REVIEW-003, BR-MATTER-011.

### P6.02 — Home screen widgets
- **Status:** todo
- **Deliverables:** ADR for an Android-only widget module (Glance), Today widget (tasks with
  completion, next appointment, "+"), Quick capture widget; updates on data change.

### P6.03 — Share into the inbox
- **Status:** todo
- **Deliverables:** `ACTION_SEND` text/plain target opening Quick capture pre-filled.

### P6.04 — Shortcuts and Quick Settings tile
- **Status:** todo
- **Deliverables:** static launcher shortcuts (Capture, Today, Search), capture tile, `prumio://capture`.

### P6.05 — Voice capture
- **Status:** todo
- **Deliverables:** microphone button in Quick capture using the system speech recognizer
  (on-device when available; no network permission added — privacy gate must stay green).

### P6.06 — Labels
- **Status:** todo
- **Deliverables:** migration (`label`, `label_assignment`), label management, assignment in editors,
  filters in lists, labels in backup format (minor, compatible addition).

### P6.07 — Calendar files (.ics)
- **Status:** todo
- **Deliverables:** export appointments (and dated tasks as VTODO) to `.ics`; import VEVENTs from a
  file; RRULE mapping via the codec; tests with sample files from common calendar apps.

### P6.08 — Encrypted backups
- **Status:** todo
- **Deliverables:** encrypted container per backup-format.md, password UX with clear warnings, tests.
- **Rules:** BR-BACKUP-005.

### P6.09 — Onboarding
- **Status:** todo
- **Deliverables:** three skippable screens (what Prumio is, privacy, optional choices), `onboarding_completed`.

### P6.10 — Large screens
- **Status:** todo
- **Deliverables:** list-detail scenes (Navigation 3 scene strategy) for matters, tasks and agenda on
  tablets/foldables; screenshots at expanded width.

### P6.11 — Accessibility audit
- **Status:** todo
- **Deliverables:** TalkBack pass on every screen (device QA section J), fixes, docs updated.

### P6.12 — Localization completeness
- **Status:** todo
- **Deliverables:** missing-translation check task (fails if a key exists in one language only),
  pseudo-locale screenshots (accented, long, RTL sanity), copy review in pt-BR.

### P6.13 — Performance
- **Status:** todo
- **Deliverables:** baseline profile, startup benchmark (cold start < 1 s target on a mid-range
  profile), 10k-item dataset tests for lists and search, fixes.

## Gate record

| Date | Result | Notes |
|---|---|---|
| | | |
