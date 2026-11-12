package app.prumio.core.domain.reminder

import app.prumio.core.domain.error.storageCall
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.days

/** Diffs the projection, preserving request-code identities. Force restores OS state after boot/update/permission changes. Rules: BR-REM-001, BR-REM-002, BR-REM-007. */
class ReconcileRemindersUseCase(
    private val observeInputs: ObserveReminderInputsUseCase,
    private val planner: ReminderPlanner,
    private val scheduled: ScheduledAlarmStore,
    private val snoozes: SnoozeStore,
    private val delivered: DeliveredAlarmStore,
    private val scheduler: AlarmScheduler,
    private val coordinator: ReminderCoordinator,
    private val access: NotificationAccess,
) {
    suspend operator fun invoke(forceReschedule: Boolean = false) = coordinator.reconciliation.withLock {
        storageCall {
            val input = observeInputs().first()
            input.snoozes.keys.filterNot { planner.isCurrent(it, input) }.forEach { snoozes.remove(it) }
            val desired = planner.plan(input).filter {
                access.status().notificationsAllowed || it.triggerAt > input.now
            }
            val previous = scheduled.all().associateBy { it.key }
            val desiredKeys = desired.map { it.key }.toSet()
            previous.values.filter { it.key !in desiredKeys }.forEach { scheduler.cancel(it.key, it.requestCode) }
            val used = previous.values.map { it.requestCode }.toMutableSet()
            val next = desired.map { alarm ->
                val before = previous[alarm.key]
                val code =
                    before?.requestCode ?: generateSequence(1) { it + 1 }.first { it !in used }.also { used += it }
                if (forceReschedule || before?.triggerAt != alarm.triggerAt) scheduler.schedule(alarm, code)
                ScheduledAlarm(alarm.key, alarm.triggerAt, code)
            }
            scheduled.replaceAll(next)
            delivered.pruneBefore(input.now - 30.days)
        }
    }
}
