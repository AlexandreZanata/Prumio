package app.prumio.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioTimePicker
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.ui.format.rememberIs24HourFormat
import app.prumio.core.ui.format.timeText
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.action_cancel
import app.prumio.core.ui.resources.action_clear
import app.prumio.core.ui.resources.action_done
import app.prumio.core.ui.resources.no_time
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/**
 * A labelled time that opens the time picker (device 12/24 h) when tapped. Shows "No time" when
 * empty; [onTimeChange] receives null when cleared (only if [clearable]).
 */
@Composable
fun TimeField(
    label: String,
    time: LocalTime?,
    onTimeChange: (LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
    clearable: Boolean = true,
    suggestedTime: LocalTime = LocalTime(9, 0),
) {
    var picking by rememberSaveable { mutableStateOf(false) }
    PrumioListItem(
        headline = time?.timeText() ?: stringResource(Res.string.no_time),
        modifier = modifier,
        overline = label,
        onClick = { picking = true },
        leading = { PrumioIcon(icon = PrumioIcons.Schedule, contentDescription = null) },
        trailing = if (time != null && clearable) {
            {
                PrumioIconButton(
                    icon = PrumioIcons.Close,
                    contentDescription = stringResource(Res.string.action_clear, label),
                    onClick = { onTimeChange(null) },
                )
            }
        } else {
            null
        },
    )
    if (picking) {
        PrumioTimePicker(
            initialTime = time ?: suggestedTime,
            is24Hour = rememberIs24HourFormat(),
            confirmLabel = stringResource(Res.string.action_done),
            dismissLabel = stringResource(Res.string.action_cancel),
            onConfirm = {
                picking = false
                onTimeChange(it)
            },
            onDismiss = { picking = false },
        )
    }
}
