package app.prumio.core.domain.reminder

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface ScheduledAlarmStore {
    suspend fun all(): List<ScheduledAlarm>
    suspend fun replaceAll(alarms: List<ScheduledAlarm>)
}
