package app.prumio.core.testing

import app.prumio.core.domain.reminder.CompleteTaskFromNotificationUseCase
import app.prumio.core.domain.reminder.DeliverAlarmUseCase
import app.prumio.core.domain.reminder.GetReminderReliabilityUseCase
import app.prumio.core.domain.reminder.NotificationContentFactory
import app.prumio.core.domain.reminder.ObserveReminderInputsUseCase
import app.prumio.core.domain.reminder.ReconcileRemindersUseCase
import app.prumio.core.domain.reminder.ReminderCoordinator
import app.prumio.core.domain.reminder.ReminderPlanner
import app.prumio.core.domain.reminder.SnoozeAlarmUseCase

/** Real reminder operations wired to controllable platform and storage ports. */
class ReminderTestEnvironment(val domain: TestUseCases = TestUseCases()) {
    val scheduled = FakeScheduledAlarmStore()
    val snoozes = FakeSnoozeStore()
    val delivered = FakeDeliveredAlarmStore()
    val scheduler = FakeAlarmScheduler()
    val presenter = FakeNotificationPresenter()
    val trigger = FakeReminderReconciliationTrigger()
    val planner = ReminderPlanner()
    val coordinator = ReminderCoordinator()
    val inputs get() =
        ObserveReminderInputsUseCase(
            domain.repos.tasks,
            domain.repos.appointments,
            domain.repos.matters,
            domain.repos.settings,
            snoozes,
            delivered,
            domain.time,
        )
    val reconcile get() =
        ReconcileRemindersUseCase(
            inputs,
            planner,
            scheduled,
            snoozes,
            delivered,
            scheduler,
            coordinator,
            domain.notificationAccess,
        )
    val deliver get() =
        DeliverAlarmUseCase(
            inputs,
            planner,
            NotificationContentFactory(),
            presenter,
            delivered,
            snoozes,
            domain.notificationAccess,
            trigger,
            coordinator,
        )
    val snooze get() =
        SnoozeAlarmUseCase(
            inputs,
            planner,
            snoozes,
            presenter,
            trigger,
        )
    val reliability get() =
        GetReminderReliabilityUseCase(
            domain.notificationAccess,
            domain.repos.settings,
            domain.repos.reminders,
            domain.repos.matters,
        )
    val complete get() =
        CompleteTaskFromNotificationUseCase(
            domain.completeTask,
            presenter,
            trigger,
        )
}
