package app.prumio.core.screenshottesting

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import app.prumio.core.designsystem.theme.PrumioTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoborazziOptions
import com.github.takahirom.roborazzi.captureRoboImage
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.PreviewContextConfigurationEffect
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Base class of every screenshot test (docs/architecture/testing-strategy.md#screenshots).
 *
 * `screenshot("name") { … }` renders the content in the light theme, the dark theme and with a
 * 200 % font scale, and compares each against `src/androidHostTest/screenshots/<Class>/name_*.png`.
 * Record new or changed images with `./gradlew <module>:testAndroidHostTest -PrecordScreenshots`
 * and review them before committing.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w360dp-h640dp-xhdpi")
abstract class ScreenshotTest {
    @OptIn(ExperimentalRoborazziApi::class)
    private val options = RoborazziOptions(compareOptions = RoborazziOptions.CompareOptions(changeThreshold = 0.005f))

    protected fun screenshot(name: String, content: @Composable () -> Unit) {
        val directory = "src/androidHostTest/screenshots/${this::class.simpleName}"
        Variant.entries.forEach { variant ->
            captureRoboImage(filePath = "$directory/${name}_${variant.suffix}.png", roborazziOptions = options) {
                Themed(variant, content)
            }
        }
    }

    @OptIn(ExperimentalResourceApi::class)
    @Composable
    private fun Themed(variant: Variant, content: @Composable () -> Unit) {
        PreviewContextConfigurationEffect()
        val density = LocalDensity.current
        CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = variant.fontScale)) {
            PrumioTheme(darkTheme = variant.dark) {
                Box(Modifier.background(PrumioTheme.colors.background).padding(PrumioTheme.spacing.sm)) { content() }
            }
        }
    }

    private enum class Variant(val suffix: String, val dark: Boolean, val fontScale: Float) {
        Light("light", dark = false, fontScale = 1f),
        Dark("dark", dark = true, fontScale = 1f),
        LargeFont("font200", dark = false, fontScale = 2f),
    }
}
