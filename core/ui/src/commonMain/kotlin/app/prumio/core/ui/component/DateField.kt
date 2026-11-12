package app.prumio.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioDatePicker
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.ui.format.relativeDateText
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.action_cancel
import app.prumio.core.ui.resources.action_clear
import app.prumio.core.ui.resources.action_done
import app.prumio.core.ui.resources.action_pick_date
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/**
 * A labelled date that opens the date picker when tapped ("Due date · Tomorrow"). Shows
 * "Pick a date…" when empty; [onDateChange] receives null when cleared (only if [clearable]).
 */
@Composable
fun DateField(
    label: String,
    date: LocalDate?,
    today: LocalDate,
    onDateChange: (LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    clearable: Boolean = true,
) {
    var picking by rememberSaveable { mutableStateOf(false) }
    PrumioListItem(
        headline = date?.relativeDateText(today) ?: stringResource(Res.string.action_pick_date),
        modifier = modifier,
        overline = label,
        onClick = { picking = true },
        leading = { PrumioIcon(icon = PrumioIcons.Event, contentDescription = null) },
        trailing = if (date != null && clearable) {
            {
                PrumioIconButton(
                    icon = PrumioIcons.Close,
                    contentDescription = stringResource(Res.string.action_clear, label),
                    onClick = { onDateChange(null) },
                )
            }
        } else {
            null
        },
    )
    if (picking) {
        PrumioDatePicker(
            initialDate = date ?: today,
            confirmLabel = stringResource(Res.string.action_done),
            dismissLabel = stringResource(Res.string.action_cancel),
            onConfirm = {
                picking = false
                onDateChange(it)
            },
            onDismiss = { picking = false },
        )
    }
}
