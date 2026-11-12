package app.prumio.core.domain.matter

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.domain.text.optionalText
import app.prumio.core.domain.text.requiredText
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** User input for creating or editing a matter. Only [title] is required (BR-GEN-001). */
data class MatterDraft(val title: String, val description: String? = null)

/** Normalizes and validates a draft. Rules: BR-MATTER-001 (title), BR-GEN-002. */
fun MatterDraft.normalized(): Outcome<MatterDraft, DomainError> {
    val title = requiredText(title, field = "title", max = TextLimits.TITLE, ruleId = "BR-MATTER-001")
        .valueOr { return it.asFailure() }
    val description = optionalText(description, field = "description", max = TextLimits.NOTES)
        .valueOr { return it.asFailure() }
    return MatterDraft(title = title, description = description).asSuccess()
}

/** A follow-up date with an optional time (BR-MATTER-009). */
data class FollowUp(val date: LocalDate, val time: LocalTime? = null)
