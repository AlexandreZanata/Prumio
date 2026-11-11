package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskId
import kotlinx.coroutines.flow.first

/** Everything needed to undo the deletion of a matter. */
data class DeletedMatter(val matter: Matter, val history: List<MatterEntry>, val linkedTaskIds: List<TaskId>)

/** Permanently deletes a matter and its history; its tasks become standalone. Rule: BR-GEN-005. */
class DeleteMatterUseCase(private val matters: MatterRepository, private val tasks: TaskRepository) {
    suspend operator fun invoke(id: MatterId): Outcome<DeletedMatter, DomainError> {
        val matter = matters.load(id).valueOr { return it.asFailure() }
        val snapshot = storageCall {
            DeletedMatter(matter, matters.observeHistory(id).first(), tasks.getByMatter(id).map { it.id })
        }.valueOr { return it.asFailure() }
        return storageCall { matters.delete(id) }.map { snapshot }
    }
}
