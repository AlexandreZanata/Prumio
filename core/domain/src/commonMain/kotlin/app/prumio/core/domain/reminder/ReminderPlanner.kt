package app.prumio.core.domain.reminder

import app.prumio.core.domain.appointment.AppointmentTimePolicy
import app.prumio.core.model.AppointmentTiming
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

/** Pure projection. Rules: BR-REM-001, BR-REM-003, BR-REM-004, BR-REM-005, BR-REM-007, BR-REM-009. */
class ReminderPlanner {
    fun plan(input: PlanningInput): List<PlannedAlarm> = candidates(input).mapNotNull { original ->
        val snooze = input.snoozes[original.key]
        if (original.key in input.delivered && snooze == null) return@mapNotNull null
        val trigger = snooze ?: original.triggerAt
        when {
            trigger > input.now + 14.days -> null
            trigger < input.now && input.now - trigger >= 12.hours -> null
            trigger < input.now -> original.copy(triggerAt = input.now, priority = AlarmPriority.Deferrable)
            else -> original.copy(triggerAt = trigger)
        }
    }.sortedWith(compareBy<PlannedAlarm> { it.triggerAt }.thenBy { it.key.value }).take(200)

    /** BR-REM-007: anchor edits, deletion and closure invalidate the old snooze identity. */
    fun isCurrent(key: AlarmKey, input: PlanningInput): Boolean = candidates(input).any { it.key == key }

    private fun candidates(input: PlanningInput): List<PlannedAlarm> = buildList {
        input.tasks.filter { it.isOpen && it.dueDate != null }.forEach { task ->
            val anchor = LocalDateTime(
                requireNotNull(task.dueDate),
                task.dueTime ?: input.settings.defaultReminderTime,
            ).toInstant(input.zone)
            task.reminders.forEach {
                add(
                    alarm(
                        AlarmKind.Task,
                        task.id.value,
                        anchor,
                        it.offsetMinutes,
                        task.dueTime != null,
                    ),
                )
            }
        }
        input.appointments.forEach { a ->
            val anchor = when (val timing = a.timing) {
                is AppointmentTiming.AllDay -> LocalDateTime(
                    timing.startDate,
                    input.settings.defaultReminderTime,
                ).toInstant(input.zone)

                is AppointmentTiming.Timed -> AppointmentTimePolicy.start(a, input.zone)
            }
            a.reminders.forEach { add(alarm(AlarmKind.Appointment, a.id.value, anchor, it.offsetMinutes, !a.isAllDay)) }
        }
        input.matters.filter { !it.isResolved && it.followUpDate != null }.forEach { m ->
            val anchor = LocalDateTime(
                requireNotNull(m.followUpDate),
                m.followUpTime ?: input.settings.defaultReminderTime,
            ).toInstant(input.zone)
            add(alarm(AlarmKind.Matter, m.id.value, anchor, 0, m.followUpTime != null))
        }
    }

    private fun alarm(kind: AlarmKind, id: String, anchor: Instant, offset: Int, explicit: Boolean): PlannedAlarm =
        PlannedAlarm(
            key = AlarmKey("${kind.name.lowercase()}:$id:${anchor.epochSeconds.floorDiv(60)}:$offset"),
            triggerAt = anchor - offset.minutes,
            kind = kind,
            itemId = id,
            anchor = anchor,
            priority = if (explicit) AlarmPriority.TimeCritical else AlarmPriority.Deferrable,
        )
}
