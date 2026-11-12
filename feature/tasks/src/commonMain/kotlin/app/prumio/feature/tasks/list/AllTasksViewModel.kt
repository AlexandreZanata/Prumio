package app.prumio.feature.tasks.list

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.domain.task.ObserveTaskListUseCase
import app.prumio.core.domain.task.SetTaskDoneUseCase
import app.prumio.core.domain.task.TaskListFilter
import app.prumio.core.domain.task.TaskOverduePolicy
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** Segments Anytime / Upcoming / Overdue / Completed / Dismissed. Rules: BR-TASK-009, BR-TASK-004. */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AllTasksViewModel(
    key: AllTasksKey,
    observeTaskList: ObserveTaskListUseCase,
    private val setTaskDone: SetTaskDoneUseCase,
    private val time: TimeProvider,
) : PrumioViewModel<AllTasksUiState, AllTasksAction, AllTasksEffect>(
    AllTasksUiState(filter = TaskListFilter.entries.firstOrNull { it.name == key.filter } ?: TaskListFilter.Upcoming),
) {
    private val filter = MutableStateFlow(currentState.filter)

    init {
        filter
            .flatMapLatest { observeTaskList(it) }
            .onEach { tasks ->
                val now = time.localNow()
                updateState { state ->
                    state.copy(
                        tasks = tasks.map { TaskListItem(it, TaskOverduePolicy.isOverdue(it, now)) },
                        today = now.date,
                        isLoading = false,
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AllTasksAction) {
        when (action) {
            is AllTasksAction.SelectFilter -> {
                updateState { it.copy(filter = action.filter, isLoading = true) }
                filter.value = action.filter
            }

            is AllTasksAction.SetDone -> viewModelScope.launch {
                setTaskDone(action.id, action.done).onSuccess { result ->
                    result.nextActionMatterId?.let {
                        sendEffect(AllTasksEffect.Navigate(MatterDetailKey(it.value, promptNextAction = true)))
                    }
                }.onFailure {
                    sendEffect(AllTasksEffect.ShowMessage(it.toUiMessage()))
                }
            }

            is AllTasksAction.Open -> sendEffect(AllTasksEffect.Navigate(TaskDetailKey(action.id.value)))

            AllTasksAction.Add -> sendEffect(AllTasksEffect.Navigate(TaskEditorKey()))

            AllTasksAction.Close -> sendEffect(AllTasksEffect.Close)
        }
    }
}
