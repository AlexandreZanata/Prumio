package app.prumio.core.data.reminder

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.replaceReminders
import app.prumio.core.data.storage.toModel
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.reminder.ReminderRepository
import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderOwner
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

internal class SqlDelightReminderRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : ReminderRepository {
    override fun observeAll() = database.reminderQueries.selectAll().asFlow().mapToList(dispatchers.io).map { rows ->
        rows.map { it.toModel() }
    }
    override suspend fun replaceFor(owner: ReminderOwner, reminders: List<Reminder>) = withContext(dispatchers.io) {
        database.transaction { database.replaceReminders(owner, reminders) }
    }
}
