package app.prumio.feature.matters.detail

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.matter.MatterDetail
import app.prumio.core.domain.matter.OpenActionsChoice
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.TaskId
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource

// Matter detail (docs/product/screens.md#matter-detail): status, next action, follow-up, history.

/** The "Mark as waiting" sheet (BR-MATTER-004). */
internal data class WaitingDraft(val waitingFor: String = "", val followUp: LocalDate? = null)

/** A note being added ([entryId] null) or edited (BR-MATTER-008). */
internal data class NoteDraft(
    val entryId: MatterEntryId? = null,
    val text: String = "",
    val date: LocalDate,
    val error: StringResource? = null,
)

internal data class MatterDetailUiState(
    val detail: MatterDetail? = null,
    val today: LocalDate = LocalDate(1970, 1, 1),
    /** Open actions that are overdue (BR-TASK-004). */
    val overdueTaskIds: Set<TaskId> = emptySet(),
    val waiting: WaitingDraft? = null,
    val note: NoteDraft? = null,
    val isConfirmingResolve: Boolean = false,
    val promptNextAction: Boolean = false,
    val showPermissionExplanation: Boolean = false,
    val showOtherActions: Boolean = false,
)

internal sealed interface MatterDetailAction {
    data object ConfirmPermission : MatterDetailAction
    data class PermissionResult(val granted: Boolean) : MatterDetailAction
    data object StartWaiting : MatterDetailAction

    data class ChangeWaiting(val draft: WaitingDraft) : MatterDetailAction

    data object ConfirmWaiting : MatterDetailAction

    data object Resume : MatterDetailAction

    data object Resolve : MatterDetailAction

    data class ConfirmResolve(val openActions: OpenActionsChoice) : MatterDetailAction

    data object Reopen : MatterDetailAction

    data class SetFollowUp(val date: LocalDate?, val time: LocalTime? = null) : MatterDetailAction

    data object AddNextAction : MatterDetailAction

    data class OpenTask(val id: TaskId) : MatterDetailAction

    data class SetTaskDone(val id: TaskId, val done: Boolean) : MatterDetailAction

    data object ToggleOtherActions : MatterDetailAction

    data object StartNote : MatterDetailAction

    data class EditNote(val entry: MatterEntry) : MatterDetailAction

    data class ChangeNote(val text: String, val date: LocalDate) : MatterDetailAction

    data object SaveNote : MatterDetailAction

    data object DeleteNote : MatterDetailAction

    /** Closes any open sheet or dialog. */
    data object Dismiss : MatterDetailAction

    data object Edit : MatterDetailAction

    data object Delete : MatterDetailAction

    data object Close : MatterDetailAction
}

internal sealed interface MatterDetailEffect {
    data class Navigate(val key: NavKey) : MatterDetailEffect

    data object RequestPermission : MatterDetailEffect
    data object Close : MatterDetailEffect

    data class ShowMessage(val message: UiMessage) : MatterDetailEffect
}
