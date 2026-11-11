package app.prumio.feature.capture

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.scene.DialogSceneStrategy
import app.prumio.core.navigation.CaptureKey
import app.prumio.core.navigation.Navigator

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.captureEntries(navigator: Navigator) {
    entry<CaptureKey>(metadata = DialogSceneStrategy.dialog()) {
        CaptureRoute(navigator = navigator)
    }
}
