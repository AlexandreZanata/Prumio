package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.invalidTransition
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus

/**
 * Reopens a completed or dismissed task. A task linked to a resolved matter is unlinked
 * (docs/domain/state-machines.md#task). Rule: BR-TASK-003.
 */
class ReopenTaskUseCase(
    private val tasks: TaskRepository,
    private val matters: MatterRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val writer = TaskWriter(tasks, matters, MatterHistory(ids, time))

    suspend operator fun invoke(id: TaskId): Outcome<Task, DomainError> {
        val task = storageCall { tasks.get(id) }.valueOr { return it.asFailure() } ?: return notFound("Task", id.value)
        if (task.isOpen) return invalidTransition("BR-TASK-003", task.status, TaskStatus.Open)
        val matterId = task.matterId
        val matterResolved = matterId != null &&
            storageCall { matters.get(matterId) }.valueOr { return it.asFailure() }?.isResolved == true
        val reopened = task.copy(
            status = TaskStatus.Open,
            completedAt = null,
            dismissedAt = null,
            matterId = if (matterResolved) null else matterId,
            updatedAt = time.now(),
        )
        return writer.save(previous = task, task = reopened)
    }
}
