package app.prumio.core.domain.appointment

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.domain.text.ValidationReasons
import app.prumio.core.domain.text.optionalText
import app.prumio.core.domain.text.requiredText
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.MatterId
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone

/** When an appointment happens, as entered by the user (the time zone is added on save, BR-APPT-003). */
sealed interface AppointmentSchedule {
    data class Timed(val start: LocalDateTime, val end: LocalDateTime? = null) : AppointmentSchedule

    data class AllDay(val startDate: LocalDate, val endDate: LocalDate = startDate) : AppointmentSchedule
}

/** User input for creating or editing an appointment. */
data class AppointmentDraft(
    val title: String,
    val schedule: AppointmentSchedule,
    val notes: String? = null,
    val location: String? = null,
    val matterId: MatterId? = null,
)

/**
 * Normalizes and validates a draft.
 *
 * Rules: BR-APPT-001 (title and start required), BR-APPT-002 (end not before start), BR-GEN-002.
 */
fun AppointmentDraft.normalized(): Outcome<AppointmentDraft, DomainError> {
    val title = requiredText(title, field = "title", max = TextLimits.TITLE, ruleId = "BR-APPT-001")
        .valueOr { return it.asFailure() }
    val notes = optionalText(notes, field = "notes", max = TextLimits.NOTES).valueOr { return it.asFailure() }
    val location = optionalText(location, field = "location", max = TextLimits.LOCATION).valueOr {
        return it.asFailure()
    }
    val endBeforeStart = when (val s = schedule) {
        is AppointmentSchedule.Timed -> s.end != null && s.end < s.start
        is AppointmentSchedule.AllDay -> s.endDate < s.startDate
    }
    if (endBeforeStart) {
        return DomainError.Validation(
            "BR-APPT-002",
            field = "end",
            reason = ValidationReasons.END_BEFORE_START,
        ).asFailure()
    }
    return copy(title = title, notes = notes, location = location).asSuccess()
}

/** The stored timing: timed schedules are anchored to [zone] (BR-APPT-003, BR-TIME-002). */
fun AppointmentSchedule.toTiming(zone: TimeZone): AppointmentTiming = when (this) {
    is AppointmentSchedule.Timed -> AppointmentTiming.Timed(start = start, end = end, timeZone = zone)
    is AppointmentSchedule.AllDay -> AppointmentTiming.AllDay(startDate = startDate, endDate = endDate)
}

/** The schedule as the user sees it, for editing. */
fun AppointmentTiming.toSchedule(): AppointmentSchedule = when (this) {
    is AppointmentTiming.Timed -> AppointmentSchedule.Timed(start = start, end = end)
    is AppointmentTiming.AllDay -> AppointmentSchedule.AllDay(startDate = startDate, endDate = endDate)
}
