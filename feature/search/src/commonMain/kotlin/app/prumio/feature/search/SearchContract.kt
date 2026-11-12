package app.prumio.feature.search

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.search.SearchResults
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.ui.message.UiMessage

internal data class SearchUiState(val query: String = "", val results: SearchResults? = null)

internal sealed interface SearchAction {
    data object Refresh : SearchAction

    data class ChangeQuery(val query: String) : SearchAction
    data class OpenTask(val id: TaskId) : SearchAction
    data class OpenAppointment(val id: AppointmentId) : SearchAction
    data class OpenMatter(val id: MatterId) : SearchAction
    data object OpenInbox : SearchAction
    data object Close : SearchAction
}

internal sealed interface SearchEffect {
    data class Navigate(val key: NavKey) : SearchEffect
    data object Close : SearchEffect
    data class ShowMessage(val message: UiMessage) : SearchEffect
}
