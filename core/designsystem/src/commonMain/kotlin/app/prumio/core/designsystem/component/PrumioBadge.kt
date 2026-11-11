package app.prumio.core.designsystem.component

import androidx.compose.material3.Badge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** A count badge (inbox tab). Hidden at 0; "99+" above 99 (BR-INBOX-005). */
@Composable
fun PrumioBadge(count: Int, modifier: Modifier = Modifier) {
    val text = badgeText(count) ?: return
    Badge(modifier = modifier) { Text(text) }
}

/** Text of a count badge, or null when no badge is shown (BR-INBOX-005). */
internal fun badgeText(count: Int): String? = when {
    count <= 0 -> null
    count > 99 -> "99+"
    else -> count.toString()
}
