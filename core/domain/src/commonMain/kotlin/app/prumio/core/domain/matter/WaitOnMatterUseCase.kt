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
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.domain.text.optionalText
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus

/**
 * Open → Waiting, optionally saying for whom/what and when to check again (both optional).
 * Rules: BR-MATTER-002, BR-MATTER-003, BR-MATTER-004, BR-MATTER-009.
 */
class WaitOnMatterUseCase(private val matters: MatterRepository, ids: IdGenerator, private val time: TimeProvider) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(
        id: MatterId,
        waitingFor: String? = null,
        followUp: FollowUp? = null,
    ): Outcome<Matter, DomainError> {
        val text = optionalText(waitingFor, field = "waitingFor", max = TextLimits.WAITING_FOR).valueOr {
            return it.asFailure()
        }
        val matter = matters.load(id).valueOr { return it.asFailure() }
        if (matter.status !=
            MatterStatus.Open
        ) {
            return invalidTransition("BR-MATTER-002", matter.status, MatterStatus.Waiting)
        }
        val now = time.now()
        val waiting = matter.copy(
            status = MatterStatus.Waiting,
            waitingFor = text,
            followUpDate = followUp?.date ?: matter.followUpDate,
            followUpTime = if (followUp != null) followUp.time else matter.followUpTime,
            lastActivityAt = now,
            updatedAt = now,
        )
        val entries = listOfNotNull(
            history.statusChanged(matter, MatterStatus.Waiting),
            followUp?.let { history.followUpSet(matter.id, it) },
        )
        return storageCall { matters.save(waiting, entries) }.map { waiting }
    }
}
