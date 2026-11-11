package app.prumio.core.testing

import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlin.time.Instant

class FakeMatterRepository(private val store: FakeStore) : MatterRepository {
    override fun observe(id: MatterId): Flow<Matter?> = store.matters.map { it[id] }

    override fun observeUnresolved(): Flow<List<Matter>> = store.matters.map { all ->
        all.values.filter {
            !it.isResolved
        }.sortedWith(compareByDescending<Matter> { it.lastActivityAt }.thenBy { it.id.value })
    }

    override fun observeResolvedSince(instant: Instant): Flow<List<Matter>> = store.matters.map { all ->
        all.values
            .filter { it.status == MatterStatus.Resolved && it.resolvedAt.let { at -> at != null && at >= instant } }
            .sortedWith(compareByDescending<Matter> { it.resolvedAt }.thenBy { it.id.value })
    }

    override fun observeHistory(id: MatterId): Flow<List<MatterEntry>> = store.matterEntries.map { all ->
        all.values
            .filter { it.matterId == id }
            .sortedWith(
                compareByDescending<MatterEntry> {
                    it.occurredOn
                }.thenByDescending { it.createdAt }.thenBy { it.id.value },
            )
    }

    override suspend fun get(id: MatterId): Matter? = store.matters.value[id]

    override suspend fun getEntry(id: MatterEntryId): MatterEntry? = store.matterEntries.value[id]

    override suspend fun save(matter: Matter, entries: List<MatterEntry>, tasks: List<Task>) {
        store.putMatter(matter)
        store.putEntries(entries)
        tasks.forEach(store::putTask)
    }

    override suspend fun deleteEntry(matter: Matter, entryId: MatterEntryId) {
        store.matterEntries.update { it - entryId }
        store.putMatter(matter)
    }

    override suspend fun delete(id: MatterId) {
        store.matterEntries.update { entries -> entries.filterValues { it.matterId != id } }
        store.tasks.update { tasks ->
            tasks.mapValues { (_, task) ->
                if (task.matterId ==
                    id
                ) {
                    task.copy(matterId = null)
                } else {
                    task
                }
            }
        }
        store.appointments.update { all ->
            all.mapValues { (_, a) ->
                if (a.matterId ==
                    id
                ) {
                    a.copy(matterId = null)
                } else {
                    a
                }
            }
        }
        store.matters.update { it - id }
    }
}
