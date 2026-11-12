package app.prumio.core.domain.agenda

import app.prumio.core.domain.appointment.ObserveAppointmentsUseCase
import app.prumio.core.model.Appointment
import app.prumio.core.model.Task
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.aTask
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.timedAt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Duration.Companion.hours

class ObserveAgendaUseCaseTest {
    private val repos = FakeRepositories()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 9, 0))
    private val observe = ObserveAgendaUseCase(ObserveAppointmentsUseCase(repos.appointments, time), repos.tasks, time)
    private val today = LocalDate(2026, 1, 15)
    private val tomorrow = LocalDate(2026, 1, 16)

    private fun put(vararg appointments: Appointment) =
        repos.store.appointments.update { current -> current + appointments.associateBy { it.id } }

    private fun put(vararg tasks: Task) = tasks.forEach(repos.store::putTask)

    private fun AgendaDay.titles() = items.map {
        when (it) {
            is AgendaItem.AppointmentItem -> it.appointment.title
            is AgendaItem.TaskItem -> it.task.title
        }
    }

    @Test
    fun `BR-APPT-007 all-day first, then timed appointments and tasks by time, then untimed tasks`() = runTest {
        put(
            anAppointment("a1", "Dentist", timedAt(LocalDateTime(2026, 1, 15, 14, 0))),
            anAppointment("a2", "Holiday", allDay(today)),
            anAppointment("a3", "Standup", timedAt(LocalDateTime(2026, 1, 15, 10, 0))),
        )
        put(
            aTask("t1", "Call bank", dueDate = today, dueTime = LocalTime(11, 0)),
            aTask("t2", "Buy milk", dueDate = today),
            aTask("t3", "Pay rent", dueDate = today, createdAt = time.now() - 1.hours),
        )

        val day = observe(today, tomorrow).first().first()

        assertEquals(listOf("Holiday", "Standup", "Call bank", "Dentist", "Pay rent", "Buy milk"), day.titles())
    }

    @Test
    fun `BR-APPT-007 overdue and closed tasks are not repeated in the agenda`() = runTest {
        put(
            aTask("late", "Late", dueDate = LocalDate(2026, 1, 14)),
            aTask("passed", "Passed", dueDate = today, dueTime = LocalTime(8, 0)),
            aTask("done", "Done", dueDate = today, status = TaskStatus.Completed),
            aTask("next", "Tomorrow", dueDate = tomorrow),
        )

        val days = observe(today, tomorrow).first()

        assertEquals(listOf(today, tomorrow), days.map { it.date })
        assertEquals(emptyList(), days[0].titles())
        assertEquals(listOf("Tomorrow"), days[1].titles())
    }

    @Test
    fun `BR-APPT-005 times are shown only on the day they happen`() = runTest {
        put(
            anAppointment(
                "trip",
                "Trip",
                timedAt(LocalDateTime(2026, 1, 15, 20, 0), LocalDateTime(2026, 1, 16, 8, 0)),
            ),
        )

        val (first, second) = observe(today, tomorrow).first()

        val onFirst = first.items.single() as AgendaItem.AppointmentItem
        assertEquals(LocalTime(20, 0), onFirst.startTime)
        assertEquals(null, onFirst.endTime)
        val onSecond = second.items.single() as AgendaItem.AppointmentItem
        assertEquals(null, onSecond.startTime)
    }
}
