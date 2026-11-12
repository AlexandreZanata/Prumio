package app.prumio.feature.tasks.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of "All tasks". */
@Composable
internal fun AllTasksRoute(
    key: AllTasksKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: AllTasksViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is AllTasksEffect.Navigate -> navigator.navigate(effect.key)
            AllTasksEffect.Close -> navigator.goBack()
            is AllTasksEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    AllTasksScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
