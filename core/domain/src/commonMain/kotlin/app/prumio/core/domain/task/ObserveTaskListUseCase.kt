package app.prumio.core.domain.task

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.common.time.today
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Duration.Companion.days

/** The lists of the "All tasks" screen. Rule: BR-TASK-009. */
enum class TaskListFilter {
    /** Open, no due date, newest first. */
    Anytime,

    /** Open, due after today, by due date-time. */
    Upcoming,

    /** Open and overdue (BR-TASK-004), by due date-time. */
    Overdue,

    /** Completed in the last 30 days, most recent first. */
    Completed,

    /** Dismissed, most recent first. */
    Dismissed,
}

/** Rule: BR-TASK-009. "Today" is evaluated when the flow is collected (BR-TIME-003). */
class ObserveTaskListUseCase(private val tasks: TaskRepository, private val time: TimeProvider) {
    operator fun invoke(filter: TaskListFilter): Flow<List<Task>> = when (filter) {
        TaskListFilter.Anytime -> tasks.observeOpenUndated()

        TaskListFilter.Upcoming -> tasks.observeOpen().map { open ->
            val today = time.today()
            open.filter { task -> task.dueDate.let { it != null && it > today } }.sortedWith(TaskOverduePolicy.byDue)
        }

        TaskListFilter.Overdue -> tasks.observeOpenDueOnOrBefore(time.today()).map { due ->
            val now = time.localNow()
            due.filter { TaskOverduePolicy.isOverdue(it, now) }.sortedWith(TaskOverduePolicy.byDue)
        }

        TaskListFilter.Completed -> tasks.observeCompletedSince(time.now() - 30.days)

        TaskListFilter.Dismissed -> tasks.observeDismissed()
    }
}
