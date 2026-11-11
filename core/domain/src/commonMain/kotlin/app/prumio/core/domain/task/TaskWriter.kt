package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.Task

/**
 * The single place where tasks are persisted together with the history of their matter.
 *
 * Rules: BR-TASK-008 (link/unlink appends ActionLinked/ActionUnlinked), BR-MATTER-003 (same
 * transaction as the change), BR-MATTER-012 (linked changes touch the matter's activity).
 */
internal class TaskWriter(
    private val tasks: TaskRepository,
    private val matters: MatterRepository,
    private val history: MatterHistory,
) {
    /** Saves [task] (previously [previous], null when new); [statusEntry] is recorded on its matter. */
    suspend fun save(previous: Task?, task: Task, statusEntry: MatterEntryKind? = null): Outcome<Task, DomainError> {
        val matterId = task.matterId
        val matter = if (matterId == null) {
            null
        } else {
            storageCall { matters.get(matterId) }.valueOr { return it.asFailure() }
                ?: return notFound("Matter", matterId.value)
        }
        val unlinkedFrom = previous?.matterId?.takeIf { it != matterId }
        val oldMatter = unlinkedFrom?.let { id -> storageCall { matters.get(id) }.valueOr { return it.asFailure() } }
        val kinds = listOfNotNull(
            MatterEntryKind.ActionLinked.takeIf { previous?.matterId != matterId },
            statusEntry,
        )
        return storageCall {
            if (matter != null && kinds.isNotEmpty()) {
                matters.save(
                    history.touched(matter),
                    kinds.map {
                        history.entry(matter.id, it, task = task)
                    },
                    listOf(task),
                )
            } else {
                tasks.save(task)
            }
            if (oldMatter != null) {
                matters.save(
                    history.touched(oldMatter),
                    listOf(history.entry(oldMatter.id, MatterEntryKind.ActionUnlinked, task = task)),
                )
            }
        }.map { task }
    }
}
