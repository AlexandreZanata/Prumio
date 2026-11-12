package app.prumio.feature.agenda.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of the appointment detail. */
@Composable
internal fun AppointmentDetailRoute(
    key: AppointmentDetailKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: AppointmentDetailViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is AppointmentDetailEffect.Navigate -> navigator.navigate(effect.key)
            AppointmentDetailEffect.Close -> navigator.goBack()
            is AppointmentDetailEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    AppointmentDetailScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
