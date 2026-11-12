package app.prumio.feature.agenda.editor

import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.jetbrains.compose.resources.StringResource

// Appointment editor (docs/product/screens.md#appointment-editor--detail). Times are device-local.

internal data class AppointmentEditorUiState(
    val isNew: Boolean = true,
    val isLoading: Boolean = true,
    val title: String = "",
    val isAllDay: Boolean = false,
    val startDate: LocalDate = LocalDate(1970, 1, 1),
    val startTime: LocalTime = LocalTime(9, 0),
    /** Optional end; for timed appointments the end is [endDate] (default: start date) at [endTime]. */
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,
    val location: String = "",
    val notes: String = "",
    val today: LocalDate = LocalDate(1970, 1, 1),
    val titleError: StringResource? = null,
    val endError: StringResource? = null,
    val isSaving: Boolean = false,
) {
    val canSave: Boolean get() = title.isNotBlank() && !isSaving && !isLoading
}

internal sealed interface AppointmentEditorAction {
    data class ChangeTitle(val title: String) : AppointmentEditorAction

    data class ChangeAllDay(val allDay: Boolean) : AppointmentEditorAction

    data class ChangeStartDate(val date: LocalDate) : AppointmentEditorAction

    data class ChangeStartTime(val time: LocalTime) : AppointmentEditorAction

    data class ChangeEndDate(val date: LocalDate?) : AppointmentEditorAction

    data class ChangeEndTime(val time: LocalTime?) : AppointmentEditorAction

    data class ChangeLocation(val location: String) : AppointmentEditorAction

    data class ChangeNotes(val notes: String) : AppointmentEditorAction

    data object Save : AppointmentEditorAction

    data object Close : AppointmentEditorAction
}

internal sealed interface AppointmentEditorEffect {
    data object Close : AppointmentEditorEffect

    data class ShowMessage(val message: UiMessage) : AppointmentEditorEffect
}
