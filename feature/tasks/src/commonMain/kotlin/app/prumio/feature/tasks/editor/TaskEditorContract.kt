package app.prumio.feature.tasks.editor

import app.prumio.core.model.Matter
import app.prumio.core.model.MatterId
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource

// Task editor (docs/product/screens.md#tasks): only the title is required (BR-GEN-001, BR-TASK-001).

internal data class TaskEditorUiState(
    val isNew: Boolean = true,
    val isLoading: Boolean = true,
    val title: String = "",
    val notes: String = "",
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val matterId: MatterId? = null,
    /** Unresolved matters the task can be linked to (plus the current one). */
    val matters: List<Matter> = emptyList(),
    val today: LocalDate = LocalDate(1970, 1, 1),
    val titleError: StringResource? = null,
    val isPickingMatter: Boolean = false,
    val reminderOffsets: List<Int> = emptyList(),
    val showPermissionExplanation: Boolean = false,
    val isSaving: Boolean = false,
) {
    val matterTitle: String? get() = matters.firstOrNull { it.id == matterId }?.title
    val canSave: Boolean get() = title.isNotBlank() && !isSaving && !isLoading
}

internal sealed interface TaskEditorAction {
    data class ChangeTitle(val title: String) : TaskEditorAction

    data class ChangeNotes(val notes: String) : TaskEditorAction

    /** Removing the date also removes the time (BR-TASK-002). */
    data class ChangeDueDate(val date: LocalDate?) : TaskEditorAction

    data class ChangeDueTime(val time: LocalTime?) : TaskEditorAction

    data class PickMatter(val open: Boolean) : TaskEditorAction

    data class SelectMatter(val id: MatterId?) : TaskEditorAction

    data class ChangeReminders(val offsets: List<Int>) : TaskEditorAction
    data object ConfirmPermission : TaskEditorAction
    data class PermissionResult(val granted: Boolean) : TaskEditorAction
    data object Save : TaskEditorAction

    data object Close : TaskEditorAction
}

internal sealed interface TaskEditorEffect {
    data object RequestPermission : TaskEditorEffect
    data object Close : TaskEditorEffect

    data class ShowMessage(val message: UiMessage) : TaskEditorEffect
}
