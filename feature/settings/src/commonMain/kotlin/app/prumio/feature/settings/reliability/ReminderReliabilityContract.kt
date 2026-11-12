package app.prumio.feature.settings.reliability

import app.prumio.core.domain.reminder.ReminderReliability
import app.prumio.core.ui.message.UiMessage

internal enum class ReliabilityTarget { Notifications, ExactAlarms, Battery }
internal data class ReminderReliabilityUiState(val status: ReminderReliability? = null)
internal sealed interface ReminderReliabilityAction {
    data object Refresh : ReminderReliabilityAction
    data object DismissBanner : ReminderReliabilityAction
    data class Fix(val target: ReliabilityTarget) : ReminderReliabilityAction
    data object Close : ReminderReliabilityAction
}
internal sealed interface ReminderReliabilityEffect {
    data class OpenSettings(val target: ReliabilityTarget) : ReminderReliabilityEffect
    data class ShowMessage(val message: UiMessage) : ReminderReliabilityEffect
    data object Close : ReminderReliabilityEffect
}
