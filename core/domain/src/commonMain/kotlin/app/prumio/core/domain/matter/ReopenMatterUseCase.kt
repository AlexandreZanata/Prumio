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
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus

/** Resolved → Open. Rules: BR-MATTER-002, BR-MATTER-007. */
class ReopenMatterUseCase(private val matters: MatterRepository, ids: IdGenerator, private val time: TimeProvider) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(id: MatterId): Outcome<Matter, DomainError> {
        val matter = matters.load(id).valueOr { return it.asFailure() }
        if (!matter.isResolved) return invalidTransition("BR-MATTER-002", matter.status, MatterStatus.Open)
        val now = time.now()
        val open = matter.copy(status = MatterStatus.Open, resolvedAt = null, lastActivityAt = now, updatedAt = now)
        return storageCall { matters.save(open, listOf(history.statusChanged(matter, MatterStatus.Open))) }.map { open }
    }
}
