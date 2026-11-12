package app.prumio.feature.inbox

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

/** Stateful entry point of the Inbox tab. */
@Composable
internal fun InboxRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: InboxViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is InboxEffect.Navigate -> navigator.navigate(effect.key)
            is InboxEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    InboxScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
