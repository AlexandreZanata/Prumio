package app.prumio.core.designsystem.component

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme

/** For rare long operations only (backup import/export). Local reads never show a spinner. */
@Composable
fun PrumioLoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(modifier = modifier, color = PrumioTheme.colors.primary)
}
