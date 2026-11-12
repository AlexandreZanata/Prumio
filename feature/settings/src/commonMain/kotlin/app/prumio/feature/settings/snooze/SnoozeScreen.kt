package app.prumio.feature.settings.snooze

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.reminder.SnoozeOption
import app.prumio.core.ui.resources.action_cancel
import app.prumio.feature.settings.resources.Res
import app.prumio.feature.settings.resources.snooze_one_hour
import app.prumio.feature.settings.resources.snooze_ten_minutes
import app.prumio.feature.settings.resources.snooze_title
import app.prumio.feature.settings.resources.snooze_tomorrow
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

@Composable
internal fun SnoozeScreen(state: SnoozeUiState, onAction: (SnoozeAction) -> Unit, modifier: Modifier = Modifier) {
    PrumioCard(modifier = modifier.fillMaxWidth()) {
        Column(Modifier.verticalScroll(rememberScrollState()).padding(PrumioTheme.spacing.lg)) {
            PrumioText(stringResource(Res.string.snooze_title), style = PrumioTextStyle.Title)
            state.options.forEach { option ->
                PrumioListItem(
                    headline = stringResource(
                        when (option) {
                            SnoozeOption.TenMinutes -> Res.string.snooze_ten_minutes
                            SnoozeOption.OneHour -> Res.string.snooze_one_hour
                            SnoozeOption.Tomorrow -> Res.string.snooze_tomorrow
                        },
                    ),
                    onClick = { if (!state.isSaving) onAction(SnoozeAction.Select(option)) },
                )
            }
            PrumioButton(text = stringResource(CoreUiRes.string.action_cancel), onClick = {
                onAction(SnoozeAction.Close)
            }, variant = PrumioButtonVariant.Text)
        }
    }
}
