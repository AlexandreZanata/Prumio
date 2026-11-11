package app.prumio.feature.inbox

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.Navigator

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
@Suppress("UNUSED_PARAMETER") // The placeholder does not navigate yet; the real screen will.
fun EntryProviderScope<NavKey>.inboxEntries(navigator: Navigator) {
    entry<InboxKey> { InboxRoute() }
}
