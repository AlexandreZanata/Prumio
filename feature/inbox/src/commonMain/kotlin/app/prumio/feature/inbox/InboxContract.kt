package app.prumio.feature.inbox

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.inbox.ConversionType
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import app.prumio.core.ui.message.UiMessage
import org.jetbrains.compose.resources.StringResource
import kotlin.time.Instant

// Inbox (docs/product/screens.md#inbox): newest first, organize / edit / discard with Undo.

internal data class InboxUiState(
    val items: List<InboxItem> = emptyList(),
    /** Reference for ages ("2 days ago"), refreshed with every update of the list. */
    val now: Instant = Instant.fromEpochMilliseconds(0),
    val isLoading: Boolean = true,
    /** The item whose actions sheet is open. */
    val selected: InboxItem? = null,
    /** Text being edited for [selected]; null when not editing. */
    val editText: String? = null,
    val editError: StringResource? = null,
)

internal sealed interface InboxAction {
    data class Select(val id: InboxItemId) : InboxAction

    data object DismissSheet : InboxAction

    data class Organize(val into: ConversionType) : InboxAction

    data object StartEdit : InboxAction

    data class ChangeEditText(val text: String) : InboxAction

    data object SaveEdit : InboxAction

    data object Discard : InboxAction
}

internal sealed interface InboxEffect {
    data class Navigate(val key: NavKey) : InboxEffect

    data class ShowMessage(val message: UiMessage) : InboxEffect
}
