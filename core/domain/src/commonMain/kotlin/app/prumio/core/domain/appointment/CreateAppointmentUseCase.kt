package app.prumio.core.domain.appointment

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId

/** Creates an appointment anchored to the current device zone. Rules: BR-APPT-001, BR-APPT-002, BR-APPT-003. */
class CreateAppointmentUseCase(
    private val appointments: AppointmentRepository,
    private val matters: MatterRepository,
    private val ids: IdGenerator,
    private val time: TimeProvider,
) {
    suspend operator fun invoke(draft: AppointmentDraft): Outcome<Appointment, DomainError> {
        val valid = draft.normalized().valueOr { return it.asFailure() }
        requireMatter(matters, valid.matterId).valueOr { return it.asFailure() }
        val now = time.now()
        val appointment = Appointment(
            id = AppointmentId(ids.newId()),
            title = valid.title,
            notes = valid.notes,
            location = valid.location,
            timing = valid.schedule.toTiming(time.timeZone()),
            matterId = valid.matterId,
            createdAt = now,
            updatedAt = now,
        )
        return storageCall { appointments.save(appointment) }.map { appointment }
    }
}
