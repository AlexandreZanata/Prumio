package app.prumio.core.testing

import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

class FakeTaskRepository(private val store: FakeStore) : TaskRepository {
    private val byDue = compareBy<Task>({ it.dueDate }, { it.dueTime }, { it.createdAt }, { it.id.value })

    private fun select(sort: Comparator<Task> = byDue, filter: (Task) -> Boolean): Flow<List<Task>> =
        store.tasks.map { tasks -> tasks.values.filter(filter).sortedWith(sort) }

    override fun observe(id: TaskId): Flow<Task?> = store.tasks.map { it[id] }

    override fun observeOpen(): Flow<List<Task>> = select { it.isOpen }

    override fun observeOpenDueOnOrBefore(date: LocalDate): Flow<List<Task>> =
        select { task -> task.isOpen && task.dueDate.let { it != null && it <= date } }

    override fun observeOpenDueBetween(from: LocalDate, to: LocalDate): Flow<List<Task>> =
        select { task -> task.isOpen && task.dueDate.let { it != null && it in from..to } }

    override fun observeOpenUndated(): Flow<List<Task>> =
        select(compareByDescending<Task> { it.createdAt }.thenBy { it.id.value }) { it.isOpen && it.dueDate == null }

    override fun observeCompletedSince(instant: Instant): Flow<List<Task>> =
        select(compareByDescending<Task> { it.completedAt }.thenBy { it.id.value }) { task ->
            task.status == TaskStatus.Completed && task.completedAt.let { it != null && it >= instant }
        }

    override fun observeDismissed(): Flow<List<Task>> = select(
        compareByDescending<Task> {
            it.dismissedAt
        }.thenBy { it.id.value },
    ) { it.status == TaskStatus.Dismissed }

    override fun observeByMatter(matterId: MatterId): Flow<List<Task>> =
        select(compareBy<Task>({ it.createdAt }, { it.id.value })) { it.matterId == matterId }

    override suspend fun get(id: TaskId): Task? = store.tasks.value[id]

    override suspend fun getByMatter(matterId: MatterId): List<Task> = store.tasks.value.values.filter {
        it.matterId == matterId
    }.sortedWith(compareBy({ it.createdAt }, { it.id.value }))

    override suspend fun save(task: Task) = store.putTask(task)

    override suspend fun delete(id: TaskId) = store.tasks.update { it - id }
}
