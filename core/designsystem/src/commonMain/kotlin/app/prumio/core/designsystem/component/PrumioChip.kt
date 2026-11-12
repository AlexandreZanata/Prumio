package app.prumio.core.designsystem.component

import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.prumio.core.designsystem.theme.PrumioTheme

/** Selectable chip: filters, quick choices ("Tomorrow", "1 hour before"). */
@Composable
fun PrumioChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        modifier = modifier,
        leadingIcon = leadingIcon?.let { { Icon(it, contentDescription = null, modifier = Modifier) } },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = PrumioTheme.colors.primaryContainer,
            selectedLabelColor = PrumioTheme.colors.onPrimaryContainer,
        ),
    )
}
