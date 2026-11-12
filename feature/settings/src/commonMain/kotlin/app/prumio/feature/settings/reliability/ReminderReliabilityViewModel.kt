package app.prumio.feature.settings.reliability

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.reminder.GetReminderReliabilityUseCase
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.UpdateSettingUseCase
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.launch

/** Reports system capabilities honestly; never requests a permission implicitly (BR-REM-013/014). */
internal class ReminderReliabilityViewModel(
    private val getReliability: GetReminderReliabilityUseCase,
    private val updateSetting: UpdateSettingUseCase,
) : PrumioViewModel<ReminderReliabilityUiState, ReminderReliabilityAction, ReminderReliabilityEffect>(
    ReminderReliabilityUiState(),
) {
    init {
        refresh()
    }
    override fun onAction(action: ReminderReliabilityAction) {
        when (action) {
            ReminderReliabilityAction.Refresh -> refresh()

            ReminderReliabilityAction.DismissBanner -> viewModelScope.launch {
                updateSetting(SettingKeys.NotificationBannerDismissed, true).onSuccess {
                    refresh()
                }.onFailure { sendEffect(ReminderReliabilityEffect.ShowMessage(it.toUiMessage())) }
            }

            is ReminderReliabilityAction.Fix -> sendEffect(ReminderReliabilityEffect.OpenSettings(action.target))

            ReminderReliabilityAction.Close -> sendEffect(ReminderReliabilityEffect.Close)
        }
    }
    private fun refresh() {
        viewModelScope.launch {
            getReliability().onSuccess { status -> updateState { it.copy(status = status) } }
                .onFailure { sendEffect(ReminderReliabilityEffect.ShowMessage(it.toUiMessage())) }
        }
    }
}
