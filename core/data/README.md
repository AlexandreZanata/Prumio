# `:core:data`

> Adapters that implement the domain ports on top of the database: repositories, mappers,
> settings store and the backup serializer.

| | |
|---|---|
| **Layer** | Data |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.data` |
| **May depend on** | `:core:common`, `:core:database`, `:core:domain`, `:core:model` |
| **Used by** | `:app:shell` (DI assembly only) |

## Responsibility

- `SqlDelight<Aggregate>Repository` classes implementing the `<Aggregate>Repository` ports of `:core:domain`.
- Mapping between generated SQLDelight rows and `:core:model` types (`<Table>Mappers.kt`).
- Atomic multi-table writes required by the domain (one transaction per port method).
- Settings persistence (`SettingKey` registry → `setting` table).
- Backup export/import serialization (phase 5, docs/domain/backup-format.md).

Detailed rules: [docs/architecture/data-layer.md](../../docs/architecture/data-layer.md).

## Package layout

```
app.prumio.core.data
├── storage/     ColumnMappers (time/enum formats), RowMappers (row → model), Writes (shared upserts
│                that also maintain search_text; used inside transactions)
├── inbox/ task/ appointment/ matter/ search/ settings/   SqlDelight<Aggregate>Repository
├── backup/                                               phase 5
└── di/          coreDataModule (binds every port)
```

Never write an entity outside `storage/Writes.kt`: conversions, matters and tasks all reuse it.

## Rules

- No business decisions: validate nothing that the domain already validated, compute no rule.
- Reads return `Flow` (via `asFlow().mapToList(dispatchers.io)`); writes are `suspend` and run on `dispatchers.io`.
- Wrap storage exceptions into `DomainError.Storage` at the port boundary.
- Nothing outside `:app:shell` may depend on this module (enforced by the build).

## How to extend

New repository → [docs/ai/recipes/add-repository.md](../../docs/ai/recipes/add-repository.md).

## Tests

`./gradlew :core:data:testAndroidHostTest`. Repository tests use a real in-memory SQLite database
(`src/androidHostTest`) — never mocks of SQLDelight.
