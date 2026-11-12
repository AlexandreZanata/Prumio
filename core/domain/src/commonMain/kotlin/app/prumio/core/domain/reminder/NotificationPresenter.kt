package app.prumio.core.domain.reminder

/** Reminder subsystem port (docs/architecture/contracts.md). */
interface NotificationPresenter {
    suspend fun show(content: NotificationContent)
    suspend fun cancelFor(itemKey: String)
}
