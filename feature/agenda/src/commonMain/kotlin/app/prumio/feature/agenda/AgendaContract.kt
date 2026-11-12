package app.prumio.feature.agenda

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.agenda.AgendaDay
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.TaskId
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate

// Agenda (docs/product/screens.md#agenda): week strip + continuous list of days.

internal data class AgendaUiState(
    val today: LocalDate = LocalDate(1970, 1, 1),
    /** Day highlighted in the week strip; the list scrolls to it. */
    val selectedDate: LocalDate = LocalDate(1970, 1, 1),
    /** Loaded days, chronological (BR-APPT-007); empty days are included. */
    val days: List<AgendaDay> = emptyList(),
    val isLoading: Boolean = true,
) {
    /** Days shown in the list: those with items, plus today and the selected day. */
    val visibleDays: List<AgendaDay>
        get() = days.filter { it.items.isNotEmpty() || it.date == today || it.date == selectedDate }

    fun hasItems(date: LocalDate): Boolean = days.any { it.date == date && it.items.isNotEmpty() }

    val isWeekEmpty: Boolean get() = !isLoading && days.take(DAYS_IN_WEEK).all { it.items.isEmpty() }

    companion object {
        const val DAYS_IN_WEEK = 7

        /** How many days are loaded from the first one. */
        const val LOADED_DAYS = 35
    }
}

internal sealed interface AgendaAction {
    /** Highlights [date]; loads from it when it is outside the loaded days. */
    data class SelectDate(val date: LocalDate) : AgendaAction

    data object GoToToday : AgendaAction

    /** Re-reads "today" (screen resumed; BR-TODAY-007). */
    data object Refresh : AgendaAction

    data object AddAppointment : AgendaAction

    data class OpenAppointment(val id: AppointmentId) : AgendaAction

    data class OpenTask(val id: TaskId) : AgendaAction

    data class SetTaskDone(val id: TaskId, val done: Boolean) : AgendaAction
}

internal sealed interface AgendaEffect {
    data class Navigate(val key: NavKey) : AgendaEffect

    data class ShowMessage(val message: UiMessage) : AgendaEffect
}
