package app.prumio.feature.settings

import androidx.compose.runtime.Composable

/** Platform-only version and per-app language settings; no platform types cross this boundary. */
internal interface SettingsPlatform {
    val version: String
    val supportsAppLanguage: Boolean
    fun openAppLanguage()
}

@Composable
internal expect fun rememberSettingsPlatform(): SettingsPlatform
