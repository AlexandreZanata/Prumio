package app.prumio.core.domain.reminder

import app.prumio.core.model.AppointmentId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Reminder
import app.prumio.core.model.ReminderId
import app.prumio.core.model.ReminderOwner
import app.prumio.core.model.Settings
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.timedAt
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant

class ReminderPlannerTest {
    private val now = Instant.parse("2026-01-15T08:00:00Z")
    private val planner = ReminderPlanner()
    private fun task(
        id: String = "t1",
        date: LocalDate = LocalDate(2026, 1, 15),
        time: LocalTime? = LocalTime(10, 0),
        offset: Int = 0,
    ) = aTask(
        id,
        dueDate = date,
        dueTime = time,
    ).copy(reminders = listOf(Reminder(ReminderId("r$id"), ReminderOwner.Task(TaskId(id)), offset, now)))
    private fun input() = PlanningInput(now, TimeZone.UTC, Settings(), listOf(task()), emptyList(), emptyList())

    @Test fun `BR-REM-004 task offsets and floating default time produce anchors`() {
        val alarm = planner.plan(input()).single()
        assertEquals(now + 2.hours, alarm.triggerAt)
        assertEquals(now + 1.hours, planner.plan(input().copy(tasks = listOf(task(time = null)))).single().triggerAt)
        assertEquals(now + 1.hours, planner.plan(input().copy(tasks = listOf(task(offset = 60)))).single().triggerAt)
    }

    @Test fun `BR-REM-003 window limit and tie ordering are deterministic`() {
        val many = (0..210).map { task("t$it") }
        val alarms = planner.plan(input().copy(tasks = many))
        assertEquals(200, alarms.size)
        assertEquals(alarms.sortedBy { it.key.value }, alarms)
        assertTrue(planner.plan(input().copy(tasks = listOf(task(date = LocalDate(2026, 2, 10))))).isEmpty())
    }

    @Test fun `BR-REM-009 missed reminders under 12h catch up once and older ones are skipped`() {
        val overdue = input().copy(now = now + 3.hours)
        val alarm = planner.plan(overdue).single()
        assertEquals(overdue.now, alarm.triggerAt)
        assertTrue(planner.plan(overdue.copy(delivered = setOf(alarm.key))).isEmpty())
        assertTrue(planner.plan(input().copy(now = now + 14.hours)).isEmpty())
    }

    @Test fun `BR-REM-007 snooze overrides delivery without changing the anchor`() {
        val alarm = planner.plan(input()).single()
        val snoozed = planner.plan(
            input().copy(snoozes = mapOf(alarm.key to (now + 4.hours)), delivered = setOf(alarm.key)),
        ).single()
        assertEquals(alarm.anchor, snoozed.anchor)
        assertEquals(now + 4.hours, snoozed.triggerAt)
    }

    @Test fun `BR-REM-005 closed tasks and resolved matters never plan alarms`() {
        val completed = task().copy(status = TaskStatus.Completed)
        val resolved =
            aMatter(status = MatterStatus.Resolved, followUpDate = LocalDate(2026, 1, 15))
        assertTrue(planner.plan(input().copy(tasks = listOf(completed), matters = listOf(resolved))).isEmpty())
    }

    @Test fun `BR-REM-004 all-day appointments and matter follow-ups use the default time`() {
        val a = anAppointment(
            timing = allDay(LocalDate(2026, 1, 15)),
        ).copy(
            reminders = listOf(
                Reminder(ReminderId("a"), ReminderOwner.Appointment(AppointmentId("appointment-1")), 0, now),
            ),
        )
        val m = aMatter(followUpDate = LocalDate(2026, 1, 15))
        val alarms = planner.plan(input().copy(tasks = emptyList(), appointments = listOf(a), matters = listOf(m)))
        assertEquals(listOf(now + 1.hours, now + 1.hours), alarms.map { it.triggerAt })
    }

    @Test fun `BR-TIME-002 timed appointments keep their instant when the device travels`() {
        val a = anAppointment(
            timing = timedAt(LocalDateTime(2026, 1, 15, 10, 0), zone = TimeZone.of("America/Sao_Paulo")),
        ).copy(
            reminders = listOf(
                Reminder(ReminderId("a"), ReminderOwner.Appointment(AppointmentId("appointment-1")), 0, now),
            ),
        )
        assertEquals(
            now + 5.hours,
            planner.plan(
                input().copy(tasks = emptyList(), appointments = listOf(a), zone = TimeZone.of("Europe/Lisbon")),
            ).single().triggerAt,
        )
    }

    @Test fun `BR-TIME-001 DST gap floating times move forward and overlap chooses earlier instant`() {
        val spring = input().copy(
            now = Instant.parse("2026-03-08T05:00:00Z"),
            zone = TimeZone.of("America/New_York"),
            tasks = listOf(task(date = LocalDate(2026, 3, 8), time = LocalTime(2, 30))),
        )
        assertEquals(Instant.parse("2026-03-08T07:30:00Z"), planner.plan(spring).single().anchor)
        val fall = spring.copy(
            now = Instant.parse("2026-11-01T04:00:00Z"),
            tasks = listOf(task(date = LocalDate(2026, 11, 1), time = LocalTime(1, 30))),
        )
        assertEquals(Instant.parse("2026-11-01T05:30:00Z"), planner.plan(fall).single().anchor)
    }
}
