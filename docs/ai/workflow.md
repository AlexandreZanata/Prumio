# Agent workflow

The loop every AI agent follows for every task. It is deliberately boring: predictable steps make
AI-built software reliable.

## 0. Orient (once per session)

1. Read [AGENTS.md](../../AGENTS.md) completely.
2. Read [docs/README.md](../README.md) and [architecture/overview.md](../architecture/overview.md).
3. Run `git status` and `./scripts/verify.sh` to know the starting point is green. If it is red and
   you did not cause it, report it before doing anything else.

## 1. Understand the task

1. Locate the task in `docs/plan/phase-*.md` (id `P<phase>.<nn>`). If the owner gave a free-form
   request, map it to a plan task or write a short task description in the same format first.
2. Read every **Input** the task lists, the README of each module it touches, and the business
   rules it cites.
3. Write down (in your response or scratchpad) the acceptance criteria in your own words and the
   files you expect to change. If anything is ambiguous or contradicts a doc → stop and ask.

## 2. Search before creating

Before writing any new symbol, search for an existing one:

| Need | Search |
|---|---|
| UI element | [design-system/components.md](../design-system/components.md), then `rg "fun Prumio" core/designsystem` |
| Domain operation | `rg "UseCase" core/domain`, [architecture/contracts.md](../architecture/contracts.md) |
| String | `rg "<text>" --glob "*.xml" core/ui feature` |
| Helper / formatter | `core/common`, `core/ui/.../format`, `core/ui/.../label` |
| Test double | `core/testing` |

Reuse by reference. If something almost fits, extend it (parameter, variant) rather than copy it.

## 3. Implement

1. Follow the matching recipe in [recipes/](recipes/README.md).
2. Domain first, then data, then presentation. Tests alongside each layer — for business rules,
   write the test first, named after the rule id.
3. Keep commits small and focused; message format in
   [architecture/conventions.md](../architecture/conventions.md#commits-and-branches).
4. Run `./gradlew spotlessApply` before committing.

## 4. Validate

1. Run the task's **Validation** commands.
2. Run `./scripts/verify.sh` — all gates must pass. Never edit a test, a rule, a threshold or a
   gate to make it pass unless the task is explicitly about that gate.
3. For UI changes, check the preview (and screenshots from phase 2); for device behavior, run the
   relevant section of the [device QA checklist](../plan/qa/device-qa-checklist.md) if you can, or
   list it as pending for the owner.

## 5. Update docs and status

In the same change:
- Business rules you implemented → `Status: implemented` in [business-rules.md](../domain/business-rules.md).
- Components you added → [components.md](../design-system/components.md).
- Contracts you changed → [contracts.md](../architecture/contracts.md), model.md, database-schema.md.
- Module public API changed → that module's README.md.
- Task status → `done` in the phase file and in the status board of [plan/README.md](../plan/README.md).
- `CHANGELOG.md` → one line under *Unreleased*.
- A decision with alternatives → new ADR.

## 6. Self-review and report

1. Go through [review-checklist.md](review-checklist.md).
2. Report to the owner: what changed (files/modules), which rules/tasks are done, how it was
   validated (commands and results), what is pending or uncertain, and any follow-up you recommend.
   Be factual: if something was not run or did not pass, say so.

## Escalate instead of guessing

Stop and ask the owner when the task needs: a new dependency or module, a change to module rules,
a schema change after a release, a change to backup format, anything about privacy, anything about
iOS/other platforms, or when two documents disagree.
