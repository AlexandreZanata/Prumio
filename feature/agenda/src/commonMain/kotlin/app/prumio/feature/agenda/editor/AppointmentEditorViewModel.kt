package app.prumio.feature.agenda.editor

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.Outcome
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.domain.appointment.AppointmentDraft
import app.prumio.core.domain.appointment.AppointmentSchedule
import app.prumio.core.domain.appointment.AppointmentTimePolicy
import app.prumio.core.domain.appointment.CreateAppointmentUseCase
import app.prumio.core.domain.appointment.ObserveAppointmentUseCase
import app.prumio.core.domain.appointment.UpdateAppointmentUseCase
import app.prumio.core.domain.error.DomainError
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.fieldMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.appointment_saved
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toLocalDateTime

/** Creates or edits an appointment. Rules: BR-APPT-001 (title + start), BR-APPT-002 (range), BR-APPT-003. */
internal class AppointmentEditorViewModel(
    key: AppointmentEditorKey,
    private val observeAppointment: ObserveAppointmentUseCase,
    private val createAppointment: CreateAppointmentUseCase,
    private val updateAppointment: UpdateAppointmentUseCase,
    private val time: TimeProvider,
) : PrumioViewModel<AppointmentEditorUiState, AppointmentEditorAction, AppointmentEditorEffect>(
    newAppointment(key, time.localNow()),
) {
    private val id = key.appointmentId?.let(::AppointmentId)
    private var existing: Appointment? = null

    init {
        viewModelScope.launch {
            val appointment = id?.let { observeAppointment(it).first() }
            existing = appointment
            updateState { state -> appointment?.let { state.loaded(it) } ?: state.copy(isLoading = false) }
        }
    }

    override fun onAction(action: AppointmentEditorAction) {
        when (action) {
            is AppointmentEditorAction.ChangeTitle -> updateState { it.copy(title = action.title, titleError = null) }

            is AppointmentEditorAction.ChangeAllDay -> updateState {
                it.copy(isAllDay = action.allDay, endError = null)
            }

            is AppointmentEditorAction.ChangeStartDate -> updateState {
                it.copy(startDate = action.date, endError = null)
            }

            is AppointmentEditorAction.ChangeStartTime -> updateState {
                it.copy(startTime = action.time, endError = null)
            }

            is AppointmentEditorAction.ChangeEndDate -> updateState { it.copy(endDate = action.date, endError = null) }

            is AppointmentEditorAction.ChangeEndTime -> updateState { it.copy(endTime = action.time, endError = null) }

            is AppointmentEditorAction.ChangeLocation -> updateState { it.copy(location = action.location) }

            is AppointmentEditorAction.ChangeNotes -> updateState { it.copy(notes = action.notes) }

            AppointmentEditorAction.Save -> save()

            AppointmentEditorAction.Close -> sendEffect(AppointmentEditorEffect.Close)
        }
    }

    private fun save() {
        val state = currentState
        if (!state.canSave) return
        updateState { it.copy(isSaving = true) }
        val draft = AppointmentDraft(
            title = state.title,
            schedule = state.schedule(),
            notes = state.notes,
            location = state.location,
            matterId = existing?.matterId,
        )
        viewModelScope.launch {
            val result: Outcome<Appointment, DomainError> =
                if (id == null) createAppointment(draft) else updateAppointment(id, draft)
            result
                .onSuccess {
                    sendEffect(AppointmentEditorEffect.ShowMessage(UiMessage(Res.string.appointment_saved)))
                    sendEffect(AppointmentEditorEffect.Close)
                }
                .onFailure { error ->
                    updateState { it.copy(isSaving = false) }
                    when {
                        error is DomainError.Validation && error.field == "title" ->
                            updateState { it.copy(titleError = error.fieldMessage()) }

                        error is DomainError.Validation && error.field == "end" ->
                            updateState { it.copy(endError = error.fieldMessage()) }

                        else -> sendEffect(AppointmentEditorEffect.ShowMessage(error.toUiMessage()))
                    }
                }
        }
    }

    private fun AppointmentEditorUiState.loaded(appointment: Appointment): AppointmentEditorUiState {
        val base = copy(
            isLoading = false,
            title = appointment.title,
            location = appointment.location.orEmpty(),
            notes = appointment.notes.orEmpty(),
        )
        return when (val timing = appointment.timing) {
            is AppointmentTiming.AllDay -> base.copy(
                isAllDay = true,
                startDate = timing.startDate,
                endDate = timing.endDate.takeIf { it != timing.startDate },
            )

            is AppointmentTiming.Timed -> {
                val zone = time.timeZone()
                val start = AppointmentTimePolicy.localStart(appointment, zone)
                val end = timing.end?.let { AppointmentTimePolicy.end(appointment, zone).toLocalDateTime(zone) }
                base.copy(
                    isAllDay = false,
                    startDate = start.date,
                    startTime = start.time,
                    endDate = end?.date?.takeIf { it != start.date },
                    endTime = end?.time,
                )
            }
        }
    }

    private companion object {
        fun newAppointment(key: AppointmentEditorKey, now: LocalDateTime): AppointmentEditorUiState {
            val date = key.date?.let(LocalDate::parse) ?: now.date
            // Next full hour, kept on the same day.
            val hour = (now.hour + 1).coerceAtMost(23)
            return AppointmentEditorUiState(
                isNew = key.appointmentId == null,
                startDate = date,
                startTime = LocalTime(hour, 0),
                today = now.date,
            )
        }
    }
}

/** The schedule the form describes (BR-APPT-001). */
internal fun AppointmentEditorUiState.schedule(): AppointmentSchedule = if (isAllDay) {
    AppointmentSchedule.AllDay(startDate, endDate ?: startDate)
} else {
    AppointmentSchedule.Timed(
        start = LocalDateTime(startDate, startTime),
        end = endTime?.let { LocalDateTime(endDate ?: startDate, it) },
    )
}
