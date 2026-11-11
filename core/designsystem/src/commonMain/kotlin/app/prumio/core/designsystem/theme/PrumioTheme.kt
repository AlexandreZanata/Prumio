package app.prumio.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

private val LocalPrumioColors = staticCompositionLocalOf { LightPrumioColors }

private val PrumioShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(28.dp),
)

/** Root theme. Wrap every screen, preview and screenshot test in it. */
@Composable
fun PrumioTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) DarkPrumioColors else LightPrumioColors
    CompositionLocalProvider(LocalPrumioColors provides colors) {
        MaterialTheme(
            colorScheme = colors.toMaterialColorScheme(),
            typography = PrumioMaterialTypography,
            shapes = PrumioShapes,
            content = content,
        )
    }
}

/** Token accessors: `PrumioTheme.colors.primary`, `PrumioTheme.spacing.md`. */
object PrumioTheme {
    val colors: PrumioColors
        @Composable
        @ReadOnlyComposable
        get() = LocalPrumioColors.current

    val spacing: PrumioSpacing get() = PrumioSpacing
}
