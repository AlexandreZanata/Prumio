package app.prumio.feature.tasks

import app.cash.turbine.test
import app.prumio.core.domain.task.PostponeOption
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.runViewModelTest
import app.prumio.feature.tasks.detail.TaskDetailAction
import app.prumio.feature.tasks.detail.TaskDetailEffect
import app.prumio.feature.tasks.detail.TaskDetailViewModel
import app.prumio.feature.tasks.resources.Res
import app.prumio.feature.tasks.resources.task_completed
import app.prumio.feature.tasks.resources.task_deleted
import app.prumio.feature.tasks.resources.task_dismissed
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TaskDetailViewModelTest {
    private val domain = TestUseCases()

    private fun createViewModel(): TaskDetailViewModel {
        domain.repos.store.putMatter(aMatter("m1", "Apartment rental"))
        domain.repos.store.putTask(aTask("t1", "Pay rent", dueDate = LocalDate(2026, 1, 14), matterId = "m1"))
        return TaskDetailViewModel(
            TaskDetailKey("t1"),
            domain.observeTask,
            domain.observeMatterDetail,
            domain.setTaskDone,
            domain.postponeTask,
            domain.dismissTask,
            domain.reopenTask,
            domain.deleteTask,
            domain.restoreTask,
            domain.time,
        )
    }

    private suspend fun stored() = domain.repos.tasks.get(TaskId("t1"))

    @Test
    fun `BR-TASK-004 shows the task, its matter and whether it is overdue`() = runViewModelTest {
        val state = createViewModel().state.value

        assertEquals("Pay rent", state.task?.title)
        assertEquals("Apartment rental", state.matterTitle)
        assertTrue(state.isOverdue)
    }

    @Test
    fun `BR-TASK-003 completing offers Undo`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(TaskDetailAction.SetDone(true))
            val message = assertIs<TaskDetailEffect.ShowMessage>(awaitItem()).message
            assertEquals(Res.string.task_completed, message.text)
            assertEquals(TaskDetailEffect.Navigate(MatterDetailKey("m1", promptNextAction = true)), awaitItem())
            assertEquals(TaskStatus.Completed, stored()?.status)
            assertFalse(viewModel.state.value.isOverdue)
            message.onAction?.invoke()
            assertEquals(TaskStatus.Open, stored()?.status)
        }
    }

    @Test
    fun `BR-TASK-003 dismissing offers Undo`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(TaskDetailAction.Dismiss)
            val message = assertIs<TaskDetailEffect.ShowMessage>(awaitItem()).message
            assertEquals(Res.string.task_dismissed, message.text)
            assertEquals(TaskDetailEffect.Navigate(MatterDetailKey("m1", promptNextAction = true)), awaitItem())
            assertEquals(TaskStatus.Dismissed, stored()?.status)
            message.onAction?.invoke()
            assertEquals(TaskStatus.Open, stored()?.status)
        }
    }

    @Test
    fun `BR-TASK-007 postponing moves the due date`() = runViewModelTest {
        val viewModel = createViewModel()
        viewModel.onAction(TaskDetailAction.ShowPostpone(true))
        assertTrue(viewModel.state.value.isPostponing)

        viewModel.onAction(TaskDetailAction.Postpone(PostponeOption.Tomorrow))

        assertFalse(viewModel.state.value.isPostponing)
        assertEquals(LocalDate(2026, 1, 16), stored()?.dueDate)
    }

    @Test
    fun `BR-GEN-005 deleting closes the screen and Undo restores the task`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(TaskDetailAction.Delete)
            val message = assertIs<TaskDetailEffect.ShowMessage>(awaitItem()).message
            assertEquals(Res.string.task_deleted, message.text)
            assertEquals(TaskDetailEffect.Close, awaitItem())
            assertNull(stored())
            message.onAction?.invoke()
            assertEquals(MatterId("m1"), stored()?.matterId)
        }
    }

    @Test
    fun `navigation actions and failures`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(TaskDetailAction.Edit)
            assertEquals(TaskDetailEffect.Navigate(TaskEditorKey(taskId = "t1")), awaitItem())
            viewModel.onAction(TaskDetailAction.OpenMatter)
            assertEquals(TaskDetailEffect.Navigate(MatterDetailKey("m1")), awaitItem())
            viewModel.onAction(TaskDetailAction.Close)
            assertEquals(TaskDetailEffect.Close, awaitItem())
            viewModel.onAction(TaskDetailAction.SetDone(false))
            assertIs<TaskDetailEffect.ShowMessage>(awaitItem())
        }
    }
}
