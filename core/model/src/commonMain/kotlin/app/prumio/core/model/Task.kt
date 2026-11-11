package app.prumio.core.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

/** Lifecycle of a task (docs/domain/state-machines.md#task). */
enum class TaskStatus {
    Open,
    Completed,
    Dismissed,
}

/** A concrete action: "what do I need to do?" (docs/domain/model.md#task). */
data class Task(
    val id: TaskId,
    val title: String,
    val notes: String? = null,
    val status: TaskStatus = TaskStatus.Open,
    val dueDate: LocalDate? = null,
    val dueTime: LocalTime? = null,
    val recurrence: TaskRecurrence? = null,
    val matterId: MatterId? = null,
    val completedAt: Instant? = null,
    val dismissedAt: Instant? = null,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isOpen: Boolean get() = status == TaskStatus.Open

    val hasDueTime: Boolean get() = dueTime != null

    val isRecurring: Boolean get() = recurrence != null
}

/** How the next due date of a recurring task is computed (BR-REC-005). */
enum class RecurrenceBasis {
    FromDueDate,
    FromCompletion,
}

data class TaskRecurrence(val rule: RecurrenceRule, val basis: RecurrenceBasis = RecurrenceBasis.FromDueDate)
