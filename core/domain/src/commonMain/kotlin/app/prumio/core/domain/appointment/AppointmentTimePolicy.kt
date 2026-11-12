package app.prumio.core.domain.appointment

import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentTiming
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/**
 * Where an appointment sits on the device's timeline.
 * Rules: BR-TIME-001 (all-day is floating), BR-TIME-002 (timed keeps its zone), BR-APPT-005.
 */
object AppointmentTimePolicy {
    fun start(appointment: Appointment, deviceZone: TimeZone): Instant = when (val t = appointment.timing) {
        is AppointmentTiming.Timed -> t.start.toInstant(t.timeZone)
        is AppointmentTiming.AllDay -> t.startDate.atStartOfDayIn(deviceZone)
    }

    /** End of the appointment; a point-in-time appointment ends when it starts. */
    fun end(appointment: Appointment, deviceZone: TimeZone): Instant = when (val t = appointment.timing) {
        is AppointmentTiming.Timed -> (t.end ?: t.start).toInstant(t.timeZone)
        is AppointmentTiming.AllDay -> t.endDate.plus(DatePeriod(days = 1)).atStartOfDayIn(deviceZone)
    }

    /** Start as shown to the user, in the device zone. */
    fun localStart(appointment: Appointment, deviceZone: TimeZone): LocalDateTime =
        start(appointment, deviceZone).toLocalDateTime(deviceZone)

    /** BR-APPT-005: any part overlaps the local day; a point in time occurs on the day of its start. */
    fun occursOn(appointment: Appointment, date: LocalDate, deviceZone: TimeZone): Boolean {
        val timing = appointment.timing
        if (timing is AppointmentTiming.AllDay) return date in timing.startDate..timing.endDate
        val dayStart = date.atStartOfDayIn(deviceZone)
        val dayEnd = date.plus(DatePeriod(days = 1)).atStartOfDayIn(deviceZone)
        val start = start(appointment, deviceZone)
        val end = end(appointment, deviceZone)
        return if (start == end) start >= dayStart && start < dayEnd else start < dayEnd && end > dayStart
    }

    /** Agenda order within a day: all-day first, then by start (BR-APPT-007). */
    fun dayOrder(deviceZone: TimeZone): Comparator<Appointment> =
        compareBy<Appointment>({ !it.isAllDay }, { start(it, deviceZone) }, { it.title })
}
