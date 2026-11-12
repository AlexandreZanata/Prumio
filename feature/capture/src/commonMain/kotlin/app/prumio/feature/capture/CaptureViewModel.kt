package app.prumio.feature.capture

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.inbox.CaptureInboxItemUseCase
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.fieldMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.feature.capture.resources.Res
import app.prumio.feature.capture.resources.capture_saved
import kotlinx.coroutines.launch

/** Saves the text to the inbox, then closes with "Saved to Inbox". Rules: BR-INBOX-001, BR-INBOX-002. */
internal class CaptureViewModel(private val captureInboxItem: CaptureInboxItemUseCase) :
    PrumioViewModel<CaptureUiState, CaptureAction, CaptureEffect>(CaptureUiState()) {
    override fun onAction(action: CaptureAction) {
        when (action) {
            is CaptureAction.ChangeText -> updateState { it.copy(text = action.text, error = null) }
            CaptureAction.Save -> save()
            CaptureAction.Close -> sendEffect(CaptureEffect.Close)
        }
    }

    private fun save() {
        if (!currentState.canSave) return
        updateState { it.copy(isSaving = true) }
        viewModelScope.launch {
            captureInboxItem(currentState.text)
                .onSuccess {
                    sendEffect(CaptureEffect.ShowMessage(UiMessage(Res.string.capture_saved)))
                    sendEffect(CaptureEffect.Close)
                }
                .onFailure { error ->
                    updateState { it.copy(isSaving = false) }
                    if (error is DomainError.Validation) {
                        updateState { it.copy(error = error.fieldMessage()) }
                    } else {
                        sendEffect(CaptureEffect.ShowMessage(error.toUiMessage()))
                    }
                }
        }
    }
}
