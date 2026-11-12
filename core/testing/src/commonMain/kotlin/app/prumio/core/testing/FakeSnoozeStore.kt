package app.prumio.core.testing

import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.SnoozeStore
import kotlin.time.Instant

class FakeSnoozeStore : SnoozeStore {
    val values = mutableMapOf<AlarmKey, Instant>()
    override suspend fun all() = values.toMap()
    override suspend fun put(key: AlarmKey, until: Instant) {
        values[key] = until
    }
    override suspend fun remove(key: AlarmKey) {
        values.remove(key)
    }
}
