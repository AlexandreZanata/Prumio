package app.prumio.core.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/** Something with a date and time where you need to be (docs/domain/model.md#appointment). */
data class Appointment(
    val id: AppointmentId,
    val title: String,
    val notes: String? = null,
    val location: String? = null,
    val timing: AppointmentTiming,
    val recurrence: RecurrenceRule? = null,
    val matterId: MatterId? = null,
    val reminders: List<Reminder> = emptyList(),
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isAllDay: Boolean get() = timing is AppointmentTiming.AllDay
}

sealed interface AppointmentTiming {
    /** A fixed instant anchored to [timeZone] (BR-TIME-002). A null [end] means "no end time". */
    data class Timed(val start: LocalDateTime, val end: LocalDateTime?, val timeZone: TimeZone) : AppointmentTiming

    /** Floating whole days, inclusive (BR-TIME-001). */
    data class AllDay(val startDate: LocalDate, val endDate: LocalDate) : AppointmentTiming
}
