package app.prumio.core.domain.reminder

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface AlarmScheduler {
    fun canScheduleExact(): Boolean
    suspend fun schedule(alarm: PlannedAlarm, requestCode: Int)
    suspend fun cancel(key: AlarmKey, requestCode: Int)
}
