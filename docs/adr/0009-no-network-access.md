# ADR-0009: Ship without any network access

- **Status:** Accepted
- **Date:** 2026-11-11
- **Deciders:** Project owner

## Context

Prumio's promise is privacy by design: no account, no telemetry, data stays on the device. The
strongest, user-verifiable guarantee is the absence of the `INTERNET` permission.

## Decision

- The app manifest removes `android.permission.INTERNET` (`tools:node="remove"`).
- `VerifyNoNetworkPermissionTask` runs in `check` for every variant and fails if the merged
  manifest requests it.
- `PrivacyTest` rejects known analytics, crash-reporting, ads and account SDKs (BR-PRIV-002).
- Links to external pages (source code, help) open in the user's browser.

## Consequences

- No sync, remote crash reports, remote config or online features until the owner decides
  otherwise through a new ADR.
- Libraries that require network cannot be used.

## Alternatives considered

- **Network allowed but unused** — weaker guarantee, not verifiable by users.
