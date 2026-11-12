package app.prumio.core.domain.inbox

import app.prumio.core.model.Appointment
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow

/** Port: storage of inbox items (docs/architecture/contracts.md#inbox-phase-1). */
interface InboxRepository {
    /** Newest first (BR-INBOX-003). */
    fun observeAll(): Flow<List<InboxItem>>

    fun observeCount(): Flow<Int>

    suspend fun get(id: InboxItemId): InboxItem?

    /** Inserts or replaces the item. */
    suspend fun save(item: InboxItem)

    suspend fun delete(id: InboxItemId)

    /** Atomically stores [target] and deletes the item (BR-INBOX-004). */
    suspend fun convert(id: InboxItemId, target: ConversionTarget)
}

/** What an inbox item becomes, fully built by the domain. */
sealed interface ConversionTarget {
    data class ToTask(val task: Task) : ConversionTarget

    data class ToAppointment(val appointment: Appointment) : ConversionTarget

    data class ToMatter(val matter: Matter, val created: MatterEntry) : ConversionTarget
}
