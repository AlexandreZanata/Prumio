package app.prumio.feature.matters

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

/** Stateful entry point of the Matters tab. */
@Composable
internal fun MattersRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: MattersViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is MattersEffect.Navigate -> navigator.navigate(effect.key)
        }
    }
    MattersScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
