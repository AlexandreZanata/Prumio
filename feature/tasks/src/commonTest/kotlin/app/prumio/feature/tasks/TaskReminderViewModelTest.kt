package app.prumio.feature.tasks

import app.cash.turbine.test
import app.prumio.core.domain.reminder.NotificationAccessState
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.runViewModelTest
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
import kotlin.test.assertTrue

class TaskReminderViewModelTest {
    private val domain = TestUseCases()
    private fun editor() = TaskEditorViewModel(
        TaskEditorKey(),
        domain.observeTask,
        domain.observeMatters,
        domain.createTask,
        domain.updateTask,
        domain.time,
        domain.checkReminderPermission,
        domain.recordNotificationPermission,
    )

    @Test fun `BR-REM-014 permission follows explanation and refusal preserves wishes`() = runViewModelTest {
        domain.notificationAccess.current = NotificationAccessState(false, true, false, true)
        val viewModel = editor()
        viewModel.onAction(TaskEditorAction.ChangeTitle("Reminder"))
        viewModel.onAction(TaskEditorAction.ChangeDueDate(LocalDate(2026, 1, 15)))
        viewModel.onAction(TaskEditorAction.ChangeDueTime(LocalTime(10, 0)))
        viewModel.onAction(TaskEditorAction.ChangeReminders(listOf(0, 30)))
        viewModel.onAction(TaskEditorAction.Save)
        assertTrue(viewModel.state.value.showPermissionExplanation)
        assertTrue(domain.repos.tasks.observeOpen().first().isEmpty())
        viewModel.effects.test {
            viewModel.onAction(TaskEditorAction.ConfirmPermission)
            assertEquals(TaskEditorEffect.RequestPermission, awaitItem())
            viewModel.onAction(TaskEditorAction.PermissionResult(false))
            assertIs<TaskEditorEffect.ShowMessage>(awaitItem())
            assertEquals(TaskEditorEffect.Close, awaitItem())
        }
        assertEquals(
            listOf(0, 30),
            domain.repos.tasks.observeOpen().first().single().reminders.map {
                it.offsetMinutes
            },
        )
        assertEquals(1, domain.observeSettings().first().notificationPermissionDenials)
        assertFalse(viewModel.state.value.showPermissionExplanation)
    }

    @Test fun `BR-REM-018 invalid sixth offset keeps the previous selection`() = runViewModelTest {
        val viewModel = editor()
        viewModel.onAction(TaskEditorAction.ChangeReminders(listOf(0)))
        viewModel.effects.test {
            viewModel.onAction(TaskEditorAction.ChangeReminders(listOf(0, 5, 10, 15, 30, 60)))
            assertIs<TaskEditorEffect.ShowMessage>(awaitItem())
        }
        assertEquals(listOf(0), viewModel.state.value.reminderOffsets)
    }

    @Test fun `BR-REM-014 permission granted continues saving and explicit removal is respected`() = runViewModelTest {
        val viewModel = editor()
        viewModel.onAction(TaskEditorAction.ChangeTitle("Silent"))
        viewModel.onAction(TaskEditorAction.ChangeDueDate(LocalDate(2026, 1, 15)))
        viewModel.onAction(TaskEditorAction.ChangeDueTime(LocalTime(10, 0)))
        viewModel.onAction(TaskEditorAction.ChangeReminders(emptyList()))
        viewModel.onAction(TaskEditorAction.PermissionResult(true))
        assertTrue(domain.repos.tasks.observeOpen().first().single().reminders.isEmpty())
    }
}
