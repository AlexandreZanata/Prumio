package app.prumio.feature.settings

import androidx.compose.runtime.Composable
import app.prumio.feature.settings.reliability.ReliabilityTarget

/** Platform-only version and per-app language settings; no platform types cross this boundary. */
internal interface SettingsPlatform {
    val version: String
    val supportsAppLanguage: Boolean
    fun openReminderSettings(target: ReliabilityTarget)
    fun openAppLanguage()
}

@Composable
internal expect fun rememberSettingsPlatform(): SettingsPlatform
