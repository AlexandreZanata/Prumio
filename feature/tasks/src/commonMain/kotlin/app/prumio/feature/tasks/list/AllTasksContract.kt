package app.prumio.feature.tasks.list

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.task.TaskListFilter
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate

// All tasks (docs/product/screens.md#tasks): one segment at a time (BR-TASK-009).

internal data class TaskListItem(val task: Task, val isOverdue: Boolean)

internal data class AllTasksUiState(
    val filter: TaskListFilter = TaskListFilter.Upcoming,
    val tasks: List<TaskListItem> = emptyList(),
    val today: LocalDate = LocalDate(1970, 1, 1),
    val isLoading: Boolean = true,
)

internal sealed interface AllTasksAction {
    data class SelectFilter(val filter: TaskListFilter) : AllTasksAction

    data class SetDone(val id: TaskId, val done: Boolean) : AllTasksAction

    data class Open(val id: TaskId) : AllTasksAction

    data object Add : AllTasksAction

    data object Close : AllTasksAction
}

internal sealed interface AllTasksEffect {
    data class Navigate(val key: NavKey) : AllTasksEffect

    data object Close : AllTasksEffect

    data class ShowMessage(val message: UiMessage) : AllTasksEffect
}
