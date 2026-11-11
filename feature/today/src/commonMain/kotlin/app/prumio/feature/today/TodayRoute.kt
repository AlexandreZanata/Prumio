package app.prumio.feature.today

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

/** Stateful entry point: owns the ViewModel, renders [TodayScreen], turns effects into navigation. */
@Composable
internal fun TodayRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: TodayViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is TodayEffect.Navigate -> navigator.navigate(effect.key)
        }
    }
    TodayScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
