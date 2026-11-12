package app.prumio.core.domain.reminder

import kotlin.time.Instant

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface SnoozeStore {
    suspend fun all(): Map<AlarmKey, Instant>
    suspend fun put(key: AlarmKey, until: Instant)
    suspend fun remove(key: AlarmKey)
}
