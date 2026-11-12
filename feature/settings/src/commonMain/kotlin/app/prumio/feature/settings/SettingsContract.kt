package app.prumio.feature.settings

import app.prumio.core.model.ThemePreference
import app.prumio.core.ui.message.UiMessage

internal data class SettingsUiState(
    val theme: ThemePreference = ThemePreference.System,
    val defaultReminderTime: kotlinx.datetime.LocalTime = kotlinx.datetime.LocalTime(9, 0),
    val defaultAppointmentNoticeMinutes: Int = 30,
    val showDeniedBanner: Boolean = false,
)

internal sealed interface SettingsAction {
    data class ChangeTheme(val theme: ThemePreference) : SettingsAction
    data class ChangeReminderTime(val time: kotlinx.datetime.LocalTime) : SettingsAction
    data class ChangeAppointmentNotice(val minutes: Int) : SettingsAction
    data object Refresh : SettingsAction
    data object DismissBanner : SettingsAction
    data object OpenReliability : SettingsAction
    data object OpenLanguage : SettingsAction
    data object OpenSource : SettingsAction
    data object Close : SettingsAction
}

internal sealed interface SettingsEffect {
    data object OpenReliability : SettingsEffect
    data object OpenLanguage : SettingsEffect
    data object OpenSource : SettingsEffect
    data object Close : SettingsEffect
    data class ShowMessage(val message: UiMessage) : SettingsEffect
}
