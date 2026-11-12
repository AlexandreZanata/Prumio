package app.prumio.core.data

import app.cash.turbine.test
import app.prumio.core.data.appointment.SqlDelightAppointmentRepository
import app.prumio.core.data.reminder.SqlDelightDeliveredAlarmStore
import app.prumio.core.data.reminder.SqlDelightReminderRepository
import app.prumio.core.data.reminder.SqlDelightScheduledAlarmStore
import app.prumio.core.data.reminder.SqlDelightSnoozeStore
import app.prumio.core.data.task.SqlDelightTaskRepository
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.ScheduledAlarm
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderId
import app.prumio.core.model.ReminderOwner
import app.prumio.core.model.TaskId
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class ReminderRepositoryTest : RepositoryTestBase() {
    private val tasks = SqlDelightTaskRepository(database, dispatchers)
    private val appointments = SqlDelightAppointmentRepository(database, dispatchers)
    private val reminders = SqlDelightReminderRepository(database, dispatchers)

    @Test fun `BR-REM-001 aggregate reminders round-trip update live and delete with owners`() = runTest {
        val taskReminder = Reminder(ReminderId("r1"), ReminderOwner.Task(TaskId("t1")), 10, TestInstant)
        val appointmentReminder =
            Reminder(ReminderId("r2"), ReminderOwner.Appointment(AppointmentId("a1")), 30, TestInstant)
        val task = aTask("t1").copy(reminders = listOf(taskReminder))
        val appointment = anAppointment("a1").copy(reminders = listOf(appointmentReminder))
        tasks.save(task)
        appointments.save(appointment)
        assertEquals(task, tasks.get(task.id))
        assertEquals(appointment, appointments.get(appointment.id))
        assertEquals(listOf(appointment), appointments.observeAll().first())
        assertEquals(setOf(taskReminder, appointmentReminder), reminders.observeAll().first().toSet())
        tasks.observe(task.id).test {
            assertEquals(task, awaitItem())
            reminders.replaceFor(taskReminder.owner, emptyList())
            assertEquals(emptyList(), awaitItem()?.reminders)
        }
        appointments.observe(appointment.id).test {
            assertEquals(appointment, awaitItem())
            reminders.replaceFor(appointmentReminder.owner, listOf(appointmentReminder.copy(offsetMinutes = 60)))
            assertEquals(60, awaitItem()?.reminders?.single()?.offsetMinutes)
        }
        tasks.delete(task.id)
        appointments.delete(appointment.id)
        assertTrue(reminders.observeAll().first().isEmpty())
        tasks.save(task)
        appointments.save(appointment)
        assertEquals(task.reminders, tasks.observeOpen().first().single().reminders)
        assertEquals(appointment.reminders, appointments.get(appointment.id)?.reminders)
    }

    @Test fun `BR-REM-001 BR-REM-007 scheduling snooze and delivery survive adapter recreation`() = runTest {
        val key = AlarmKey("task:t1:100:0")
        val scheduled = SqlDelightScheduledAlarmStore(database, dispatchers)
        val snoozes = SqlDelightSnoozeStore(database, dispatchers)
        val delivered = SqlDelightDeliveredAlarmStore(database, dispatchers)
        val alarm = ScheduledAlarm(key, TestInstant, 42)
        scheduled.replaceAll(listOf(alarm))
        snoozes.put(key, TestInstant + 1.hours)
        delivered.markDelivered(key, TestInstant)
        assertEquals(listOf(alarm), SqlDelightScheduledAlarmStore(database, dispatchers).all())
        assertEquals(mapOf(key to (TestInstant + 1.hours)), SqlDelightSnoozeStore(database, dispatchers).all())
        assertEquals(setOf(key), SqlDelightDeliveredAlarmStore(database, dispatchers).recent(TestInstant))
        assertTrue(delivered.recent(TestInstant + 1.hours).isEmpty())
        delivered.pruneBefore(TestInstant + 1.hours)
        assertTrue(delivered.recent(TestInstant).isEmpty())
        scheduled.replaceAll(emptyList())
        snoozes.remove(key)
        assertTrue(scheduled.all().isEmpty())
        assertTrue(snoozes.all().isEmpty())
    }
}
