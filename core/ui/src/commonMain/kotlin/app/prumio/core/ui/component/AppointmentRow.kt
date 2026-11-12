package app.prumio.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.Appointment
import app.prumio.core.ui.format.timeText
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.all_day
import app.prumio.core.ui.resources.time_range
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/**
 * How an appointment looks everywhere: time (or "All day"), title, location. [start] and [end] are
 * already converted to the device zone by the domain (AppointmentTimePolicy); null for all-day.
 */
@Composable
fun AppointmentRow(
    appointment: Appointment,
    start: LocalTime?,
    end: LocalTime?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = PrumioTheme.spacing.lg, vertical = PrumioTheme.spacing.sm),
        horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
    ) {
        val time = when {
            start == null -> stringResource(Res.string.all_day)
            end == null -> start.timeText()
            else -> stringResource(Res.string.time_range, start.timeText(), end.timeText())
        }
        PrumioText(
            text = time,
            modifier = Modifier.width(88.dp),
            style = PrumioTextStyle.Label,
            color = PrumioTheme.colors.primary,
        )
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xxs)) {
            PrumioText(text = appointment.title, style = PrumioTextStyle.BodyStrong, maxLines = 2)
            appointment.location?.let {
                PrumioText(
                    text = it,
                    style = PrumioTextStyle.Caption,
                    color = PrumioTheme.colors.onSurfaceVariant,
                    maxLines = 1,
                )
            }
        }
    }
}
