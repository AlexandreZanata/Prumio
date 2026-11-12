package app.prumio.feature.inbox

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.inbox.ConversionTarget
import app.prumio.core.domain.inbox.ConvertInboxItemUseCase
import app.prumio.core.domain.inbox.DiscardInboxItemUseCase
import app.prumio.core.domain.inbox.EditInboxItemUseCase
import app.prumio.core.domain.inbox.ObserveInboxUseCase
import app.prumio.core.domain.inbox.RestoreInboxItemUseCase
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.fieldMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.core.ui.resources.action_undo
import app.prumio.feature.inbox.resources.Res
import app.prumio.feature.inbox.resources.inbox_discarded
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import app.prumio.core.ui.resources.Res as CoreUiRes

/**
 * Rules: BR-INBOX-003 (order, from the use case), BR-INBOX-004 (organize → editor of the new item),
 * BR-INBOX-006 and BR-GEN-005 (discard with Undo).
 */
internal class InboxViewModel(
    observeInbox: ObserveInboxUseCase,
    private val editInboxItem: EditInboxItemUseCase,
    private val discardInboxItem: DiscardInboxItemUseCase,
    private val restoreInboxItem: RestoreInboxItemUseCase,
    private val convertInboxItem: ConvertInboxItemUseCase,
    private val time: TimeProvider,
) : PrumioViewModel<InboxUiState, InboxAction, InboxEffect>(InboxUiState()) {
    init {
        observeInbox()
            .onEach { items -> updateState { it.copy(items = items, now = time.now(), isLoading = false) } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: InboxAction) {
        when (action) {
            is InboxAction.Select -> updateState { state ->
                state.copy(selected = state.items.firstOrNull { it.id == action.id }, editText = null, editError = null)
            }

            InboxAction.DismissSheet -> closeSheet()

            is InboxAction.Organize -> organize(action)

            InboxAction.StartEdit -> updateState { it.copy(editText = it.selected?.text) }

            is InboxAction.ChangeEditText -> updateState { it.copy(editText = action.text, editError = null) }

            InboxAction.SaveEdit -> saveEdit()

            InboxAction.Discard -> discard()
        }
    }

    private fun closeSheet() = updateState { it.copy(selected = null, editText = null, editError = null) }

    private fun organize(action: InboxAction.Organize) {
        val item = currentState.selected ?: return
        closeSheet()
        viewModelScope.launch {
            convertInboxItem(item.id, action.into)
                .onSuccess { target ->
                    val key = when (target) {
                        is ConversionTarget.ToTask -> TaskEditorKey(taskId = target.task.id.value)
                        is ConversionTarget.ToAppointment -> AppointmentEditorKey(target.appointment.id.value)
                        is ConversionTarget.ToMatter -> MatterEditorKey(target.matter.id.value)
                    }
                    sendEffect(InboxEffect.Navigate(key))
                }
                .onFailure { sendEffect(InboxEffect.ShowMessage(it.toUiMessage())) }
        }
    }

    private fun saveEdit() {
        val item = currentState.selected ?: return
        val text = currentState.editText ?: return
        viewModelScope.launch {
            editInboxItem(item.id, text)
                .onSuccess { closeSheet() }
                .onFailure { error ->
                    if (error is DomainError.Validation) {
                        updateState { it.copy(editError = error.fieldMessage()) }
                    } else {
                        sendEffect(InboxEffect.ShowMessage(error.toUiMessage()))
                    }
                }
        }
    }

    private fun discard() {
        val item = currentState.selected ?: return
        closeSheet()
        viewModelScope.launch {
            discardInboxItem(item.id)
                .onSuccess { discarded ->
                    val message = UiMessage(
                        text = Res.string.inbox_discarded,
                        actionLabel = CoreUiRes.string.action_undo,
                        onAction = { restoreInboxItem(discarded) },
                    )
                    sendEffect(InboxEffect.ShowMessage(message))
                }
                .onFailure { sendEffect(InboxEffect.ShowMessage(it.toUiMessage())) }
        }
    }
}
