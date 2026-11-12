package app.prumio.core.domain.today

import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import app.prumio.core.testing.timedAt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ObserveTodayUseCaseTest {
    private val repos = FakeRepositories()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 12, 0))
    private val observe = ObserveTodayUseCase(repos.tasks, repos.appointments, repos.matters, repos.inbox, time)

    @Test
    fun `BR-TODAY-002 BR-TODAY-003 tasks are counted and ordered overdue, timed, untimed`() = runTest {
        repos.tasks.save(aTask(id = "two-days-late", dueDate = LocalDate(2026, 1, 13)))
        repos.tasks.save(aTask(id = "morning", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(8, 0)))
        repos.tasks.save(aTask(id = "evening", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(18, 0)))
        repos.tasks.save(aTask(id = "afternoon", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(15, 0)))
        repos.tasks.save(aTask(id = "untimed-old", dueDate = LocalDate(2026, 1, 15), createdAt = TestInstant - 2.hours))
        repos.tasks.save(aTask(id = "untimed-new", dueDate = LocalDate(2026, 1, 15)))
        repos.tasks.save(aTask(id = "tomorrow", dueDate = LocalDate(2026, 1, 16)))
        repos.tasks.save(aTask(id = "done", status = TaskStatus.Completed, completedAt = TestInstant))
        repos.tasks.save(
            aTask(
                id = "done-yesterday",
                status = TaskStatus.Completed,
                completedAt =
                    TestInstant - 24.hours,
            ),
        )

        val today = observe().first()

        assertEquals(LocalDate(2026, 1, 15), today.date)
        assertEquals(listOf("two-days-late", "morning"), today.overdueTasks.map { it.id.value })
        assertEquals(
            listOf("afternoon", "evening", "untimed-old", "untimed-new"),
            today.dueTodayTasks.map {
                it.id.value
            },
        )
        assertEquals(6, today.taskCount)
        assertEquals(1, today.doneTodayCount)
    }

    @Test
    fun `BR-TODAY-002 BR-TODAY-004 appointments of today and the next one`() = runTest {
        repos.appointments.save(anAppointment(id = "past", timing = timedAt(LocalDateTime(2026, 1, 15, 9, 0))))
        repos.appointments.save(
            anAppointment(
                id = "ongoing",
                timing = timedAt(LocalDateTime(2026, 1, 15, 11, 0), LocalDateTime(2026, 1, 15, 13, 0)),
            ),
        )
        repos.appointments.save(anAppointment(id = "later", timing = timedAt(LocalDateTime(2026, 1, 15, 16, 0))))
        repos.appointments.save(anAppointment(id = "holiday", timing = allDay(LocalDate(2026, 1, 15))))

        val today = observe().first()

        assertEquals(4, today.appointmentCountToday)
        assertEquals(listOf("holiday"), today.allDayToday.map { it.id.value })
        assertEquals("ongoing", today.nextAppointment?.appointment?.id?.value)
        assertEquals(LocalDateTime(2026, 1, 15, 11, 0), today.nextAppointment?.start)
    }

    @Test
    fun `BR-TODAY-004 without appointments ahead today, the next one within seven days is shown`() = runTest {
        repos.appointments.save(anAppointment(id = "far", timing = timedAt(LocalDateTime(2026, 1, 30, 10, 0))))
        assertNull(observe().first().nextAppointment)

        repos.appointments.save(anAppointment(id = "trip", timing = allDay(LocalDate(2026, 1, 18))))
        repos.appointments.save(anAppointment(id = "friday", timing = timedAt(LocalDateTime(2026, 1, 16, 10, 0))))
        assertEquals("friday", observe().first().nextAppointment?.appointment?.id?.value)

        time.advanceBy(30.minutes)
        repos.appointments.save(anAppointment(id = "now", timing = timedAt(LocalDateTime(2026, 1, 15, 12, 30))))
        assertEquals("now", observe().first().nextAppointment?.appointment?.id?.value)
    }

    @Test
    fun `BR-TODAY-005 up to five matters that need attention, plus the inbox count`() = runTest {
        repeat(6) { index -> repos.matters.save(aMatter(id = "m$index", lastActivityAt = TestInstant - index.hours)) }
        repos.matters.save(aMatter(id = "calm", status = MatterStatus.Waiting))
        repos.inbox.save(anInboxItem(id = "i1"))

        val today = observe().first()

        assertEquals(6, today.attentionMatterCount)
        assertEquals(listOf("m5", "m4", "m3", "m2", "m1"), today.attentionMatters.map { it.matter.id.value })
        assertEquals(1, today.inboxCount)
    }
}
