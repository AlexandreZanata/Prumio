package app.prumio.core.notifications.di

import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Binds the domain scheduling ports (`AlarmScheduler`, `NotificationPresenter`) to platform
 * implementations. Specification: docs/domain/reminders-and-notifications.md. Implemented in phase 4.
 */
internal expect val notificationPlatformModule: Module

val coreNotificationsModule = module {
    includes(notificationPlatformModule)
}
