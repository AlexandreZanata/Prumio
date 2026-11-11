package app.prumio.core.domain.appointment

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.MatterId

/** An appointment may relate to an existing matter only. */
internal suspend fun requireMatter(matters: MatterRepository, matterId: MatterId?): Outcome<Unit, DomainError> {
    if (matterId == null) return Unit.asSuccess()
    val matter = storageCall { matters.get(matterId) }.valueOr { return it.asFailure() }
    return if (matter == null) notFound("Matter", matterId.value) else Unit.asSuccess()
}
