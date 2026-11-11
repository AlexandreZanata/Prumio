package app.prumio.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.ui.format.dateTimeText
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.overdue_since
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.stringResource

/**
 * When something is due: "Today, 15:30", "Tomorrow", "Overdue · Mon, 12 Jan". [isOverdue] comes
 * from the domain (BR-TASK-004); this component only shows it, in words and color.
 */
@Composable
fun DueLabel(date: LocalDate, time: LocalTime?, today: LocalDate, isOverdue: Boolean, modifier: Modifier = Modifier) {
    val text = dateTimeText(date, time, today)
    PrumioText(
        text = if (isOverdue) stringResource(Res.string.overdue_since, text) else text,
        modifier = modifier,
        style = PrumioTextStyle.Caption,
        color = if (isOverdue) PrumioTheme.colors.statusOverdue else PrumioTheme.colors.onSurfaceVariant,
        maxLines = 1,
    )
}
