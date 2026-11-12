package app.prumio.core.domain.reminder

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.domain.error.DomainError
import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderId
import app.prumio.core.model.ReminderOwner
import app.prumio.core.model.Settings
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

/** Default wishes, independent of notification permissions. Rules: BR-TASK-006, BR-APPT-004. */
object ReminderPolicy {
    val presets: List<Int> = listOf(0, 5, 10, 15, 30, 60, 120, 1440, 2880, 10080)
    fun taskOffsets(time: LocalTime?, previousTime: LocalTime?, existing: List<Int>, requested: List<Int>?): List<Int> =
        requested ?: existing.ifEmpty { if (time != null && previousTime == null) listOf(0) else emptyList() }
    fun appointmentOffsets(allDay: Boolean, settings: Settings): List<Int> =
        listOf(if (allDay) 0 else settings.defaultAppointmentNoticeMinutes)
}

/** BR-REM-018: unique nonnegative offsets, at most five per item. */
fun normalizedOffsets(offsets: List<Int>): Outcome<List<Int>, DomainError> =
    if (offsets.any { it < 0 } || offsets.distinct().size > 5) {
        DomainError.Validation("BR-REM-018", "reminders", "reminder_offsets").asFailure()
    } else {
        offsets.distinct().sorted().asSuccess()
    }

/** Keeps identities of unchanged wishes; called inside the domain's create/update operations. */
internal fun buildReminders(
    owner: ReminderOwner,
    offsets: List<Int>,
    existing: List<Reminder>,
    ids: IdGenerator,
    now: Instant,
): List<Reminder> = offsets.map { offset ->
    existing.firstOrNull { it.offsetMinutes == offset }
        ?: Reminder(ReminderId(ids.newId()), owner, offset, now)
}
