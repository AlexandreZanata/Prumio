package app.prumio.core.data.reminder

import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.ScheduledAlarm
import app.prumio.core.domain.reminder.ScheduledAlarmStore
import kotlinx.coroutines.withContext
import kotlin.time.Instant

internal class SqlDelightScheduledAlarmStore(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : ScheduledAlarmStore {
    override suspend fun all() = withContext(dispatchers.io) {
        database.scheduled_alarmQueries.selectAll().executeAsList().map {
            ScheduledAlarm(AlarmKey(it.key), Instant.fromEpochMilliseconds(it.trigger_at), it.request_code.toInt())
        }
    }
    override suspend fun replaceAll(alarms: List<ScheduledAlarm>) = withContext(dispatchers.io) {
        database.transaction {
            database.scheduled_alarmQueries.deleteAll()
            alarms.forEach {
                database.scheduled_alarmQueries.put(
                    it.key.value,
                    it.triggerAt.toEpochMilliseconds(),
                    it.requestCode.toLong(),
                )
            }
        }
    }
}
