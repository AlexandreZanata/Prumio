package app.prumio.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

/**
 * Prumio color tokens. The single place where colors are defined (docs/design-system/tokens.md).
 * Status colors encode Matter/Task state and are always paired with a text label (accessibility).
 */
@Immutable
data class PrumioColors(
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
    primary = Color(0xFF2E6B5E),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFB2EFDF),
    onPrimaryContainer = Color(0xFF00201A),
    secondary = Color(0xFF4B635C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFCDE8DF),
    onSecondaryContainer = Color(0xFF07201A),
    background = Color(0xFFF7FAF8),
    onBackground = Color(0xFF181C1B),
    surface = Color(0xFFF7FAF8),
    onSurface = Color(0xFF181C1B),
    surfaceVariant = Color(0xFFDBE5E0),
    onSurfaceVariant = Color(0xFF3F4945),
    outline = Color(0xFF6F7975),
    outlineVariant = Color(0xFFBFC9C4),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    statusOpen = Color(0xFF2B5F8E),
    statusWaiting = Color(0xFF8A5A00),
    statusResolved = Color(0xFF2E6B3A),
    statusOverdue = Color(0xFFB3261E),
    isDark = false,
)

internal val DarkPrumioColors = PrumioColors(
    primary = Color(0xFF96D3C3),
    onPrimary = Color(0xFF00382E),
    primaryContainer = Color(0xFF105145),
    onPrimaryContainer = Color(0xFFB2EFDF),
    secondary = Color(0xFFB1CCC3),
    onSecondary = Color(0xFF1D352E),
    secondaryContainer = Color(0xFF344C45),
    onSecondaryContainer = Color(0xFFCDE8DF),
    background = Color(0xFF101413),
    onBackground = Color(0xFFDFE4E1),
    surface = Color(0xFF101413),
    onSurface = Color(0xFFDFE4E1),
    surfaceVariant = Color(0xFF3F4945),
    onSurfaceVariant = Color(0xFFBFC9C4),
    outline = Color(0xFF89938F),
    outlineVariant = Color(0xFF3F4945),
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
        secondary = secondary,
        onSecondary = onSecondary,
        secondaryContainer = secondaryContainer,
        onSecondaryContainer = onSecondaryContainer,
        background = background,
        onBackground = onBackground,
        surface = surface,
        onSurface = onSurface,
        surfaceVariant = surfaceVariant,
        onSurfaceVariant = onSurfaceVariant,
        outline = outline,
        outlineVariant = outlineVariant,
        error = error,
        onError = onError,
    )
}
