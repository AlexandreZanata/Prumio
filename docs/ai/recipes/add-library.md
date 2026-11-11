# Recipe: add a third-party library

Every dependency is a long-term cost and a privacy risk. **Owner approval is required** (AGENTS.md §6).

## Acceptance criteria for a library

- Solves a real need that cannot be met with the existing stack in reasonable code.
- Kotlin Multiplatform (for `commonMain`) or confined to `androidMain` behind an interface.
- Open-source license compatible with Apache-2.0; actively maintained; stable release.
- **No network access, analytics, tracking or account features** (BR-PRIV-002; `PrivacyTest`
  rejects known SDKs and the privacy gate rejects `INTERNET`).
- Small impact on APK size and startup.

## Steps

1. Write an ADR (`docs/adr/NNNN-<library>.md`) with the need, alternatives and the decision.
2. Add the version and coordinates to `gradle/libs.versions.toml` (correct group, alphabetical).
3. Use it from the single module that needs it; expose your own types, not the library's, across modules.
4. Run `./scripts/verify.sh`; check the merged manifest gained no permissions
   (`app/android/build/intermediates/merged_manifests/`).
5. Mention the library in the relevant architecture doc.
