package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.map
import app.prumio.core.domain.error.DomainError
import app.prumio.core.model.TaskId

/**
 * What a task's completion control does: completes it when [done], reopens it otherwise.
 * Preserves the next-action prompt from [CompleteTaskUseCase]. Rules: BR-TASK-003, BR-MATTER-006.
 */
class SetTaskDoneUseCase(private val complete: CompleteTaskUseCase, private val reopen: ReopenTaskUseCase) {
    suspend operator fun invoke(id: TaskId, done: Boolean): Outcome<ClosedTask, DomainError> =
        if (done) complete(id) else reopen(id).map { ClosedTask(it, matterNeedsNextAction = false) }
}
