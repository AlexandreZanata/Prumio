package app.prumio.feature.settings

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.domain.settings.ObserveSettingsUseCase
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.UpdateSettingUseCase
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** Appearance preferences, with domain defaults (BR-SET-001). */
internal class SettingsViewModel(
    observeSettings: ObserveSettingsUseCase,
    private val updateSetting: UpdateSettingUseCase,
) : PrumioViewModel<SettingsUiState, SettingsAction, SettingsEffect>(SettingsUiState()) {
    init {
        observeSettings().onEach { settings ->
            updateState { it.copy(theme = settings.theme) }
        }.launchIn(viewModelScope)
    }

    override fun onAction(action: SettingsAction) {
        when (action) {
            is SettingsAction.ChangeTheme -> viewModelScope.launch {
                updateSetting(SettingKeys.Theme, action.theme).onFailure {
                    sendEffect(SettingsEffect.ShowMessage(it.toUiMessage()))
                }
            }

            SettingsAction.OpenLanguage -> sendEffect(SettingsEffect.OpenLanguage)

            SettingsAction.OpenSource -> sendEffect(SettingsEffect.OpenSource)

            SettingsAction.Close -> sendEffect(SettingsEffect.Close)
        }
    }
}
