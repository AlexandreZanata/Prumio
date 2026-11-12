package app.prumio.feature.agenda

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import app.prumio.core.ui.mvi.OnTimeChange
import org.koin.compose.viewmodel.koinViewModel

/** Stateful entry point of the Agenda tab. Re-reads "today" when resumed (BR-TODAY-007). */
@Composable
internal fun AgendaRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: AgendaViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    OnTimeChange { viewModel.onAction(AgendaAction.Refresh) }
    LifecycleResumeEffect(viewModel) {
        viewModel.onAction(AgendaAction.Refresh)
        onPauseOrDispose {}
    }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is AgendaEffect.Navigate -> navigator.navigate(effect.key)
            is AgendaEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    AgendaScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
