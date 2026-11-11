package app.prumio.core.designsystem.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

/**
 * Shows short messages at the bottom of the app, optionally with an action such as Undo.
 * One instance lives in the app shell; screens reach it through `LocalMessenger` (:core:ui).
 */
@Stable
class PrumioMessenger internal constructor(internal val hostState: SnackbarHostState) {
    /**
     * Shows [text]; returns true when the user tapped [actionLabel]. Messages with an action stay
     * long enough to be noticed (≥ 5 s, BR-GEN-005).
     */
    suspend fun show(text: String, actionLabel: String? = null): Boolean {
        val result = hostState.showSnackbar(
            message = text,
            actionLabel = actionLabel,
            withDismissAction = false,
            duration = if (actionLabel != null) SnackbarDuration.Long else SnackbarDuration.Short,
        )
        return result == SnackbarResult.ActionPerformed
    }
}

@Composable
fun rememberPrumioMessenger(): PrumioMessenger = remember { PrumioMessenger(SnackbarHostState()) }

/** Where [messenger] draws its messages; place it in the `snackbarHost` slot of [PrumioScaffold]. */
@Composable
fun PrumioSnackbarHost(messenger: PrumioMessenger, modifier: Modifier = Modifier) {
    SnackbarHost(hostState = messenger.hostState, modifier = modifier)
}
