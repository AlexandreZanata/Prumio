package app.prumio.app.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Observes application chrome in the root lifecycle's ViewModelStore. */
@Composable
internal fun ShellRoute(
    modifier: Modifier = Modifier,
    initialDeepLink: String? = null,
    onConsumeDeepLink: () -> Unit = {},
    viewModel: ShellViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val consumeDeepLink by rememberUpdatedState(onConsumeDeepLink)
    LaunchedEffect(initialDeepLink) {
        initialDeepLink?.let {
            viewModel.onAction(ShellAction.OpenLink(it))
            consumeDeepLink()
        }
    }
    PrumioAppContent(state = state, effects = viewModel.effects, modifier = modifier)
}
