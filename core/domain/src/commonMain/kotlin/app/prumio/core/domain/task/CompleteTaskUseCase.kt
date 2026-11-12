package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus

/**
 * Completes an open task. Rules: BR-TASK-003, BR-MATTER-006. Recurring tasks (BR-TASK-005) are
 * handled in phase 5; until then recurrence cannot be stored.
 */
class CompleteTaskUseCase(
    private val tasks: TaskRepository,
    private val matters: MatterRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val writer = TaskWriter(tasks, matters, MatterHistory(ids, time))

    suspend operator fun invoke(id: TaskId): Outcome<ClosedTask, DomainError> =
        closeTask(id, TaskStatus.Completed, MatterEntryKind.ActionCompleted, tasks, matters, writer) { task ->
            val now = time.now()
            task.copy(status = TaskStatus.Completed, completedAt = now, updatedAt = now)
        }
}
