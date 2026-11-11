package app.prumio.core.domain.task

import app.prumio.core.model.Task
import kotlinx.datetime.LocalDateTime

/** When an open task is overdue. Rule: BR-TASK-004. */
object TaskOverduePolicy {
    fun isOverdue(task: Task, now: LocalDateTime): Boolean {
        val dueDate = task.dueDate ?: return false
        if (!task.isOpen) return false
        val dueTime = task.dueTime
        return dueDate < now.date || (dueDate == now.date && dueTime != null && dueTime < now.time)
    }

    /** Order of due tasks: by due date, then timed by time, untimed last within a day, then creation. */
    val byDue: Comparator<Task> =
        compareBy<Task>({ it.dueDate }, { it.dueTime == null }, { it.dueTime }, { it.createdAt })
}
