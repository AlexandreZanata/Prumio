package app.prumio.app.shell

import androidx.lifecycle.viewModelScope
import app.prumio.core.domain.inbox.ObserveInboxCountUseCase
import app.prumio.core.domain.settings.ObserveSettingsUseCase
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

/** App-wide appearance and Inbox badge (BR-SET-001, BR-INBOX-005); the sole shell ViewModel (ADR-0021). */
internal class ShellViewModel(observeInboxCount: ObserveInboxCountUseCase, observeSettings: ObserveSettingsUseCase) :
    PrumioViewModel<ShellUiState, Nothing, Nothing>(ShellUiState()) {
    init {
        combine(observeInboxCount(), observeSettings()) { count, settings -> ShellUiState(count, settings.theme) }
            .onEach { state -> updateState { state } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: Nothing) = Unit
}
