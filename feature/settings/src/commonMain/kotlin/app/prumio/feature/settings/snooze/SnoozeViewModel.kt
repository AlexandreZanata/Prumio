package app.prumio.feature.settings.snooze

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.reminder.AlarmKey
import app.prumio.core.domain.reminder.SnoozeAlarmUseCase
import app.prumio.core.domain.reminder.snoozeOptions
import app.prumio.core.navigation.SnoozeKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.feature.settings.resources.Res
import app.prumio.feature.settings.resources.snooze_saved
import kotlinx.coroutines.launch

/** Snoozes one alarm through the domain; the sheet never changes the item's due date. */
internal class SnoozeViewModel(key: SnoozeKey, private val snooze: SnoozeAlarmUseCase) :
    PrumioViewModel<SnoozeUiState, SnoozeAction, SnoozeEffect>(
        SnoozeUiState(options = snoozeOptions(AlarmKey(key.alarmKey))),
    ) {
    private val alarmKey = AlarmKey(key.alarmKey)
    override fun onAction(action: SnoozeAction) {
        when (action) {
            SnoozeAction.Close -> sendEffect(SnoozeEffect.Close)

            is SnoozeAction.Select -> if (!currentState.isSaving) {
                updateState { it.copy(isSaving = true) }
                viewModelScope.launch {
                    snooze(alarmKey, action.option).onSuccess {
                        sendEffect(SnoozeEffect.ShowMessage(UiMessage(Res.string.snooze_saved)))
                        sendEffect(SnoozeEffect.Close)
                    }.onFailure {
                        updateState { state -> state.copy(isSaving = false) }
                        sendEffect(SnoozeEffect.ShowMessage(it.toUiMessage()))
                    }
                }
            }
        }
    }
}
