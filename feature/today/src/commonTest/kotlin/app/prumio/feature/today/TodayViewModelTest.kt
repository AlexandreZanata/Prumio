package app.prumio.feature.today

import app.cash.turbine.test
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.DayPeriod
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MattersKey
import app.prumio.core.navigation.SearchKey
import app.prumio.core.navigation.SettingsKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import app.prumio.core.testing.runViewModelTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull

class TodayViewModelTest {
    private val domain = TestUseCases()
    private fun create() =
        TodayViewModel(domain.getDayPeriod, domain.observeCurrentDate, domain.observeToday, domain.setTaskDone)

    @Test
    fun `BR-MATTER-006 closing the last action opens the optional next-action question`() = runViewModelTest {
        val vm = create()
        domain.repos.store.putMatter(aMatter("m1"))
        domain.repos.store.putTask(aTask("linked", matterId = "m1", dueDate = LocalDate(2026, 1, 15)))
        vm.effects.test {
            vm.onAction(TodayAction.SetTaskDone(TaskId("linked"), true))
            assertIs<TodayEffect.ShowMessage>(awaitItem())
            assertEquals(TodayEffect.Navigate(MatterDetailKey("m1", promptNextAction = true)), awaitItem())
        }
    }

    @Test
    fun `BR-TODAY-002 summary counts and task sections are live`() = runViewModelTest {
        domain.repos.store.putTask(aTask("t1", dueDate = LocalDate(2026, 1, 14)))
        domain.repos.store.putTask(aTask("t2", dueDate = LocalDate(2026, 1, 15)))
        domain.repos.store.putAppointment(anAppointment())
        domain.repos.store.putMatter(aMatter())
        val vm = create()
        val day = assertNotNull(vm.state.value.snapshot)
        assertEquals(2, day.taskCount)
        assertEquals(1, day.appointmentCountToday)
        assertEquals(1, day.attentionMatterCount)
        assertEquals(listOf(TaskId("t1")), day.overdueTasks.map { it.id })
        assertEquals(DayPeriod.Morning, vm.state.value.dayPeriod)
    }

    @Test
    fun `BR-TODAY-006 inbox nudge updates and opens the Inbox`() = runViewModelTest {
        val vm = create()
        domain.repos.store.putInboxItem(anInboxItem())
        assertEquals(1, vm.state.value.snapshot?.inboxCount)
        vm.effects.test {
            vm.onAction(TodayAction.OpenInbox)
            assertEquals(TodayEffect.Navigate(InboxKey), awaitItem())
        }
    }

    @Test
    fun `BR-GEN-005 inline completion offers Undo and refreshes counts`() = runViewModelTest {
        domain.repos.store.putTask(aTask("t1", dueDate = LocalDate(2026, 1, 15)))
        val vm = create()
        vm.effects.test {
            vm.onAction(TodayAction.SetTaskDone(TaskId("t1"), true))
            val message = assertIs<TodayEffect.ShowMessage>(awaitItem()).message
            assertEquals(0, vm.state.value.snapshot?.taskCount)
            assertEquals(1, vm.state.value.snapshot?.doneTodayCount)
            message.onAction?.invoke()
            assertEquals(TaskStatus.Open, domain.repos.tasks.get(TaskId("t1"))?.status)
            vm.onAction(TodayAction.SetTaskDone(TaskId("t1"), true))
            awaitItem()
            vm.onAction(TodayAction.SetTaskDone(TaskId("t1"), false))
            expectNoEvents()
        }
    }

    @Test
    fun `BR-TODAY-007 resume recomputes date after midnight and time-zone change`() = runViewModelTest {
        val vm = create()
        domain.time.setLocal(LocalDateTime(2026, 1, 16, 0, 30))
        vm.onAction(TodayAction.Refresh)
        assertEquals(LocalDate(2026, 1, 16), vm.state.value.date)
        domain.time.moveTo(TimeZone.of("America/Cuiaba"))
        vm.onAction(TodayAction.Refresh)
        assertEquals(LocalDate(2026, 1, 15), vm.state.value.date)
        assertEquals(vm.state.value.date, vm.state.value.snapshot?.date)
        assertEquals(DayPeriod.Evening, vm.state.value.dayPeriod)
    }

    @Test
    fun `Today links use the shared navigation contract`() = runViewModelTest {
        val vm = create()
        val cases = listOf(
            TodayAction.OpenSearch to SearchKey,
            TodayAction.OpenSettings to SettingsKey,
            TodayAction.OpenMatters to MattersKey,
            TodayAction.OpenAllTasks to AllTasksKey(),
            TodayAction.OpenDoneToday to AllTasksKey("Completed"),
            TodayAction.OpenTask(TaskId("t1")) to TaskDetailKey("t1"),
            TodayAction.OpenAppointment(AppointmentId("a1")) to AppointmentDetailKey("a1"),
            TodayAction.OpenMatter(MatterId("m1")) to MatterDetailKey("m1"),
        )
        vm.effects.test {
            cases.forEach { (action, key) ->
                vm.onAction(action)
                assertEquals(TodayEffect.Navigate(key), awaitItem())
            }
        }
    }

    @Test
    fun `missing tasks show errors`() = runViewModelTest {
        val vm = create()
        vm.effects.test {
            vm.onAction(TodayAction.SetTaskDone(TaskId("gone"), true))
            assertIs<TodayEffect.ShowMessage>(awaitItem())
        }
    }
}
