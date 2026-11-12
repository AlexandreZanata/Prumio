package app.prumio.feature.matters

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.MattersKey
import app.prumio.core.navigation.Navigator
import app.prumio.feature.matters.detail.MatterDetailRoute
import app.prumio.feature.matters.editor.MatterEditorRoute

/** The list, editor and detail entries provided by this feature. */
fun EntryProviderScope<NavKey>.mattersEntries(navigator: Navigator) {
    entry<MattersKey> { MattersRoute(navigator = navigator) }
    entry<MatterEditorKey> { key -> MatterEditorRoute(key = key, navigator = navigator) }
    entry<MatterDetailKey> { key -> MatterDetailRoute(key = key, navigator = navigator) }
}
