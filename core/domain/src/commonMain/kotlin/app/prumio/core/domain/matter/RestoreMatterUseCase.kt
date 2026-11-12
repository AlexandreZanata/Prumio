package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.Matter

/** Undo of a matter deletion: matter, history and links of tasks that still exist. Rule: BR-GEN-005. */
class RestoreMatterUseCase(private val matters: MatterRepository, private val tasks: TaskRepository) {
    suspend operator fun invoke(deleted: DeletedMatter): Outcome<Matter, DomainError> {
        val relinked = storageCall {
            deleted.linkedTaskIds.mapNotNull { tasks.get(it) }
        }.valueOr { return it.asFailure() }
            .map { it.copy(matterId = deleted.matter.id) }
        return storageCall { matters.save(deleted.matter, deleted.history, relinked) }.map { deleted.matter }
    }
}
