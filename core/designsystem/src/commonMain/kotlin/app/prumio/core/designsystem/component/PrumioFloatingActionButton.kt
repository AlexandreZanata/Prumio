package app.prumio.core.designsystem.component

import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.prumio.core.designsystem.theme.PrumioTheme

/** The single floating action button style (used by "Add anything"). */
@Composable
fun PrumioFloatingActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = PrumioTheme.colors.primaryContainer,
        contentColor = PrumioTheme.colors.onPrimaryContainer,
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}
