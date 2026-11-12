package app.prumio.feature.today

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.task.SetTaskDoneUseCase
import app.prumio.core.domain.today.GetDayPeriodUseCase
import app.prumio.core.domain.today.ObserveCurrentDateUseCase
import app.prumio.core.domain.today.ObserveTodayUseCase
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MattersKey
import app.prumio.core.navigation.SearchKey
import app.prumio.core.navigation.SettingsKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.core.ui.resources.action_undo
import app.prumio.feature.today.resources.Res
import app.prumio.feature.today.resources.today_task_completed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Live day projection and rollover. Rules: BR-TODAY-001 … BR-TODAY-007, BR-TIME-003, BR-GEN-005. */
@OptIn(ExperimentalCoroutinesApi::class)
internal class TodayViewModel(
    private val getDayPeriod: GetDayPeriodUseCase,
    private val observeCurrentDate: ObserveCurrentDateUseCase,
    private val observeToday: ObserveTodayUseCase,
    private val setTaskDone: SetTaskDoneUseCase,
) : PrumioViewModel<TodayUiState, TodayAction, TodayEffect>(TodayUiState()) {
    private val refresh = MutableStateFlow(0)

    init {
        refresh.flatMapLatest { observeCurrentDate() }
            .onEach { date -> updateState { it.copy(date = date, dayPeriod = getDayPeriod()) } }
            .flatMapLatest { observeToday() }
            .onEach { snapshot -> updateState { it.copy(snapshot = snapshot, dayPeriod = getDayPeriod()) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: TodayAction) {
        when (action) {
            TodayAction.Refresh -> refresh.value++

            TodayAction.OpenSearch -> navigate(SearchKey)

            TodayAction.OpenSettings -> navigate(SettingsKey)

            TodayAction.OpenInbox -> navigate(InboxKey)

            TodayAction.OpenMatters -> navigate(MattersKey)

            TodayAction.OpenAllTasks -> navigate(AllTasksKey())

            TodayAction.OpenDoneToday -> navigate(AllTasksKey("Completed"))

            is TodayAction.OpenTask -> navigate(TaskDetailKey(action.id.value))

            is TodayAction.OpenAppointment -> navigate(AppointmentDetailKey(action.id.value))

            is TodayAction.OpenMatter -> navigate(MatterDetailKey(action.id.value))

            is TodayAction.SetTaskDone -> viewModelScope.launch {
                setTaskDone(action.id, action.done).onSuccess { result ->
                    if (action.done) {
                        sendEffect(
                            TodayEffect.ShowMessage(
                                UiMessage(
                                    text = Res.string.today_task_completed,
                                    actionLabel = CoreUiRes.string.action_undo,
                                    onAction = { setTaskDone(action.id, false) },
                                ),
                            ),
                        )
                    }
                    result.nextActionMatterId?.let {
                        navigate(MatterDetailKey(it.value, promptNextAction = true))
                    }
                }.onFailure { sendEffect(TodayEffect.ShowMessage(it.toUiMessage())) }
            }
        }
    }

    private fun navigate(key: NavKey) = sendEffect(TodayEffect.Navigate(key))
}
