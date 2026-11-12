package app.prumio.feature.settings.snooze

import app.prumio.core.domain.reminder.SnoozeOption
import app.prumio.core.ui.message.UiMessage
internal data class SnoozeUiState(val options: List<SnoozeOption> = emptyList(), val isSaving: Boolean = false)
internal sealed interface SnoozeAction {
    data class Select(val option: SnoozeOption) : SnoozeAction
    data object Close : SnoozeAction
}
internal sealed interface SnoozeEffect {
    data object Close : SnoozeEffect
    data class ShowMessage(val message: UiMessage) : SnoozeEffect
}
