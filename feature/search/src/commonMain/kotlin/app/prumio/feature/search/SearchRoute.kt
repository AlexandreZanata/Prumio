package app.prumio.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.navigation.Navigator

/** Stateful entry point. Placeholder: gains a ViewModel when the feature is implemented (see README.md). */
@Composable
internal fun SearchRoute(navigator: Navigator, modifier: Modifier = Modifier) {
    SearchScreen(onNavigateBack = navigator::goBack, modifier = modifier)
}
