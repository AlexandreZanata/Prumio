@AGENTS.md

## Claude Code notes

- Each module folder has a `CLAUDE.md` that imports its `README.md` (the module contract), so it is
  loaded automatically when you work inside that module. Read it; it lists the module's public API,
  allowed dependencies and extension recipes.
- Prefer plan mode for tasks that touch more than one module.
- Use subagents for broad read-only exploration; keep edits in the main session.
- Run `./scripts/verify.sh` in the background for long runs and read its summary at the end.
