package app.prumio.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.model.InboxItem
import app.prumio.core.ui.format.ageText
import kotlin.time.Instant

/** An inbox item with how long it has been waiting ("2 days ago"). */
@Composable
fun InboxItemRow(
    item: InboxItem,
    now: Instant,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    PrumioListItem(
        headline = item.text,
        modifier = modifier,
        supporting = ageText(item.createdAt, now),
        onClick = onClick,
        trailing = trailing,
    )
}
