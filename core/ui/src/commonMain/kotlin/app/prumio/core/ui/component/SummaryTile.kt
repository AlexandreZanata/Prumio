package app.prumio.core.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme

/** One count of Today's summary ("3 · Tasks"). Read by screen readers as one phrase. */
@Composable
fun SummaryTile(count: Int, label: String, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    PrumioCard(modifier = modifier.clearAndSetSemantics { contentDescription = "$count $label" }, onClick = onClick) {
        Column(modifier = Modifier.padding(PrumioTheme.spacing.md)) {
            PrumioText(text = count.toString(), style = PrumioTextStyle.Display, color = PrumioTheme.colors.primary)
            PrumioText(text = label, style = PrumioTextStyle.Label, color = PrumioTheme.colors.onSurfaceVariant)
        }
    }
}
