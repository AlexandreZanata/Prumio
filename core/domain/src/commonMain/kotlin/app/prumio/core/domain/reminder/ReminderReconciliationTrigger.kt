package app.prumio.core.domain.reminder

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface ReminderReconciliationTrigger {
    fun request(forceReschedule: Boolean = false)
}
