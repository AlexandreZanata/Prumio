package app.prumio.core.domain.appointment

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId

/**
 * Replaces the editable fields of an appointment. Changing the schedule re-anchors it to the
 * current device zone; other edits keep its zone. Rules: BR-APPT-001, BR-APPT-002, BR-APPT-003.
 */
class UpdateAppointmentUseCase(
    private val appointments: AppointmentRepository,
    private val matters: MatterRepository,
    private val time: TimeProvider,
) {
    suspend operator fun invoke(id: AppointmentId, draft: AppointmentDraft): Outcome<Appointment, DomainError> {
        val valid = draft.normalized().valueOr { return it.asFailure() }
        requireMatter(matters, valid.matterId).valueOr { return it.asFailure() }
        val existing = storageCall { appointments.get(id) }.valueOr { return it.asFailure() }
            ?: return notFound("Appointment", id.value)
        val timing = if (valid.schedule ==
            existing.timing.toSchedule()
        ) {
            existing.timing
        } else {
            valid.schedule.toTiming(time.timeZone())
        }
        val updated = existing.copy(
            title = valid.title,
            notes = valid.notes,
            location = valid.location,
            timing = timing,
            matterId = valid.matterId,
            updatedAt = time.now(),
        )
        return storageCall { appointments.save(updated) }.map { updated }
    }
}
