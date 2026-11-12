package app.prumio.app.shell

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Observes application chrome in the root lifecycle's ViewModelStore. */
@Composable
internal fun ShellRoute(modifier: Modifier = Modifier, viewModel: ShellViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    PrumioAppContent(state = state, modifier = modifier)
}
