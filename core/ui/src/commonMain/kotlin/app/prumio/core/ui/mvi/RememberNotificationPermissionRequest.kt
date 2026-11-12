package app.prumio.core.ui.mvi

import androidx.compose.runtime.Composable

/** Returns a launcher; callers invoke it only after the contextual explanation was accepted. */
@Composable
expect fun rememberNotificationPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit
