package app.prumio.feature.tasks

import app.cash.turbine.test
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.runViewModelTest
import app.prumio.core.ui.resources.validation_too_long
import app.prumio.feature.tasks.editor.TaskEditorAction
import app.prumio.feature.tasks.editor.TaskEditorEffect
import app.prumio.feature.tasks.editor.TaskEditorViewModel
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue
import app.prumio.core.ui.resources.Res as CoreUiRes

class TaskEditorViewModelTest {
    private val domain = TestUseCases()

    private fun createViewModel(key: TaskEditorKey = TaskEditorKey()) = TaskEditorViewModel(
        key,
        domain.observeTask,
        domain.observeMatters,
        domain.createTask,
        domain.updateTask,
        domain.time,
    )

    @Test
    fun `BR-GEN-001 a title is enough to create a task`() = runViewModelTest {
        val viewModel = createViewModel()
        assertTrue(viewModel.state.value.isNew)
        assertFalse(viewModel.state.value.canSave)

        viewModel.effects.test {
            viewModel.onAction(TaskEditorAction.ChangeTitle("Buy printer ink"))
            viewModel.onAction(TaskEditorAction.Save)
            assertIs<TaskEditorEffect.ShowMessage>(awaitItem())
            assertEquals(TaskEditorEffect.Close, awaitItem())
        }
        val saved = domain.repos.tasks.observeOpen().first().single()
        assertEquals("Buy printer ink", saved.title)
        assertNull(saved.dueDate)
    }

    @Test
    fun `BR-TASK-002 removing the date removes the time`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.onAction(TaskEditorAction.ChangeDueDate(LocalDate(2026, 1, 16)))
        viewModel.onAction(TaskEditorAction.ChangeDueTime(LocalTime(15, 0)))
        assertEquals(LocalTime(15, 0), viewModel.state.value.dueTime)
        viewModel.onAction(TaskEditorAction.ChangeDueDate(null))

        assertNull(viewModel.state.value.dueTime)
    }

    @Test
    fun `editing loads the task and saves changes with its matter`() = runViewModelTest {
        domain.repos.store.putMatter(aMatter("m1", "Apartment rental"))
        domain.repos.store.putTask(aTask("t1", "Send contract", dueDate = LocalDate(2026, 1, 20)))
        val viewModel = createViewModel(TaskEditorKey(taskId = "t1"))
        assertEquals("Send contract", viewModel.state.value.title)
        assertEquals(listOf("Apartment rental"), viewModel.state.value.matters.map { it.title })

        viewModel.onAction(TaskEditorAction.PickMatter(open = true))
        assertTrue(viewModel.state.value.isPickingMatter)
        viewModel.onAction(TaskEditorAction.SelectMatter(MatterId("m1")))
        assertEquals("Apartment rental", viewModel.state.value.matterTitle)
        viewModel.onAction(TaskEditorAction.ChangeNotes("Both copies"))
        viewModel.onAction(TaskEditorAction.Save)

        val saved = domain.repos.tasks.get(TaskId("t1"))!!
        assertEquals(MatterId("m1"), saved.matterId)
        assertEquals("Both copies", saved.notes)
    }

    @Test
    fun `BR-MATTER-005 a task created from a matter is linked to it`() = runViewModelTest {
        domain.repos.store.putMatter(aMatter("m1", "Apartment rental"))
        val viewModel = createViewModel(TaskEditorKey(matterId = "m1"))

        viewModel.onAction(TaskEditorAction.ChangeTitle("Call the landlord"))
        viewModel.onAction(TaskEditorAction.Save)

        assertEquals(MatterId("m1"), domain.repos.tasks.observeOpen().first().single().matterId)
    }

    @Test
    fun `BR-TASK-001 invalid titles show a field error`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.onAction(TaskEditorAction.ChangeTitle("x".repeat(501)))
        viewModel.onAction(TaskEditorAction.Save)

        assertEquals(CoreUiRes.string.validation_too_long, viewModel.state.value.titleError)
        viewModel.effects.test {
            viewModel.onAction(TaskEditorAction.Close)
            assertEquals(TaskEditorEffect.Close, awaitItem())
        }
    }
}
