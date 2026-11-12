package app.prumio.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioChip
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.ThemePreference
import app.prumio.core.ui.component.TimeField
import app.prumio.core.ui.label.reminderOffsetText
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.destination_settings
import app.prumio.feature.settings.resources.Res
import app.prumio.feature.settings.resources.reliability_banner
import app.prumio.feature.settings.resources.reliability_dismiss
import app.prumio.feature.settings.resources.settings_about
import app.prumio.feature.settings.resources.settings_appearance
import app.prumio.feature.settings.resources.settings_appointment_notice
import app.prumio.feature.settings.resources.settings_language
import app.prumio.feature.settings.resources.settings_language_description
import app.prumio.feature.settings.resources.settings_licenses
import app.prumio.feature.settings.resources.settings_licenses_text
import app.prumio.feature.settings.resources.settings_privacy
import app.prumio.feature.settings.resources.settings_reliability
import app.prumio.feature.settings.resources.settings_reminder_time
import app.prumio.feature.settings.resources.settings_reminders
import app.prumio.feature.settings.resources.settings_source
import app.prumio.feature.settings.resources.settings_theme_dark
import app.prumio.feature.settings.resources.settings_theme_light
import app.prumio.feature.settings.resources.settings_theme_system
import app.prumio.feature.settings.resources.settings_version
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Base settings: appearance and a transparent About section (P3.13). */
@Composable
internal fun SettingsScreen(
    state: SettingsUiState,
    version: String,
    supportsAppLanguage: Boolean,
    onAction: (SettingsAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(title = stringResource(CoreUiRes.string.destination_settings), onNavigateBack = {
                onAction(SettingsAction.Close)
            }, navigateBackContentDescription = stringResource(CoreUiRes.string.action_back))
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(
                padding,
            ).verticalScroll(rememberScrollState()).padding(PrumioTheme.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
        ) {
            PrumioSectionHeader(title = stringResource(Res.string.settings_appearance))
            ThemePreference.entries.forEach { theme ->
                PrumioChip(
                    label = stringResource(
                        when (theme) {
                            ThemePreference.System -> Res.string.settings_theme_system
                            ThemePreference.Light -> Res.string.settings_theme_light
                            ThemePreference.Dark -> Res.string.settings_theme_dark
                        },
                    ),
                    selected = state.theme == theme,
                    onClick = { onAction(SettingsAction.ChangeTheme(theme)) },
                )
            }
            if (supportsAppLanguage) {
                PrumioListItem(
                    headline = stringResource(
                        Res.string.settings_language,
                    ),
                    supporting = stringResource(Res.string.settings_language_description),
                    onClick = {
                        onAction(SettingsAction.OpenLanguage)
                    },
                )
            }
            PrumioSectionHeader(title = stringResource(Res.string.settings_reminders))
            if (state.showDeniedBanner) {
                PrumioListItem(headline = stringResource(Res.string.reliability_banner), onClick = {
                    onAction(SettingsAction.OpenReliability)
                })
                PrumioButton(text = stringResource(Res.string.reliability_dismiss), onClick = {
                    onAction(SettingsAction.DismissBanner)
                })
            }
            TimeField(
                label = stringResource(Res.string.settings_reminder_time),
                time = state.defaultReminderTime,
                onTimeChange = {
                    it?.let { value -> onAction(SettingsAction.ChangeReminderTime(value)) }
                },
                clearable = false,
            )
            PrumioText(stringResource(Res.string.settings_appointment_notice))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs)) {
                (
                    listOf(
                        0,
                        5,
                        10,
                        15,
                        30,
                        60,
                        120,
                    ) + state.defaultAppointmentNoticeMinutes
                    ).distinct().sorted().forEach { offset ->
                    PrumioChip(
                        label = reminderOffsetText(offset),
                        selected =
                            offset == state.defaultAppointmentNoticeMinutes,
                        onClick = { onAction(SettingsAction.ChangeAppointmentNotice(offset)) },
                    )
                }
            }
            PrumioListItem(headline = stringResource(Res.string.settings_reliability), onClick = {
                onAction(SettingsAction.OpenReliability)
            })
            PrumioSectionHeader(title = stringResource(Res.string.settings_about))
            PrumioListItem(headline = stringResource(Res.string.settings_version), supporting = version)
            PrumioListItem(headline = stringResource(Res.string.settings_source), onClick = {
                onAction(SettingsAction.OpenSource)
            })
            PrumioSectionHeader(title = stringResource(Res.string.settings_licenses))
            PrumioText(text = stringResource(Res.string.settings_licenses_text))
            PrumioText(text = stringResource(Res.string.settings_privacy))
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PrumioTheme {
        SettingsScreen(state = SettingsUiState(), version = "0.1.0", supportsAppLanguage = true, onAction = {})
    }
}
