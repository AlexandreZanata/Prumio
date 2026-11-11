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
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId

/** Replaces the editable fields of a task, in any state. Rules: BR-TASK-001, BR-TASK-002, BR-TASK-008, BR-GEN-004. */
class UpdateTaskUseCase(
    private val tasks: TaskRepository,
    matters: MatterRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val writer = TaskWriter(tasks, matters, MatterHistory(ids, time))

    suspend operator fun invoke(id: TaskId, draft: TaskDraft): Outcome<Task, DomainError> {
        val valid = draft.normalized().valueOr { return it.asFailure() }
        val task = storageCall { tasks.get(id) }.valueOr { return it.asFailure() } ?: return notFound("Task", id.value)
        val updated = task.copy(
            title = valid.title,
            notes = valid.notes,
            dueDate = valid.dueDate,
            dueTime = valid.dueTime,
            recurrence = valid.recurrence,
            matterId = valid.matterId,
            updatedAt = time.now(),
        )
        return writer.save(previous = task, task = updated)
    }
}
