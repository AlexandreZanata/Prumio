package app.prumio.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.graphics.luminance
import app.prumio.core.designsystem.component.TAG_BACKGROUND_ALPHA
import kotlin.math.max
import kotlin.math.min
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * WCAG 2.2 contrast of every text color on the backgrounds it is drawn on, in both themes (P2.03,
 * docs/design-system/tokens.md#contrast). Text needs 4.5:1; cards are `surfaceContainerHigh`.
 * `brand`/`onBrand` are only used by the logo, which WCAG exempts, so they are not listed.
 */
class PrumioColorsContrastTest {
    private fun contrast(a: Color, b: Color): Double {
        val lighter = max(a.luminance(), b.luminance())
        val darker = min(a.luminance(), b.luminance())
        return (lighter + 0.05) / (darker + 0.05)
    }

    private fun textPairs(c: PrumioColors): Map<String, Pair<Color, Color>> = buildMap {
        put("onPrimary/primary", c.onPrimary to c.primary)
        put("onPrimaryContainer/primaryContainer", c.onPrimaryContainer to c.primaryContainer)
        put("onSecondary/secondary", c.onSecondary to c.secondary)
        put("onSecondaryContainer/secondaryContainer", c.onSecondaryContainer to c.secondaryContainer)
        put("onBackground/background", c.onBackground to c.background)
        put("onSurfaceVariant/background", c.onSurfaceVariant to c.background)
        put("onSurfaceVariant/surfaceVariant", c.onSurfaceVariant to c.surfaceVariant)
        put("onSurfaceVariant/surfaceContainer", c.onSurfaceVariant to c.surfaceContainer)
        put("onSurface/card", c.onSurface to c.surfaceContainerHigh)
        put("onSurfaceVariant/card", c.onSurfaceVariant to c.surfaceContainerHigh)
        put("inverseOnSurface/inverseSurface", c.inverseOnSurface to c.inverseSurface)
        put("inversePrimary/inverseSurface", c.inversePrimary to c.inverseSurface)
        put("onError/error", c.onError to c.error)
        put("error/background", c.error to c.background)
        put("primary/background", c.primary to c.background)
        put("primary/card", c.primary to c.surfaceContainerHigh)
        listOf(
            "statusOpen" to c.statusOpen,
            "statusWaiting" to c.statusWaiting,
            "statusResolved" to c.statusResolved,
            "statusOverdue" to c.statusOverdue,
        ).forEach { (name, status) ->
            val tag = status.copy(alpha = TAG_BACKGROUND_ALPHA)
            put("$name/background", status to c.background)
            // PrumioTag draws the status label on a tint of itself, on screens and inside cards.
            put("$name/tag on background", status to tag.compositeOver(c.background))
            put("$name/tag on card", status to tag.compositeOver(c.surfaceContainerHigh))
        }
    }

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
