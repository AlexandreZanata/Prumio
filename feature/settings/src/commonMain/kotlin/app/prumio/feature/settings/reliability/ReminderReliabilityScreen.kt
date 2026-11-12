package app.prumio.feature.settings.reliability

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.ui.resources.action_back
import app.prumio.feature.settings.resources.Res
import app.prumio.feature.settings.resources.reliability_banner
import app.prumio.feature.settings.resources.reliability_battery
import app.prumio.feature.settings.resources.reliability_battery_optimized
import app.prumio.feature.settings.resources.reliability_battery_text
import app.prumio.feature.settings.resources.reliability_battery_unrestricted
import app.prumio.feature.settings.resources.reliability_disabled
import app.prumio.feature.settings.resources.reliability_dismiss
import app.prumio.feature.settings.resources.reliability_enabled
import app.prumio.feature.settings.resources.reliability_exact
import app.prumio.feature.settings.resources.reliability_fix
import app.prumio.feature.settings.resources.reliability_inexact
import app.prumio.feature.settings.resources.reliability_notifications
import app.prumio.feature.settings.resources.settings_reliability
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Status, limitations and explicit links to Android settings. */
@Composable
internal fun ReminderReliabilityScreen(
    state: ReminderReliabilityUiState,
    onAction: (ReminderReliabilityAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioScaffold(modifier = modifier, topBar = { ReliabilityTopBar(onAction) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).verticalScroll(rememberScrollState())
                .padding(PrumioTheme.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
        ) {
            state.status?.let { status ->
                if (status.showDeniedBanner) {
                    PrumioText(stringResource(Res.string.reliability_banner))
                    PrumioButton(
                        text = stringResource(Res.string.reliability_dismiss),
                        onClick = { onAction(ReminderReliabilityAction.DismissBanner) },
                    )
                }
                PrumioSectionHeader(stringResource(Res.string.reliability_notifications))
                AccessStatus(status.access.notificationsAllowed)
                FixButton(ReliabilityTarget.Notifications, onAction)
                PrumioSectionHeader(stringResource(Res.string.reliability_exact))
                AccessStatus(status.access.exactAlarmsAllowed)
                if (!status.access.exactAlarmsAllowed) PrumioText(stringResource(Res.string.reliability_inexact))
                FixButton(ReliabilityTarget.ExactAlarms, onAction)
                PrumioSectionHeader(stringResource(Res.string.reliability_battery))
                PrumioText(
                    stringResource(
                        if (status.access.batteryOptimized) {
                            Res.string.reliability_battery_optimized
                        } else {
                            Res.string.reliability_battery_unrestricted
                        },
                    ),
                )
                PrumioText(stringResource(Res.string.reliability_battery_text))
                FixButton(ReliabilityTarget.Battery, onAction)
            }
        }
    }
}

@Composable
private fun FixButton(target: ReliabilityTarget, onAction: (ReminderReliabilityAction) -> Unit) {
    PrumioButton(stringResource(Res.string.reliability_fix), { onAction(ReminderReliabilityAction.Fix(target)) })
}

@Composable
private fun ReliabilityTopBar(onAction: (ReminderReliabilityAction) -> Unit) {
    PrumioTopBar(
        title = stringResource(Res.string.settings_reliability),
        onNavigateBack = { onAction(ReminderReliabilityAction.Close) },
        navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
    )
}

@Composable
private fun AccessStatus(allowed: Boolean) {
    PrumioText(stringResource(if (allowed) Res.string.reliability_enabled else Res.string.reliability_disabled))
}
