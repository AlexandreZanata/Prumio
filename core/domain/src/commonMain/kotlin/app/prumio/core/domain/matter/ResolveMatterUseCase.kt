package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.invalidTransition
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskStatus

/** What happens to the open actions of a matter being resolved. */
enum class OpenActionsChoice {
    /** Default: they no longer matter. */
    Dismiss,

    /** Keep them as standalone tasks. */
    Unlink,
}

/**
 * Open/Waiting → Resolved: follow-up and "waiting for" are cleared and open actions are dismissed
 * or unlinked, all in one transaction. Rules: BR-MATTER-002, BR-MATTER-003, BR-MATTER-007.
 */
class ResolveMatterUseCase(
    private val matters: MatterRepository,
    private val tasks: TaskRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(
        id: MatterId,
        openActions: OpenActionsChoice = OpenActionsChoice.Dismiss,
    ): Outcome<Matter, DomainError> {
        val matter = matters.load(id).valueOr { return it.asFailure() }
        if (matter.isResolved) return invalidTransition("BR-MATTER-002", matter.status, MatterStatus.Resolved)
        val now = time.now()
        val open = storageCall { tasks.getByMatter(id) }.valueOr { return it.asFailure() }.filter { it.isOpen }
        val changedTasks = open.map { task ->
            when (openActions) {
                OpenActionsChoice.Dismiss -> task.copy(
                    status = TaskStatus.Dismissed,
                    dismissedAt = now,
                    updatedAt = now,
                )

                OpenActionsChoice.Unlink -> task.copy(matterId = null, updatedAt = now)
            }
        }
        val actionKind = if (openActions ==
            OpenActionsChoice.Dismiss
        ) {
            MatterEntryKind.ActionDismissed
        } else {
            MatterEntryKind.ActionUnlinked
        }
        val entries =
            changedTasks.map { history.entry(id, actionKind, task = it) } +
                history.statusChanged(matter, MatterStatus.Resolved)
        val resolved = matter.copy(
            status = MatterStatus.Resolved,
            waitingFor = null,
            followUpDate = null,
            followUpTime = null,
            resolvedAt = now,
            lastActivityAt = now,
            updatedAt = now,
        )
        return storageCall { matters.save(resolved, entries, changedTasks) }.map { resolved }
    }
}
