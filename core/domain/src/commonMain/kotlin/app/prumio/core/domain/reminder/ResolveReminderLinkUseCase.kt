package app.prumio.core.domain.reminder

import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId

/** Validates deep-link targets against current data; missing items lead to Today with feedback. Rule: BR-REM-017. */
class ResolveReminderLinkUseCase(
    private val tasks: TaskRepository,
    private val appointments: AppointmentRepository,
    private val matters: MatterRepository,
) {
    suspend operator fun invoke(uri: String) = storageCall {
        if (!uri.startsWith("prumio://")) return@storageCall ReminderLink(ReminderLinkKind.Today)
        val path = uri.removePrefix("prumio://")
        val host = path.substringBefore('/')
        val id = path.substringAfter('/', "").takeIf { it.isNotBlank() && '/' !in it && '?' !in it && '#' !in it }
        val kind = when (host) {
            "task" -> ReminderLinkKind.Task
            "appointment" -> ReminderLinkKind.Appointment
            "matter" -> ReminderLinkKind.Matter
            "capture" -> ReminderLinkKind.Capture
            "snooze" -> ReminderLinkKind.Snooze
            else -> ReminderLinkKind.Today
        }
        val exists = when (kind) {
            ReminderLinkKind.Task -> id != null && tasks.get(TaskId(id)) != null
            ReminderLinkKind.Appointment -> id != null && appointments.get(AppointmentId(id)) != null
            ReminderLinkKind.Matter -> id != null && matters.get(MatterId(id)) != null
            ReminderLinkKind.Snooze -> id != null && snoozeOwnerExists(id)
            else -> true
        }
        if (exists) ReminderLink(kind, id) else ReminderLink(ReminderLinkKind.Today, missing = true)
    }
    private suspend fun snoozeOwnerExists(key: String): Boolean {
        val parts = key.split(':')
        if (parts.size != 4 || parts[1].isBlank() || parts[2].toLongOrNull() == null ||
            parts[3].toIntOrNull()?.let { it >= 0 } != true
        ) {
            return false
        }
        return when (parts[0]) {
            "task" -> tasks.get(TaskId(parts[1])) != null
            "appointment" -> appointments.get(AppointmentId(parts[1])) != null
            "matter" -> matters.get(MatterId(parts[1])) != null
            else -> false
        }
    }
}
