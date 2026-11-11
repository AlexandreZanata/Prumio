package app.prumio.core.data

import app.cash.turbine.test
import app.prumio.core.data.inbox.SqlDelightInboxRepository
import app.prumio.core.data.matter.SqlDelightMatterRepository
import app.prumio.core.data.task.SqlDelightTaskRepository
import app.prumio.core.domain.inbox.ConversionTarget
import app.prumio.core.model.InboxItemId
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours

class InboxAndTaskRepositoryTest : RepositoryTestBase() {
    private val inbox = SqlDelightInboxRepository(database, dispatchers)
    private val tasks = SqlDelightTaskRepository(database, dispatchers)
    private val matters = SqlDelightMatterRepository(database, dispatchers)

    @Test
    fun `BR-INBOX-003 inbox items round-trip and are observed newest first`() = runTest {
        inbox.observeAll().test {
            assertEquals(emptyList(), awaitItem())
            inbox.save(anInboxItem(id = "old", createdAt = TestInstant))
            assertEquals(listOf("old"), awaitItem().map { it.id.value })
            inbox.save(anInboxItem(id = "new", createdAt = TestInstant + 1.hours))
            assertEquals(listOf("new", "old"), awaitItem().map { it.id.value })
        }
        val edited = anInboxItem(id = "old", text = "Edited")
        inbox.save(edited)
        assertEquals(edited, inbox.get(InboxItemId("old")))
        inbox.observeCount().test { assertEquals(2, awaitItem()) }
        inbox.delete(InboxItemId("old"))
        assertNull(inbox.get(InboxItemId("old")))
    }

    @Test
    fun `BR-INBOX-004 conversion stores the target and removes the item atomically`() = runTest {
        inbox.save(anInboxItem(id = "i1"))
        inbox.save(anInboxItem(id = "i2"))
        inbox.save(anInboxItem(id = "i3"))

        inbox.convert(InboxItemId("i1"), ConversionTarget.ToTask(aTask(id = "t1")))
        inbox.convert(InboxItemId("i2"), ConversionTarget.ToAppointment(anAppointment(id = "a1")))
        inbox.convert(
            InboxItemId("i3"),
            ConversionTarget.ToMatter(
                aMatter(id = "m1"),
                aMatterEntry(id = "e1", matterId = "m1", kind = MatterEntryKind.Created),
            ),
        )

        assertEquals("t1", tasks.get(TaskId("t1"))?.id?.value)
        assertEquals("m1", matters.get(MatterId("m1"))?.id?.value)
        inbox.observeCount().test { assertEquals(0, awaitItem()) }
    }

    @Test
    fun `tasks round-trip every field`() = runTest {
        matters.save(aMatter(id = "m1"))
        val task = aTask(
            id = "t1",
            notes = "Notes",
            dueDate = LocalDate(2026, 1, 20),
            dueTime = LocalTime(9, 30),
            matterId = "m1",
        )
        tasks.save(task)
        assertEquals(task, tasks.get(task.id))

        val completed = task.copy(status = TaskStatus.Completed, completedAt = TestInstant + 1.hours)
        tasks.save(completed)
        assertEquals(completed, tasks.get(task.id))
        assertEquals(listOf(completed), tasks.getByMatter(MatterId("m1")))
    }

    @Test
    fun `task lists filter and order like the domain expects`() = runTest {
        tasks.save(aTask(id = "late", dueDate = LocalDate(2026, 1, 10)))
        tasks.save(aTask(id = "today-timed", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(14, 0)))
        tasks.save(aTask(id = "today", dueDate = LocalDate(2026, 1, 15)))
        tasks.save(aTask(id = "next", dueDate = LocalDate(2026, 1, 16)))
        tasks.save(aTask(id = "undated"))
        tasks.save(aTask(id = "done", status = TaskStatus.Completed))
        tasks.save(aTask(id = "dropped", status = TaskStatus.Dismissed))

        fun List<app.prumio.core.model.Task>.ids() = map { it.id.value }
        tasks.observeOpenDueOnOrBefore(LocalDate(2026, 1, 15)).test {
            assertEquals(listOf("late", "today", "today-timed"), awaitItem().ids())
        }
        tasks.observeOpenDueBetween(LocalDate(2026, 1, 15), LocalDate(2026, 1, 16)).test {
            assertEquals(listOf("today", "today-timed", "next"), awaitItem().ids())
        }
        tasks.observeOpenUndated().test { assertEquals(listOf("undated"), awaitItem().ids()) }
        tasks.observeOpen().test { assertEquals(5, awaitItem().size) }
        tasks.observeCompletedSince(TestInstant).test { assertEquals(listOf("done"), awaitItem().ids()) }
        tasks.observeDismissed().test { assertEquals(listOf("dropped"), awaitItem().ids()) }
        tasks.observe(TaskId("next")).test {
            assertEquals("next", awaitItem()?.id?.value)
            tasks.delete(TaskId("next"))
            assertNull(awaitItem())
        }
    }
}
