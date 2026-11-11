# Backup format

The contract of Prumio backup files. Being offline must never mean losing everything when a phone
breaks. Rules: BR-BACKUP-001 … BR-BACKUP-006, BR-SET-003. Implemented in phase 5
(`:core:domain/backup` ports, `:core:data/backup` serializer).

## File

- Name: `prumio-backup-YYYYMMDD-HHmm.json` (local time of export). Automatic backups:
  `prumio-auto-YYYYMMDD.json`.
- Encoding: UTF-8 JSON, no BOM. Encrypted backups (phase 6) wrap the same JSON (see below).
- One file contains everything; no attachments exist in Prumio.

## Version 1 schema

```json
{
  "format": "prumio-backup",
  "formatVersion": 1,
  "appVersion": "0.2.0",
  "exportedAt": "2026-11-11T14:30:00Z",
  "data": {
    "inboxItems":   [{ "id": "…", "text": "…", "createdAt": "…", "updatedAt": "…" }],
    "tasks":        [{ "id": "…", "title": "…", "notes": null, "status": "OPEN",
                       "dueDate": "2026-11-12", "dueTime": "09:00",
                       "recurrence": "FREQ=WEEKLY;BYDAY=MO", "recurrenceBasis": "FROM_DUE_DATE",
                       "matterId": null, "completedAt": null, "dismissedAt": null,
                       "createdAt": "…", "updatedAt": "…" }],
    "taskCompletions": [{ "id": "…", "taskId": "…", "occurrenceDate": "2026-11-09", "completedAt": "…" }],
    "appointments": [{ "id": "…", "title": "…", "notes": null, "location": null, "allDay": false,
                       "startDate": "2026-11-12", "startTime": "16:00",
                       "endDate": "2026-11-12", "endTime": null, "timeZone": "America/Sao_Paulo",
                       "recurrence": null, "matterId": null, "createdAt": "…", "updatedAt": "…" }],
    "appointmentExceptions": [],
    "matters":      [{ "id": "…", "title": "…", "description": null, "status": "WAITING",
                       "waitingFor": "Dispatcher", "followUpDate": "2026-11-14", "followUpTime": null,
                       "resolvedAt": null, "lastActivityAt": "…", "createdAt": "…", "updatedAt": "…" }],
    "matterEntries": [{ "id": "…", "matterId": "…", "kind": "STATUS_CHANGED", "text": null,
                        "fromStatus": "OPEN", "toStatus": "WAITING", "taskId": null,
                        "occurredOn": "2026-11-11", "createdAt": "…", "updatedAt": "…" }],
    "reminders":    [{ "id": "…", "ownerType": "TASK", "ownerId": "…", "offsetMinutes": 0, "createdAt": "…" }],
    "labels": [], "labelAssignments": [],
    "settings":     { "default_reminder_time": "09:00", "quiet_hours_enabled": "false" }
  }
}
```

Rules:

- Instants are ISO-8601 UTC strings; local dates/times use the formats of
  [time-semantics.md](time-semantics.md). Enums are `UPPER_SNAKE_CASE`.
- Ids are preserved exactly (BR-GEN-003).
- Excluded: scheduling state (scheduled alarms, snoozes, deliveries) and device-specific settings
  (`auto_backup_folder`, `last_backup_at`, `onboarding_completed`).
- Unknown fields are ignored on import (forward-compatible within a version); missing optional
  fields take defaults; missing required fields make the file invalid.

## Import algorithm (BR-BACKUP-002, BR-BACKUP-003)

1. Parse the whole file. Reject if `format` ≠ `prumio-backup`, `formatVersion` > supported, or any
   entity fails validation (same rules as the domain). Report the first errors in plain language.
2. Show counts (current vs file) and ask for confirmation.
3. Write a safety backup of the current data to app-private storage.
4. In **one transaction**: delete all user data, insert everything from the file.
5. Reconcile reminders. Report success with counts.

Any failure in step 4 rolls back; the user's data stays as it was.

## Versioning

- `formatVersion` increases only for incompatible changes. Each new version ships a migration
  `vN → vN+1` applied at import; the app can always import every older version.
- A golden file per version lives in `core/data/src/androidHostTest/resources/backup/` and is
  imported by a test forever.

## Encrypted container (phase 6, BR-BACKUP-005)

```json
{ "format": "prumio-backup-encrypted", "formatVersion": 1,
  "kdf": { "name": "PBKDF2-HMAC-SHA256", "iterations": 310000, "salt": "<base64 16 bytes>" },
  "cipher": { "name": "AES-256-GCM", "nonce": "<base64 12 bytes>" },
  "payload": "<base64 ciphertext of the plain v1 JSON>" }
```
