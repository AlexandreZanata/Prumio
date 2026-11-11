package app.prumio.feature.today

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.TodayKey

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.todayEntries(navigator: Navigator) {
    entry<TodayKey> { TodayRoute(navigator = navigator) }
}
