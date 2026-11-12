package app.prumio.core.domain.matter

import app.prumio.core.domain.task.TaskOverduePolicy
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task
import kotlinx.datetime.LocalDate

/** Which linked task moves a matter forward. Rule: BR-MATTER-005. */
object NextActionPolicy {
    /** Open linked tasks: dated before undated, earliest due date-time first, then oldest. */
    fun openActions(linkedTasks: List<Task>): List<Task> = linkedTasks.filter {
        it.isOpen
    }.sortedWith(compareBy<Task> { it.dueDate == null }.then(TaskOverduePolicy.byDue))

    fun nextAction(linkedTasks: List<Task>): Task? = openActions(linkedTasks).firstOrNull()
}

/** Whether a matter deserves attention, and in which order. Rule: BR-MATTER-010. */
object MatterAttentionPolicy {
    fun needsAttention(matter: Matter, openActions: List<Task>, today: LocalDate): Boolean {
        if (matter.isResolved) return false
        val followUpDue = matter.followUpDate.let { it != null && it <= today }
        val noNextAction = matter.status == MatterStatus.Open && openActions.none { it.isOpen }
        val nextActionDue = NextActionPolicy.nextAction(openActions)?.dueDate.let { it != null && it <= today }
        return followUpDue || noNextAction || nextActionDue
    }

    /** Follow-up date ascending (none last), then least recent activity first. */
    val order: Comparator<Matter> =
        compareBy<Matter>({ it.followUpDate == null }, { it.followUpDate }, { it.lastActivityAt }, { it.id.value })
}
