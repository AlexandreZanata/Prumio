package app.prumio.core.domain.task

import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.FakeIdGenerator
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.assertInvalidTransition
import app.prumio.core.testing.assertNotFound
import app.prumio.core.testing.assertSuccess
import app.prumio.core.testing.assertValidation
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours

class TaskUseCasesTest {
    private val repos = FakeRepositories()
    private val ids = FakeIdGenerator()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 9, 0))
    private val create = CreateTaskUseCase(repos.tasks, repos.matters, ids, time)
    private val complete = CompleteTaskUseCase(repos.tasks, repos.matters, ids, time)
    private val dismiss = DismissTaskUseCase(repos.tasks, repos.matters, ids, time)
    private val reopen = ReopenTaskUseCase(repos.tasks, repos.matters, ids, time)
    private val setDone = SetTaskDoneUseCase(complete, reopen)

    private suspend fun historyKinds(matterId: String) = repos.matters.observeHistory(MatterId(matterId)).first().map {
        it.kind
    }

    @Test
    fun `BR-TASK-001 BR-GEN-001 a task is created open with only a title`() = runTest {
        val task = create(TaskDraft("Pay rent")).assertSuccess()

        assertEquals(TaskStatus.Open, task.status)
        assertEquals(task, repos.tasks.get(task.id))
        create(TaskDraft(" ")).assertValidation("BR-TASK-001")
    }

    @Test
    fun `BR-TASK-008 linking a task to a matter is recorded in its history`() = runTest {
        repos.matters.save(aMatter(id = "m1"))

        val task = create(TaskDraft("Call dispatcher", matterId = MatterId("m1"))).assertSuccess()

        assertEquals(listOf(MatterEntryKind.ActionLinked), historyKinds("m1"))
        assertEquals(time.now(), repos.matters.get(MatterId("m1"))?.lastActivityAt)
        assertEquals("Call dispatcher", repos.matters.observeHistory(MatterId("m1")).first().single().text)
        assertEquals(task.id, repos.matters.observeHistory(MatterId("m1")).first().single().taskId)
        create(TaskDraft("x", matterId = MatterId("missing"))).assertNotFound()
    }

    @Test
    fun `BR-TASK-008 moving a task between matters records both sides`() = runTest {
        repos.matters.save(aMatter(id = "m1"))
        repos.matters.save(aMatter(id = "m2"))
        val task = create(TaskDraft("Call", matterId = MatterId("m1"))).assertSuccess()
        val update = UpdateTaskUseCase(repos.tasks, repos.matters, ids, time)

        update(task.id, TaskDraft("Call", matterId = MatterId("m2"))).assertSuccess()

        assertEquals(listOf(MatterEntryKind.ActionLinked, MatterEntryKind.ActionUnlinked), historyKinds("m1").sorted())
        assertEquals(listOf(MatterEntryKind.ActionLinked), historyKinds("m2"))
        LinkTaskToMatterUseCase(repos.tasks, repos.matters, ids, time)(task.id, null).assertSuccess()
        assertNull(repos.tasks.get(task.id)?.matterId)
        update(TaskId("missing"), TaskDraft("x")).assertNotFound()
        update(task.id, TaskDraft("")).assertValidation("BR-TASK-001")
    }

    @Test
    fun `BR-TASK-002 editing can set and remove dates`() = runTest {
        val task = create(TaskDraft("Water plants")).assertSuccess()
        val update = UpdateTaskUseCase(repos.tasks, repos.matters, ids, time)

        val dated = update(
            task.id,
            TaskDraft("Water plants", dueDate = LocalDate(2026, 1, 16), dueTime = LocalTime(8, 0)),
        )
            .assertSuccess()
        val undated = update(
            task.id,
            TaskDraft("Water plants", dueDate = null, dueTime = LocalTime(8, 0)),
        ).assertSuccess()

        assertEquals(LocalTime(8, 0), dated.dueTime)
        assertNull(undated.dueTime)
    }

    @Test
    fun `BR-TASK-003 completing, dismissing and reopening follow the state machine`() = runTest {
        val task = create(TaskDraft("Pay rent")).assertSuccess()

        val completed = complete(task.id).assertSuccess().task
        assertEquals(TaskStatus.Completed, completed.status)
        assertEquals(time.now(), completed.completedAt)
        complete(task.id).assertInvalidTransition("BR-TASK-003")
        dismiss(task.id).assertInvalidTransition("BR-TASK-003")

        val reopened = reopen(task.id).assertSuccess()
        assertEquals(TaskStatus.Open, reopened.status)
        assertNull(reopened.completedAt)
        reopen(task.id).assertInvalidTransition("BR-TASK-003")

        val dismissed = dismiss(task.id).assertSuccess().task
        assertEquals(time.now(), dismissed.dismissedAt)
        assertNull(reopen(task.id).assertSuccess().dismissedAt)
        complete(TaskId("missing")).assertNotFound()
        reopen(TaskId("missing")).assertNotFound()
    }

    @Test
    fun `BR-MATTER-006 closing the last open action of an open matter asks for the next one`() = runTest {
        repos.matters.save(aMatter(id = "m1"))
        val first = create(TaskDraft("Send form", matterId = MatterId("m1"))).assertSuccess()
        val second = create(TaskDraft("Pay fee", matterId = MatterId("m1"))).assertSuccess()

        assertFalse(complete(first.id).assertSuccess().matterNeedsNextAction)
        assertTrue(dismiss(second.id).assertSuccess().matterNeedsNextAction)
        assertTrue(
            historyKinds("m1").containsAll(listOf(MatterEntryKind.ActionCompleted, MatterEntryKind.ActionDismissed)),
        )
    }

    @Test
    fun `BR-MATTER-006 a waiting matter does not ask for a next action`() = runTest {
        repos.matters.save(aMatter(id = "m1", status = MatterStatus.Waiting))
        val task = create(TaskDraft("Follow up", matterId = MatterId("m1"))).assertSuccess()

        assertFalse(complete(task.id).assertSuccess().matterNeedsNextAction)
        assertFalse(complete(create(TaskDraft("Alone")).assertSuccess().id).assertSuccess().matterNeedsNextAction)
    }

    @Test
    fun `reopening a task of a resolved matter unlinks it`() = runTest {
        repos.matters.save(aMatter(id = "m1"))
        val task = create(TaskDraft("Call", matterId = MatterId("m1"))).assertSuccess()
        complete(task.id).assertSuccess()
        repos.matters.save(aMatter(id = "m1", status = MatterStatus.Resolved))

        assertNull(reopen(task.id).assertSuccess().matterId)
    }

    @Test
    fun `BR-TASK-007 postponing moves the due date and keeps the time`() = runTest {
        val task = create(
            TaskDraft("Call bank", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(10, 0)),
        ).assertSuccess()
        val postpone = PostponeTaskUseCase(repos.tasks, repos.matters, ids, time)

        assertEquals(LocalDate(2026, 1, 16), postpone(task.id, PostponeOption.Tomorrow).assertSuccess().dueDate)
        assertEquals(LocalDate(2026, 1, 22), postpone(task.id, PostponeOption.NextWeek).assertSuccess().dueDate)
        val moved = postpone(task.id, PostponeOption.To(LocalDate(2026, 2, 1))).assertSuccess()
        assertEquals(LocalDate(2026, 2, 1), moved.dueDate)
        assertEquals(LocalTime(10, 0), moved.dueTime)

        complete(task.id).assertSuccess()
        postpone(task.id, PostponeOption.Tomorrow).assertInvalidTransition("BR-TASK-007")
        postpone(TaskId("missing"), PostponeOption.Tomorrow).assertNotFound()
    }

    @Test
    fun `BR-GEN-005 a deleted task can be restored, unlinked if its matter is gone`() = runTest {
        repos.matters.save(aMatter(id = "m1"))
        val task = create(TaskDraft("Call", matterId = MatterId("m1"))).assertSuccess()
        val delete = DeleteTaskUseCase(repos.tasks)
        val restore = RestoreTaskUseCase(repos.tasks, repos.matters)

        val deleted = delete(task.id).assertSuccess()
        assertNull(repos.tasks.get(task.id))
        assertEquals(MatterId("m1"), restore(deleted).assertSuccess().matterId)

        delete(task.id).assertSuccess()
        repos.matters.delete(MatterId("m1"))
        assertNull(restore(deleted).assertSuccess().matterId)
        delete(TaskId("missing")).assertNotFound()
        LinkTaskToMatterUseCase(repos.tasks, repos.matters, ids, time)(TaskId("missing"), null).assertNotFound()
    }

    @Test
    fun `BR-TASK-004 a task is overdue after its due date, or after its due time today`() {
        val now = LocalDateTime(2026, 1, 15, 9, 0)

        assertTrue(TaskOverduePolicy.isOverdue(aTask(dueDate = LocalDate(2026, 1, 14)), now))
        assertTrue(
            TaskOverduePolicy.isOverdue(aTask(dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(8, 59)), now),
        )
        assertFalse(
            TaskOverduePolicy.isOverdue(aTask(dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(9, 0)), now),
        )
        assertFalse(TaskOverduePolicy.isOverdue(aTask(dueDate = LocalDate(2026, 1, 15)), now))
        assertFalse(TaskOverduePolicy.isOverdue(aTask(), now))
        assertFalse(
            TaskOverduePolicy.isOverdue(aTask(dueDate = LocalDate(2026, 1, 1), status = TaskStatus.Completed), now),
        )
    }

    @Test
    fun `BR-TIME-001 BR-TIME-003 due dates float with the device time zone`() = runTest {
        repos.tasks.save(aTask(id = "t1", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(8, 0)))
        val overdue = ObserveTaskListUseCase(repos.tasks, time)

        assertEquals(listOf("t1"), overdue(TaskListFilter.Overdue).first().map { it.id.value })
        time.moveTo(TimeZone.of("America/Sao_Paulo")) // 09:00 UTC is 06:00 there
        assertTrue(overdue(TaskListFilter.Overdue).first().isEmpty())
    }

    @Test
    fun `BR-TASK-009 all-tasks lists filter and order`() = runTest {
        repos.tasks.save(aTask(id = "anytime"))
        repos.tasks.save(aTask(id = "late", dueDate = LocalDate(2026, 1, 10)))
        repos.tasks.save(aTask(id = "today", dueDate = LocalDate(2026, 1, 15)))
        repos.tasks.save(aTask(id = "soon", dueDate = LocalDate(2026, 1, 20)))
        repos.tasks.save(aTask(id = "sooner", dueDate = LocalDate(2026, 1, 16), dueTime = LocalTime(7, 0)))
        repos.tasks.save(aTask(id = "done", status = TaskStatus.Completed, completedAt = time.now() - 2.hours))
        repos.tasks.save(
            aTask(
                id = "old-done",
                status = TaskStatus.Completed,
                completedAt =
                    time.now() - (31 * 24).hours,
            ),
        )
        repos.tasks.save(aTask(id = "dropped", status = TaskStatus.Dismissed))
        val lists = ObserveTaskListUseCase(repos.tasks, time)

        suspend fun ids(filter: TaskListFilter) = lists(filter).first().map { it.id.value }
        assertEquals(listOf("anytime"), ids(TaskListFilter.Anytime))
        assertEquals(listOf("sooner", "soon"), ids(TaskListFilter.Upcoming))
        assertEquals(listOf("late"), ids(TaskListFilter.Overdue))
        assertEquals(listOf("done"), ids(TaskListFilter.Completed))
        assertEquals(listOf("dropped"), ids(TaskListFilter.Dismissed))
        assertEquals("today", ObserveTaskUseCase(repos.tasks)(TaskId("today")).first()?.id?.value)
    }

    @Test
    fun `BR-TASK-003 the completion control completes and reopens`() = runTest {
        val task = create(TaskDraft("Pay rent")).assertSuccess()

        assertEquals(TaskStatus.Completed, setDone(task.id, done = true).assertSuccess().task.status)
        assertEquals(TaskStatus.Open, setDone(task.id, done = false).assertSuccess().task.status)
        setDone(task.id, done = false).assertInvalidTransition("BR-TASK-003")
    }
}
