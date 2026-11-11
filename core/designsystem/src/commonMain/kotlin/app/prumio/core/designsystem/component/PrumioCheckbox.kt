package app.prumio.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme

/**
 * The round completion control of tasks (48 dp target). [contentDescription] names the action and
 * the item, e.g. "Complete task Pay rent" (docs/architecture/accessibility.md).
 */
@Composable
fun PrumioCheckbox(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    IconToggleButton(checked = checked, onCheckedChange = onCheckedChange, modifier = modifier, enabled = enabled) {
        Icon(
            imageVector = if (checked) PrumioIcons.TaskDone else PrumioIcons.TaskOpen,
            contentDescription = contentDescription,
            tint = if (checked) PrumioTheme.colors.statusResolved else PrumioTheme.colors.outline,
        )
    }
}
