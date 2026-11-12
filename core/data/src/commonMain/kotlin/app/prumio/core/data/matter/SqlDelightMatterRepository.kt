package app.prumio.core.data.matter

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.toColumn
import app.prumio.core.data.storage.toModel
import app.prumio.core.data.storage.upsertMatter
import app.prumio.core.data.storage.upsertMatterEntry
import app.prumio.core.data.storage.upsertTask
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlin.time.Instant

internal class SqlDelightMatterRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : MatterRepository {
    private val queries = database.matterQueries
    private val entryQueries = database.matter_entryQueries

    override fun observe(id: MatterId): Flow<Matter?> =
        queries.selectById(id.value).asFlow().mapToOneOrNull(dispatchers.io).map { it?.toModel() }

    override fun observeUnresolved(): Flow<List<Matter>> =
        queries.selectUnresolved().asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toModel() } }

    override fun observeResolvedSince(instant: Instant): Flow<List<Matter>> =
        queries.selectResolvedSince(instant.toColumn()).asFlow().mapToList(dispatchers.io).map { rows ->
            rows.map { it.toModel() }
        }

    override fun observeHistory(id: MatterId): Flow<List<MatterEntry>> =
        entryQueries.selectByMatter(id.value).asFlow().mapToList(dispatchers.io).map { rows ->
            rows.map { it.toModel() }
        }

    override suspend fun get(id: MatterId): Matter? = withContext(dispatchers.io) {
        queries.selectById(id.value).executeAsOneOrNull()?.toModel()
    }

    override suspend fun getEntry(id: MatterEntryId): MatterEntry? = withContext(dispatchers.io) {
        entryQueries.selectById(id.value).executeAsOneOrNull()?.toModel()
    }

    override suspend fun save(matter: Matter, entries: List<MatterEntry>, tasks: List<Task>) =
        withContext(dispatchers.io) {
            database.transaction {
                database.upsertMatter(matter)
                entries.forEach(database::upsertMatterEntry)
                tasks.forEach(database::upsertTask)
            }
        }

    override suspend fun deleteEntry(matter: Matter, entryId: MatterEntryId) = withContext(dispatchers.io) {
        database.transaction {
            entryQueries.deleteById(entryId.value)
            database.upsertMatter(matter)
        }
    }

    override suspend fun delete(id: MatterId) = withContext(dispatchers.io) {
        queries.deleteById(id.value)
        Unit
    }
}
