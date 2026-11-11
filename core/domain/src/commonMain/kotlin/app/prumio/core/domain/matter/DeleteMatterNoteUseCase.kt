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
import app.prumio.core.model.MatterEntryId

/** Deletes a note (system entries cannot be deleted) and returns it for Undo. Rules: BR-MATTER-008, BR-GEN-005. */
class DeleteMatterNoteUseCase(private val matters: MatterRepository, ids: IdGenerator, time: TimeProvider) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(entryId: MatterEntryId): Outcome<MatterEntry, DomainError> {
        val entry = editableEntry(matters, entryId).valueOr { return it.asFailure() }
        val matter = matters.load(entry.matterId).valueOr { return it.asFailure() }
        return storageCall { matters.deleteEntry(history.touched(matter), entryId) }.map { entry }
    }
}
