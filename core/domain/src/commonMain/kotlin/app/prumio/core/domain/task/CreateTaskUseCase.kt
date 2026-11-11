package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId

/** Creates an open task from a draft. Rules: BR-TASK-001, BR-TASK-002, BR-TASK-008, BR-GEN-003, BR-GEN-004. */
class CreateTaskUseCase(
    tasks: TaskRepository,
    matters: MatterRepository,
    private val ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val writer = TaskWriter(tasks, matters, MatterHistory(ids, time))

    suspend operator fun invoke(draft: TaskDraft): Outcome<Task, DomainError> {
        val valid = draft.normalized().valueOr { return it.asFailure() }
        val now = time.now()
        val task = Task(
            id = TaskId(ids.newId()),
            title = valid.title,
            notes = valid.notes,
            dueDate = valid.dueDate,
            dueTime = valid.dueTime,
            recurrence = valid.recurrence,
            matterId = valid.matterId,
            createdAt = now,
            updatedAt = now,
        )
        return writer.save(previous = null, task = task)
    }
}
