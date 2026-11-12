package app.prumio.core.domain.appointment

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.model.Appointment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

/** The appointments occurring on one local day, in agenda order. */
data class DayAppointments(val date: LocalDate, val appointments: List<Appointment>)

/**
 * Appointments per day for [from]..[to] in the device zone, every day included (possibly empty).
 * Past appointments are returned like any other: they have no state (BR-APPT-006).
 * Rules: BR-APPT-005, BR-APPT-006, BR-APPT-007, BR-TIME-002.
 */
class ObserveAppointmentsUseCase(private val appointments: AppointmentRepository, private val time: TimeProvider) {
    operator fun invoke(from: LocalDate, to: LocalDate): Flow<List<DayAppointments>> {
        val oneDay = DatePeriod(days = 1)
        return appointments.observeOverlapping(from.minus(oneDay), to.plus(oneDay)).map { candidates ->
            val zone = time.timeZone()
            generateSequence(from) { it.plus(oneDay) }.takeWhile { it <= to }.map { date ->
                DayAppointments(
                    date = date,
                    appointments = candidates
                        .filter { AppointmentTimePolicy.occursOn(it, date, zone) }
                        .sortedWith(AppointmentTimePolicy.dayOrder(zone)),
                )
            }.toList()
        }
    }
}
