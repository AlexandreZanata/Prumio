package app.prumio.feature.matters.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import app.prumio.core.ui.mvi.rememberNotificationPermissionRequest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of a matter and its history. */
@Composable
internal fun MatterDetailRoute(
    key: MatterDetailKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: MatterDetailViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val requestPermission =
        rememberNotificationPermissionRequest { viewModel.onAction(MatterDetailAction.PermissionResult(it)) }
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is MatterDetailEffect.Navigate -> navigator.navigate(effect.key)
            MatterDetailEffect.RequestPermission -> requestPermission()
            MatterDetailEffect.Close -> navigator.goBack()
            is MatterDetailEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    MatterDetailScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
