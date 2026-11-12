package app.prumio.core.domain.agenda

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.domain.appointment.AppointmentTimePolicy
import app.prumio.core.domain.appointment.ObserveAppointmentsUseCase
import app.prumio.core.domain.task.TaskOverduePolicy
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime

/** One line of the agenda. */
sealed interface AgendaItem {
    /**
     * [startTime]/[endTime] are the times shown on this day, in the device zone: null when the
     * appointment is all-day or starts/ends on another day.
     */
    data class AppointmentItem(val appointment: Appointment, val startTime: LocalTime?, val endTime: LocalTime?) :
        AgendaItem

    data class TaskItem(val task: Task) : AgendaItem
}

data class AgendaDay(val date: LocalDate, val items: List<AgendaItem>)

/**
 * Appointments and open tasks per day for [from]..[to] (every day present, possibly empty).
 * Rules: BR-APPT-007 (order; overdue tasks live in Today, not here), BR-APPT-005, BR-TIME-003.
 */
class ObserveAgendaUseCase(
    private val observeAppointments: ObserveAppointmentsUseCase,
    private val tasks: TaskRepository,
    private val time: TimeProvider,
) {
    operator fun invoke(from: LocalDate, to: LocalDate): Flow<List<AgendaDay>> =
        combine(observeAppointments(from, to), tasks.observeOpenDueBetween(from, to)) { days, open ->
            val now = time.localNow()
            val dueTasks = open
                .filter { it.dueDate != null && !TaskOverduePolicy.isOverdue(it, now) }
                .groupBy { it.dueDate }
            days.map { day ->
                val appointments = day.appointments.map { it.toItem(day.date) }
                val dayTasks = dueTasks[day.date].orEmpty().map { AgendaItem.TaskItem(it) }
                AgendaDay(day.date, (appointments + dayTasks).sortedWith(agendaOrder))
            }
        }

    private fun Appointment.toItem(date: LocalDate): AgendaItem.AppointmentItem {
        val zone = time.timeZone()
        if (timing is AppointmentTiming.AllDay) return AgendaItem.AppointmentItem(this, null, null)
        val start = AppointmentTimePolicy.localStart(this, zone)
        val end = AppointmentTimePolicy.end(this, zone).toLocalDateTime(zone)
        val hasEnd = (timing as AppointmentTiming.Timed).end != null
        return AgendaItem.AppointmentItem(
            appointment = this,
            startTime = start.time.takeIf { start.date == date },
            endTime = end.time.takeIf { hasEnd && end.date == date && start.date == date },
        )
    }

    private companion object {
        // All-day (and continuing) appointments, then timed items by time, then untimed tasks by creation.
        val agendaOrder: Comparator<AgendaItem> = compareBy<AgendaItem>(
            { item ->
                when (item) {
                    is AgendaItem.AppointmentItem -> if (item.startTime == null) 0 else 1
                    is AgendaItem.TaskItem -> if (item.task.dueTime == null) 2 else 1
                }
            },
            { item ->
                when (item) {
                    is AgendaItem.AppointmentItem -> item.startTime
                    is AgendaItem.TaskItem -> item.task.dueTime
                }
            },
            { item -> (item as? AgendaItem.TaskItem)?.task?.createdAt },
        )
    }
}
