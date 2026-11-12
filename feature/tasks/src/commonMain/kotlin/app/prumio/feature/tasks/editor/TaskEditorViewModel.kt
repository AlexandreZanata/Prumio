package app.prumio.feature.tasks.editor

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.Outcome
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.ObserveMattersUseCase
import app.prumio.core.domain.task.CreateTaskUseCase
import app.prumio.core.domain.task.ObserveTaskUseCase
import app.prumio.core.domain.task.TaskDraft
import app.prumio.core.domain.task.UpdateTaskUseCase
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.fieldMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.feature.tasks.resources.Res
import app.prumio.feature.tasks.resources.task_saved
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Creates (no task id in [key]) or edits a task. Rules: BR-GEN-001, BR-TASK-001, BR-TASK-002, BR-MATTER-005. */
internal class TaskEditorViewModel(
    key: TaskEditorKey,
    private val observeTask: ObserveTaskUseCase,
    private val observeMatters: ObserveMattersUseCase,
    private val createTask: CreateTaskUseCase,
    private val updateTask: UpdateTaskUseCase,
    time: TimeProvider,
) : PrumioViewModel<TaskEditorUiState, TaskEditorAction, TaskEditorEffect>(
    TaskEditorUiState(isNew = key.taskId == null, matterId = key.matterId?.let(::MatterId), today = time.today()),
) {
    private val taskId = key.taskId?.let(::TaskId)
    private var existing: Task? = null

    init {
        viewModelScope.launch {
            val task = taskId?.let { observeTask(it).first() }
            existing = task
            val overview = observeMatters().first()
            val matters = overview.needsAttention + overview.waiting + overview.open
            updateState { state ->
                val loaded = task?.let {
                    state.copy(
                        title = it.title,
                        notes = it.notes.orEmpty(),
                        dueDate = it.dueDate,
                        dueTime = it.dueTime,
                        matterId = it.matterId,
                    )
                } ?: state
                loaded.copy(isLoading = false, matters = matters.map { it.matter })
            }
        }
    }

    override fun onAction(action: TaskEditorAction) {
        when (action) {
            is TaskEditorAction.ChangeTitle -> updateState { it.copy(title = action.title, titleError = null) }

            is TaskEditorAction.ChangeNotes -> updateState { it.copy(notes = action.notes) }

            is TaskEditorAction.ChangeDueDate -> updateState {
                it.copy(dueDate = action.date, dueTime = if (action.date == null) null else it.dueTime)
            }

            is TaskEditorAction.ChangeDueTime -> updateState { it.copy(dueTime = action.time) }

            is TaskEditorAction.PickMatter -> updateState { it.copy(isPickingMatter = action.open) }

            is TaskEditorAction.SelectMatter -> updateState { it.copy(matterId = action.id, isPickingMatter = false) }

            TaskEditorAction.Save -> save()

            TaskEditorAction.Close -> sendEffect(TaskEditorEffect.Close)
        }
    }

    private fun save() {
        val state = currentState
        if (!state.canSave) return
        updateState { it.copy(isSaving = true) }
        val draft = TaskDraft(
            title = state.title,
            notes = state.notes,
            dueDate = state.dueDate,
            dueTime = state.dueTime,
            recurrence = existing?.recurrence,
            matterId = state.matterId,
        )
        viewModelScope.launch {
            val result: Outcome<Task, DomainError> = if (taskId ==
                null
            ) {
                createTask(draft)
            } else {
                updateTask(taskId, draft)
            }
            result
                .onSuccess {
                    sendEffect(TaskEditorEffect.ShowMessage(UiMessage(Res.string.task_saved)))
                    sendEffect(TaskEditorEffect.Close)
                }
                .onFailure { error ->
                    updateState { it.copy(isSaving = false) }
                    if (error is DomainError.Validation && error.field == "title") {
                        updateState { it.copy(titleError = error.fieldMessage()) }
                    } else {
                        sendEffect(TaskEditorEffect.ShowMessage(error.toUiMessage()))
                    }
                }
        }
    }
}
