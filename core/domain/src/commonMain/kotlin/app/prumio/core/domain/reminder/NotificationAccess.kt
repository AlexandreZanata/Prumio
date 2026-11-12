package app.prumio.core.domain.reminder

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface NotificationAccess {
    fun status(): NotificationAccessState
}
