package app.prumio.core.domain.reminder

import kotlin.time.Instant

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface DeliveredAlarmStore {
    suspend fun recent(since: Instant): Set<AlarmKey>
    suspend fun markDelivered(key: AlarmKey, at: Instant)
    suspend fun pruneBefore(instant: Instant)
}
