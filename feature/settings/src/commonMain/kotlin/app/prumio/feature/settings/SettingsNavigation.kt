package app.prumio.feature.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.SettingsKey

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.settingsEntries(navigator: Navigator) {
    entry<SettingsKey> { SettingsRoute(navigator = navigator) }
}
