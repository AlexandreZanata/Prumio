package app.prumio.core.domain.appointment

import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import kotlinx.coroutines.flow.Flow

/** One appointment, live; null once deleted. */
class ObserveAppointmentUseCase(private val appointments: AppointmentRepository) {
    operator fun invoke(id: AppointmentId): Flow<Appointment?> = appointments.observe(id)
}
