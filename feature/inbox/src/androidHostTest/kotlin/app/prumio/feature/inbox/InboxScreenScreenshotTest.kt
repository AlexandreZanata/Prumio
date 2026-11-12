package app.prumio.feature.inbox

import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.TestInstant
import app.prumio.core.testing.anInboxItem
import org.junit.Test

class InboxScreenScreenshotTest : ScreenshotTest() {
    @Test fun empty() = screenshot("empty") { InboxScreen(InboxUiState(isLoading = false), {}) }

    @Test fun typical() = screenshot("typical") {
        InboxScreen(
            InboxUiState(
                isLoading = false,
                now = TestInstant,
                items = listOf(
                    anInboxItem(text = "Call the plumber"),
                    anInboxItem("i2", "Ask about the documents\nBring a copy"),
                ),
            ),
            {
            },
        )
    }

    @Test fun longText() = screenshot("long_text") {
        InboxScreen(
            InboxUiState(
                isLoading = false,
                now = TestInstant,
                items = listOf(anInboxItem(text = "Something to remember ".repeat(30))),
            ),
            {
            },
        )
    }
}
