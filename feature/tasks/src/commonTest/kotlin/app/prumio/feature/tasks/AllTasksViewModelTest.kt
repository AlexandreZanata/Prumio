package app.prumio.feature.tasks

import app.cash.turbine.test
import app.prumio.core.domain.task.TaskListFilter
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.runViewModelTest
import app.prumio.feature.tasks.list.AllTasksAction
import app.prumio.feature.tasks.list.AllTasksEffect
import app.prumio.feature.tasks.list.AllTasksViewModel
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AllTasksViewModelTest {
    private val domain = TestUseCases()

    private fun createViewModel(filter: String? = null): AllTasksViewModel {
        domain.repos.store.putTask(aTask("undated", "Read a book"))
        domain.repos.store.putTask(aTask("late", "Pay rent", dueDate = LocalDate(2026, 1, 10)))
        domain.repos.store.putTask(aTask("next", "Dentist", dueDate = LocalDate(2026, 1, 20)))
        return AllTasksViewModel(AllTasksKey(filter), domain.observeTaskList, domain.setTaskDone, domain.time)
    }

    private fun AllTasksViewModel.titles() = state.value.tasks.map { it.task.title }

    @Test
    fun `BR-MATTER-006 closing the last action opens the optional next-action question`() = runViewModelTest {
        val vm = createViewModel()
        domain.repos.store.putMatter(aMatter("m1"))
        domain.repos.store.putTask(aTask("linked", matterId = "m1"))
        vm.effects.test {
            vm.onAction(AllTasksAction.SetDone(TaskId("linked"), true))
            assertEquals(AllTasksEffect.Navigate(MatterDetailKey("m1", promptNextAction = true)), awaitItem())
        }
    }

    @Test
    fun `BR-TASK-009 each segment lists its tasks`() = runViewModelTest {
        val viewModel = createViewModel()
        assertEquals(TaskListFilter.Upcoming, viewModel.state.value.filter)
        assertEquals(listOf("Dentist"), viewModel.titles())

        viewModel.onAction(AllTasksAction.SelectFilter(TaskListFilter.Anytime))
        assertEquals(listOf("Read a book"), viewModel.titles())
    }

    @Test
    fun `BR-TASK-004 overdue tasks are marked and the key picks the segment`() = runViewModelTest {
        val viewModel = createViewModel(filter = "Overdue")

        assertEquals(TaskListFilter.Overdue, viewModel.state.value.filter)
        assertTrue(viewModel.state.value.tasks.single().isOverdue)
    }

    @Test
    fun `completing from the list updates the segment`() = runViewModelTest {
        val viewModel = createViewModel(filter = "Overdue")

        viewModel.onAction(AllTasksAction.SetDone(TaskId("late"), done = true))

        assertEquals(TaskStatus.Completed, domain.repos.tasks.get(TaskId("late"))?.status)
        assertEquals(emptyList(), viewModel.titles())
    }

    @Test
    fun `navigation and failures`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(AllTasksAction.Open(TaskId("next")))
            assertEquals(AllTasksEffect.Navigate(TaskDetailKey("next")), awaitItem())
            viewModel.onAction(AllTasksAction.Add)
            assertEquals(AllTasksEffect.Navigate(TaskEditorKey()), awaitItem())
            viewModel.onAction(AllTasksAction.Close)
            assertEquals(AllTasksEffect.Close, awaitItem())
            viewModel.onAction(AllTasksAction.SetDone(TaskId("missing"), done = true))
            assertIs<AllTasksEffect.ShowMessage>(awaitItem())
        }
    }
}
