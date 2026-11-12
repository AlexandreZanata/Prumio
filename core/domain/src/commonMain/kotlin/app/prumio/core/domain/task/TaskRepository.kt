package app.prumio.core.domain.task

import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate
import kotlin.time.Instant

/**
 * Port: storage of tasks (docs/architecture/contracts.md#tasks-phase-1-completions-phase-5).
 * Dated lists are ordered by due date, due time (untimed first), creation; writes that also touch
 * a matter go through `MatterRepository.save` to stay atomic.
 */
interface TaskRepository {
    fun observe(id: TaskId): Flow<Task?>

    fun observeOpen(): Flow<List<Task>>

    fun observeOpenDueOnOrBefore(date: LocalDate): Flow<List<Task>>

    fun observeOpenDueBetween(from: LocalDate, to: LocalDate): Flow<List<Task>>

    /** Newest first. */
    fun observeOpenUndated(): Flow<List<Task>>

    /** Most recently completed first. */
    fun observeCompletedSince(instant: Instant): Flow<List<Task>>

    /** Most recently dismissed first. */
    fun observeDismissed(): Flow<List<Task>>

    fun observeByMatter(matterId: MatterId): Flow<List<Task>>

    suspend fun get(id: TaskId): Task?

    suspend fun getByMatter(matterId: MatterId): List<Task>

    /** Inserts or replaces the task. */
    suspend fun save(task: Task)

    suspend fun delete(id: TaskId)
}
