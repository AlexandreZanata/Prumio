package app.prumio.core.data.inbox

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.toModel
import app.prumio.core.data.storage.upsertAppointment
import app.prumio.core.data.storage.upsertInboxItem
import app.prumio.core.data.storage.upsertMatter
import app.prumio.core.data.storage.upsertMatterEntry
import app.prumio.core.data.storage.upsertTask
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.inbox.ConversionTarget
import app.prumio.core.domain.inbox.InboxRepository
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class SqlDelightInboxRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : InboxRepository {
    private val queries = database.inbox_itemQueries

    override fun observeAll(): Flow<List<InboxItem>> =
        queries.selectAll().asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toModel() } }

    override fun observeCount(): Flow<Int> = queries.countAll().asFlow().mapToOne(dispatchers.io).map { it.toInt() }

    override suspend fun get(id: InboxItemId): InboxItem? = withContext(dispatchers.io) {
        queries.selectById(id.value).executeAsOneOrNull()?.toModel()
    }

    override suspend fun save(item: InboxItem) = withContext(dispatchers.io) {
        database.transaction { database.upsertInboxItem(item) }
    }

    override suspend fun delete(id: InboxItemId) = withContext(dispatchers.io) {
        queries.deleteById(id.value)
        Unit
    }

    override suspend fun convert(id: InboxItemId, target: ConversionTarget) = withContext(dispatchers.io) {
        database.transaction {
            when (target) {
                is ConversionTarget.ToTask -> database.upsertTask(target.task)

                is ConversionTarget.ToAppointment -> database.upsertAppointment(target.appointment)

                is ConversionTarget.ToMatter -> {
                    database.upsertMatter(target.matter)
                    database.upsertMatterEntry(target.created)
                }
            }
            queries.deleteById(id.value)
        }
    }
}
