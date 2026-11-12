package app.prumio.core.testing

import app.prumio.core.domain.inbox.ConversionTarget
import app.prumio.core.domain.inbox.InboxRepository
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeInboxRepository(private val store: FakeStore) : InboxRepository {
    override fun observeAll(): Flow<List<InboxItem>> = store.inboxItems.map { items ->
        items.values.sortedWith(compareByDescending<InboxItem> { it.createdAt }.thenBy { it.id.value })
    }

    override fun observeCount(): Flow<Int> = store.inboxItems.map { it.size }

    override suspend fun get(id: InboxItemId): InboxItem? = store.inboxItems.value[id]

    override suspend fun save(item: InboxItem) = store.inboxItems.update { it + (item.id to item) }

    override suspend fun delete(id: InboxItemId) = store.inboxItems.update { it - id }

    override suspend fun convert(id: InboxItemId, target: ConversionTarget) {
        when (target) {
            is ConversionTarget.ToTask -> store.putTask(target.task)

            is ConversionTarget.ToAppointment -> store.appointments.update {
                it +
                    (target.appointment.id to target.appointment)
            }

            is ConversionTarget.ToMatter -> {
                store.putMatter(target.matter)
                store.putEntries(listOf(target.created))
            }
        }
        delete(id)
    }
}
