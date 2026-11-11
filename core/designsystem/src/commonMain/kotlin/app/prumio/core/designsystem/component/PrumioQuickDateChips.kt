package app.prumio.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme
import kotlinx.datetime.LocalDate

/** One quick choice: a label such as "Tomorrow" and the date it means. */
data class QuickDate(val label: String, val date: LocalDate)

/**
 * Quick date choices before a full picker (docs/product/ux-principles.md #5): one chip per option
 * plus a "Pick…" chip ([pickLabel]) that opens [PrumioDatePicker] through [onPick].
 */
@Composable
fun PrumioQuickDateChips(
    options: List<QuickDate>,
    selected: LocalDate?,
    onSelect: (LocalDate) -> Unit,
    pickLabel: String,
    onPick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm),
    ) {
        options.forEach { option ->
            PrumioChip(label = option.label, selected = option.date == selected, onClick = { onSelect(option.date) })
        }
        PrumioChip(
            label = pickLabel,
            selected = selected != null && options.none { it.date == selected },
            onClick = onPick,
        )
    }
}
