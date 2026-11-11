package app.prumio.core.designsystem.component

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme

/**
 * Top bar of a screen. Pass [onNavigateBack] on non-root screens to show a back arrow;
 * [navigateBackContentDescription] is then read by screen readers.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrumioTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onNavigateBack: (() -> Unit)? = null,
    navigateBackContentDescription: String = "",
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        title = { Text(title) },
        modifier = modifier,
        navigationIcon = {
            if (onNavigateBack != null) {
                PrumioIconButton(
                    icon = PrumioIcons.Back,
                    contentDescription = navigateBackContentDescription,
                    onClick = onNavigateBack,
                )
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(containerColor = PrumioTheme.colors.background),
    )
}
