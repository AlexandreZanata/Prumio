package app.prumio.core.data

import app.cash.turbine.test
import app.prumio.core.data.appointment.SqlDelightAppointmentRepository
import app.prumio.core.data.matter.SqlDelightMatterRepository
import app.prumio.core.data.task.SqlDelightTaskRepository
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskId
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.timedAt
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours

class AppointmentMatterRepositoryTest : RepositoryTestBase() {
    private val appointments = SqlDelightAppointmentRepository(database, dispatchers)
    private val matters = SqlDelightMatterRepository(database, dispatchers)
    private val tasks = SqlDelightTaskRepository(database, dispatchers)

    @Test
    fun `BR-TIME-002 timed and all-day appointments round-trip with their zone`() = runTest {
        val zone = TimeZone.of("America/Sao_Paulo")
        val timed = anAppointment(
            id = "a1",
            timing = timedAt(LocalDateTime(2026, 1, 20, 16, 0), LocalDateTime(2026, 1, 20, 17, 30), zone),
            location = "Clinic",
        )
        val pointInTime = anAppointment(id = "a2", timing = timedAt(LocalDateTime(2026, 1, 21, 8, 0)))
        val trip = anAppointment(id = "a3", timing = allDay(LocalDate(2026, 1, 19), LocalDate(2026, 1, 22)))

        listOf(timed, pointInTime, trip).forEach { appointments.save(it) }

        assertEquals(timed, appointments.get(timed.id))
        assertEquals(pointInTime, appointments.get(pointInTime.id))
        assertEquals(trip, appointments.get(trip.id))
        appointments.observeOverlapping(LocalDate(2026, 1, 20), LocalDate(2026, 1, 20)).test {
            assertEquals(listOf("a3", "a1"), awaitItem().map { it.id.value })
        }
        appointments.observe(AppointmentId("a2")).test {
            assertEquals(pointInTime, awaitItem())
            appointments.delete(AppointmentId("a2"))
            assertNull(awaitItem())
        }
    }

    @Test
    fun `BR-MATTER-003 matter, history and tasks are saved together`() = runTest {
        val matter = aMatter(id = "m1")
        val created = aMatterEntry(id = "e1", matterId = "m1", kind = MatterEntryKind.Created, text = null)
        val note = aMatterEntry(id = "e2", matterId = "m1", text = "Called", occurredOn = LocalDate(2026, 1, 16))
        matters.save(matter, entries = listOf(created, note), tasks = listOf(aTask(id = "t1", matterId = "m1")))

        assertEquals(matter, matters.get(matter.id))
        assertEquals(note, matters.getEntry(note.id))
        assertEquals(MatterId("m1"), tasks.get(TaskId("t1"))?.matterId)
        matters.observeHistory(matter.id).test { assertEquals(listOf("e2", "e1"), awaitItem().map { it.id.value }) }

        val edited = note.copy(text = "Called twice", updatedAt = TestInstant + 1.hours)
        matters.save(matter, entries = listOf(edited))
        assertEquals(edited, matters.getEntry(note.id))

        matters.deleteEntry(matter.copy(lastActivityAt = TestInstant + 2.hours), note.id)
        assertNull(matters.getEntry(note.id))
        assertEquals(TestInstant + 2.hours, matters.get(matter.id)?.lastActivityAt)
    }

    @Test
    fun `matters are listed by status and deletion unlinks tasks`() = runTest {
        matters.save(aMatter(id = "open", lastActivityAt = TestInstant))
        matters.save(aMatter(id = "waiting", status = MatterStatus.Waiting, lastActivityAt = TestInstant + 1.hours))
        matters.save(aMatter(id = "done", status = MatterStatus.Resolved, lastActivityAt = TestInstant))
        tasks.save(aTask(id = "t1", matterId = "open"))

        matters.observeUnresolved().test { assertEquals(listOf("waiting", "open"), awaitItem().map { it.id.value }) }
        matters.observeResolvedSince(TestInstant).test { assertEquals(listOf("done"), awaitItem().map { it.id.value }) }

        matters.delete(MatterId("open"))
        assertNull(matters.get(MatterId("open")))
        assertNull(tasks.get(TaskId("t1"))?.matterId)
        matters.observe(MatterId("waiting")).test { assertEquals(MatterStatus.Waiting, awaitItem()?.status) }
    }
}
