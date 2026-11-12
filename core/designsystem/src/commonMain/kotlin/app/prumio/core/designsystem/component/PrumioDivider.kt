package app.prumio.core.designsystem.component

import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme

/** Thin separator between groups. Prefer spacing; use dividers sparingly. */
@Composable
fun PrumioDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(modifier = modifier, color = PrumioTheme.colors.outlineVariant)
}
