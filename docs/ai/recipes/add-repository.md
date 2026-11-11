# Recipe: add a repository (domain port + data adapter + fake)

## Steps

1. **Specify the port** in [contracts.md](../../architecture/contracts.md) first (signatures + semantics).
2. **Declare the interface** in `core/domain/src/commonMain/kotlin/app/prumio/core/domain/<aggregate>/<Aggregate>Repository.kt`.
   Reads: `Flow`; writes: `suspend`; multi-table atomic operations: one intent-revealing method.
3. **SQL**: queries in `core/database/.../<table>.sq` ([add-database-table.md](add-database-table.md) for new tables).
4. **Adapter** in `core/data/src/commonMain/kotlin/app/prumio/core/data/<aggregate>/`:
   - `SqlDelight<Aggregate>Repository.kt` (`internal class … : <Aggregate>Repository`)
   - `<Table>Mappers.kt` (`internal fun Row.toModel()`, explicit enum mapping)
   Follow [data-layer.md](../../architecture/data-layer.md): `asFlow().mapToList(dispatchers.io)`,
   `withContext(dispatchers.io) { database.transaction { … } }`.
5. **Bind** in `core/data/.../di/CoreDataModule.kt`:
   `singleOf(::SqlDelightTaskRepository) bind TaskRepository::class`.
6. **Fake** in `core/testing/src/commonMain/kotlin/app/prumio/core/testing/Fake<Aggregate>Repository.kt`:
   in-memory `MutableStateFlow<Map<Id, Model>>`, same semantics as the adapter (ordering, filters,
   emissions after writes). Add `a<Entity>()` builders in `core/testing/.../Builders.kt`.
7. **Tests**:
   - Adapter: `core/data/src/androidHostTest/...` on in-memory SQLite — every method, every `Flow`
     re-emits after a write (Turbine), mapper round-trips.
   - Fake parity: a shared contract test (`<Aggregate>RepositoryContract` in `:core:testing`) run
     against both fake and adapter is preferred for complex ports.

## Validation

```bash
./gradlew :core:data:testAndroidHostTest :core:domain:testAndroidHostTest :app:shell:testAndroidHostTest
```
