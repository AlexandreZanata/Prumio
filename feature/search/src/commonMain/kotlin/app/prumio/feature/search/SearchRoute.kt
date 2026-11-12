package app.prumio.feature.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

/** Stateful entry point of offline search. */
@Composable
internal fun SearchRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    LifecycleResumeEffect(viewModel) {
        viewModel.onAction(SearchAction.Refresh)
        onPauseOrDispose {}
    }
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is SearchEffect.Navigate -> navigator.navigate(effect.key)
            SearchEffect.Close -> navigator.goBack()
            is SearchEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    SearchScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
