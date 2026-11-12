package app.prumio.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioChip
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.reminder.ReminderPolicy
import app.prumio.core.ui.label.reminderOffsetText
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.reminder_field
import app.prumio.core.ui.resources.reminder_limit
import org.jetbrains.compose.resources.stringResource

/** Shared offset editor; the domain validates the five-reminder limit (BR-REM-018). */
@Composable
fun ReminderField(offsets: List<Int>, onChange: (List<Int>) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        PrumioText(stringResource(Res.string.reminder_field))
        PrumioText(stringResource(Res.string.reminder_limit))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs)) {
            (ReminderPolicy.presets + offsets).distinct().sorted().forEach { offset ->
                PrumioChip(label = reminderOffsetText(offset), selected = offset in offsets, onClick = {
                    onChange(if (offset in offsets) offsets - offset else offsets + offset)
                })
            }
        }
    }
}
