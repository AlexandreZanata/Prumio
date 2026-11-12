package app.prumio.feature.settings

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.reminder.GetReminderReliabilityUseCase
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
    private val getReliability: GetReminderReliabilityUseCase,
) : PrumioViewModel<SettingsUiState, SettingsAction, SettingsEffect>(SettingsUiState()) {
    init {
        refresh()
        observeSettings().onEach { settings ->
            updateState {
                it.copy(
                    theme = settings.theme,
                    defaultReminderTime = settings.defaultReminderTime,
                    defaultAppointmentNoticeMinutes = settings.defaultAppointmentNoticeMinutes,
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun refresh() {
        viewModelScope.launch {
            getReliability().onSuccess { status -> updateState { it.copy(showDeniedBanner = status.showDeniedBanner) } }
                .onFailure { sendEffect(SettingsEffect.ShowMessage(it.toUiMessage())) }
        }
    }
    override fun onAction(action: SettingsAction) {
        when (action) {
            SettingsAction.Refresh -> refresh()

            SettingsAction.DismissBanner -> viewModelScope.launch {
                updateSetting(SettingKeys.NotificationBannerDismissed, true).onSuccess { refresh() }
                    .onFailure { sendEffect(SettingsEffect.ShowMessage(it.toUiMessage())) }
            }

            is SettingsAction.ChangeTheme -> viewModelScope.launch {
                updateSetting(SettingKeys.Theme, action.theme).onFailure {
                    sendEffect(SettingsEffect.ShowMessage(it.toUiMessage()))
                }
            }

            is SettingsAction.ChangeReminderTime -> viewModelScope.launch {
                updateSetting(SettingKeys.DefaultReminderTime, action.time).onFailure {
                    sendEffect(SettingsEffect.ShowMessage(it.toUiMessage()))
                }
            }

            is SettingsAction.ChangeAppointmentNotice -> viewModelScope.launch {
                updateSetting(SettingKeys.DefaultAppointmentNoticeMinutes, action.minutes).onFailure {
                    sendEffect(SettingsEffect.ShowMessage(it.toUiMessage()))
                }
            }

            SettingsAction.OpenReliability -> sendEffect(SettingsEffect.OpenReliability)

            SettingsAction.OpenLanguage -> sendEffect(SettingsEffect.OpenLanguage)

            SettingsAction.OpenSource -> sendEffect(SettingsEffect.OpenSource)

            SettingsAction.Close -> sendEffect(SettingsEffect.Close)
        }
    }
}
