package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterId

/** Loads a matter or fails with NotFound. */
internal suspend fun MatterRepository.load(id: MatterId): Outcome<Matter, DomainError> {
    val matter = storageCall { get(id) }.valueOr { return it.asFailure() } ?: return notFound("Matter", id.value)
    return matter.asSuccess()
}
