package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Task

/** Undo of a deletion: same id and fields; unlinked if its matter is gone. Rule: BR-GEN-005. */
class RestoreTaskUseCase(private val tasks: TaskRepository, private val matters: MatterRepository) {
    suspend operator fun invoke(task: Task): Outcome<Task, DomainError> {
        val matterId = task.matterId
        val matterExists =
            matterId == null || storageCall { matters.get(matterId) }.valueOr { return it.asFailure() } != null
        val restored = if (matterExists) task else task.copy(matterId = null)
        return storageCall { tasks.save(restored) }.map { restored }
    }
}
