package app.prumio.core.data.di

import app.prumio.core.data.appointment.SqlDelightAppointmentRepository
import app.prumio.core.data.inbox.SqlDelightInboxRepository
import app.prumio.core.data.matter.SqlDelightMatterRepository
import app.prumio.core.data.reminder.SqlDelightDeliveredAlarmStore
import app.prumio.core.data.reminder.SqlDelightReminderRepository
import app.prumio.core.data.reminder.SqlDelightScheduledAlarmStore
import app.prumio.core.data.reminder.SqlDelightSnoozeStore
import app.prumio.core.data.search.SqlDelightSearchRepository
import app.prumio.core.data.settings.SqlDelightSettingsRepository
import app.prumio.core.data.task.SqlDelightTaskRepository
import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.domain.inbox.InboxRepository
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.domain.reminder.DeliveredAlarmStore
import app.prumio.core.domain.reminder.ReminderRepository
import app.prumio.core.domain.reminder.ScheduledAlarmStore
import app.prumio.core.domain.reminder.SnoozeStore
import app.prumio.core.domain.search.SearchRepository
import app.prumio.core.domain.settings.SettingsRepository
import app.prumio.core.domain.task.TaskRepository
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

/** Binds every domain port to its SQLDelight adapter. Recipe: docs/ai/recipes/add-repository.md. */
val coreDataModule = module {
    singleOf(::SqlDelightReminderRepository) { bind<ReminderRepository>() }
    singleOf(::SqlDelightScheduledAlarmStore) { bind<ScheduledAlarmStore>() }
    singleOf(::SqlDelightSnoozeStore) { bind<SnoozeStore>() }
    singleOf(::SqlDelightDeliveredAlarmStore) { bind<DeliveredAlarmStore>() }

    singleOf(::SqlDelightAppointmentRepository) { bind<AppointmentRepository>() }
    singleOf(::SqlDelightInboxRepository) { bind<InboxRepository>() }
    singleOf(::SqlDelightMatterRepository) { bind<MatterRepository>() }
    singleOf(::SqlDelightSearchRepository) { bind<SearchRepository>() }
    singleOf(::SqlDelightSettingsRepository) { bind<SettingsRepository>() }
    singleOf(::SqlDelightTaskRepository) { bind<TaskRepository>() }
}
