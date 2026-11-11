# Database schema

SQLite via SQLDelight, file `prumio.db` in app-private storage. One `.sq` file per table in
`core/database/src/commonMain/sqldelight/app/prumio/core/database/`. Derived from
[domain/model.md](../domain/model.md); formats from [domain/time-semantics.md](../domain/time-semantics.md).

## Conventions

- Tables and columns: singular `snake_case`. Primary key `id TEXT NOT NULL PRIMARY KEY` (UUID).
- Instants: `INTEGER` epoch ms, suffix `_at`. Local dates: `TEXT` `yyyy-MM-dd`, suffix `_date`.
  Local times: `TEXT` `HH:mm`, suffix `_time`. Booleans: `INTEGER` 0/1 (`AS Boolean` adapter).
- Enums: `TEXT` in `UPPER_SNAKE_CASE`, mapped explicitly in `:core:data`.
- Every foreign key declares `ON DELETE` behavior explicitly.
- Index every column used in a `WHERE` or `ORDER BY` of a frequent query.

## Version 1 (phase 1)

Status: `setting` exists (phase 0). Phase 1 task P1.04 adds the rest. **Until the first public
release (v0.1), schema version 1 may still be edited and `databases/1.db` regenerated**; after
release, every change is a migration (below).

```sql
CREATE TABLE setting (                       -- exists
    key TEXT NOT NULL PRIMARY KEY,
    value TEXT NOT NULL,
    updated_at INTEGER NOT NULL
);

CREATE TABLE inbox_item (
    id TEXT NOT NULL PRIMARY KEY,
    text TEXT NOT NULL,
    search_text TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
CREATE INDEX inbox_item_created_at ON inbox_item(created_at);

CREATE TABLE matter (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    description TEXT,
    status TEXT NOT NULL,                    -- OPEN | WAITING | RESOLVED
    waiting_for TEXT,
    follow_up_date TEXT,
    follow_up_time TEXT,
    resolved_at INTEGER,
    last_activity_at INTEGER NOT NULL,
    search_text TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
CREATE INDEX matter_status ON matter(status);
CREATE INDEX matter_follow_up_date ON matter(follow_up_date);

CREATE TABLE matter_entry (
    id TEXT NOT NULL PRIMARY KEY,
    matter_id TEXT NOT NULL REFERENCES matter(id) ON DELETE CASCADE,
    kind TEXT NOT NULL,                      -- CREATED | NOTE | STATUS_CHANGED | ACTION_* | FOLLOW_UP_*
    text TEXT,
    from_status TEXT,
    to_status TEXT,
    task_id TEXT,                            -- no FK: entries keep history after a task is deleted
    occurred_on TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL
);
CREATE INDEX matter_entry_matter ON matter_entry(matter_id, occurred_on, created_at);

CREATE TABLE task (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    notes TEXT,
    status TEXT NOT NULL,                    -- OPEN | COMPLETED | DISMISSED
    due_date TEXT,
    due_time TEXT,
    recurrence TEXT,                         -- RRULE subset (phase 5)
    recurrence_basis TEXT,                   -- FROM_DUE_DATE | FROM_COMPLETION (phase 5)
    matter_id TEXT REFERENCES matter(id) ON DELETE SET NULL,
    completed_at INTEGER,
    dismissed_at INTEGER,
    search_text TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    CHECK (due_time IS NULL OR due_date IS NOT NULL),
    CHECK (recurrence IS NULL OR due_date IS NOT NULL)
);
CREATE INDEX task_status_due ON task(status, due_date, due_time);
CREATE INDEX task_matter ON task(matter_id);

CREATE TABLE appointment (
    id TEXT NOT NULL PRIMARY KEY,
    title TEXT NOT NULL,
    notes TEXT,
    location TEXT,
    all_day INTEGER NOT NULL,
    start_date TEXT NOT NULL,
    start_time TEXT,                         -- NULL iff all_day
    end_date TEXT NOT NULL,
    end_time TEXT,
    time_zone TEXT,                          -- IANA id; NULL iff all_day
    recurrence TEXT,                         -- phase 5
    matter_id TEXT REFERENCES matter(id) ON DELETE SET NULL,
    search_text TEXT NOT NULL,
    created_at INTEGER NOT NULL,
    updated_at INTEGER NOT NULL,
    CHECK ((all_day = 1 AND start_time IS NULL AND time_zone IS NULL) OR
           (all_day = 0 AND start_time IS NOT NULL AND time_zone IS NOT NULL)),
    CHECK (end_date >= start_date)
);
CREATE INDEX appointment_dates ON appointment(start_date, end_date);
```

## Later changes

| Version | Phase | Change |
|---|---|---|
| 1 (still open) | 4 | `reminder` (id, owner_type, owner_id, offset_minutes, created_at; unique(owner_type, owner_id, offset_minutes)); `scheduled_alarm` (key PK, trigger_at, request_code UNIQUE); `snooze` (key PK, until_at); `delivered_alarm` (key PK, delivered_at). Added to version 1 because v0.1 is released at the end of phase 4 |
| 2 | 5 | `task_completion` (id, task_id FK CASCADE, occurrence_date, completed_at); `appointment_exception` (appointment_id FK CASCADE, original_start_date, original_start_time, kind, override columns; PK(appointment_id, original_start_date, original_start_time)). First real migration (`1.sqm`) |
| 3 | 6 | `label` (id, name UNIQUE COLLATE NOCASE, color); `label_assignment` (label_id FK CASCADE, owner_type, owner_id; PK all three) |

Owners in `reminder`/`label_assignment` are polymorphic (no FK); the owning repository deletes them
in the same transaction when deleting the owner.

## Migrations

- File `N.sqm` migrates from version N to N+1 (`1.sqm`: 1 → 2). Pure SQL, no data loss: create
  tables, add columns with defaults, backfill with `UPDATE`.
- After writing a migration: update the `.sq` files to the final shape, run
  `./gradlew :core:database:generateCommonMainPrumioDatabaseSchema` to add `databases/<N+1>.db`,
  and commit it. `check` runs `verifySqlDelightMigration`, which proves migrations from every
  committed version produce the current schema.
- Never edit a migration or `.db` file that shipped in a release.
- Every migration gets a data test: insert rows at version N, migrate, assert rows at N+1.

Recipe: [ai/recipes/add-database-table.md](../ai/recipes/add-database-table.md).
