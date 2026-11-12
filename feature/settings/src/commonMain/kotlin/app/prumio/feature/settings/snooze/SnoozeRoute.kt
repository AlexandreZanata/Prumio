package app.prumio.feature.settings.snooze

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.SnoozeKey
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
internal fun SnoozeRoute(
    key: SnoozeKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: SnoozeViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) {
        when (it) {
            SnoozeEffect.Close -> navigator.goBack()
            is SnoozeEffect.ShowMessage -> showMessage(it.message)
        }
    }
    SnoozeScreen(state, viewModel::onAction, modifier)
}
