package app.prumio.core.domain.reminder

import app.prumio.core.domain.error.storageCall
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.withLock

/** Revalidates before showing, then records delivery; ignored notifications never alter an item. Rules: BR-REM-005, BR-REM-006, BR-REM-009, BR-GEN-006. */
class DeliverAlarmUseCase(
    private val observeInputs: ObserveReminderInputsUseCase,
    private val planner: ReminderPlanner,
    private val factory: NotificationContentFactory,
    private val presenter: NotificationPresenter,
    private val delivered: DeliveredAlarmStore,
    private val snoozes: SnoozeStore,
    private val access: NotificationAccess,
    private val trigger: ReminderReconciliationTrigger,
    private val coordinator: ReminderCoordinator,
) {
    suspend operator fun invoke(key: AlarmKey) = coordinator.delivery.withLock {
        val result = storageCall {
            val input = observeInputs().first()
            val alarm = planner.plan(input).firstOrNull { it.key == key && it.triggerAt <= input.now }
            if (alarm != null && access.status().notificationsAllowed) {
                factory.create(alarm, input)?.let { presenter.show(it) }
                delivered.markDelivered(key, input.now)
                snoozes.remove(key)
            }
        }
        trigger.request()
        result
    }
}
