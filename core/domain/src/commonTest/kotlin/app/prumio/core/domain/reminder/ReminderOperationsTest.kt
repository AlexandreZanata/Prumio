package app.prumio.core.domain.reminder

import app.cash.turbine.test
import app.prumio.core.common.Outcome
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.task.TaskDraft
import app.prumio.core.model.TaskStatus
import app.prumio.core.testing.ReminderTestEnvironment
import app.prumio.core.testing.aMatter
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class ReminderOperationsTest {
    private val env = ReminderTestEnvironment()
    private val domain = env.domain
    private suspend fun task() = (
        domain.createTask(
            TaskDraft("Call", dueDate = LocalDate(2026, 1, 15), dueTime = LocalTime(10, 0)),
        ) as Outcome.Success
        ).value

    @Test fun `BR-REM-001 reconciliation is serialized idempotent and force restores OS state`() = runTest {
        task()
        (1..5).map { async { env.reconcile() } }.awaitAll()
        assertEquals(1, env.scheduler.scheduled.size)
        val code = env.scheduled.all().single().requestCode
        env.reconcile(forceReschedule = true)
        assertEquals(listOf(code, code), env.scheduler.scheduled.map { it.second })
    }

    @Test fun `BR-REM-005 changed closed or deleted owners cancel alarms and stale snoozes`() = runTest {
        val task = task()
        env.reconcile()
        val old = env.scheduled.all().single().key
        env.snoozes.put(old, domain.time.now() + 1.hours)
        domain.updateTask(task.id, TaskDraft("Call", dueDate = LocalDate(2026, 1, 16), dueTime = LocalTime(10, 0)))
        env.reconcile()
        assertEquals(listOf(old), env.scheduler.cancelled)
        assertTrue(env.snoozes.all().isEmpty())
        domain.completeTask(task.id)
        env.reconcile()
        assertTrue(env.scheduled.all().isEmpty())
        domain.deleteTask(task.id)
        env.reconcile()
    }

    @Test fun `BR-REM-006 BR-GEN-006 delivery happens once without completing the task`() = runTest {
        val task = task()
        env.reconcile()
        val key = env.scheduled.all().single().key
        env.deliver(key)
        assertTrue(env.presenter.shown.isEmpty())
        domain.time.advanceBy(1.hours)
        (1..4).map { async { env.deliver(key) } }.awaitAll()
        assertEquals("Call", env.presenter.shown.single().title)
        assertEquals(TaskStatus.Open, domain.repos.tasks.get(task.id)?.status)
        assertTrue(key in env.delivered.recent(domain.time.now() - 1.hours))
        env.reconcile()
        assertTrue(env.scheduled.all().isEmpty())
    }

    @Test fun `BR-REM-014 denied access preserves wishes without a hot loop`() = runTest {
        val task = task()
        env.reconcile()
        val key = env.scheduled.all().single().key
        domain.notificationAccess.current = NotificationAccessState(false, true, false, true)
        domain.time.advanceBy(1.hours)
        env.deliver(key)
        env.reconcile()
        assertTrue(env.presenter.shown.isEmpty())
        assertTrue(env.scheduled.all().isEmpty())
        assertTrue(env.delivered.recent(domain.time.now() - 1.hours).isEmpty())
        assertEquals(1, domain.repos.tasks.get(task.id)?.reminders?.size)
        domain.notificationAccess.current = NotificationAccessState(true, false, true, false)
        env.reconcile(forceReschedule = true)
        env.deliver(key)
        assertEquals(1, env.presenter.shown.size)
    }

    @Test fun `BR-REM-007 snooze survives delivery and affects only its identity`() = runTest {
        val task = task()
        env.reconcile()
        val key = env.scheduled.all().single().key
        domain.time.advanceBy(1.hours)
        env.deliver(key)
        for (option in listOf(SnoozeOption.TenMinutes, SnoozeOption.OneHour, SnoozeOption.Tomorrow)) {
            assertIs<Outcome.Success<Unit>>(env.snooze(key, option))
            env.reconcile()
            val until = env.snoozes.all().getValue(key)
            val expected = when (option) {
                SnoozeOption.TenMinutes -> domain.time.now() + 10.minutes

                SnoozeOption.OneHour -> domain.time.now() +
                    1.hours

                SnoozeOption.Tomorrow -> kotlin.time.Instant.parse("2026-01-16T09:00:00Z")
            }
            assertEquals(expected, until)
            assertEquals(until, env.scheduled.all().single().triggerAt)
        }
        assertEquals(task, domain.repos.tasks.get(task.id))
        assertEquals(
            listOf("task:${task.id.value}", "task:${task.id.value}", "task:${task.id.value}"),
            env.presenter.cancelled,
        )
        domain.time.setLocal(LocalDateTime(2026, 1, 16, 9, 0))
        env.deliver(key)
        assertEquals(2, env.presenter.shown.size)
        assertTrue(env.snoozes.all().isEmpty())
    }

    @Test fun `BR-REM-008 matters permit only tomorrow and stale keys fail safely`() = runTest {
        val matter = aMatter("m1", followUpDate = LocalDate(2026, 1, 15), waitingFor = "Reply")
        domain.repos.store.putMatter(matter)
        env.reconcile()
        val key = env.scheduled.all().single().key
        assertEquals(listOf(SnoozeOption.Tomorrow), snoozeOptions(key))
        assertIs<Outcome.Failure<*>>(env.snooze(key, SnoozeOption.TenMinutes))
        env.deliver(key)
        assertEquals("Reply", env.presenter.shown.single().waitingFor)
        assertIs<Outcome.Success<Unit>>(env.snooze(key, SnoozeOption.Tomorrow))
        assertIs<Outcome.Failure<*>>(env.snooze(AlarmKey("missing"), SnoozeOption.OneHour))
    }

    @Test fun `BR-REM-008 complete action uses the normal task transition and cancels notification`() = runTest {
        val task = task()
        env.complete(task.id)
        assertEquals(TaskStatus.Completed, domain.repos.tasks.get(task.id)?.status)
        assertEquals(listOf("task:${task.id.value}"), env.presenter.cancelled)
        assertTrue(env.trigger.requests > 0)
    }

    @Test fun `BR-REM-002 BR-SET-002 settings changes emit new input and replace floating alarms`() = runTest {
        domain.createTask(TaskDraft("Call", dueDate = LocalDate(2026, 1, 16), reminderOffsets = listOf(0)))
        env.reconcile()
        val previous = env.scheduled.all().single()
        env.inputs().test {
            assertEquals(LocalTime(9, 0), awaitItem().settings.defaultReminderTime)
            domain.updateSetting(SettingKeys.DefaultReminderTime, LocalTime(8, 30))
            assertEquals(LocalTime(8, 30), awaitItem().settings.defaultReminderTime)
        }
        env.reconcile()
        assertTrue(previous.key in env.scheduler.cancelled)
        assertEquals(previous.triggerAt - 30.minutes, env.scheduled.all().single().triggerAt)
    }
}
