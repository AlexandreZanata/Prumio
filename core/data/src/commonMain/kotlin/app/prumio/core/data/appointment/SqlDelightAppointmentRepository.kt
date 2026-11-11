package app.prumio.core.data.appointment

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.toColumn
import app.prumio.core.data.storage.toModel
import app.prumio.core.data.storage.upsertAppointment
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

internal class SqlDelightAppointmentRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : AppointmentRepository {
    private val queries = database.appointmentQueries

    override fun observe(id: AppointmentId): Flow<Appointment?> =
        queries.selectById(id.value).asFlow().mapToOneOrNull(dispatchers.io).map { it?.toModel() }

    override fun observeOverlapping(from: LocalDate, to: LocalDate): Flow<List<Appointment>> =
        queries.selectOverlapping(fromDate = from.toColumn(), toDate = to.toColumn())
            .asFlow()
            .mapToList(dispatchers.io)
            .map { rows -> rows.map { it.toModel() } }

    override suspend fun get(id: AppointmentId): Appointment? = withContext(dispatchers.io) {
        queries.selectById(id.value).executeAsOneOrNull()?.toModel()
    }

    override suspend fun save(appointment: Appointment) = withContext(dispatchers.io) {
        database.transaction { database.upsertAppointment(appointment) }
    }

    override suspend fun delete(id: AppointmentId) = withContext(dispatchers.io) {
        queries.deleteById(id.value)
        Unit
    }
}
