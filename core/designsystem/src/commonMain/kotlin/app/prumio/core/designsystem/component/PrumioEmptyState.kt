package app.prumio.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme

/** Calm placeholder for empty lists and screens. Never blames the user; suggests what comes next. */
@Composable
fun PrumioEmptyState(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    icon: ImageVector? = null,
) {
    Column(
        modifier = modifier.padding(PrumioTheme.spacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm),
    ) {
        if (icon != null) {
            PrumioIcon(
                icon = icon,
                contentDescription = null,
                modifier = Modifier.size(PrumioTheme.spacing.xxxl),
                tint = PrumioTheme.colors.outline,
            )
        }
        PrumioText(text = title, style = PrumioTextStyle.Subtitle, textAlign = TextAlign.Center)
        if (description != null) {
            PrumioText(
                text = description,
                style = PrumioTextStyle.Body,
                color = PrumioTheme.colors.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )
        }
    }
}
