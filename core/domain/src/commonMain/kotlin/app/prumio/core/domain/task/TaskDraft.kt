package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.asSuccess
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.domain.text.optionalText
import app.prumio.core.domain.text.requiredText
import app.prumio.core.model.MatterId
import app.prumio.core.model.TaskRecurrence
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

/** User input for creating or editing a task. Only [title] is required (BR-GEN-001). */
data class TaskDraft(
    val title: String,
    val notes: String? = null,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val recurrence: TaskRecurrence? = null,
    val matterId: MatterId? = null,
)

/**
 * Normalizes and validates a draft.
 *
 * Rules: BR-TASK-001 (title), BR-GEN-002 (trim, limits), BR-TASK-002 (removing the date removes
 * time and recurrence).
 */
fun TaskDraft.normalized(): Outcome<TaskDraft, DomainError> {
    val title = requiredText(title, field = "title", max = TextLimits.TITLE, ruleId = "BR-TASK-001")
        .valueOr { return it.asFailure() }
    val notes = optionalText(notes, field = "notes", max = TextLimits.NOTES).valueOr { return it.asFailure() }
    return copy(
        title = title,
        notes = notes,
        dueTime = dueTime.takeIf { dueDate != null },
        recurrence = recurrence.takeIf { dueDate != null },
    ).asSuccess()
}
