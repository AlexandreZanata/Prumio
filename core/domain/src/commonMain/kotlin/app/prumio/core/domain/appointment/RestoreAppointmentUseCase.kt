package app.prumio.core.domain.appointment

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Appointment

/** Undo of a deletion: same id and fields; unlinked if its matter is gone. Rule: BR-GEN-005. */
class RestoreAppointmentUseCase(
    private val appointments: AppointmentRepository,
    private val matters: MatterRepository,
) {
    suspend operator fun invoke(appointment: Appointment): Outcome<Appointment, DomainError> {
        val matterId = appointment.matterId
        val matterExists =
            matterId == null || storageCall { matters.get(matterId) }.valueOr { return it.asFailure() } != null
        val restored = if (matterExists) appointment else appointment.copy(matterId = null)
        return storageCall { appointments.save(restored) }.map { restored }
    }
}
