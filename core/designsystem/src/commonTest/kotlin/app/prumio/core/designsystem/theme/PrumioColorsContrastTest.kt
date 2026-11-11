package app.prumio.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertTrue

/** WCAG 2.x contrast of every text/background token pair (docs/architecture/accessibility.md). */
class PrumioColorsContrastTest {
    private fun contrast(a: Color, b: Color): Double {
        val lighter = max(a.luminance(), b.luminance())
        val darker = min(a.luminance(), b.luminance())
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun textPairs(c: PrumioColors) = mapOf(
        "onPrimary/primary" to (c.onPrimary to c.primary),
        "onPrimaryContainer/primaryContainer" to (c.onPrimaryContainer to c.primaryContainer),
        "onSecondary/secondary" to (c.onSecondary to c.secondary),
        "onSecondaryContainer/secondaryContainer" to (c.onSecondaryContainer to c.secondaryContainer),
        "onBackground/background" to (c.onBackground to c.background),
        "onSurfaceVariant/surfaceVariant" to (c.onSurfaceVariant to c.surfaceVariant),
        "onSurfaceVariant/surfaceContainer" to (c.onSurfaceVariant to c.surfaceContainer),
        "inverseOnSurface/inverseSurface" to (c.inverseOnSurface to c.inverseSurface),
        "onError/error" to (c.onError to c.error),
        "primary/background" to (c.primary to c.background),
        "statusOpen/background" to (c.statusOpen to c.background),
        "statusWaiting/background" to (c.statusWaiting to c.background),
        "statusResolved/background" to (c.statusResolved to c.background),
        "statusOverdue/background" to (c.statusOverdue to c.background),
    )

    private fun assertAccessible(name: String, colors: PrumioColors) {
        val failures = textPairs(colors).mapNotNull { (pair, colorPair) ->
            val ratio = contrast(colorPair.first, colorPair.second)
            if (ratio < 4.5) "$pair = ${(ratio * 100).toInt() / 100.0}" else null
        }
        val outline = contrast(colors.outline, colors.background)
        assertTrue(failures.isEmpty(), "$name theme text pairs below 4.5:1: $failures")
        assertTrue(outline >= 3.0, "$name theme outline below 3:1 for UI components: $outline")
    }

    @Test
    fun `light theme text tokens meet WCAG AA`() = assertAccessible("light", LightPrumioColors)

    @Test
    fun `dark theme text tokens meet WCAG AA`() = assertAccessible("dark", DarkPrumioColors)

    @Test
    fun `brand green is the logo color in both themes`() {
        assertTrue(LightPrumioColors.brand == Color(0xFF88C29A) && DarkPrumioColors.brand == Color(0xFF88C29A))
    }
}
