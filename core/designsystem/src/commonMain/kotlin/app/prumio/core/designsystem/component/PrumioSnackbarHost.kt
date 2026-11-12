package app.prumio.core.designsystem.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalAccessibilityManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Shows short messages at the bottom of the app, optionally with an action such as Undo.
 * One instance lives in the app shell; screens reach it through `LocalMessenger` (:core:ui).
 */
@Stable
class PrumioMessenger internal constructor(
    internal val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    /**
     * Shows [text]; returns true when the user tapped [actionLabel]. Messages with an action stay
     * visible for 5 s, extended by accessibility preferences (BR-GEN-005).
     */
    suspend fun show(text: String, actionLabel: String? = null): Boolean {
        val result = hostState.showSnackbar(
            message = text,
            actionLabel = actionLabel,
            withDismissAction = false,
            duration = if (actionLabel != null) SnackbarDuration.Indefinite else SnackbarDuration.Short,
        )
        return result == SnackbarResult.ActionPerformed
    }

    /**
     * Runs [block] (typically one [show] and its action) in the app's scope and returns at once.
     * It outlives the screen that posted it, so "Saved" after closing a dialog and Undo after
     * leaving a deleted item's screen still work.
     */
    fun post(block: suspend PrumioMessenger.() -> Unit) {
        scope.launch { block() }
    }
}

@Composable
fun rememberPrumioMessenger(): PrumioMessenger {
    val scope = rememberCoroutineScope()
    return remember(scope) { PrumioMessenger(SnackbarHostState(), scope) }
}

/** Where [messenger] draws its messages; place it in the `snackbarHost` slot of [PrumioScaffold]. */
@Composable
fun PrumioSnackbarHost(messenger: PrumioMessenger, modifier: Modifier = Modifier) {
    val data = messenger.hostState.currentSnackbarData
    val accessibility = LocalAccessibilityManager.current
    LaunchedEffect(data, accessibility) {
        if (data?.visuals?.actionLabel != null) {
            val timeout = accessibility?.calculateRecommendedTimeoutMillis(
                originalTimeoutMillis = 5_000L,
                containsIcons = false,
                containsText = true,
                containsControls = true,
            ) ?: 5_000L
            delay(timeout)
            data.dismiss()
        }
    }
    SnackbarHost(hostState = messenger.hostState, modifier = modifier)
}
