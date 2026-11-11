<p align="center"><img src="docs/brand/logo/prumio-logo-rounded-1024.png" alt="Prumio logo" width="128"></p>

# Prumio

**Your life in order. Your mind at ease.**

Prumio is a free, open-source, offline-first personal organizer for tasks, reminders,
appointments and life's unfinished business. Private by design. No account required.

[![CI](https://github.com/AlexandreZanata/Prumio/actions/workflows/ci.yml/badge.svg)](https://github.com/AlexandreZanata/Prumio/actions/workflows/ci.yml)
[![License: Apache 2.0](https://img.shields.io/badge/License-Apache_2.0-blue.svg)](LICENSE)

> **Status: foundation (phase 0 of 7).** The architecture, documentation, build and quality gates
> are in place; features are being built phase by phase. See the [roadmap](docs/plan/README.md).

---

## Why Prumio

Most organizers become one more thing you have to manage. Prumio is built on one idea:
**the app should remember things for you**, so they stop occupying your head.

It separates three kinds of commitments that usually get mixed up:

| | Question it answers | Example |
|---|---|---|
| **Tasks** | What do I need to do? | Pay the internet bill · Buy groceries |
| **Agenda** | Where do I need to be? | Doctor appointment Thursday 16:00 |
| **Matters** | What do I need to resolve in my life? | Sort out the motorcycle documents — *waiting for a reply* |

**Matters** are Prumio's signature: ongoing subjects with a *next action*, a short *history* and a
*waiting* state, so nothing unfinished silently disappears.

And because thoughts arrive at the worst moments, **capture takes seconds**: type a sentence, save,
done. It lands in the **Inbox** and you organize it later — or never, if it stops mattering.

### Principles

- **Zero effort to capture.** A title is enough to save anything. Everything else is optional.
- **Attention without excess.** Useful reminders; no streaks, scores, badges or guilt.
- **Nothing disappears by being ignored.** A task stays open until you complete, reschedule or dismiss it.
- **Private by design.** No network permission at all, no account, no telemetry, no ads. Your data stays on your device; backups go where you choose.
- **Predictable.** Plain local rules you can read in [business-rules.md](docs/domain/business-rules.md). No AI, no remote processing.

## Features (planned for Android v1.0)

- Quick capture from anywhere (button, widget, share sheet, shortcut, voice) into the Inbox
- Tasks with optional date, time, recurrence and reminders
- Agenda with appointments, advance notices and recurring events
- Matters with status (Open, Waiting, Resolved), next action, follow-up date and history
- Today: your day at a glance — tasks, next appointment, matters that need attention
- Offline reminders: snooze, quiet hours, optional morning summary, follow-ups
- Weekly life review: stalled matters, matters without next action, what's coming
- Fast accent-insensitive search
- Backup and restore to a file you control (optionally encrypted), calendar export (.ics)
- English and Brazilian Portuguese

Full catalog with release targets: [docs/product/features.md](docs/product/features.md).

## Technology

Kotlin only. [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) architecture with
**Android as the single target today**; shared code is kept platform-neutral so iOS can be added later.

| Layer | Technology |
|---|---|
| UI | Compose Multiplatform, Material 3 (wrapped by the Prumio design system) |
| Navigation | Navigation 3 |
| Presentation | Unidirectional data flow (State / Action / Effect) with ViewModels |
| Domain | Pure Kotlin use cases and rule engines (recurrence, reminder planning) |
| Persistence | SQLite via SQLDelight |
| Dependency injection | Koin |
| Concurrency | Coroutines and Flow |
| Notifications | AlarmManager + NotificationManager, derived from data |
| Quality gates | Konsist architecture tests, ktlint + Compose rules, module dependency rules, privacy gate |

Architecture overview: [docs/architecture/overview.md](docs/architecture/overview.md).

## Build and run

Requirements: JDK 21 and the Android SDK (platform 37) with `ANDROID_HOME` set.

```bash
./gradlew :app:android:installDebug   # build and install on a connected device or emulator
./scripts/verify.sh                   # the full quality gate: formatting, tests, architecture, privacy, APK
```

## Built with AI, documented for AI

Prumio is developed mostly by AI coding agents under human direction. The repository is designed for
that: explicit contracts, stable business-rule IDs, one recipe per kind of change, and automated gates
that fail fast with actionable messages. Agents start at [AGENTS.md](AGENTS.md).

## Documentation

| Start here | |
|---|---|
| [docs/README.md](docs/README.md) | Map of all documentation |
| [Product vision](docs/product/vision.md) | Philosophy, scope and non-goals |
| [Domain model](docs/domain/model.md) and [business rules](docs/domain/business-rules.md) | The logical contracts |
| [Architecture](docs/architecture/overview.md) and [modules](docs/architecture/modules.md) | How the code is organized |
| [Design system](docs/design-system/README.md) | Tokens and the component catalog |
| [Execution plan](docs/plan/README.md) | Phases, tasks and validation gates |
| [ADRs](docs/adr/README.md) | Why things are the way they are |

## Contributing

Contributions are welcome — from humans and from agents driven by humans. Read
[CONTRIBUTING.md](CONTRIBUTING.md). Privacy policy: [PRIVACY.md](PRIVACY.md). Security: [SECURITY.md](SECURITY.md).

## License

[Apache License 2.0](LICENSE). "Prumio" is a working name, pending trademark availability checks.
