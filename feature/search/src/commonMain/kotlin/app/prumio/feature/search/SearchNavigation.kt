package app.prumio.feature.search

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.SearchKey

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.searchEntries(navigator: Navigator) {
    entry<SearchKey> { SearchRoute(navigator = navigator) }
}
