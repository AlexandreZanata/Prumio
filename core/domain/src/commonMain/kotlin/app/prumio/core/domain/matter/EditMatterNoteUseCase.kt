package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.text.ValidationReasons
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import kotlinx.datetime.LocalDate

/** Edits a note's text and date. System entries cannot be edited. Rules: BR-MATTER-008, BR-MATTER-012. */
class EditMatterNoteUseCase(private val matters: MatterRepository, ids: IdGenerator, private val time: TimeProvider) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(
        entryId: MatterEntryId,
        text: String,
        occurredOn: LocalDate,
    ): Outcome<MatterEntry, DomainError> {
        val note = noteText(text).valueOr { return it.asFailure() }
        val date = noteDate(occurredOn, time.today()).valueOr { return it.asFailure() }
        val entry = editableEntry(matters, entryId).valueOr { return it.asFailure() }
        val matter = matters.load(entry.matterId).valueOr { return it.asFailure() }
        val edited = entry.copy(text = note, occurredOn = date, updatedAt = time.now())
        return storageCall { matters.save(history.touched(matter), listOf(edited)) }.map { edited }
    }
}

/** Loads an entry that the user may change: only notes (BR-MATTER-008). */
internal suspend fun editableEntry(
    matters: MatterRepository,
    entryId: MatterEntryId,
): Outcome<MatterEntry, DomainError> {
    val entry = storageCall { matters.getEntry(entryId) }.valueOr { return it.asFailure() }
        ?: return notFound("MatterEntry", entryId.value)
    if (!entry.isEditable) {
        return DomainError.Validation(
            "BR-MATTER-008",
            field = "entry",
            reason = ValidationReasons.NOT_EDITABLE,
        ).asFailure()
    }
    return Outcome.Success(entry)
}
