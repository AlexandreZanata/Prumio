# Phase 5 — Automation and peace of mind (release v0.2)

**Goal:** Prumio handles repetition, follow-ups and backups so the user can stop thinking about
them. **Gate:** G5 → release **v0.2**. **Depends on:** phase 4 (v0.1 released: from now on every
schema change is a migration).

### P5.01 — Recurrence codec
- **Status:** todo
- **Deliverables:** `RecurrenceRuleCodec` (RRULE subset parse/format), rejection of unsupported parts,
  round-trip property tests.
- **Rules:** BR-REC-001, BR-REC-002.

### P5.02 — Occurrence engine
- **Status:** todo
- **Depends on:** P5.01
- **Deliverables:** `occurrences(rule, first, from, to, limit)` per recurrence.md, month-end clamping,
  DST handling, performance test.
- **Rules:** BR-REC-003, BR-REC-004.

### P5.03 — Recurring tasks
- **Status:** todo
- **Depends on:** P5.02
- **Deliverables:** migration 1 → 2 (`task_completion`), completion advance (both bases), end of
  series, undo; planner uses the current due date.
- **Rules:** BR-TASK-005, BR-REC-005, BR-REC-006, BR-REC-007.

### P5.04 — Repeat editor
- **Status:** todo
- **Depends on:** P5.01
- **Deliverables:** presets + custom editor component in `:core:ui`, recurrence labels en + pt-BR,
  used by task and appointment editors.

### P5.05 — Recurring appointments
- **Status:** todo
- **Depends on:** P5.02
- **Deliverables:** migration (`appointment_exception`), expansion in agenda and Today, edit/delete
  scopes, reminders per occurrence.
- **Rules:** BR-REC-008.

### P5.06 — Multiple and custom reminders
- **Status:** todo
- **Deliverables:** up to 5 offsets per item, custom offset entry, planner tests.

### P5.07 — Matter follow-up reminders
- **Status:** todo
- **Deliverables:** follow-up anchors in the planner, Follow-ups channel content, Snooze → Tomorrow.
- **Rules:** BR-MATTER-009, BR-MATTER-011 prerequisite data.

### P5.08 — Morning summary
- **Status:** todo
- **Deliverables:** settings (enabled, time), summary computation at delivery time, skip when empty.
- **Rules:** BR-REM-012.

### P5.09 — Quiet hours
- **Status:** todo
- **Deliverables:** settings, deferral of deferrable alarms (including across midnight), tests.
- **Rules:** BR-REM-010, BR-REM-011.

### P5.10 — Backup export and import
- **Status:** todo
- **Deliverables:** `BackupCodec` (format v1), golden file test, `BackupRepository` (one
  transaction), `BackupFileGateway` (Storage Access Framework), safety backup, confirmation with
  counts, Settings → Backup screen.
- **Rules:** BR-BACKUP-001, BR-BACKUP-002, BR-BACKUP-003, BR-BACKUP-006, BR-SET-003, BR-PRIV-003.

### P5.11 — Automatic backups
- **Status:** todo
- **Depends on:** P5.10
- **Deliverables:** daily WorkManager job writing to a persisted SAF folder, keep 7, status in Settings.
- **Rules:** BR-BACKUP-004.

### P5.12 — Appointments linked to matters
- **Status:** todo
- **Deliverables:** link/unlink in editors, related appointments in matter detail (F-MAT-05).

### P5.13 — Release v0.2
- **Status:** todo
- **Depends on:** P5.01 … P5.12
- **Deliverables:** G5 checklist executed, version 0.2.0, release notes en + pt-BR.

## Gate record

| Date | Result | Notes |
|---|---|---|
| | | |
