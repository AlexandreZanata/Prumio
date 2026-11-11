package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterId

/** Edits title and description. Rules: BR-MATTER-001, BR-MATTER-012. */
class UpdateMatterUseCase(private val matters: MatterRepository, private val time: TimeProvider) {
    suspend operator fun invoke(id: MatterId, draft: MatterDraft): Outcome<Matter, DomainError> {
        val valid = draft.normalized().valueOr { return it.asFailure() }
        val matter = matters.load(id).valueOr { return it.asFailure() }
        val now = time.now()
        val updated = matter.copy(
            title = valid.title,
            description = valid.description,
            lastActivityAt = now,
            updatedAt = now,
        )
        return storageCall { matters.save(updated) }.map { updated }
    }
}
