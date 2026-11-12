package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId

/** Makes a task a next action of [matterId], or unlinks it when null. Rules: BR-TASK-008, BR-MATTER-005. */
class LinkTaskToMatterUseCase(
    private val tasks: TaskRepository,
    matters: MatterRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val writer = TaskWriter(tasks, matters, MatterHistory(ids, time))

    suspend operator fun invoke(taskId: TaskId, matterId: MatterId?): Outcome<Task, DomainError> {
        val task =
            storageCall { tasks.get(taskId) }.valueOr { return it.asFailure() } ?: return notFound("Task", taskId.value)
        if (task.matterId == matterId) return writer.save(previous = task, task = task)
        return writer.save(previous = task, task = task.copy(matterId = matterId, updatedAt = time.now()))
    }
}
