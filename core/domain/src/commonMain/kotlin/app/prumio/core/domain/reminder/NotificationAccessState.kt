package app.prumio.core.domain.reminder

data class NotificationAccessState(
    val notificationsAllowed: Boolean,
    val canAskPermission: Boolean,
    val exactAlarmsAllowed: Boolean,
    val batteryOptimized: Boolean,
)
