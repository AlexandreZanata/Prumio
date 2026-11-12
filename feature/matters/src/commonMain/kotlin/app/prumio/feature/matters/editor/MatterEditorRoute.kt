package app.prumio.feature.matters.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/** Stateful entry point of the matter editor. */
@Composable
internal fun MatterEditorRoute(
    key: MatterEditorKey,
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: MatterEditorViewModel = koinViewModel { parametersOf(key) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            MatterEditorEffect.Close -> navigator.goBack()

            is MatterEditorEffect.OpenCreated -> {
                navigator.goBack()
                navigator.navigate(MatterDetailKey(effect.id.value))
            }

            is MatterEditorEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    MatterEditorScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
