package app.prumio.feature.settings

import app.prumio.core.domain.reminder.NotificationAccessState
import app.prumio.core.domain.reminder.ReminderReliability
import app.prumio.core.domain.reminder.SnoozeOption
import app.prumio.core.model.ThemePreference
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.feature.settings.reliability.ReminderReliabilityScreen
import app.prumio.feature.settings.reliability.ReminderReliabilityUiState
import app.prumio.feature.settings.snooze.SnoozeScreen
import app.prumio.feature.settings.snooze.SnoozeUiState
import org.junit.Test

class SettingsScreenScreenshotTest : ScreenshotTest() {
    @Test fun system() = screenshot("system") { SettingsScreen(SettingsUiState(), "0.1.0-debug", true, {}) }

    @Test fun legacyAndroid() = screenshot("legacy_android") {
        SettingsScreen(SettingsUiState(ThemePreference.Dark), "0.1.0-debug", false, {})
    }

    @Test fun deniedBanner() = screenshot("denied_banner") {
        SettingsScreen(SettingsUiState(showDeniedBanner = true), "0.1.0-debug", true, {})
    }

    @Test fun reliability() = screenshot("reliability") {
        ReminderReliabilityScreen(
            ReminderReliabilityUiState(
                ReminderReliability(NotificationAccessState(false, true, false, true), true),
            ),
            {
            },
        )
    }

    @Test fun snooze() = screenshot("snooze") {
        SnoozeScreen(SnoozeUiState(SnoozeOption.entries), {})
    }
}
