package app.prumio.feature.tasks

import app.prumio.core.model.TaskStatus
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.aTask
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.validation_too_long
import app.prumio.feature.tasks.detail.TaskDetailScreen
import app.prumio.feature.tasks.detail.TaskDetailUiState
import app.prumio.feature.tasks.editor.TaskEditorScreen
import app.prumio.feature.tasks.editor.TaskEditorUiState
import app.prumio.feature.tasks.list.AllTasksScreen
import app.prumio.feature.tasks.list.AllTasksUiState
import app.prumio.feature.tasks.list.TaskListItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Test

class TasksScreenScreenshotTest : ScreenshotTest() {
    private val today = LocalDate(2026, 1, 15)

    @Test fun editorEmpty() = screenshot("editor_empty") {
        TaskEditorScreen(TaskEditorUiState(isLoading = false, today = today), {})
    }

    @Test fun editorTypical() = screenshot("editor_typical") {
        TaskEditorScreen(
            TaskEditorUiState(
                isLoading = false,
                title = "Send the signed contract",
                notes = "Both copies",
                today = today,
                dueDate = today,
                dueTime = LocalTime(15, 0),
            ),
            {
            },
        )
    }

    @Test fun editorError() = screenshot("editor_error") {
        TaskEditorScreen(
            TaskEditorUiState(
                isLoading = false,
                title = "A long title ".repeat(10),
                titleError = Res.string.validation_too_long,
                today = today,
            ),
            {
            },
        )
    }

    @Test fun detailOpen() = screenshot("detail_open") {
        TaskDetailScreen(
            TaskDetailUiState(
                task = aTask(title = "Pay rent", dueDate = today),
                today = today,
                isOverdue = true,
            ),
            {
            },
        )
    }

    @Test fun detailCompleted() = screenshot("detail_completed") {
        TaskDetailScreen(
            TaskDetailUiState(
                task = aTask(title = "Pay rent", status = TaskStatus.Completed),
                today = today,
            ),
            {
            },
        )
    }

    @Test fun listEmpty() = screenshot("list_empty") {
        AllTasksScreen(AllTasksUiState(isLoading = false, today = today), {})
    }

    @Test fun listTypical() = screenshot("list_typical") {
        AllTasksScreen(
            AllTasksUiState(
                isLoading = false,
                today = today,
                tasks = listOf(
                    TaskListItem(aTask(title = "Call the accountant", dueDate = today), false),
                    TaskListItem(aTask("t2", "A long task title ".repeat(10), dueDate = today), true),
                ),
            ),
            {
            },
        )
    }
}
