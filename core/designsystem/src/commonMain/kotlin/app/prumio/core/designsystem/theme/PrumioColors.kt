package app.prumio.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/** Prumio Green, the color of the logo (docs/brand/README.md). Identical in light and dark themes. */
internal val PrumioGreen = Color(0xFF88C29A)

/**
 * Prumio color tokens. The single place where colors are defined (docs/design-system/tokens.md).
 *
 * The palette is a tonal scale of [PrumioGreen]. The brand green itself is a *surface* color
 * (logo, FAB, selected states, highlights): white text on it fails contrast, so text and icons on
 * it use `onPrimaryContainer`/`onBrand`. Status colors are always paired with a text label.
 * Contrast is verified by `PrumioColorsContrastTest`.
 */
@Immutable
data class PrumioColors(
    val brand: Color,
    val onBrand: Color,
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceContainerLowest: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val outline: Color,
    val outlineVariant: Color,
    val error: Color,
    val onError: Color,
    val statusOpen: Color,
    val statusWaiting: Color,
    val statusResolved: Color,
    val statusOverdue: Color,
    val isDark: Boolean,
)

internal val LightPrumioColors = PrumioColors(
    brand = PrumioGreen,
    onBrand = Color(0xFFFFFFFF),
    primary = Color(0xFF2E6E47),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = PrumioGreen,
    onPrimaryContainer = Color(0xFF0C2715),
    secondary = Color(0xFF4E6354),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD0E8D5),
    onSecondaryContainer = Color(0xFF0C1F12),
    background = Color(0xFFF6FAF6),
    onBackground = Color(0xFF171D19),
    surface = Color(0xFFF6FAF6),
    onSurface = Color(0xFF171D19),
    surfaceVariant = Color(0xFFDDE6DE),
    onSurfaceVariant = Color(0xFF414942),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFF0F5F0),
    surfaceContainer = Color(0xFFEAF0EA),
    surfaceContainerHigh = Color(0xFFE4EBE5),
    surfaceContainerHighest = Color(0xFFDEE5DF),
    inverseSurface = Color(0xFF2C322D),
    inverseOnSurface = Color(0xFFEDF2EC),
    inversePrimary = PrumioGreen,
    outline = Color(0xFF717971),
    outlineVariant = Color(0xFFC0C9C0),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    statusOpen = Color(0xFF2B5F8E),
    statusWaiting = Color(0xFF784E00),
    statusResolved = Color(0xFF235E3B),
    statusOverdue = Color(0xFFA3221B),
    isDark = false,
)

internal val DarkPrumioColors = PrumioColors(
    brand = PrumioGreen,
    onBrand = Color(0xFFFFFFFF),
    primary = PrumioGreen,
    onPrimary = Color(0xFF0C2715),
    primaryContainer = Color(0xFF2C5A3B),
    onPrimaryContainer = Color(0xFFC3EBCC),
    secondary = Color(0xFFB4CCB8),
    onSecondary = Color(0xFF203527),
    secondaryContainer = Color(0xFF364B3C),
    onSecondaryContainer = Color(0xFFD0E8D5),
    background = Color(0xFF0F1511),
    onBackground = Color(0xFFDEE5DE),
    surface = Color(0xFF0F1511),
    onSurface = Color(0xFFDEE5DE),
    surfaceVariant = Color(0xFF414942),
    onSurfaceVariant = Color(0xFFC0C9C0),
    surfaceContainerLowest = Color(0xFF0A0F0C),
    surfaceContainerLow = Color(0xFF171D19),
    surfaceContainer = Color(0xFF1B211D),
    surfaceContainerHigh = Color(0xFF252B27),
    surfaceContainerHighest = Color(0xFF303631),
    inverseSurface = Color(0xFFDEE5DE),
    inverseOnSurface = Color(0xFF2C322D),
    inversePrimary = Color(0xFF2E6E47),
    outline = Color(0xFF8B938B),
    outlineVariant = Color(0xFF414942),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    statusOpen = Color(0xFF9CCBFA),
    statusWaiting = Color(0xFFFFB951),
    statusResolved = Color(0xFF94D69B),
    statusOverdue = Color(0xFFFFB4AB),
    isDark = true,
)

internal fun PrumioColors.toMaterialColorScheme(): ColorScheme {
    val base = if (isDark) darkColorScheme() else lightColorScheme()
    return base.copy(
        primary = primary,
        onPrimary = onPrimary,
        primaryContainer = primaryContainer,
        onPrimaryContainer = onPrimaryContainer,
        inversePrimary = inversePrimary,
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        // Prumio has no tertiary accent: map it to secondary so no off-brand hue can appear.
        tertiary = secondary,
        onTertiary = onSecondary,
        tertiaryContainer = secondaryContainer,
        onTertiaryContainer = onSecondaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        surfaceTint = primary,
        surfaceBright = surfaceContainerLowest,
        surfaceDim = surfaceContainerHighest,
        surfaceContainerLowest = surfaceContainerLowest,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainer = surfaceContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerHighest = surfaceContainerHighest,
        inverseSurface = inverseSurface,
        inverseOnSurface = inverseOnSurface,
        outline = outline,
        outlineVariant = outlineVariant,
        error = error,
        onError = onError,
    )
}
