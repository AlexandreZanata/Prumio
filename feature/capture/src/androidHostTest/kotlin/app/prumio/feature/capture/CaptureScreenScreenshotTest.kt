package app.prumio.feature.capture

import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.validation_too_long
import org.junit.Test

class CaptureScreenScreenshotTest : ScreenshotTest() {
    @Test fun empty() = screenshot("empty") { CaptureScreen(CaptureUiState(), {}) }

    @Test fun typical() = screenshot("typical") {
        CaptureScreen(CaptureUiState(text = "Call the plumber\nAsk about the kitchen tap"), {})
    }

    @Test fun error() = screenshot("error") {
        CaptureScreen(CaptureUiState(text = "A thought ".repeat(20), error = Res.string.validation_too_long), {})
    }
}
