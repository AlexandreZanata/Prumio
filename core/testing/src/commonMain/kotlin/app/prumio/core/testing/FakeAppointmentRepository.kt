package app.prumio.core.testing

import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate

class FakeAppointmentRepository(private val store: FakeStore) : AppointmentRepository {
    override fun observeAll(): Flow<List<Appointment>> = store.appointments.map { it.values.toList() }

    override fun observe(id: AppointmentId): Flow<Appointment?> = store.appointments.map { it[id] }

    override fun observeOverlapping(from: LocalDate, to: LocalDate): Flow<List<Appointment>> =
        store.appointments.map { all ->
            all.values
                .filter { it.storedStart() <= to && it.storedEnd() >= from }
                .sortedWith(
                    compareBy({
                        it.storedStart()
                    }, { (it.timing as? AppointmentTiming.Timed)?.start?.time }, { it.id.value }),
                )
        }

    override suspend fun get(id: AppointmentId): Appointment? = store.appointments.value[id]

    override suspend fun save(appointment: Appointment) = store.appointments.update {
        it +
            (appointment.id to appointment)
    }

    override suspend fun delete(id: AppointmentId) = store.appointments.update { it - id }

    private fun Appointment.storedStart(): LocalDate = when (val t = timing) {
        is AppointmentTiming.Timed -> t.start.date
        is AppointmentTiming.AllDay -> t.startDate
    }

    private fun Appointment.storedEnd(): LocalDate = when (val t = timing) {
        is AppointmentTiming.Timed -> (t.end ?: t.start).date
        is AppointmentTiming.AllDay -> t.endDate
    }
}
