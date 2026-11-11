# Contributing to Prumio

Thank you for helping! Prumio is developed mostly by AI coding agents directed by humans, so the
process is explicit and the same for everyone.

## Before you start

1. Read [AGENTS.md](AGENTS.md) — the rules apply to humans too.
2. Find or propose a task: [docs/plan/README.md](docs/plan/README.md). For anything not in the plan,
   open an issue first (feature requests must fit [the vision](docs/product/vision.md)).
3. Requirements: JDK 21, Android SDK platform 37, `ANDROID_HOME` set.

## Workflow

1. Branch from `main`: `task/p1-07-inbox-task-repositories` or `fix/<short-name>`.
2. Follow the matching recipe in [docs/ai/recipes/](docs/ai/recipes/README.md).
3. Keep the docs in sync (business rules, component catalog, contracts, module README).
4. Format and validate:
   ```bash
   ./gradlew spotlessApply
   ./scripts/verify.sh
   ```
5. Commit with [Conventional Commits](https://www.conventionalcommits.org/), citing task and rule ids:
   `feat(tasks): complete a task (P1.11, BR-TASK-003)`.
6. Open a pull request using the template; CI must be green.

## Pull request expectations

- One task (or one fix) per PR, small enough to review in one sitting.
- The [definition of done](docs/ai/definition-of-done.md) is satisfied and stated in the PR.
- No new dependencies, modules, permissions or rule changes without prior agreement (ADR).
- Never commit secrets, keystores or `local.properties`.

## Using AI agents

Agents must be pointed at `AGENTS.md` (Claude Code loads it through `CLAUDE.md` automatically).
Review agent output like any contribution: the gates catch structural mistakes, not wrong product
decisions. The human submitting the PR is responsible for it.

## Translations

UI strings exist in English and Brazilian Portuguese. Improvements to wording are welcome; new
languages need a discussion first (docs/architecture/i18n.md).

## Code of conduct

Be kind, assume good intent, keep discussions about the work. Harassment of any kind is not
tolerated; maintainers may remove content and contributors that violate this.

## License

By contributing you agree that your contributions are licensed under the Apache License 2.0.
