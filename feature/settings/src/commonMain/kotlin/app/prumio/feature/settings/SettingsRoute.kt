package app.prumio.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.navigation.Navigator

/** Stateful entry point. Placeholder: gains a ViewModel when the feature is implemented (see README.md). */
@Composable
internal fun SettingsRoute(navigator: Navigator, modifier: Modifier = Modifier) {
    SettingsScreen(onNavigateBack = navigator::goBack, modifier = modifier)
}
