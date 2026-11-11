package app.prumio.core.domain.appointment

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId

/** Permanently deletes an appointment and returns it so the UI can offer Undo. Rule: BR-GEN-005. */
class DeleteAppointmentUseCase(private val appointments: AppointmentRepository) {
    suspend operator fun invoke(id: AppointmentId): Outcome<Appointment, DomainError> {
        val appointment = storageCall { appointments.get(id) }.valueOr { return it.asFailure() }
            ?: return notFound("Appointment", id.value)
        return storageCall { appointments.delete(id) }.map { appointment }
    }
}
