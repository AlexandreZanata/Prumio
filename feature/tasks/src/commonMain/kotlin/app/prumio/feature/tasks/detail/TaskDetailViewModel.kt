package app.prumio.feature.tasks.detail

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.Outcome
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.ObserveMatterDetailUseCase
import app.prumio.core.domain.task.ClosedTask
import app.prumio.core.domain.task.DeleteTaskUseCase
import app.prumio.core.domain.task.DismissTaskUseCase
import app.prumio.core.domain.task.ObserveTaskUseCase
import app.prumio.core.domain.task.PostponeOption
import app.prumio.core.domain.task.PostponeTaskUseCase
import app.prumio.core.domain.task.ReopenTaskUseCase
import app.prumio.core.domain.task.RestoreTaskUseCase
import app.prumio.core.domain.task.SetTaskDoneUseCase
import app.prumio.core.domain.task.TaskOverduePolicy
import app.prumio.core.model.TaskId
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.core.ui.resources.action_undo
import app.prumio.feature.tasks.resources.Res
import app.prumio.feature.tasks.resources.task_completed
import app.prumio.feature.tasks.resources.task_deleted
import app.prumio.feature.tasks.resources.task_dismissed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.StringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Rules: BR-TASK-003 (complete/reopen/dismiss), BR-TASK-004, BR-TASK-007 (postpone), BR-GEN-005 (delete + Undo). */
@OptIn(ExperimentalCoroutinesApi::class)
internal class TaskDetailViewModel(
    key: TaskDetailKey,
    observeTask: ObserveTaskUseCase,
    observeMatterDetail: ObserveMatterDetailUseCase,
    private val setTaskDone: SetTaskDoneUseCase,
    private val postponeTask: PostponeTaskUseCase,
    private val dismissTask: DismissTaskUseCase,
    private val reopenTask: ReopenTaskUseCase,
    private val deleteTask: DeleteTaskUseCase,
    private val restoreTask: RestoreTaskUseCase,
    private val time: TimeProvider,
) : PrumioViewModel<TaskDetailUiState, TaskDetailAction, TaskDetailEffect>(TaskDetailUiState()) {
    private val id = TaskId(key.taskId)

    init {
        observeTask(id)
            .onEach { task ->
                val now = time.localNow()
                updateState {
                    it.copy(
                        task = task,
                        today = now.date,
                        isOverdue = task != null && TaskOverduePolicy.isOverdue(task, now),
                    )
                }
            }
            .map { it?.matterId }
            .flatMapLatest { matterId ->
                matterId?.let { observeMatterDetail(it).map { detail -> detail?.matter?.title } } ?: flowOf(null)
            }
            .onEach { title -> updateState { it.copy(matterTitle = title) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: TaskDetailAction) {
        when (action) {
            is TaskDetailAction.SetDone -> perform {
                setTaskDone(id, action.done).onSuccess { result ->
                    if (!result.task.isOpen) message(Res.string.task_completed) { setTaskDone(id, done = false) }
                    promptNextAction(result)
                }
            }

            is TaskDetailAction.ShowPostpone -> updateState { it.copy(isPostponing = action.show) }

            is TaskDetailAction.Postpone -> postpone(action.option)

            TaskDetailAction.Dismiss -> perform {
                dismissTask(id).onSuccess { result ->
                    message(Res.string.task_dismissed) { reopenTask(id) }
                    promptNextAction(result)
                }
            }

            TaskDetailAction.Delete -> perform {
                deleteTask(id).onSuccess { deleted ->
                    message(Res.string.task_deleted) { restoreTask(deleted) }
                    sendEffect(TaskDetailEffect.Close)
                }
            }

            TaskDetailAction.Edit -> sendEffect(TaskDetailEffect.Navigate(TaskEditorKey(taskId = id.value)))

            TaskDetailAction.OpenMatter -> currentState.task?.matterId?.let {
                sendEffect(TaskDetailEffect.Navigate(MatterDetailKey(it.value)))
            }

            TaskDetailAction.Close -> sendEffect(TaskDetailEffect.Close)
        }
    }

    private fun promptNextAction(result: ClosedTask) {
        result.nextActionMatterId?.let {
            sendEffect(TaskDetailEffect.Navigate(MatterDetailKey(it.value, promptNextAction = true)))
        }
    }

    private fun postpone(option: PostponeOption) {
        updateState { it.copy(isPostponing = false) }
        perform { postponeTask(id, option) }
    }

    /** Runs [block] and reports any failure as a message. */
    private fun perform(block: suspend () -> Outcome<*, DomainError>) {
        viewModelScope.launch { block().onFailure { sendEffect(TaskDetailEffect.ShowMessage(it.toUiMessage())) } }
    }

    private fun message(text: StringResource, undo: suspend () -> Unit) {
        val message = UiMessage(text = text, actionLabel = CoreUiRes.string.action_undo, onAction = undo)
        sendEffect(TaskDetailEffect.ShowMessage(message))
    }
}
