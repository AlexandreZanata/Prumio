package app.prumio.feature.matters.editor

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.CreateMatterUseCase
import app.prumio.core.domain.matter.MatterDraft
import app.prumio.core.domain.matter.ObserveMatterDetailUseCase
import app.prumio.core.domain.matter.UpdateMatterUseCase
import app.prumio.core.model.MatterId
import app.prumio.core.navigation.MatterEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.fieldMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.feature.matters.resources.Res
import app.prumio.feature.matters.resources.matter_saved
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/** Creates or edits a matter. Rules: BR-GEN-001, BR-MATTER-001. */
internal class MatterEditorViewModel(
    key: MatterEditorKey,
    private val observeMatterDetail: ObserveMatterDetailUseCase,
    private val createMatter: CreateMatterUseCase,
    private val updateMatter: UpdateMatterUseCase,
) : PrumioViewModel<MatterEditorUiState, MatterEditorAction, MatterEditorEffect>(
    MatterEditorUiState(isNew = key.matterId == null),
) {
    private val id = key.matterId?.let(::MatterId)

    init {
        viewModelScope.launch {
            val matter = id?.let { observeMatterDetail(it).first()?.matter }
            updateState { state ->
                state.copy(
                    isLoading = false,
                    title = matter?.title.orEmpty(),
                    description = matter?.description.orEmpty(),
                )
            }
        }
    }

    override fun onAction(action: MatterEditorAction) {
        when (action) {
            is MatterEditorAction.ChangeTitle -> updateState { it.copy(title = action.title, titleError = null) }
            is MatterEditorAction.ChangeDescription -> updateState { it.copy(description = action.description) }
            MatterEditorAction.Save -> save()
            MatterEditorAction.Close -> sendEffect(MatterEditorEffect.Close)
        }
    }

    private fun save() {
        val state = currentState
        if (!state.canSave) return
        updateState { it.copy(isSaving = true) }
        val draft = MatterDraft(title = state.title, description = state.description)
        viewModelScope.launch {
            val result = if (id == null) createMatter(draft) else updateMatter(id, draft)
            result
                .onSuccess { matter ->
                    sendEffect(MatterEditorEffect.ShowMessage(UiMessage(Res.string.matter_saved)))
                    sendEffect(if (id == null) MatterEditorEffect.OpenCreated(matter.id) else MatterEditorEffect.Close)
                }
                .onFailure { error ->
                    updateState { it.copy(isSaving = false) }
                    if (error is DomainError.Validation && error.field == "title") {
                        updateState { it.copy(titleError = error.fieldMessage()) }
                    } else {
                        sendEffect(MatterEditorEffect.ShowMessage(error.toUiMessage()))
                    }
                }
        }
    }
}
