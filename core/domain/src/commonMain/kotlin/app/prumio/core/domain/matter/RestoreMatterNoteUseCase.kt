package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.model.MatterEntry

/** Restores a deleted note with its identity, text and date intact. Rules: BR-GEN-005, BR-MATTER-008. */
class RestoreMatterNoteUseCase(private val matters: MatterRepository, ids: IdGenerator, time: TimeProvider) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(note: MatterEntry): Outcome<MatterEntry, DomainError> {
        val matter = matters.load(note.matterId).valueOr { return it.asFailure() }
        return storageCall { matters.save(history.touched(matter), listOf(note)) }.map { note }
    }
}
