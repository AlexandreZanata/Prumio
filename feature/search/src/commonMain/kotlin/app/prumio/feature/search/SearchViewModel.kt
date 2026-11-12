package app.prumio.feature.search

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.search.SearchUseCase
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.transformLatest

/** Debounced offline search; matching, minimum length and grouping come from BR-SEARCH-001 … BR-SEARCH-004. */
@OptIn(ExperimentalCoroutinesApi::class)
internal class SearchViewModel(search: SearchUseCase) :
    PrumioViewModel<SearchUiState, SearchAction, SearchEffect>(SearchUiState()) {
    private val query = MutableStateFlow("")
    private val refresh = MutableStateFlow(0)

    init {
        query.combine(refresh) { text, _ -> text }.transformLatest { text ->
            delay(DEBOUNCE_MS)
            emit(search(text))
        }.onEach { result ->
            result.onSuccess { results -> updateState { it.copy(results = results) } }
                .onFailure { sendEffect(SearchEffect.ShowMessage(it.toUiMessage())) }
        }.launchIn(viewModelScope)
    }

    override fun onAction(action: SearchAction) {
        when (action) {
            SearchAction.Refresh -> refresh.value++

            is SearchAction.ChangeQuery -> {
                if (action.query == currentState.query) return
                updateState { it.copy(query = action.query, results = null) }
                query.value = action.query
            }

            is SearchAction.OpenTask -> sendEffect(SearchEffect.Navigate(TaskDetailKey(action.id.value)))

            is SearchAction.OpenAppointment -> sendEffect(SearchEffect.Navigate(AppointmentDetailKey(action.id.value)))

            is SearchAction.OpenMatter -> sendEffect(SearchEffect.Navigate(MatterDetailKey(action.id.value)))

            SearchAction.OpenInbox -> sendEffect(SearchEffect.Navigate(InboxKey))

            SearchAction.Close -> sendEffect(SearchEffect.Close)
        }
    }

    private companion object {
        const val DEBOUNCE_MS = 200L
    }
}
