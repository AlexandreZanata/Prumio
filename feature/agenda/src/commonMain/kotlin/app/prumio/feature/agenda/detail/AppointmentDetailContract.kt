package app.prumio.feature.agenda.detail

import androidx.navigation3.runtime.NavKey
import app.prumio.core.model.Appointment
import app.prumio.core.ui.message.UiMessage
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime

// Appointment detail: everything about one appointment, plus Edit and Delete (Undo).

internal data class AppointmentDetailUiState(
    val appointment: Appointment? = null,
    /** Start and end in the device zone (BR-TIME-002); null for all-day appointments. */
    val localStart: LocalDateTime? = null,
    val localEnd: LocalDateTime? = null,
    val today: LocalDate = LocalDate(1970, 1, 1),
)

internal sealed interface AppointmentDetailAction {
    data object Edit : AppointmentDetailAction

    data object Delete : AppointmentDetailAction

    data object Close : AppointmentDetailAction
}

internal sealed interface AppointmentDetailEffect {
    data class Navigate(val key: NavKey) : AppointmentDetailEffect

    data object Close : AppointmentDetailEffect

    data class ShowMessage(val message: UiMessage) : AppointmentDetailEffect
}
