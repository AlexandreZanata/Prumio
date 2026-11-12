package app.prumio.core.data.appointment

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.byOwner
import app.prumio.core.data.storage.remindersFor
import app.prumio.core.data.storage.replaceReminders
import app.prumio.core.data.storage.toColumn
import app.prumio.core.data.storage.toModel
import app.prumio.core.data.storage.upsertAppointment
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.ReminderOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate

internal class SqlDelightAppointmentRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : AppointmentRepository {
    private val queries = database.appointmentQueries

    override fun observeAll(): Flow<List<Appointment>> = queries.selectAll().asFlow().mapToList(dispatchers.io)
        .combine(database.reminderQueries.selectAll().asFlow().mapToList(dispatchers.io)) { rows, reminders ->
            val byOwner = reminders.byOwner("APPOINTMENT")
            rows.map { it.toModel(byOwner[it.id].orEmpty()) }
        }

    override fun observe(id: AppointmentId): Flow<Appointment?> = queries.selectById(
        id.value,
    ).asFlow().mapToOneOrNull(
        dispatchers.io,
    ).combine(database.reminderQueries.selectAll().asFlow().mapToList(dispatchers.io)) {
            row,
            reminders,
        ->
        row?.let { row -> row.toModel(reminders.byOwner("APPOINTMENT")[row.id].orEmpty()) }
    }

    override fun observeOverlapping(from: LocalDate, to: LocalDate): Flow<List<Appointment>> =
        queries.selectOverlapping(fromDate = from.toColumn(), toDate = to.toColumn())
            .asFlow()
            .mapToList(dispatchers.io)
            .combine(database.reminderQueries.selectAll().asFlow().mapToList(dispatchers.io)) { rows, reminders ->
                val byOwner = reminders.byOwner("APPOINTMENT")
                rows.map { it.toModel(byOwner[it.id].orEmpty()) }
            }

    override suspend fun get(id: AppointmentId): Appointment? = withContext(dispatchers.io) {
        queries.selectById(id.value).executeAsOneOrNull()?.let { row ->
            row.toModel(database.remindersFor("APPOINTMENT", row.id))
        }
    }

    override suspend fun save(appointment: Appointment) = withContext(dispatchers.io) {
        database.transaction { database.upsertAppointment(appointment) }
    }

    override suspend fun delete(id: AppointmentId) = withContext(dispatchers.io) {
        database.transaction {
            database.replaceReminders(ReminderOwner.Appointment(id), emptyList())
            queries.deleteById(id.value)
        }
        Unit
    }
}
