package app.prumio.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Icon from [app.prumio.core.designsystem.icon.PrumioIcons].
 * [contentDescription] is required for meaningful icons; pass null only for decorative ones.
 */
@Composable
fun PrumioIcon(
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = Color.Unspecified,
) {
    if (tint == Color.Unspecified) {
        Icon(imageVector = icon, contentDescription = contentDescription, modifier = modifier)
    } else {
        Icon(imageVector = icon, contentDescription = contentDescription, modifier = modifier, tint = tint)
    }
}
