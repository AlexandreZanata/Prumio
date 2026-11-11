package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId

/** Creates an open matter with a Created history entry. Rules: BR-MATTER-001, BR-MATTER-003, BR-MATTER-012. */
class CreateMatterUseCase(
    private val matters: MatterRepository,
    private val ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(draft: MatterDraft): Outcome<Matter, DomainError> {
        val valid = draft.normalized().valueOr { return it.asFailure() }
        val now = time.now()
        val matter = Matter(
            id = MatterId(ids.newId()),
            title = valid.title,
            description = valid.description,
            lastActivityAt = now,
            createdAt = now,
            updatedAt = now,
        )
        return storageCall {
            matters.save(matter, listOf(history.entry(matter.id, MatterEntryKind.Created)))
        }.map { matter }
    }
}
