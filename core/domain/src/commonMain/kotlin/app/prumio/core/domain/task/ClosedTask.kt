package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.invalidTransition
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus

/**
 * Result of completing or dismissing a task. [matterNeedsNextAction] is true when the task was the
 * last open action of an open matter, so the UI asks "What's the next action?" (BR-MATTER-006).
 */
data class ClosedTask(val task: Task, val matterNeedsNextAction: Boolean) {
    /** The matter whose next-action question the UI should open (BR-MATTER-006). */
    val nextActionMatterId: MatterId? get() = task.matterId.takeIf { matterNeedsNextAction }
}

/** Shared transition Open → Completed/Dismissed (BR-TASK-003, BR-MATTER-006). */
internal suspend fun closeTask(
    id: TaskId,
    to: TaskStatus,
    entry: MatterEntryKind,
    tasks: TaskRepository,
    matters: MatterRepository,
    writer: TaskWriter,
    close: (Task) -> Task,
): Outcome<ClosedTask, DomainError> {
    val task = storageCall { tasks.get(id) }.valueOr { return it.asFailure() } ?: return notFound("Task", id.value)
    if (!task.isOpen) return invalidTransition("BR-TASK-003", task.status, to)
    val matterId = task.matterId
    val needsNextAction = if (matterId == null) {
        false
    } else {
        val matter = storageCall { matters.get(matterId) }.valueOr { return it.asFailure() }
        val otherOpen = storageCall { tasks.getByMatter(matterId) }.valueOr { return it.asFailure() }
            .any { it.isOpen && it.id != task.id }
        matter?.status == MatterStatus.Open && !otherOpen
    }
    return writer.save(previous = task, task = close(task), statusEntry = entry).map { ClosedTask(it, needsNextAction) }
}
