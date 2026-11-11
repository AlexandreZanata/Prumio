package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId

/** Permanently deletes a task and returns it so the UI can offer Undo. Rule: BR-GEN-005. */
class DeleteTaskUseCase(private val tasks: TaskRepository) {
    suspend operator fun invoke(id: TaskId): Outcome<Task, DomainError> {
        val task = storageCall { tasks.get(id) }.valueOr { return it.asFailure() } ?: return notFound("Task", id.value)
        return storageCall { tasks.delete(id) }.map { task }
    }
}
