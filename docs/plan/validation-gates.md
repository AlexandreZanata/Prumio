# Validation gates

A phase is closed only when its gate passes. Gates combine **automated checks** (CI and
`./scripts/verify.sh`), **manual checks** (device, UX) and **documentation checks**, and end with the
owner's sign-off recorded in the phase file.

## Checks that apply to every task (continuous)

| Check | How |
|---|---|
| Formatting, unit tests, architecture, migrations, privacy, lint, assemble | `./scripts/verify.sh` locally and in CI |
| Rules traceability | `BusinessRuleTraceabilityTest` (part of verify) |
| Definition of done | [docs/ai/definition-of-done.md](../ai/definition-of-done.md) in the agent report |

## G0 — Foundation

Automated:
- [x] `./scripts/verify.sh` passes locally.
- [ ] CI workflow green on `main` (first push).

Manual:
- [ ] Debug APK installs and launches on an emulator (API 26) and on a recent device/emulator (API 36+).
- [ ] Bottom bar shows Today · Agenda · Matters · Inbox; tabs switch; back returns to Today.
- [ ] "Add anything" opens the Quick capture placeholder dialog; Close dismisses it.
- [ ] Today shows the localized date and greeting; Search/Settings open and go back.
- [ ] Device language Portuguese (Brazil) shows pt-BR strings; dark theme renders correctly.

Docs:
- [x] AGENTS.md, docs map, domain model, business rules, architecture, design system, recipes, plan, ADRs exist.

Sign-off: owner reviews the documentation and approves the architecture.

## G1 — Domain and persistence

Automated:
- [ ] verify green; Kover: `:core:domain` ≥ 90 % lines, `:core:data` ≥ 80 %.
- [ ] Every rule listed in phase 1 tasks is `implemented` and cited by tests.
- [ ] `KoinModulesTest` resolves every repository and use case.
- [ ] Schema snapshot regenerated; `verifySqlDelightMigration` green.

Manual / review:
- [ ] Owner reads `contracts.md` vs the code (ports match), spot-checks 5 rules end-to-end (doc → code → test).

## G2 — Design system

Automated:
- [x] Screenshot tests for every catalog component (light, dark, 200 % font) recorded and verified in CI.
- [x] Contrast test passes for all text/background token pairs.
- [x] Catalog test green (every component documented).

Manual:
- [ ] Owner reviews the screenshot gallery and approves the visual language.

## G3 — Organize (functional v0.1 without reminders)

Automated:
- [x] All BR-INBOX/TASK/APPT/MATTER/TODAY/SEARCH rules of phases 1–3 `implemented`.
- [x] ViewModel tests for every screen; screenshot tests for every screen state.
- [x] No placeholder screen remains in Today, Agenda, Matters, Inbox, Capture, Search.

Manual ([device QA](qa/device-qa-checklist.md) sections A–F, H, I):
- [x] Capture → organize → complete flows work in English and pt-BR.
- [ ] Owner dogfoods for at least 3 days and logs friction issues; blocking ones fixed.

Local implementation evidence is recorded in [phase-3-organize.md](phase-3-organize.md#gate-record).
The reminder portion of BR-MATTER-009 is implemented in phase 4; its date/time/history
logic and Phase 3 UI remain covered. The owner’s dogfooding and final sign-off remain open.

## G4 — Reminders (release v0.1)

Automated:
- [x] Planner and reconciler tests cover every BR-REM rule of phase 4, including DST and time zone travel.
- [ ] Instrumented alarm tests pass on physical API 26 and the latest available device (physical-only owner instruction in AGENTS.md).
- [x] API 26/36 Android host adapter tests; API 36 physical exact/inexact alarm → notification → content intent → Complete tests.

Manual (device QA section G, all items):
- [ ] Reminders fire on time with exact alarms; are late but delivered without; survive reboot,
      app update, time zone change, Doze; snooze and complete from notification work.
- [x] Permission flows (granted, denied, denied twice) behave per BR-REM-014 (physical API 36; en/pt-BR).
- [ ] Owner uses v0.1 for 7 days with real reminders; no missed reminder without explanation.

Release:
- [ ] Version 0.1.0, CHANGELOG, signed APK attached to a GitHub release.

Local phase-4 evidence and remaining release work: [phase-4-reminders.md](phase-4-reminders.md#gate-record).
The owner requested signing setup and an unsigned artifact while no release key exists.

## G5 — Automation (release v0.2)

Automated:
- [ ] Recurrence property tests and table tests (recurrence.md) pass; DST in 3 zones.
- [ ] Backup golden file test (v1) passes; export → import round-trip equals original data.
- [ ] First real migration verified (schema 1 → 2) with data tests.

Manual:
- [ ] Restore a backup on a second device/emulator; data and reminders reappear.
- [ ] Morning summary, quiet hours and follow-ups observed on a device over 2 days.

## G6 — Android complete

Automated:
- [ ] Startup benchmark within budget (cold start < 1 s on a mid-range device profile); 10k-item dataset tests.
- [ ] Screenshot tests in pseudo-locales and 200 % font.

Manual:
- [ ] Full TalkBack pass (device QA section J) with no blocker.
- [ ] Widgets, share target, shortcuts, tile, voice capture verified on 2 launchers.
- [ ] ICS export opens in another calendar app; import of a sample file works.

## G7 — Release v1.0 (project complete)

- [ ] Device matrix QA passed (API 26, 30, 34, latest; one Samsung, one Pixel or AOSP-like, one low-RAM device).
- [ ] Store listing, privacy policy, data safety form complete; store review passed.
- [ ] v1.0.0 tagged, CHANGELOG final, release published.
- [ ] Owner sign-off: "Android 100%".
