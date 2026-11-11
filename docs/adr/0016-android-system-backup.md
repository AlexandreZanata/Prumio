# ADR-0016: Allow Android system backup of the database

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Being offline must not mean losing everything when a phone breaks. Android offers system backup
(cloud, end-to-end encrypted on Android 9+ with a screen lock) and device-to-device transfer, both
performed by the OS and controlled by the user in system settings.

## Decision

- `android:allowBackup="true"` with explicit rules including the database and preferences
  (`data_extraction_rules.xml`, `backup_rules.xml`).
- Prumio's own export/import (phase 5) remains the primary, user-controlled backup.

## Consequences

- Users who keep system backup enabled recover their data on a new phone automatically.
- Users who disable system backup are not affected. PRIVACY.md explains this clearly.
- Scheduling state is restored too and is reconciled on first start (harmless).

## Alternatives considered

- **Disable system backup** — maximal isolation, but frequent data loss for ordinary users.
