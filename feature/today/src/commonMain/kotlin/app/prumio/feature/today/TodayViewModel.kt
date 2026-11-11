package app.prumio.feature.today

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.domain.today.GetDayPeriodUseCase
import app.prumio.core.navigation.SearchKey
import app.prumio.core.navigation.SettingsKey
import app.prumio.core.ui.mvi.PrumioViewModel

internal class TodayViewModel(private val getDayPeriod: GetDayPeriodUseCase, private val timeProvider: TimeProvider) :
    PrumioViewModel<TodayUiState, TodayAction, TodayEffect>(TodayUiState()) {
    init {
        refresh()
    }

    override fun onAction(action: TodayAction) {
        when (action) {
            TodayAction.Refresh -> refresh()
            TodayAction.OpenSearch -> sendEffect(TodayEffect.Navigate(SearchKey))
            TodayAction.OpenSettings -> sendEffect(TodayEffect.Navigate(SettingsKey))
        }
    }

    private fun refresh() {
        updateState { state -> state.copy(dayPeriod = getDayPeriod(), date = timeProvider.today()) }
    }
}
