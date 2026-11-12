package app.prumio.feature.agenda

import app.cash.turbine.test
import app.prumio.core.domain.agenda.AgendaItem
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.runViewModelTest
import app.prumio.core.testing.timedAt
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AgendaViewModelTest {
    private val domain = TestUseCases()
    private val today = LocalDate(2026, 1, 15)

    private fun createViewModel(): AgendaViewModel {
        domain.repos.store.putAppointment(anAppointment("a1", "Dentist", timedAt(LocalDateTime(2026, 1, 16, 10, 0))))
        domain.repos.store.putTask(aTask("t1", "Pay rent", dueDate = LocalDate(2026, 1, 16)))
        return AgendaViewModel(domain.observeCurrentDate, domain.observeAgenda, domain.setTaskDone)
    }

    @Test
    fun `BR-MATTER-006 closing the last action opens the optional next-action question`() = runViewModelTest {
        val vm = createViewModel()
        domain.repos.store.putMatter(aMatter("m1"))
        domain.repos.store.putTask(aTask("linked", matterId = "m1", dueDate = today))
        vm.effects.test {
            vm.onAction(AgendaAction.SetTaskDone(TaskId("linked"), true))
            assertEquals(AgendaEffect.Navigate(MatterDetailKey("m1", promptNextAction = true)), awaitItem())
        }
    }

    @Test
    fun `BR-APPT-007 days start today and list their items`() = runViewModelTest {
        val state = createViewModel().state.value

        assertEquals(today, state.today)
        assertEquals(today, state.days.first().date)
        assertEquals(AgendaUiState.LOADED_DAYS, state.days.size)
        assertEquals(listOf(today, LocalDate(2026, 1, 16)), state.visibleDays.map { it.date })
        assertTrue(state.hasItems(LocalDate(2026, 1, 16)))
        assertFalse(state.isWeekEmpty)
    }

    @Test
    fun `selecting a far date loads from it and today brings it back`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.onAction(AgendaAction.SelectDate(LocalDate(2026, 3, 1)))
        assertEquals(LocalDate(2026, 3, 1), viewModel.state.value.days.first().date)
        assertTrue(viewModel.state.value.isWeekEmpty)

        viewModel.onAction(AgendaAction.GoToToday)
        assertEquals(today, viewModel.state.value.selectedDate)
        assertEquals(today, viewModel.state.value.days.first().date)

        viewModel.onAction(AgendaAction.SelectDate(LocalDate(2026, 1, 20)))
        assertEquals(today, viewModel.state.value.days.first().date)
    }

    @Test
    fun `BR-TODAY-007 refreshing picks up a new day`() = runViewModelTest {
        val viewModel = createViewModel()
        domain.time.setLocal(LocalDateTime(2026, 1, 16, 8, 0))

        viewModel.onAction(AgendaAction.Refresh)

        assertEquals(LocalDate(2026, 1, 16), viewModel.state.value.today)
        assertEquals(LocalDate(2026, 1, 16), viewModel.state.value.selectedDate)
    }

    @Test
    fun `completing a task removes it from the agenda`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.onAction(AgendaAction.SetTaskDone(TaskId("t1"), done = true))

        assertEquals(TaskStatus.Completed, domain.repos.tasks.get(TaskId("t1"))?.status)
        val tomorrow = viewModel.state.value.days[1].items
        assertTrue(tomorrow.none { it is AgendaItem.TaskItem })
    }

    @Test
    fun `items and the add button navigate`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(AgendaAction.AddAppointment)
            assertEquals(AgendaEffect.Navigate(AppointmentEditorKey(date = "2026-01-15")), awaitItem())
            viewModel.onAction(AgendaAction.OpenAppointment(AppointmentId("a1")))
            assertEquals(AgendaEffect.Navigate(AppointmentDetailKey("a1")), awaitItem())
            viewModel.onAction(AgendaAction.OpenTask(TaskId("t1")))
            assertEquals(AgendaEffect.Navigate(TaskDetailKey("t1")), awaitItem())
            viewModel.onAction(AgendaAction.SetTaskDone(TaskId("missing"), done = true))
            assertIs<AgendaEffect.ShowMessage>(awaitItem())
        }
    }
}
