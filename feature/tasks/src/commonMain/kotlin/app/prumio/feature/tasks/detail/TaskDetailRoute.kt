package app.prumio.feature.tasks.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of the task detail. */
@Composable
internal fun TaskDetailRoute(
    key: TaskDetailKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: TaskDetailViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is TaskDetailEffect.Navigate -> navigator.navigate(effect.key)
            TaskDetailEffect.Close -> navigator.goBack()
            is TaskDetailEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    TaskDetailScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
