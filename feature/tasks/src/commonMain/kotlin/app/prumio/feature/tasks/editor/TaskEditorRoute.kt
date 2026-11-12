package app.prumio.feature.tasks.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of the task editor. */
@Composable
internal fun TaskEditorRoute(
    key: TaskEditorKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: TaskEditorViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            TaskEditorEffect.Close -> navigator.goBack()
            is TaskEditorEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    TaskEditorScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
