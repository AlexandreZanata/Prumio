package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.invalidTransition
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId

/**
 * Sets, moves or clears (null) when to check an unresolved matter again; each change is recorded.
 * Rules: BR-MATTER-009 (the reminder itself arrives in phase 5), BR-MATTER-003.
 */
class SetMatterFollowUpUseCase(
    private val matters: MatterRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(id: MatterId, followUp: FollowUp?): Outcome<Matter, DomainError> {
        val matter = matters.load(id).valueOr { return it.asFailure() }
        if (matter.isResolved) return invalidTransition("BR-MATTER-009", matter.status, "FollowUp")
        if (followUp == null && matter.followUpDate == null) return matter.asSuccess()
        val now = time.now()
        val updated = matter.copy(
            followUpDate = followUp?.date,
            followUpTime = followUp?.time,
            lastActivityAt = now,
            updatedAt = now,
        )
        val entry = if (followUp ==
            null
        ) {
            history.entry(id, MatterEntryKind.FollowUpCleared)
        } else {
            history.followUpSet(id, followUp)
        }
        return storageCall { matters.save(updated, listOf(entry)) }.map { updated }
    }
}
