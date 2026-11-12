package app.prumio.feature.agenda.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import app.prumio.core.ui.mvi.rememberNotificationPermissionRequest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of the appointment editor. */
@Composable
internal fun AppointmentEditorRoute(
    key: AppointmentEditorKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: AppointmentEditorViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    val requestPermission =
        rememberNotificationPermissionRequest { viewModel.onAction(AppointmentEditorAction.PermissionResult(it)) }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            AppointmentEditorEffect.RequestPermission -> requestPermission()
            AppointmentEditorEffect.Close -> navigator.goBack()
            is AppointmentEditorEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    AppointmentEditorScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
