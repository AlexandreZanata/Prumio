package app.prumio.core.data.reminder

import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.DeliveredAlarmStore
import kotlinx.coroutines.withContext
import kotlin.time.Instant

internal class SqlDelightDeliveredAlarmStore(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : DeliveredAlarmStore {
    override suspend fun recent(since: Instant) = withContext(dispatchers.io) {
        database.delivered_alarmQueries.selectAll().executeAsList().filter {
            it.delivered_at >=
                since.toEpochMilliseconds()
        }.map { AlarmKey(it.key) }.toSet()
    }
    override suspend fun markDelivered(key: AlarmKey, at: Instant) = withContext(dispatchers.io) {
        database.delivered_alarmQueries.put(key.value, at.toEpochMilliseconds())
        Unit
    }
    override suspend fun pruneBefore(instant: Instant) = withContext(dispatchers.io) {
        database.delivered_alarmQueries.prune(instant.toEpochMilliseconds())
        Unit
    }
}
