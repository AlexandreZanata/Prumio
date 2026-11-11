# Recipe: add or change a table

## Before v0.1 is released (schema version 1 still open)

1. Update [database-schema.md](../../architecture/database-schema.md) and, if the model changes,
   [model.md](../../domain/model.md).
2. Create/edit `core/database/src/commonMain/sqldelight/app/prumio/core/database/<table>.sq`:

   ```sql
   -- Purpose of the table (docs/architecture/database-schema.md#version-1-phase-1)
   CREATE TABLE inbox_item (
       id TEXT NOT NULL PRIMARY KEY,
       text TEXT NOT NULL,
       search_text TEXT NOT NULL,
       created_at INTEGER NOT NULL,
       updated_at INTEGER NOT NULL
   );

   CREATE INDEX inbox_item_created_at ON inbox_item(created_at);

   selectAll:
   SELECT * FROM inbox_item ORDER BY created_at DESC;
   ```
   Respect SQLite 3.18 limits and conventions (database-schema.md#conventions).
3. Regenerate the snapshot: `./gradlew :core:database:generateCommonMainPrumioDatabaseSchema`
   (overwrites `databases/1.db`) and commit it.
4. Add query tests in `core/database/src/androidHostTest`.

## After a release (every change is a migration)

1. Write `core/database/src/commonMain/sqldelight/app/prumio/core/database/<N>.sqm` migrating
   version N → N+1 (create tables, `ALTER TABLE … ADD COLUMN … DEFAULT …`, backfill `UPDATE`s).
2. Update the `.sq` files so `CREATE TABLE` statements describe the **final** schema.
3. `./gradlew :core:database:generateCommonMainPrumioDatabaseSchema` → adds `databases/<N+1>.db`. Commit it.
4. Add a migration data test (rows at N survive at N+1 with correct values).
5. Assess the backup format impact ([backup-format.md](../../domain/backup-format.md)).
6. **Never** edit a released `.sqm` or `.db`.

## Validation

```bash
./gradlew :core:database:testAndroidHostTest :core:database:verifySqlDelightMigration
```
