package app.prumio.core.domain.matter

import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

/** Port: storage of matters and their history (docs/architecture/contracts.md#matters-phase-1). */
interface MatterRepository {
    fun observe(id: MatterId): Flow<Matter?>

    /** Open and waiting matters, most recent activity first. */
    fun observeUnresolved(): Flow<List<Matter>>

    /** Most recently resolved first. */
    fun observeResolvedSince(instant: Instant): Flow<List<Matter>>

    /** Newest first: by occurredOn, then createdAt. */
    fun observeHistory(id: MatterId): Flow<List<MatterEntry>>

    suspend fun get(id: MatterId): Matter?

    suspend fun getEntry(id: MatterEntryId): MatterEntry?

    /**
     * Atomically inserts or replaces [matter], inserts or replaces [entries] and saves [tasks]
     * (BR-MATTER-003: history is written in the same transaction as the change).
     */
    suspend fun save(matter: Matter, entries: List<MatterEntry> = emptyList(), tasks: List<Task> = emptyList())

    /** Atomically deletes the entry and saves [matter] (its new activity time). */
    suspend fun deleteEntry(matter: Matter, entryId: MatterEntryId)

    /** Deletes the matter and its history; linked tasks and appointments are unlinked. */
    suspend fun delete(id: MatterId)
}
