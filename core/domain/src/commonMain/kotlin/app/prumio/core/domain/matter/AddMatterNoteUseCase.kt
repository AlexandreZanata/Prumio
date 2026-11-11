package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.domain.text.ValidationReasons
import app.prumio.core.domain.text.requiredText
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import kotlinx.datetime.LocalDate

/** Adds a dated note to the history (today by default; past allowed, future rejected). Rules: BR-MATTER-008, BR-MATTER-012. */
class AddMatterNoteUseCase(private val matters: MatterRepository, ids: IdGenerator, private val time: TimeProvider) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(
        id: MatterId,
        text: String,
        occurredOn: LocalDate? = null,
    ): Outcome<MatterEntry, DomainError> {
        val note = noteText(text).valueOr { return it.asFailure() }
        val date = noteDate(occurredOn, time.today()).valueOr { return it.asFailure() }
        val matter = matters.load(id).valueOr { return it.asFailure() }
        val entry = history.entry(id, MatterEntryKind.Note, text = note).copy(occurredOn = date)
        return storageCall { matters.save(history.touched(matter), listOf(entry)) }.map { entry }
    }
}

/** Note rules shared by add and edit (BR-MATTER-008). */
internal fun noteText(text: String) =
    requiredText(text, field = "text", max = TextLimits.MATTER_NOTE, ruleId = "BR-MATTER-008")

internal fun noteDate(date: LocalDate?, today: LocalDate): Outcome<LocalDate, DomainError> = when {
    date == null -> today.asSuccess()

    date > today -> DomainError.Validation(
        "BR-MATTER-008",
        field = "occurredOn",
        reason = ValidationReasons.FUTURE_DATE,
    ).asFailure()

    else -> date.asSuccess()
}
