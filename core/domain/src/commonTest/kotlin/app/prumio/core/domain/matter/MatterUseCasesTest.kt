package app.prumio.core.domain.matter

import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.FakeIdGenerator
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.aMatterEntry
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
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MatterUseCasesTest {
    private val repos = FakeRepositories()
    private val ids = FakeIdGenerator()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 9, 0))
    private val create = CreateMatterUseCase(repos.matters, ids, time)
    private val wait = WaitOnMatterUseCase(repos.matters, ids, time)
    private val resume = ResumeMatterUseCase(repos.matters, ids, time)
    private val resolve = ResolveMatterUseCase(repos.matters, repos.tasks, ids, time)
    private val reopen = ReopenMatterUseCase(repos.matters, ids, time)

    private suspend fun history(id: MatterId) = repos.matters.observeHistory(id).first()

    @Test
    fun `BR-MATTER-001 a matter is created open with a Created entry`() = runTest {
        val matter = create(MatterDraft("Car documents", "Before the deadline")).assertSuccess()

        assertEquals(MatterStatus.Open, matter.status)
        assertEquals(listOf(MatterEntryKind.Created), history(matter.id).map { it.kind })
        create(MatterDraft("  ")).assertValidation("BR-MATTER-001")
    }

    @Test
    fun `BR-MATTER-002 BR-MATTER-003 every status change is recorded`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()

        val steps: List<suspend () -> Unit> = listOf(
            { wait(matter.id).assertSuccess() },
            { resume(matter.id).assertSuccess() },
            { resolve(matter.id).assertSuccess() },
            { reopen(matter.id).assertSuccess() },
        )
        steps.forEach { step ->
            time.advanceBy(1.minutes)
            step()
        }

        val changes = history(matter.id).filter { it.kind == MatterEntryKind.StatusChanged }.reversed()
        assertEquals(
            listOf(
                MatterStatus.Open to MatterStatus.Waiting,
                MatterStatus.Waiting to MatterStatus.Open,
                MatterStatus.Open to MatterStatus.Resolved,
                MatterStatus.Resolved to MatterStatus.Open,
            ),
            changes.map { it.fromStatus to it.toStatus },
        )
    }

    @Test
    fun `BR-MATTER-002 forbidden transitions are rejected`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()

        resume(matter.id).assertInvalidTransition("BR-MATTER-002")
        reopen(matter.id).assertInvalidTransition("BR-MATTER-002")
        wait(matter.id).assertSuccess()
        wait(matter.id).assertInvalidTransition("BR-MATTER-002")
        resolve(matter.id).assertSuccess()
        resolve(matter.id).assertInvalidTransition("BR-MATTER-002")
        wait(MatterId("missing")).assertNotFound()
    }

    @Test
    fun `BR-MATTER-004 waiting can say for whom and when to check again, and resuming clears who`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()

        val waiting = wait(
            matter.id,
            waitingFor = " Dispatcher ",
            followUp = FollowUp(LocalDate(2026, 1, 20), LocalTime(9, 0)),
        )
            .assertSuccess()
        assertEquals("Dispatcher", waiting.waitingFor)
        assertEquals(LocalDate(2026, 1, 20), waiting.followUpDate)
        assertEquals("2026-01-20T09:00", history(matter.id).first { it.kind == MatterEntryKind.FollowUpSet }.text)

        val open = resume(matter.id).assertSuccess()
        assertNull(open.waitingFor)
        assertEquals(LocalDate(2026, 1, 20), open.followUpDate)
        wait(matter.id, waitingFor = "x".repeat(501)).assertValidation("BR-GEN-002")
    }

    @Test
    fun `BR-MATTER-007 resolving dismisses open actions by default and clears the follow-up`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()
        repos.tasks.save(aTask(id = "open", matterId = matter.id.value))
        repos.tasks.save(aTask(id = "done", matterId = matter.id.value, status = TaskStatus.Completed))
        wait(matter.id, followUp = FollowUp(LocalDate(2026, 1, 20))).assertSuccess()

        val resolved = resolve(matter.id).assertSuccess()

        assertEquals(time.now(), resolved.resolvedAt)
        assertNull(resolved.followUpDate)
        assertNull(resolved.waitingFor)
        assertEquals(TaskStatus.Dismissed, repos.tasks.get(TaskId("open"))?.status)
        assertEquals(TaskStatus.Completed, repos.tasks.get(TaskId("done"))?.status)
        assertEquals(1, history(matter.id).count { it.kind == MatterEntryKind.ActionDismissed })
        assertNull(reopen(matter.id).assertSuccess().resolvedAt)
    }

    @Test
    fun `BR-MATTER-007 resolving can keep open actions as standalone tasks`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()
        repos.tasks.save(aTask(id = "open", matterId = matter.id.value))

        resolve(matter.id, OpenActionsChoice.Unlink).assertSuccess()

        val task = repos.tasks.get(TaskId("open"))
        assertEquals(TaskStatus.Open, task?.status)
        assertNull(task?.matterId)
        assertEquals(1, history(matter.id).count { it.kind == MatterEntryKind.ActionUnlinked })
    }

    @Test
    fun `BR-MATTER-008 notes can be backdated, never future, and only notes are editable`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()
        val add = AddMatterNoteUseCase(repos.matters, ids, time)
        val edit = EditMatterNoteUseCase(repos.matters, ids, time)
        val delete = DeleteMatterNoteUseCase(repos.matters, ids, time)

        val today = add(matter.id, "Request sent").assertSuccess()
        assertEquals(LocalDate(2026, 1, 15), today.occurredOn)
        assertEquals(
            LocalDate(2026, 1, 10),
            add(matter.id, "Called", LocalDate(2026, 1, 10)).assertSuccess().occurredOn,
        )
        add(matter.id, "Later", LocalDate(2026, 1, 16)).assertValidation("BR-MATTER-008")
        add(matter.id, " ").assertValidation("BR-MATTER-008")

        val edited = edit(today.id, "Request sent by email", LocalDate(2026, 1, 14)).assertSuccess()
        assertEquals("Request sent by email", repos.matters.getEntry(today.id)?.text)
        assertEquals(LocalDate(2026, 1, 14), edited.occurredOn)
        val created = history(matter.id).first { it.kind == MatterEntryKind.Created }
        edit(created.id, "x", LocalDate(2026, 1, 14)).assertValidation("BR-MATTER-008")
        delete(created.id).assertValidation("BR-MATTER-008")
        edit(MatterEntryId("missing"), "x", LocalDate(2026, 1, 14)).assertNotFound()

        delete(today.id).assertSuccess()
        assertNull(repos.matters.getEntry(today.id))
    }

    @Test
    fun `BR-MATTER-012 every change touches the matter activity`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()

        time.advanceBy(1.hours)
        AddMatterNoteUseCase(repos.matters, ids, time)(matter.id, "Called").assertSuccess()
        assertEquals(time.now(), repos.matters.get(matter.id)?.lastActivityAt)

        time.advanceBy(1.hours)
        val updated = UpdateMatterUseCase(
            repos.matters,
            time,
        )(matter.id, MatterDraft("Motorcycle documents")).assertSuccess()
        assertEquals(time.now(), updated.lastActivityAt)
        UpdateMatterUseCase(repos.matters, time)(matter.id, MatterDraft("")).assertValidation("BR-MATTER-001")
    }

    @Test
    fun `BR-MATTER-009 the follow-up can be set, moved and cleared while unresolved`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()
        val setFollowUp = SetMatterFollowUpUseCase(repos.matters, ids, time)

        assertNull(setFollowUp(matter.id, null).assertSuccess().followUpDate)
        assertEquals(
            LocalDate(2026, 1, 20),
            setFollowUp(matter.id, FollowUp(LocalDate(2026, 1, 20))).assertSuccess().followUpDate,
        )
        assertNull(setFollowUp(matter.id, null).assertSuccess().followUpDate)
        val kinds = history(matter.id).map { it.kind }
        assertEquals(1, kinds.count { it == MatterEntryKind.FollowUpSet })
        assertEquals(1, kinds.count { it == MatterEntryKind.FollowUpCleared })

        resolve(matter.id).assertSuccess()
        setFollowUp(matter.id, FollowUp(LocalDate(2026, 1, 20))).assertInvalidTransition("BR-MATTER-009")
    }

    @Test
    fun `BR-GEN-005 a deleted matter comes back with its history and links`() = runTest {
        val matter = create(MatterDraft("Car documents")).assertSuccess()
        repos.tasks.save(aTask(id = "t1", matterId = matter.id.value))
        repos.matters.save(repos.matters.get(matter.id)!!, listOf(aMatterEntry(id = "n1", matterId = matter.id.value)))

        val deleted = DeleteMatterUseCase(repos.matters, repos.tasks)(matter.id).assertSuccess()
        assertNull(repos.matters.get(matter.id))
        assertNull(repos.tasks.get(TaskId("t1"))?.matterId)

        RestoreMatterUseCase(repos.matters, repos.tasks)(deleted).assertSuccess()
        assertEquals(matter.id, repos.tasks.get(TaskId("t1"))?.matterId)
        assertEquals(2, history(matter.id).size)
        DeleteMatterUseCase(repos.matters, repos.tasks)(MatterId("missing")).assertNotFound()
    }
}
