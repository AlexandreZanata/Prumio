package app.prumio.core.domain.reminder

/** Platform-neutral notification data; adapters localize body and action labels (BR-REM-015, BR-REM-016). */
data class NotificationContent(
    val alarm: PlannedAlarm,
    val title: String,
    val waitingFor: String? = null,
    val location: String? = null,
)
