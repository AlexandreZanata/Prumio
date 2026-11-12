package app.prumio.feature.tasks.detail

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.task.PostponeOption
import app.prumio.core.model.Task
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate

// Task detail (docs/product/screens.md#tasks): fields and every action on one task.

internal data class TaskDetailUiState(
    val task: Task? = null,
    val matterTitle: String? = null,
    val isOverdue: Boolean = false,
    val today: LocalDate = LocalDate(1970, 1, 1),
    val isPostponing: Boolean = false,
)

internal sealed interface TaskDetailAction {
    data class SetDone(val done: Boolean) : TaskDetailAction

    data class ShowPostpone(val show: Boolean) : TaskDetailAction

    data class Postpone(val option: PostponeOption) : TaskDetailAction

    data object Dismiss : TaskDetailAction

    data object Delete : TaskDetailAction

    data object Edit : TaskDetailAction

    data object OpenMatter : TaskDetailAction

    data object Close : TaskDetailAction
}

internal sealed interface TaskDetailEffect {
    data class Navigate(val key: NavKey) : TaskDetailEffect

    data object Close : TaskDetailEffect

    data class ShowMessage(val message: UiMessage) : TaskDetailEffect
}
