package app.prumio.feature.capture

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.navigation.Navigator

/** Stateful entry point. Placeholder: gains a ViewModel when quick capture is implemented (see README.md). */
@Composable
internal fun CaptureRoute(navigator: Navigator, modifier: Modifier = Modifier) {
    CaptureScreen(onClose = navigator::goBack, modifier = modifier)
}
