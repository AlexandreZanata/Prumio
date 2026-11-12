package app.prumio.feature.capture

import app.prumio.core.ui.message.UiMessage
import org.jetbrains.compose.resources.StringResource

// Quick capture (docs/product/screens.md#quick-capture): one text field, saved to the inbox.

internal data class CaptureUiState(
    val text: String = "",
    /** Validation message under the field (BR-GEN-002), null when valid. */
    val error: StringResource? = null,
    val isSaving: Boolean = false,
) {
    val canSave: Boolean get() = text.isNotBlank() && !isSaving
}

internal sealed interface CaptureAction {
    data class ChangeText(val text: String) : CaptureAction

    data object Save : CaptureAction

    data object Close : CaptureAction
}

internal sealed interface CaptureEffect {
    data object Close : CaptureEffect

    data class ShowMessage(val message: UiMessage) : CaptureEffect
}
