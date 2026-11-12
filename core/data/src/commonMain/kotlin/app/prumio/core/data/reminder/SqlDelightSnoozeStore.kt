package app.prumio.core.data.reminder

import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.SnoozeStore
import kotlinx.coroutines.withContext
import kotlin.time.Instant

internal class SqlDelightSnoozeStore(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : SnoozeStore {
    override suspend fun all() = withContext(dispatchers.io) {
        database.snoozeQueries.selectAll().executeAsList().associate {
            AlarmKey(it.key) to
                Instant.fromEpochMilliseconds(it.until_at)
        }
    }
    override suspend fun put(key: AlarmKey, until: Instant) = withContext(dispatchers.io) {
        database.snoozeQueries.put(key.value, until.toEpochMilliseconds())
        Unit
    }
    override suspend fun remove(key: AlarmKey) = withContext(dispatchers.io) {
        database.snoozeQueries.deleteByKey(key.value)
        Unit
    }
}
