package app.prumio.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme

/** Opacity of the tag color behind its label; the contrast test checks the result. */
internal const val TAG_BACKGROUND_ALPHA = 0.12f

/**
 * Non-interactive label with a color, e.g. a status. Color is never the only signal: the [label]
 * is always shown (accessibility). Use status tokens such as `PrumioTheme.colors.statusWaiting`.
 */
@Composable
fun PrumioTag(label: String, color: Color, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Row(
        modifier = modifier
            .background(color.copy(alpha = TAG_BACKGROUND_ALPHA), PrumioTheme.shape.pill)
            .padding(horizontal = PrumioTheme.spacing.sm, vertical = PrumioTheme.spacing.xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs),
    ) {
        icon?.let { PrumioIcon(icon = it, contentDescription = null, modifier = Modifier.size(14.dp), tint = color) }
        PrumioText(text = label, style = PrumioTextStyle.Label, color = color, maxLines = 1)
    }
}
