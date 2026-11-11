# `:core:database`

> The SQLite schema, queries and migrations (SQLDelight), plus the platform driver.

| | |
|---|---|
| **Layer** | Data (storage) |
| **Plugin** | `prumio.kmp.library` + `app.cash.sqldelight` |
| **Package** | `app.prumio.core.database` (generated `PrumioDatabase`) |
| **May depend on** | `:core:common` |
| **Used by** | `:core:data`, `:app:shell` (DI assembly only) |

## Responsibility

- `.sq` files: one per table, containing `CREATE TABLE`, indices and the named queries for that table.
- `.sqm` files: numbered migrations (`1.sqm` migrates schema version 1 → 2).
- `databases/*.db`: committed schema snapshots used by `verifySqlDelightMigration`.
- Driver creation per platform (`databaseDriverModule`, expect/actual) with foreign keys enabled.

Specification: [docs/architecture/database-schema.md](../../docs/architecture/database-schema.md).

## Public API

| Symbol | Contract |
|---|---|
| `PrumioDatabase` (generated) and its `*Queries` | Raw typed access, used only by `:core:data` |
| `coreDatabaseModule` | Koin: `SqlDriver` + `PrumioDatabase` singletons |
| `DATABASE_FILE_NAME` | `prumio.db`. Never rename |

## Rules

- SQL targets **SQLite 3.18** (Android 8.0, our minSdk): no `UPSERT … ON CONFLICT DO UPDATE`,
  no `RETURNING`, no window functions. Use `INSERT OR REPLACE` only on tables without foreign keys
  pointing at them; otherwise `UPDATE` then `INSERT`.
- Table and column names are `snake_case`; ids are `TEXT` UUIDs; instants are `INTEGER` epoch
  milliseconds (UTC); local dates/times are ISO-8601 `TEXT` (docs/domain/time-semantics.md).
- Never edit a released migration or `.db` snapshot. Schema changes go through a new migration
  (docs/ai/recipes/add-database-table.md).
- No business logic in SQL beyond filtering and ordering.

## Tests

`./gradlew :core:database:testAndroidHostTest` (in-memory JDBC SQLite driver, `src/androidHostTest`).
Migration verification runs in `check`.
