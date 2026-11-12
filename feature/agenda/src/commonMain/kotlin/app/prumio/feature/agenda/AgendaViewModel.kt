package app.prumio.feature.agenda

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.agenda.ObserveAgendaUseCase
import app.prumio.core.domain.task.SetTaskDoneUseCase
import app.prumio.core.domain.today.ObserveCurrentDateUseCase
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** Rules: BR-APPT-005, BR-APPT-007 (through ObserveAgendaUseCase), BR-TODAY-007 (rollover), BR-TIME-003. */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AgendaViewModel(
    private val observeCurrentDate: ObserveCurrentDateUseCase,
    private val observeAgenda: ObserveAgendaUseCase,
    private val setTaskDone: SetTaskDoneUseCase,
) : PrumioViewModel<AgendaUiState, AgendaAction, AgendaEffect>(AgendaUiState()) {
    /** First loaded day; null follows today. */
    private val firstDay = MutableStateFlow<LocalDate?>(null)
    private val refresh = MutableStateFlow(0)

    init {
        refresh
            .flatMapLatest { observeCurrentDate() }
            .onEach { today ->
                // A new day: keep following it unless the user is browsing another date.
                updateState { state ->
                    val followsToday = state.selectedDate == state.today || state.isLoading
                    state.copy(today = today, selectedDate = if (followsToday) today else state.selectedDate)
                }
            }
            .combine(firstDay) { today, first -> first ?: today }
            .flatMapLatest { from -> observeAgenda(from, from.plus(DatePeriod(days = AgendaUiState.LOADED_DAYS - 1))) }
            .onEach { days -> updateState { it.copy(days = days, isLoading = false) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AgendaAction) {
        when (action) {
            is AgendaAction.SelectDate -> select(action.date)

            AgendaAction.GoToToday -> {
                firstDay.value = null
                updateState { it.copy(selectedDate = it.today) }
            }

            AgendaAction.Refresh -> refresh.value++

            AgendaAction.AddAppointment -> sendEffect(
                AgendaEffect.Navigate(AppointmentEditorKey(date = currentState.selectedDate.toString())),
            )

            is AgendaAction.OpenAppointment -> sendEffect(AgendaEffect.Navigate(AppointmentDetailKey(action.id.value)))

            is AgendaAction.OpenTask -> sendEffect(AgendaEffect.Navigate(TaskDetailKey(action.id.value)))

            is AgendaAction.SetTaskDone -> viewModelScope.launch {
                setTaskDone(action.id, action.done).onSuccess { result ->
                    result.nextActionMatterId?.let {
                        sendEffect(AgendaEffect.Navigate(MatterDetailKey(it.value, promptNextAction = true)))
                    }
                }.onFailure { sendEffect(AgendaEffect.ShowMessage(it.toUiMessage())) }
            }
        }
    }

    private fun select(date: LocalDate) {
        val loaded = currentState.days
        val isLoaded = loaded.isNotEmpty() && date >= loaded.first().date && date <= loaded.last().date
        if (!isLoaded) firstDay.value = date
        updateState { it.copy(selectedDate = date) }
    }
}
