package app.prumio.core.data.storage

import app.prumio.core.database.PrumioDatabase
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderId
import app.prumio.core.model.ReminderOwner
import app.prumio.core.model.TaskId
import app.prumio.core.database.Reminder as ReminderRow

internal val ReminderOwner.columnType: String get() = when (this) {
    is ReminderOwner.Task -> "TASK"
    is ReminderOwner.Appointment -> "APPOINTMENT"
}
internal fun ReminderRow.toModel(): Reminder = Reminder(
    ReminderId(id),
    when (owner_type) {
        "TASK" -> ReminderOwner.Task(TaskId(owner_id))
        "APPOINTMENT" -> ReminderOwner.Appointment(AppointmentId(owner_id))
        else -> error("Invalid reminder owner")
    },
    offset_minutes.toInt(),
    created_at.toInstant(),
)

internal fun PrumioDatabase.remindersFor(type: String, id: String): List<Reminder> =
    reminderQueries.selectForOwner(type, id).executeAsList().map {
        it.toModel()
    }
internal fun PrumioDatabase.replaceReminders(owner: ReminderOwner, reminders: List<Reminder>) {
    reminderQueries.deleteForOwner(owner.columnType, owner.id)
    reminders.forEach { r ->
        reminderQueries.insert(
            r.id.value,
            owner.columnType,
            owner.id,
            r.offsetMinutes.toLong(),
            r.createdAt.toEpochMilliseconds(),
        )
    }
}

/** Joins already-loaded rows; reactive UI reads never execute extra SQL on the collector's thread. */
internal fun List<ReminderRow>.byOwner(type: String): Map<String, List<Reminder>> =
    filter { it.owner_type == type }.groupBy { it.owner_id }.mapValues { (_, rows) -> rows.map { it.toModel() } }
