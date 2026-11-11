package app.prumio.feature.agenda

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.AgendaKey
import app.prumio.core.navigation.Navigator

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
@Suppress("UNUSED_PARAMETER") // The placeholder does not navigate yet; the real screen will.
fun EntryProviderScope<NavKey>.agendaEntries(navigator: Navigator) {
    entry<AgendaKey> { AgendaRoute() }
}
