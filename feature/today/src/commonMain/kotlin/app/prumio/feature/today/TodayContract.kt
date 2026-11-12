package app.prumio.feature.today

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.today.TodaySnapshot
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.DayPeriod
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate

internal data class TodayUiState(
    val dayPeriod: DayPeriod = DayPeriod.Morning,
    val date: LocalDate? = null,
    val snapshot: TodaySnapshot? = null,
)

internal sealed interface TodayAction {
    data object Refresh : TodayAction
    data object OpenSearch : TodayAction
    data object OpenSettings : TodayAction
    data object OpenInbox : TodayAction
    data object OpenMatters : TodayAction
    data object OpenAllTasks : TodayAction
    data object OpenDoneToday : TodayAction
    data class OpenTask(val id: TaskId) : TodayAction
    data class OpenAppointment(val id: AppointmentId) : TodayAction
    data class OpenMatter(val id: MatterId) : TodayAction
    data class SetTaskDone(val id: TaskId, val done: Boolean) : TodayAction
}

internal sealed interface TodayEffect {
    data class Navigate(val key: NavKey) : TodayEffect
    data class ShowMessage(val message: UiMessage) : TodayEffect
}
