package app.prumio.core.domain.today

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.domain.appointment.AppointmentTimePolicy
import app.prumio.core.domain.inbox.InboxRepository
import app.prumio.core.domain.matter.MatterAttentionPolicy
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.domain.matter.MatterSummary
import app.prumio.core.domain.matter.NextActionPolicy
import app.prumio.core.domain.task.TaskOverduePolicy
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.Appointment
import app.prumio.core.model.Matter
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus

/** The next appointment and the local date-time it starts at. */
data class UpcomingAppointment(val appointment: Appointment, val start: LocalDateTime)

/** Everything the Today screen shows (docs/product/screens.md#today). */
data class TodaySnapshot(
    val date: LocalDate,
    /** Overdue first (oldest due first) — BR-TODAY-003. */
    val overdueTasks: List<Task>,
    /** Due today, timed by time then untimed by creation — BR-TODAY-003. */
    val dueTodayTasks: List<Task>,
    val doneTodayCount: Int,
    /** All-day appointments of today, listed above the next appointment (BR-TODAY-004). */
    val allDayToday: List<Appointment>,
    val appointmentCountToday: Int,
    val nextAppointment: UpcomingAppointment?,
    /** Up to [ATTENTION_LIMIT] matters (BR-TODAY-005). */
    val attentionMatters: List<MatterSummary>,
    val attentionMatterCount: Int,
    val inboxCount: Int,
) {
    /** BR-TODAY-002: open tasks due today or overdue. */
    val taskCount: Int get() = overdueTasks.size + dueTodayTasks.size

    companion object {
        const val ATTENTION_LIMIT = 5
        const val NEXT_APPOINTMENT_DAYS = 7
    }
}

/**
 * Live Today snapshot. "Today" is fixed when the flow starts; the screen restarts it on day
 * change (BR-TODAY-007, phase 3). Rules: BR-TODAY-002, BR-TODAY-003, BR-TODAY-004, BR-TODAY-005,
 * BR-TASK-004, BR-APPT-005, BR-MATTER-010, BR-TIME-003.
 */
class ObserveTodayUseCase(
    private val tasks: TaskRepository,
    private val appointments: AppointmentRepository,
    private val matters: MatterRepository,
    private val inbox: InboxRepository,
    private val time: TimeProvider,
) {
    operator fun invoke(): Flow<TodaySnapshot> {
        val today = time.localNow().date
        val zone = time.timeZone()
        val horizon = today.plus(DatePeriod(days = TodaySnapshot.NEXT_APPOINTMENT_DAYS + 1))
        val taskData = combine(
            tasks.observeOpenDueOnOrBefore(today),
            tasks.observeCompletedSince(today.atStartOfDayIn(zone)),
            tasks.observeOpen(),
        ) { due, done, open -> Triple(due, done.size, open) }
        return combine(
            taskData,
            appointments.observeOverlapping(today.plus(DatePeriod(days = -1)), horizon),
            matters.observeUnresolved(),
            inbox.observeCount(),
        ) { (due, doneCount, open), candidates, unresolved, inboxCount ->
            val now = time.localNow()
            val (overdue, dueToday) = due.partition { TaskOverduePolicy.isOverdue(it, now) }
            val todays = candidates.filter { AppointmentTimePolicy.occursOn(it, today, zone) }
            val attention = attentionMatters(unresolved, open, today)
            TodaySnapshot(
                date = today,
                overdueTasks = overdue.sortedWith(TaskOverduePolicy.byDue),
                dueTodayTasks = dueToday.sortedWith(
                    compareBy<Task> {
                        it.dueTime == null
                    }.then(TaskOverduePolicy.byDue),
                ),
                doneTodayCount = doneCount,
                allDayToday = todays.filter { it.isAllDay }.sortedBy { it.title },
                appointmentCountToday = todays.size,
                nextAppointment = nextAppointment(candidates, today),
                attentionMatters = attention.take(TodaySnapshot.ATTENTION_LIMIT),
                attentionMatterCount = attention.size,
                inboxCount = inboxCount,
            )
        }
    }

    /** BR-TODAY-004: first timed appointment still ahead (or ongoing) today, else the next one within 7 days. */
    private fun nextAppointment(candidates: List<Appointment>, today: LocalDate): UpcomingAppointment? {
        val zone = time.timeZone()
        val now = time.now()
        val lastDay = today.plus(DatePeriod(days = TodaySnapshot.NEXT_APPOINTMENT_DAYS))
        return candidates
            .filter { appointment ->
                val start = AppointmentTimePolicy.localStart(appointment, zone).date
                if (appointment.isAllDay) {
                    start > today && start <= lastDay
                } else {
                    val end = AppointmentTimePolicy.end(appointment, zone)
                    val stillAhead = if (end ==
                        AppointmentTimePolicy.start(appointment, zone)
                    ) {
                        end >= now
                    } else {
                        end > now
                    }
                    stillAhead && start <= lastDay
                }
            }
            .minByOrNull { AppointmentTimePolicy.start(it, zone) }
            ?.let { UpcomingAppointment(it, AppointmentTimePolicy.localStart(it, zone)) }
    }

    private fun attentionMatters(
        unresolved: List<Matter>,
        openTasks: List<Task>,
        today: LocalDate,
    ): List<MatterSummary> {
        val actionsByMatter = openTasks.groupBy { it.matterId }
        return unresolved
            .map { matter ->
                val actions = actionsByMatter[matter.id].orEmpty()
                MatterSummary(
                    matter,
                    NextActionPolicy.nextAction(actions),
                    MatterAttentionPolicy.needsAttention(matter, actions, today),
                )
            }
            .filter { it.needsAttention }
            .sortedWith(compareBy(MatterAttentionPolicy.order) { it.matter })
    }
}
