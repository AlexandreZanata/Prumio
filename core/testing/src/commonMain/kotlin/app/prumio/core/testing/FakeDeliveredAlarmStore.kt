package app.prumio.core.testing

import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.DeliveredAlarmStore
import kotlin.time.Instant

class FakeDeliveredAlarmStore : DeliveredAlarmStore {
    val values = mutableMapOf<AlarmKey, Instant>()
    override suspend fun recent(since: Instant) = values.filterValues { it >= since }.keys
    override suspend fun markDelivered(key: AlarmKey, at: Instant) {
        values[key] = at
    }
    override suspend fun pruneBefore(instant: Instant) {
        values.keys.removeAll { values.getValue(it) < instant }
    }
}
