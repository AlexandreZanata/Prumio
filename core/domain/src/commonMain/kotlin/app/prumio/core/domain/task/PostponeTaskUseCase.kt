package app.prumio.core.domain.task

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.invalidTransition
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

/** Where a postponed task goes. */
sealed interface PostponeOption {
    data object Tomorrow : PostponeOption

    /** Today + 7 days. */
    data object NextWeek : PostponeOption

    data class To(val date: LocalDate) : PostponeOption
}

/**
 * Moves the due date of an open task, keeping its time (and reminder offsets). This changes the
 * task; snoozing a notification does not. Rule: BR-TASK-007.
 */
class PostponeTaskUseCase(
    private val tasks: TaskRepository,
    matters: MatterRepository,
    ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val writer = TaskWriter(tasks, matters, MatterHistory(ids, time))

    suspend operator fun invoke(id: TaskId, option: PostponeOption): Outcome<Task, DomainError> {
        val task = storageCall { tasks.get(id) }.valueOr { return it.asFailure() } ?: return notFound("Task", id.value)
        if (!task.isOpen) return invalidTransition("BR-TASK-007", task.status, "Postponed")
        val today = time.today()
        val date = when (option) {
            PostponeOption.Tomorrow -> today.plus(DatePeriod(days = 1))
            PostponeOption.NextWeek -> today.plus(DatePeriod(days = 7))
            is PostponeOption.To -> option.date
        }
        return writer.save(previous = task, task = task.copy(dueDate = date, updatedAt = time.now()))
    }
}
