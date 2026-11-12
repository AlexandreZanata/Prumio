package app.prumio.feature.agenda.detail

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.domain.appointment.AppointmentTimePolicy
import app.prumio.core.domain.appointment.DeleteAppointmentUseCase
import app.prumio.core.domain.appointment.ObserveAppointmentUseCase
import app.prumio.core.domain.appointment.RestoreAppointmentUseCase
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.core.ui.resources.action_undo
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.appointment_deleted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.toLocalDateTime
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Rules: BR-TIME-002 (shown in the device zone), BR-GEN-005 (delete + Undo). */
internal class AppointmentDetailViewModel(
    key: AppointmentDetailKey,
    observeAppointment: ObserveAppointmentUseCase,
    private val deleteAppointment: DeleteAppointmentUseCase,
    private val restoreAppointment: RestoreAppointmentUseCase,
    private val time: TimeProvider,
) : PrumioViewModel<AppointmentDetailUiState, AppointmentDetailAction, AppointmentDetailEffect>(
    AppointmentDetailUiState(),
) {
    private val id = AppointmentId(key.appointmentId)

    init {
        observeAppointment(id)
            .onEach { appointment ->
                val zone = time.timeZone()
                val timed = appointment?.timing as? AppointmentTiming.Timed
                updateState {
                    it.copy(
                        appointment = appointment,
                        localStart = appointment?.takeIf { timed != null }?.let { a ->
                            AppointmentTimePolicy.localStart(a, zone)
                        },
                        localEnd = appointment?.takeIf { timed?.end != null }?.let { a ->
                            AppointmentTimePolicy.end(a, zone).toLocalDateTime(zone)
                        },
                        today = time.today(),
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: AppointmentDetailAction) {
        when (action) {
            AppointmentDetailAction.Edit -> sendEffect(
                AppointmentDetailEffect.Navigate(AppointmentEditorKey(appointmentId = id.value)),
            )

            AppointmentDetailAction.Delete -> viewModelScope.launch {
                deleteAppointment(id)
                    .onSuccess { deleted ->
                        val message = UiMessage(
                            text = Res.string.appointment_deleted,
                            actionLabel = CoreUiRes.string.action_undo,
                            onAction = { restoreAppointment(deleted) },
                        )
                        sendEffect(AppointmentDetailEffect.ShowMessage(message))
                        sendEffect(AppointmentDetailEffect.Close)
                    }
                    .onFailure { sendEffect(AppointmentDetailEffect.ShowMessage(it.toUiMessage())) }
            }

            AppointmentDetailAction.Close -> sendEffect(AppointmentDetailEffect.Close)
        }
    }
}
