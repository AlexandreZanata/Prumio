package app.prumio.core.designsystem.component

import androidx.compose.foundation.clickable
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import app.prumio.core.designsystem.theme.PrumioTheme

/** A row in a list: headline with optional overline, supporting text and leading/trailing slots. */
@Composable
fun PrumioListItem(
    headline: String,
    modifier: Modifier = Modifier,
    supporting: String? = null,
    overline: String? = null,
    onClick: (() -> Unit)? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    ListItem(
        headlineContent = { Text(headline, maxLines = 2, overflow = TextOverflow.Ellipsis) },
        modifier = if (onClick != null) modifier.clickable(onClick = onClick) else modifier,
        overlineContent = overline?.let { { Text(it) } },
        supportingContent = supporting?.let { { Text(it, maxLines = 2, overflow = TextOverflow.Ellipsis) } },
        leadingContent = leading,
        trailingContent = trailing,
        colors = ListItemDefaults.colors(containerColor = PrumioTheme.colors.background),
    )
}
