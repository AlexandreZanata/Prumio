package app.prumio.core.notifications.di

import app.prumio.core.domain.reminder.CompleteTaskFromNotificationUseCase
import app.prumio.core.domain.reminder.DeliverAlarmUseCase
import app.prumio.core.domain.reminder.ReconcileRemindersUseCase
import org.koin.core.context.GlobalContext

internal val deliverAlarm: DeliverAlarmUseCase get() = GlobalContext.get().get()
internal val reconcileReminders: ReconcileRemindersUseCase get() = GlobalContext.get().get()
internal val completeNotificationTask: CompleteTaskFromNotificationUseCase get() = GlobalContext.get().get()
