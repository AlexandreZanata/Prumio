package app.prumio.feature.matters

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.domain.matter.ObserveMattersUseCase
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** Rules: BR-MATTER-010 (needs attention), BR-MATTER-013 (groups and order, from the use case). */
internal class MattersViewModel(observeMatters: ObserveMattersUseCase, private val time: TimeProvider) :
    PrumioViewModel<MattersUiState, MattersAction, MattersEffect>(MattersUiState()) {
    init {
        observeMatters()
            .onEach { overview -> updateState { it.copy(overview = overview, today = time.today()) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: MattersAction) {
        when (action) {
            is MattersAction.Open -> sendEffect(MattersEffect.Navigate(MatterDetailKey(action.id.value)))
            MattersAction.New -> sendEffect(MattersEffect.Navigate(MatterEditorKey()))
            MattersAction.ToggleResolved -> updateState { it.copy(showResolved = !it.showResolved) }
        }
    }
}
