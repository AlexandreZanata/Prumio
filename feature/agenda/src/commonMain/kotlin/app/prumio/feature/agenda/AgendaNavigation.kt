package app.prumio.feature.agenda

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.AgendaKey
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.Navigator
import app.prumio.feature.agenda.detail.AppointmentDetailRoute
import app.prumio.feature.agenda.editor.AppointmentEditorRoute

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.agendaEntries(navigator: Navigator) {
    entry<AgendaKey> { AgendaRoute(navigator = navigator) }
    entry<AppointmentEditorKey> { key -> AppointmentEditorRoute(key = key, navigator = navigator) }
    entry<AppointmentDetailKey> { key -> AppointmentDetailRoute(key = key, navigator = navigator) }
}
