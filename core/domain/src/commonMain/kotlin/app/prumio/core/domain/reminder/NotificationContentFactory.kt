package app.prumio.core.domain.reminder

/** Resolves content from current domain data, with no user strings/platform API. Rules: BR-REM-005, BR-REM-015, BR-REM-016. */
class NotificationContentFactory {
    fun create(alarm: PlannedAlarm, input: PlanningInput): NotificationContent? = when (alarm.kind) {
        AlarmKind.Task -> input.tasks.firstOrNull {
            it.id.value == alarm.itemId && it.isOpen
        }?.let { NotificationContent(alarm, it.title) }

        AlarmKind.Appointment -> input.appointments.firstOrNull {
            it.id.value == alarm.itemId
        }?.let { NotificationContent(alarm, it.title, location = it.location) }

        AlarmKind.Matter -> input.matters.firstOrNull {
            it.id.value == alarm.itemId && !it.isResolved
        }?.let { NotificationContent(alarm, it.title, waitingFor = it.waitingFor) }
    }
}
