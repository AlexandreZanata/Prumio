package app.prumio.app.shell.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.navKeySerializersModule
import app.prumio.feature.agenda.agendaEntries
import app.prumio.feature.capture.captureEntries
import app.prumio.feature.inbox.inboxEntries
import app.prumio.feature.matters.mattersEntries
import app.prumio.feature.search.searchEntries
import app.prumio.feature.settings.settingsEntries
import app.prumio.feature.tasks.tasksEntries
import app.prumio.feature.today.todayEntries

/** Saves the back stack across process death; knows every key through [navKeySerializersModule]. */
internal val navSavedStateConfiguration = SavedStateConfiguration {
    serializersModule = navKeySerializersModule
}

/**
 * Hosts every screen. Each feature contributes its entries here — one line per feature.
 * Each entry gets its own ViewModelStore, so ViewModels are cleared when their screen is popped.
 */
@Composable
internal fun PrumioNavDisplay(backStack: NavBackStack<NavKey>, navigator: Navigator, modifier: Modifier = Modifier) {
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = navigator::goBack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        sceneStrategies = listOf(DialogSceneStrategy()),
        entryProvider = entryProvider {
            todayEntries(navigator)
            agendaEntries(navigator)
            mattersEntries(navigator)
            inboxEntries(navigator)
            captureEntries(navigator)
            searchEntries(navigator)
            settingsEntries(navigator)
            tasksEntries(navigator)
        },
    )
}
