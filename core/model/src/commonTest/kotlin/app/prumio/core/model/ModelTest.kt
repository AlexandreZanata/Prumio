package app.prumio.core.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Instant

class ModelTest {
    private val now = Instant.parse("2026-11-11T12:00:00Z")

    @Test
    fun `task derived properties`() {
        val task = Task(id = TaskId("t"), title = "Pay rent", createdAt = now, updatedAt = now)
        val timed = task.copy(dueDate = LocalDate(2026, 11, 12), dueTime = LocalTime(9, 0))
        val recurring = timed.copy(recurrence = TaskRecurrence(RecurrenceRule(Frequency.Monthly)))

        assertTrue(task.isOpen)
        assertFalse(task.hasDueTime)
        assertFalse(task.isRecurring)
        assertTrue(timed.hasDueTime)
        assertTrue(recurring.isRecurring)
        assertEquals(RecurrenceBasis.FromDueDate, recurring.recurrence?.basis)
        assertFalse(task.copy(status = TaskStatus.Completed).isOpen)
    }

    @Test
    fun `matter and entry derived properties`() {
        val matter =
            Matter(id = MatterId("m"), title = "Car documents", lastActivityAt = now, createdAt = now, updatedAt = now)
        val note = MatterEntry(
            id = MatterEntryId("e"),
            matterId = matter.id,
            kind = MatterEntryKind.Note,
            text = "Called",
            occurredOn = LocalDate(2026, 11, 10),
            createdAt = now,
            updatedAt = now,
        )

        assertFalse(matter.isResolved)
        assertTrue(matter.copy(status = MatterStatus.Resolved).isResolved)
        assertTrue(note.isEditable)
        assertFalse(note.copy(kind = MatterEntryKind.StatusChanged).isEditable)
    }

    @Test
    fun `appointment timing kinds`() {
        val timed = AppointmentTiming.Timed(LocalDateTime(2026, 11, 12, 16, 0), null, TimeZone.of("America/Sao_Paulo"))
        val allDay = AppointmentTiming.AllDay(LocalDate(2026, 11, 12), LocalDate(2026, 11, 13))
        val appointment =
            Appointment(id = AppointmentId("a"), title = "Doctor", timing = timed, createdAt = now, updatedAt = now)

        assertFalse(appointment.isAllDay)
        assertTrue(appointment.copy(timing = allDay).isAllDay)
    }

    @Test
    fun `recurrence rule invariants`() {
        val rule = RecurrenceRule(
            frequency = Frequency.Monthly,
            interval = 2,
            weekdays = setOf(DayOfWeek.MONDAY),
            monthly = MonthlyPattern.NthWeekday(ordinal = -1, weekday = DayOfWeek.FRIDAY),
            end = RecurrenceEnd.AfterCount(3),
        )

        assertEquals(2, rule.interval)
        assertEquals(RecurrenceEnd.Until(LocalDate(2026, 12, 31)), RecurrenceEnd.Until(LocalDate(2026, 12, 31)))
        assertEquals(MonthlyPattern.DayOfMonth(31), MonthlyPattern.DayOfMonth(31))
        assertFailsWith<IllegalArgumentException> { RecurrenceRule(Frequency.Daily, interval = 0) }
        assertFailsWith<IllegalArgumentException> { MonthlyPattern.DayOfMonth(32) }
        assertFailsWith<IllegalArgumentException> { MonthlyPattern.NthWeekday(5, DayOfWeek.MONDAY) }
        assertFailsWith<IllegalArgumentException> { RecurrenceEnd.AfterCount(0) }
    }

    @Test
    fun `settings defaults`() {
        assertEquals(Settings(theme = ThemePreference.System, weekStart = null), Settings())
    }
}
