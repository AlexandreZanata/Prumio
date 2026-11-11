package app.prumio.core.designsystem.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.prumio.core.designsystem.resources.Res
import app.prumio.core.designsystem.resources.prumio_mark
import app.prumio.core.designsystem.theme.PrumioTheme
import org.jetbrains.compose.resources.painterResource

/**
 * The Prumio mark (sprout over stacked layers). The only way to show the logo in the app.
 * Default color is the brand green; use `PrumioTheme.colors.onBrand` on brand surfaces.
 * Usage rules: docs/brand/README.md. Pass null [contentDescription] when the name is already shown next to it.
 */
@Composable
fun PrumioLogo(
    contentDescription: String?,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    color: Color = PrumioTheme.colors.brand,
) {
    Image(
        painter = painterResource(Res.drawable.prumio_mark),
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        colorFilter = ColorFilter.tint(color),
    )
}
