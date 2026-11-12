package app.prumio.core.notifications.di

import app.prumio.core.domain.reminder.AlarmScheduler
import app.prumio.core.domain.reminder.NotificationAccess
import app.prumio.core.domain.reminder.NotificationPresenter
import app.prumio.core.domain.reminder.ReminderReconciliationTrigger
import app.prumio.core.notifications.AndroidAlarmScheduler
import app.prumio.core.notifications.AndroidNotificationAccess
import app.prumio.core.notifications.AndroidNotificationPresenter
import app.prumio.core.notifications.ReminderRuntime
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val notificationPlatformModule: Module = module {
    single<AlarmScheduler> { AndroidAlarmScheduler(androidContext()) }
    single<NotificationAccess> { AndroidNotificationAccess(androidContext()) }
    single<NotificationPresenter> { AndroidNotificationPresenter(androidContext(), get()) }
    single { ReminderRuntime(androidContext(), get(), get()) }
    single<ReminderReconciliationTrigger> { get<ReminderRuntime>() }
}
