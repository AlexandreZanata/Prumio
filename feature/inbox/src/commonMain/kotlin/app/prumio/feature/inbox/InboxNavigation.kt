package app.prumio.feature.inbox

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.Navigator

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.inboxEntries(navigator: Navigator) {
    entry<InboxKey> { InboxRoute(navigator = navigator) }
}
