package app.prumio.core.domain.appointment

import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

/** Port: storage of appointments (docs/architecture/contracts.md#appointments-phase-1-exceptions-phase-5). */
interface AppointmentRepository {
    fun observe(id: AppointmentId): Flow<Appointment?>

    /**
     * Appointments whose *stored* local dates overlap [from]..[to] (inclusive). Stored dates of timed
     * appointments are local to their own zone, so callers widen the range by one day on each side
     * and filter precisely in the domain (docs/domain/time-semantics.md).
     */
    fun observeOverlapping(from: LocalDate, to: LocalDate): Flow<List<Appointment>>

    suspend fun get(id: AppointmentId): Appointment?

    /** Inserts or replaces the appointment. */
    suspend fun save(appointment: Appointment)

    suspend fun delete(id: AppointmentId)
}
