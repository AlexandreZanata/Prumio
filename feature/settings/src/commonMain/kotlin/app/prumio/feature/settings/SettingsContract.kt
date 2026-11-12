package app.prumio.feature.settings

import app.prumio.core.model.ThemePreference
import app.prumio.core.ui.message.UiMessage

internal data class SettingsUiState(val theme: ThemePreference = ThemePreference.System)

internal sealed interface SettingsAction {
    data class ChangeTheme(val theme: ThemePreference) : SettingsAction
    data object OpenLanguage : SettingsAction
    data object OpenSource : SettingsAction
    data object Close : SettingsAction
}

internal sealed interface SettingsEffect {
    data object OpenLanguage : SettingsEffect
    data object OpenSource : SettingsEffect
    data object Close : SettingsEffect
    data class ShowMessage(val message: UiMessage) : SettingsEffect
}
