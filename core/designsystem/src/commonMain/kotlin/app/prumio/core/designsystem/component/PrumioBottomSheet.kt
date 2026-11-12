package app.prumio.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme

/** Contextual choices over the current screen (postpone, snooze, organize, waiting questions). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrumioBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        modifier = modifier,
        containerColor = PrumioTheme.colors.surfaceContainerLow,
        content = content,
    )
}
