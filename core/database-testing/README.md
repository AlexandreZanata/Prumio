# `:core:database-testing`

> One reusable in-memory SQLite `PrumioDatabase` for JVM tests, so no test copies database setup.

| | |
|---|---|
| **Layer** | Test support (never shipped: only test source sets may depend on it) |
| **Plugin** | `prumio.kmp.library` |
| **Package** | `app.prumio.core.databasetesting` |
| **May depend on** | `:core:common`, `:core:database` |
| **Used by** | `androidHostTest` source sets of `:core:database` and `:core:data` |

## Public API

| Symbol | Contract |
|---|---|
| `InMemoryDatabase()` | New empty database with the production schema and foreign keys on; `.database`, `.close()` |

## Rules

- Declare it only in test source sets: `getByName("androidHostTest").dependencies { implementation(projects.core.databaseTesting) }`.
  `ModuleRules` rejects it in any production configuration.
- It uses the JVM JDBC SQLite driver, so it lives in `androidMain` and runs in host tests only.
