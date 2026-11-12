package app.prumio.feature.settings

import app.prumio.core.model.ThemePreference
import app.prumio.core.screenshottesting.ScreenshotTest
import org.junit.Test

class SettingsScreenScreenshotTest : ScreenshotTest() {
    @Test fun system() = screenshot("system") { SettingsScreen(SettingsUiState(), "0.0.1-debug", true, {}) }

    @Test fun legacyAndroid() = screenshot("legacy_android") {
        SettingsScreen(SettingsUiState(ThemePreference.Dark), "0.0.1-debug", false, {})
    }
}
