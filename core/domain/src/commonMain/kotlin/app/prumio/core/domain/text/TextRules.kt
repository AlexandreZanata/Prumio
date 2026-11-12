package app.prumio.core.domain.text

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.domain.error.DomainError

/** Maximum lengths of user text (BR-GEN-002). */
object TextLimits {
    const val TITLE = 500
    const val INBOX_TEXT = 2_000
    const val NOTES = 10_000
    const val LOCATION = 500
    const val WAITING_FOR = 500
    const val MATTER_NOTE = 5_000
}

/** Values of `DomainError.Validation.reason`, mapped to messages by the UI. */
object ValidationReasons {
    const val BLANK = "blank"
    const val TOO_LONG = "too_long"
    const val END_BEFORE_START = "end_before_start"
    const val FUTURE_DATE = "future_date"
    const val NOT_EDITABLE = "not_editable"
}

/**
 * Trims [value] and requires it to be non-blank (rule [ruleId]) and at most [max] characters
 * (BR-GEN-002).
 */
fun requiredText(value: String, field: String, max: Int, ruleId: String): Outcome<String, DomainError.Validation> {
    val trimmed = value.trim()
    return when {
        trimmed.isEmpty() -> DomainError.Validation(ruleId, field, ValidationReasons.BLANK).asFailure()
        trimmed.length > max -> DomainError.Validation("BR-GEN-002", field, ValidationReasons.TOO_LONG).asFailure()
        else -> trimmed.asSuccess()
    }
}

/** Trims [value]; blank becomes null (BR-GEN-002). Longer than [max] characters is rejected. */
fun optionalText(value: String?, field: String, max: Int): Outcome<String?, DomainError.Validation> {
    val trimmed = value?.trim()?.takeIf { it.isNotEmpty() }
    return if (trimmed != null && trimmed.length > max) {
        DomainError.Validation("BR-GEN-002", field, ValidationReasons.TOO_LONG).asFailure()
    } else {
        trimmed.asSuccess()
    }
}
