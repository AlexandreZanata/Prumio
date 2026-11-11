package app.prumio.core.data.task

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.prumio.core.common.coroutines.DispatcherProvider
import app.prumio.core.data.storage.toColumn
import app.prumio.core.data.storage.toModel
import app.prumio.core.data.storage.upsertTask
import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import kotlin.time.Instant
import app.prumio.core.database.Task as TaskRow

internal class SqlDelightTaskRepository(
    private val database: PrumioDatabase,
    private val dispatchers: DispatcherProvider,
) : TaskRepository {
    private val queries = database.taskQueries

    private fun Query<TaskRow>.observeList(): Flow<List<Task>> =
        asFlow().mapToList(dispatchers.io).map { rows -> rows.map { it.toModel() } }

    override fun observe(id: TaskId): Flow<Task?> =
        queries.selectById(id.value).asFlow().mapToOneOrNull(dispatchers.io).map { it?.toModel() }

    override fun observeOpen(): Flow<List<Task>> = queries.selectOpen().observeList()

    override fun observeOpenDueOnOrBefore(date: LocalDate): Flow<List<Task>> =
        queries.selectOpenDueOnOrBefore(date.toColumn()).observeList()

    override fun observeOpenDueBetween(from: LocalDate, to: LocalDate): Flow<List<Task>> =
        queries.selectOpenDueBetween(fromDate = from.toColumn(), toDate = to.toColumn()).observeList()

    override fun observeOpenUndated(): Flow<List<Task>> = queries.selectOpenUndated().observeList()

    override fun observeCompletedSince(instant: Instant): Flow<List<Task>> =
        queries.selectCompletedSince(instant.toColumn()).observeList()

    override fun observeDismissed(): Flow<List<Task>> = queries.selectDismissed().observeList()

    override fun observeByMatter(matterId: MatterId): Flow<List<Task>> =
        queries.selectByMatter(matterId.value).observeList()

    override suspend fun get(id: TaskId): Task? = withContext(dispatchers.io) {
        queries.selectById(id.value).executeAsOneOrNull()?.toModel()
    }

    override suspend fun getByMatter(matterId: MatterId): List<Task> = withContext(dispatchers.io) {
        queries.selectByMatter(matterId.value).executeAsList().map { it.toModel() }
    }

    override suspend fun save(task: Task) = withContext(dispatchers.io) {
        database.transaction { database.upsertTask(task) }
    }

    override suspend fun delete(id: TaskId) = withContext(dispatchers.io) {
        queries.deleteById(id.value)
        Unit
    }
}
