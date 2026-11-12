package app.prumio.core.domain.reminder

import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderOwner
import kotlinx.coroutines.flow.Flow

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface ReminderRepository {
    fun observeAll(): Flow<List<Reminder>>
    suspend fun replaceFor(owner: ReminderOwner, reminders: List<Reminder>)
}
