package app.prumio.core.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import app.prumio.core.designsystem.component.PrumioTag
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.MatterStatus
import app.prumio.core.ui.label.label

/** A matter's status: color, icon **and** text (never color alone). */
@Composable
fun MatterStatusChip(status: MatterStatus, modifier: Modifier = Modifier) {
    PrumioTag(
        label = status.label(),
        color = status.color(),
        modifier = modifier,
        icon = when (status) {
            MatterStatus.Open -> null
            MatterStatus.Waiting -> PrumioIcons.Waiting
            MatterStatus.Resolved -> PrumioIcons.Check
        },
    )
}

@Composable
private fun MatterStatus.color(): Color = when (this) {
    MatterStatus.Open -> PrumioTheme.colors.statusOpen
    MatterStatus.Waiting -> PrumioTheme.colors.statusWaiting
    MatterStatus.Resolved -> PrumioTheme.colors.statusResolved
}
