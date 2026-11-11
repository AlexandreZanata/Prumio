# Data layer

How the domain ports are implemented on SQLite. Modules: `:core:database` (SQL) and `:core:data`
(Kotlin adapters). Ports: [contracts.md](contracts.md). Schema: [database-schema.md](database-schema.md).

## Shape of a repository

```kotlin
internal class SqlDelightTaskRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : TaskRepository {
    private val queries = database.taskQueries

    override fun observeOpenUndated(): Flow<List<Task>> =
        queries.selectOpenUndated().asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toTask() } }

    override suspend fun upsert(task: Task) = withContext(dispatchers.io) {
        queries.transaction {
            queries.update(task.toParams())
            if (queries.changes().executeAsOne() == 0L) queries.insert(task.toParams())
        }
    }
}
```

Registered in `coreDataModule`: `singleOf(::SqlDelightTaskRepository) bind TaskRepository::class`.

## Rules

1. **Reads are reactive**: `Flow` from `query.asFlow().mapToList(dispatchers.io)` (or `mapToOneOrNull`).
   SQLDelight re-emits when any query touching the same tables commits.
2. **Writes are suspend and atomic**: `withContext(dispatchers.io) { database.transaction { … } }`.
   The transaction body is synchronous (no suspension inside a transaction).
3. **Multi-table consistency belongs to one port method.** When the domain needs two changes to be
   atomic (save a matter and append its history; convert an inbox item), the port exposes one
   intent-revealing method and the adapter runs one transaction. Use cases never coordinate
   transactions themselves.
4. **Mapping** lives next to the repository in `<Table>Mappers.kt`: `Row.toModel()` and
   `Model.toParams()`. Enum ↔ text via explicit `when` (never `valueOf` on stored strings without a
   fallback) so renaming a Kotlin enum never corrupts data.
5. **No business logic**: no defaults, validation or rule decisions in adapters. If a query needs a
   rule (e.g. "needs attention"), fetch candidates in SQL and apply the rule in the domain.
6. **Errors**: storage exceptions are caught at the port boundary of operations that return
   `Outcome` and wrapped in `DomainError.Storage(cause)`; elsewhere they propagate (crash = bug).
7. **Search text**: every write of a searchable entity also writes its normalized `search_text`
   (BR-SEARCH-002) using the domain's `SearchTextNormalizer`.
8. **Timestamps** are passed in by use cases (from `TimeProvider`); adapters never read the clock.

## Transactions and SQLite version

- minSdk 26 ships SQLite 3.18: no `UPSERT … DO UPDATE`, no `RETURNING`, no window functions. Use
  update-then-insert (above) for upserts on tables referenced by foreign keys.
- Foreign keys are enabled on every connection (`databaseDriverModule`).
- `INSERT OR REPLACE` is allowed only on leaf tables (nothing references them): `setting`,
  `snooze`, `scheduled_alarm`, `delivered_alarm`.

## Testing adapters

- Tests run on the JVM against a real in-memory SQLite (`JdbcSqliteDriver(IN_MEMORY)` +
  `PrumioDatabase.Schema.create(driver)`), in `src/androidHostTest`.
- Every repository method has a test; every `Flow` has a test proving it re-emits after a write (Turbine).
- Mappers round-trip: `model.toParams()` → insert → select → `toModel()` equals the original.
