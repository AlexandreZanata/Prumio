package app.prumio.feature.matters.editor

import app.prumio.core.model.MatterId
import app.prumio.core.ui.message.UiMessage
import org.jetbrains.compose.resources.StringResource

// Matter editor: title (required, BR-MATTER-001) and description.

internal data class MatterEditorUiState(
    val isNew: Boolean = true,
    val isLoading: Boolean = true,
    val title: String = "",
    val description: String = "",
    val titleError: StringResource? = null,
    val isSaving: Boolean = false,
) {
    val canSave: Boolean get() = title.isNotBlank() && !isSaving && !isLoading
}

internal sealed interface MatterEditorAction {
    data class ChangeTitle(val title: String) : MatterEditorAction

    data class ChangeDescription(val description: String) : MatterEditorAction

    data object Save : MatterEditorAction

    data object Close : MatterEditorAction
}

internal sealed interface MatterEditorEffect {
    data object Close : MatterEditorEffect

    /** A new matter was created: replace the editor with its detail. */
    data class OpenCreated(val id: MatterId) : MatterEditorEffect

    data class ShowMessage(val message: UiMessage) : MatterEditorEffect
}
