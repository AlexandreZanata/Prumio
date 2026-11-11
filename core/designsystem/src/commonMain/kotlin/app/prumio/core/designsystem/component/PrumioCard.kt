package app.prumio.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme

/** Grouping surface for related content. Clickable when [onClick] is not null. */
@Composable
fun PrumioCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colors = CardDefaults.cardColors(
        containerColor = PrumioTheme.colors.surfaceVariant,
        contentColor = PrumioTheme.colors.onSurfaceVariant,
    )
    if (onClick == null) {
        Card(modifier = modifier, colors = colors, content = content)
    } else {
        Card(onClick = onClick, modifier = modifier, colors = colors, content = content)
    }
}
