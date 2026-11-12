package app.prumio.feature.matters.detail

import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.common.Outcome
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.common.time.today
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.AddMatterNoteUseCase
import app.prumio.core.domain.matter.DeleteMatterNoteUseCase
import app.prumio.core.domain.matter.DeleteMatterUseCase
import app.prumio.core.domain.matter.EditMatterNoteUseCase
import app.prumio.core.domain.matter.FollowUp
import app.prumio.core.domain.matter.ObserveMatterDetailUseCase
import app.prumio.core.domain.matter.OpenActionsChoice
import app.prumio.core.domain.matter.ReopenMatterUseCase
import app.prumio.core.domain.matter.ResolveMatterUseCase
import app.prumio.core.domain.matter.RestoreMatterNoteUseCase
import app.prumio.core.domain.matter.RestoreMatterUseCase
import app.prumio.core.domain.matter.ResumeMatterUseCase
import app.prumio.core.domain.matter.SetMatterFollowUpUseCase
import app.prumio.core.domain.matter.WaitOnMatterUseCase
import app.prumio.core.domain.task.CompleteTaskUseCase
import app.prumio.core.domain.task.ReopenTaskUseCase
import app.prumio.core.domain.task.TaskOverduePolicy
import app.prumio.core.model.MatterId
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.fieldMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.core.ui.resources.action_undo
import app.prumio.feature.matters.resources.Res
import app.prumio.feature.matters.resources.matter_deleted
import app.prumio.feature.matters.resources.matter_note_deleted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Rules: BR-MATTER-002 … BR-MATTER-009 (each through its use case), BR-TASK-004, BR-GEN-005. */
internal class MatterDetailViewModel(
    key: MatterDetailKey,
    observeMatterDetail: ObserveMatterDetailUseCase,
    private val waitOnMatter: WaitOnMatterUseCase,
    private val resumeMatter: ResumeMatterUseCase,
    private val resolveMatter: ResolveMatterUseCase,
    private val reopenMatter: ReopenMatterUseCase,
    private val setMatterFollowUp: SetMatterFollowUpUseCase,
    private val completeTask: CompleteTaskUseCase,
    private val reopenTask: ReopenTaskUseCase,
    private val addMatterNote: AddMatterNoteUseCase,
    private val editMatterNote: EditMatterNoteUseCase,
    private val deleteMatterNote: DeleteMatterNoteUseCase,
    private val restoreMatterNote: RestoreMatterNoteUseCase,
    private val deleteMatter: DeleteMatterUseCase,
    private val restoreMatter: RestoreMatterUseCase,
    private val time: TimeProvider,
) : PrumioViewModel<MatterDetailUiState, MatterDetailAction, MatterDetailEffect>(
    MatterDetailUiState(promptNextAction = key.promptNextAction),
) {
    private val id = MatterId(key.matterId)

    init {
        observeMatterDetail(id)
            .onEach { detail ->
                val now = time.localNow()
                updateState { state ->
                    state.copy(
                        detail = detail,
                        promptNextAction = state.promptNextAction && detail?.needsNextAction == true,
                        today = now.date,
                        overdueTaskIds = detail?.openActions.orEmpty()
                            .filter { TaskOverduePolicy.isOverdue(it, now) }
                            .map { it.id }
                            .toSet(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: MatterDetailAction) {
        when (action) {
            MatterDetailAction.StartWaiting -> updateState {
                it.copy(waiting = WaitingDraft(), promptNextAction = false)
            }

            is MatterDetailAction.ChangeWaiting -> updateState { it.copy(waiting = action.draft) }

            MatterDetailAction.ConfirmWaiting -> confirmWaiting()

            MatterDetailAction.Resume -> perform { resumeMatter(id) }

            MatterDetailAction.Resolve -> resolve()

            is MatterDetailAction.ConfirmResolve -> {
                updateState { it.copy(isConfirmingResolve = false) }
                perform { resolveMatter(id, action.openActions) }
            }

            MatterDetailAction.Reopen -> perform { reopenMatter(id) }

            is MatterDetailAction.SetFollowUp -> perform {
                setMatterFollowUp(id, action.date?.let { FollowUp(it, action.time) })
            }

            MatterDetailAction.AddNextAction -> {
                updateState { it.copy(promptNextAction = false) }
                navigate(TaskEditorKey(matterId = id.value))
            }

            is MatterDetailAction.OpenTask -> navigate(TaskDetailKey(action.id.value))

            is MatterDetailAction.SetTaskDone -> viewModelScope.launch {
                if (action.done) {
                    completeTask(action.id).onSuccess { result ->
                        updateState { it.copy(promptNextAction = result.matterNeedsNextAction) }
                    }.onFailure(::showError)
                } else {
                    reopenTask(action.id).onFailure(::showError)
                }
            }

            MatterDetailAction.ToggleOtherActions -> updateState { it.copy(showOtherActions = !it.showOtherActions) }

            MatterDetailAction.StartNote -> updateState { it.copy(note = NoteDraft(date = time.today())) }

            is MatterDetailAction.EditNote -> updateState {
                it.copy(note = NoteDraft(action.entry.id, action.entry.text.orEmpty(), action.entry.occurredOn))
            }

            is MatterDetailAction.ChangeNote -> updateState { state ->
                state.copy(note = state.note?.copy(text = action.text, date = action.date, error = null))
            }

            MatterDetailAction.SaveNote -> saveNote()

            MatterDetailAction.DeleteNote -> deleteNote()

            MatterDetailAction.Dismiss -> updateState {
                it.copy(waiting = null, note = null, isConfirmingResolve = false, promptNextAction = false)
            }

            MatterDetailAction.Edit -> navigate(MatterEditorKey(id.value))

            MatterDetailAction.Delete -> delete()

            MatterDetailAction.Close -> sendEffect(MatterDetailEffect.Close)
        }
    }

    private fun navigate(key: NavKey) = sendEffect(MatterDetailEffect.Navigate(key))

    /** BR-MATTER-007: with open actions, the user chooses what happens to them first. */
    private fun resolve() {
        updateState { it.copy(promptNextAction = false) }
        if (currentState.detail?.openActions.orEmpty().isEmpty()) {
            perform { resolveMatter(id) }
        } else {
            updateState { it.copy(isConfirmingResolve = true) }
        }
    }

    private fun confirmWaiting() {
        val draft = currentState.waiting ?: return
        updateState { it.copy(waiting = null) }
        perform { waitOnMatter(id, draft.waitingFor, draft.followUp?.let { FollowUp(it) }) }
    }

    private fun saveNote() {
        val note = currentState.note ?: return
        viewModelScope.launch {
            val entryId = note.entryId
            val result = if (entryId == null) {
                addMatterNote(id, note.text, note.date)
            } else {
                editMatterNote(entryId, note.text, note.date)
            }
            result
                .onSuccess { updateState { it.copy(note = null) } }
                .onFailure { error ->
                    if (error is DomainError.Validation) {
                        updateState { it.copy(note = it.note?.copy(error = error.fieldMessage())) }
                    } else {
                        showError(error)
                    }
                }
        }
    }

    private fun deleteNote() {
        val entryId = currentState.note?.entryId ?: return
        updateState { it.copy(note = null) }
        viewModelScope.launch {
            deleteMatterNote(entryId).onSuccess { deleted ->
                sendEffect(
                    MatterDetailEffect.ShowMessage(
                        UiMessage(
                            text = Res.string.matter_note_deleted,
                            actionLabel = CoreUiRes.string.action_undo,
                            onAction = { restoreMatterNote(deleted) },
                        ),
                    ),
                )
            }.onFailure(::showError)
        }
    }

    private fun delete() {
        viewModelScope.launch {
            deleteMatter(id)
                .onSuccess { deleted ->
                    val message = UiMessage(
                        text = Res.string.matter_deleted,
                        actionLabel = CoreUiRes.string.action_undo,
                        onAction = { restoreMatter(deleted) },
                    )
                    sendEffect(MatterDetailEffect.ShowMessage(message))
                    sendEffect(MatterDetailEffect.Close)
                }
                .onFailure(::showError)
        }
    }

    private fun perform(block: suspend () -> Outcome<*, DomainError>) {
        viewModelScope.launch { block().onFailure(::showError) }
    }

    private fun showError(error: DomainError) = sendEffect(MatterDetailEffect.ShowMessage(error.toUiMessage()))
}
