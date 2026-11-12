package app.prumio.feature.capture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

/** Stateful entry point of the quick capture dialog. */
@Composable
internal fun CaptureRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: CaptureViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            CaptureEffect.Close -> navigator.goBack()
            is CaptureEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    CaptureScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
