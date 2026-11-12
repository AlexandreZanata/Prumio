package app.prumio.feature.settings

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.ReminderReliabilityKey
import app.prumio.core.navigation.SettingsKey
import app.prumio.core.navigation.SnoozeKey
import app.prumio.feature.settings.reliability.ReminderReliabilityRoute
import app.prumio.feature.settings.snooze.SnoozeRoute

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.settingsEntries(navigator: Navigator) {
    entry<ReminderReliabilityKey> {
        ReminderReliabilityRoute(navigator = navigator)
    }
    entry<SnoozeKey>(
        metadata = androidx.navigation3.scene.DialogSceneStrategy.dialog(),
    ) { key ->
        SnoozeRoute(key, navigator)
    }
    entry<SettingsKey> { SettingsRoute(navigator = navigator) }
}
