package app.prumio.core.domain.matter

import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class MatterOverviewTest {
    private val today = LocalDate(2026, 1, 15)
    private val repos = FakeRepositories()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 9, 0))

    @Test
    fun `BR-MATTER-005 the next action is the earliest dated open task, undated last`() {
        val actions = listOf(
            aTask(id = "undated", createdAt = TestInstant - 10.days),
            aTask(id = "later", dueDate = LocalDate(2026, 1, 20)),
            aTask(id = "timed", dueDate = LocalDate(2026, 1, 16), dueTime = LocalTime(9, 0)),
            aTask(id = "untimed", dueDate = LocalDate(2026, 1, 16)),
            aTask(id = "closed", dueDate = LocalDate(2026, 1, 1), status = TaskStatus.Completed),
        )

        assertEquals("timed", NextActionPolicy.nextAction(actions)?.id?.value)
        assertEquals(
            listOf("timed", "untimed", "later", "undated"),
            NextActionPolicy.openActions(actions).map {
                it.id.value
            },
        )
        assertNull(NextActionPolicy.nextAction(emptyList()))
    }

    @Test
    fun `BR-MATTER-010 a matter needs attention for a due follow-up, a missing next action or a due action`() {
        val waitingCalm = aMatter(status = MatterStatus.Waiting)
        val action = aTask(dueDate = LocalDate(2026, 1, 20))

        assertTrue(MatterAttentionPolicy.needsAttention(aMatter(), emptyList(), today))
        assertFalse(MatterAttentionPolicy.needsAttention(aMatter(), listOf(action), today))
        assertTrue(MatterAttentionPolicy.needsAttention(aMatter(), listOf(action.copy(dueDate = today)), today))
        assertFalse(MatterAttentionPolicy.needsAttention(waitingCalm, emptyList(), today))
        assertTrue(MatterAttentionPolicy.needsAttention(waitingCalm.copy(followUpDate = today), emptyList(), today))
        assertFalse(MatterAttentionPolicy.needsAttention(aMatter(status = MatterStatus.Resolved), emptyList(), today))
    }

    @Test
    fun `BR-MATTER-010 BR-MATTER-013 the overview groups each matter once, in rule order`() = runTest {
        repos.matters.save(aMatter(id = "no-action", lastActivityAt = TestInstant - 2.hours))
        repos.matters.save(
            aMatter(id = "follow-up", status = MatterStatus.Waiting, followUpDate = LocalDate(2026, 1, 14)),
        )
        repos.matters.save(
            aMatter(id = "waiting-later", status = MatterStatus.Waiting, followUpDate = LocalDate(2026, 2, 1)),
        )
        repos.matters.save(aMatter(id = "waiting-none", status = MatterStatus.Waiting))
        repos.matters.save(aMatter(id = "calm-old", lastActivityAt = TestInstant - 3.days))
        repos.matters.save(aMatter(id = "calm-new", lastActivityAt = TestInstant))
        repos.matters.save(
            aMatter(
                id = "resolved",
                status = MatterStatus.Resolved,
                lastActivityAt =
                    TestInstant - 1.days,
            ),
        )
        repos.matters.save(
            aMatter(
                id = "resolved-long-ago",
                status = MatterStatus.Resolved,
                lastActivityAt =
                    TestInstant - 100.days,
            ),
        )
        repos.tasks.save(aTask(id = "a1", matterId = "calm-old", dueDate = LocalDate(2026, 1, 20)))
        repos.tasks.save(aTask(id = "a2", matterId = "calm-new"))

        val overview = ObserveMattersUseCase(repos.matters, repos.tasks, time)().first()

        assertEquals(listOf("follow-up", "no-action"), overview.needsAttention.map { it.matter.id.value })
        assertEquals(listOf("waiting-later", "waiting-none"), overview.waiting.map { it.matter.id.value })
        assertEquals(listOf("calm-new", "calm-old"), overview.open.map { it.matter.id.value })
        assertEquals(listOf("resolved"), overview.resolved.map { it.matter.id.value })
        assertEquals("a1", overview.open.last().nextAction?.id?.value)
    }

    @Test
    fun `the detail shows history, next action and closed actions`() = runTest {
        repos.matters.save(aMatter(id = "m1"), listOf(aMatterEntry(id = "e1", matterId = "m1")))
        repos.tasks.save(aTask(id = "open", matterId = "m1", dueDate = today))
        repos.tasks.save(aTask(id = "done", matterId = "m1", status = TaskStatus.Completed))
        val observe = ObserveMatterDetailUseCase(repos.matters, repos.tasks, time)

        val detail = observe(MatterId("m1")).first()

        assertEquals("open", detail?.nextAction?.id?.value)
        assertEquals(listOf("done"), detail?.closedActions?.map { it.id.value })
        assertEquals(listOf("e1"), detail?.history?.map { it.id.value })
        assertTrue(detail?.needsAttention == true)
        assertNull(observe(MatterId("missing")).first())
    }
}
