package app.prumio.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

/** Bottom navigation container for the top-level destinations. */
@Composable
fun PrumioNavigationBar(modifier: Modifier = Modifier, content: @Composable RowScope.() -> Unit) {
    NavigationBar(modifier = modifier, content = content)
}

/** One destination of [PrumioNavigationBar]. The label doubles as the accessibility label. */
@Composable
fun RowScope.PrumioNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    selectedIcon: ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    badgeCount: Int = 0,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = {
            BadgedBox(badge = { PrumioBadge(count = badgeCount) }) {
                Icon(imageVector = if (selected) selectedIcon else icon, contentDescription = null)
            }
        },
        label = { Text(label) },
        modifier = modifier,
    )
}
