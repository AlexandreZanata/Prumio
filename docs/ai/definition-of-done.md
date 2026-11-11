# Definition of done

A change is done only when **all** applicable items are true. Agents state in their report which
items were verified and how.

## Always

- [ ] The task's acceptance criteria are met (plan task or owner request).
- [ ] `./scripts/verify.sh` passes locally (formatting, tests, architecture, migrations, privacy, lint, assemble).
- [ ] No test, rule, threshold or gate was weakened or skipped.
- [ ] New code follows the module rules and conventions (no forbidden imports, no duplication).
- [ ] Docs touched by the change are updated in the same commit (see workflow §5).
- [ ] `CHANGELOG.md` has a line under *Unreleased* (user-visible or architectural changes).
- [ ] Commit messages follow Conventional Commits and cite task/rule ids.

## When business behavior changed

- [ ] Every affected rule exists in docs/domain/business-rules.md with its final wording.
- [ ] Each implemented rule is marked `implemented` and proven by a test named with its id.
- [ ] Edge cases from the rule (limits, time zones, empty states) have tests.

## When UI changed

- [ ] Built only from `Prumio*` and `:core:ui` components; new shared components are in the catalog.
- [ ] Strings in English and Brazilian Portuguese; no hard-coded user-facing text.
- [ ] Previews exist for the new/changed screen states (empty, typical, long text).
- [ ] Accessibility: labels, touch targets, not color-only (docs/architecture/accessibility.md).
- [ ] Light and dark themes checked (screenshots from phase 2).

## When data or storage changed

- [ ] docs/domain/model.md and docs/architecture/database-schema.md updated.
- [ ] Schema snapshot (`databases/N.db`) regenerated or migration added with a data test.
- [ ] Backup format impact assessed (docs/domain/backup-format.md).

## When reminders changed

- [ ] Planner tests cover the change, including DST and quiet hours if relevant.
- [ ] Device QA items for reminders listed as run or pending.

## When a phase closes

- [ ] Its validation gate in docs/plan/validation-gates.md is passed and recorded.
