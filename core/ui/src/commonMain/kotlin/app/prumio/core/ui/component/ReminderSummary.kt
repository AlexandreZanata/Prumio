package app.prumio.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.ui.label.reminderOffsetText

/** The reminders of an item, e.g. "🔔 1 hour before · At time". Nothing is shown without reminders. */
@Composable
fun ReminderSummary(offsetsMinutes: List<Int>, modifier: Modifier = Modifier) {
    if (offsetsMinutes.isEmpty()) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs),
    ) {
        PrumioIcon(
            icon = PrumioIcons.Notifications,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = PrumioTheme.colors.onSurfaceVariant,
        )
        PrumioText(
            text = offsetsMinutes.sortedDescending().map { reminderOffsetText(it) }.joinToString(" · "),
            style = PrumioTextStyle.Caption,
            color = PrumioTheme.colors.onSurfaceVariant,
        )
    }
}
