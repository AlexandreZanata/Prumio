package app.prumio.core.testing

import app.prumio.core.domain.agenda.ObserveAgendaUseCase
import app.prumio.core.domain.appointment.CreateAppointmentUseCase
import app.prumio.core.domain.appointment.DeleteAppointmentUseCase
import app.prumio.core.domain.appointment.ObserveAppointmentUseCase
import app.prumio.core.domain.appointment.ObserveAppointmentsUseCase
import app.prumio.core.domain.appointment.RestoreAppointmentUseCase
import app.prumio.core.domain.appointment.UpdateAppointmentUseCase
import app.prumio.core.domain.inbox.CaptureInboxItemUseCase
import app.prumio.core.domain.inbox.ConvertInboxItemUseCase
import app.prumio.core.domain.inbox.DiscardInboxItemUseCase
import app.prumio.core.domain.inbox.EditInboxItemUseCase
import app.prumio.core.domain.inbox.ObserveInboxCountUseCase
import app.prumio.core.domain.inbox.ObserveInboxUseCase
import app.prumio.core.domain.inbox.RestoreInboxItemUseCase
import app.prumio.core.domain.matter.AddMatterNoteUseCase
import app.prumio.core.domain.matter.CreateMatterUseCase
import app.prumio.core.domain.matter.DeleteMatterNoteUseCase
import app.prumio.core.domain.matter.DeleteMatterUseCase
import app.prumio.core.domain.matter.EditMatterNoteUseCase
import app.prumio.core.domain.matter.ObserveMatterDetailUseCase
import app.prumio.core.domain.matter.ObserveMattersUseCase
import app.prumio.core.domain.matter.ReopenMatterUseCase
import app.prumio.core.domain.matter.ResolveMatterUseCase
import app.prumio.core.domain.matter.RestoreMatterNoteUseCase
import app.prumio.core.domain.matter.RestoreMatterUseCase
import app.prumio.core.domain.matter.ResumeMatterUseCase
import app.prumio.core.domain.matter.SetMatterFollowUpUseCase
import app.prumio.core.domain.matter.UpdateMatterUseCase
import app.prumio.core.domain.matter.WaitOnMatterUseCase
import app.prumio.core.domain.reminder.CheckReminderPermissionUseCase
import app.prumio.core.domain.reminder.GetReminderReliabilityUseCase
import app.prumio.core.domain.reminder.RecordNotificationPermissionUseCase
import app.prumio.core.domain.reminder.ResolveReminderLinkUseCase
import app.prumio.core.domain.search.SearchUseCase
import app.prumio.core.domain.settings.ObserveSettingsUseCase
import app.prumio.core.domain.settings.UpdateSettingUseCase
import app.prumio.core.domain.task.CompleteTaskUseCase
import app.prumio.core.domain.task.CreateTaskUseCase
import app.prumio.core.domain.task.DeleteTaskUseCase
import app.prumio.core.domain.task.DismissTaskUseCase
import app.prumio.core.domain.task.ObserveTaskListUseCase
import app.prumio.core.domain.task.ObserveTaskUseCase
import app.prumio.core.domain.task.PostponeTaskUseCase
import app.prumio.core.domain.task.ReopenTaskUseCase
import app.prumio.core.domain.task.RestoreTaskUseCase
import app.prumio.core.domain.task.SetTaskDoneUseCase
import app.prumio.core.domain.task.UpdateTaskUseCase
import app.prumio.core.domain.today.GetDayPeriodUseCase
import app.prumio.core.domain.today.ObserveCurrentDateUseCase
import app.prumio.core.domain.today.ObserveTodayUseCase

/**
 * Real use cases wired to fakes, for ViewModel tests: `val domain = TestUseCases()` then
 * `XxxViewModel(domain.captureInboxItem, …)`. Arrange data through [repos] (`repos.store`).
 */
class TestUseCases(
    val repos: FakeRepositories = FakeRepositories(),
    val time: FakeTimeProvider = FakeTimeProvider(),
    val ids: FakeIdGenerator = FakeIdGenerator(),
) {
    val notificationAccess = FakeNotificationAccess()
    val getReminderReliability get() = GetReminderReliabilityUseCase(
        notificationAccess,
        repos.settings,
        repos.reminders,
        repos.matters,
    )
    val checkReminderPermission get() = CheckReminderPermissionUseCase(
        notificationAccess,
        repos.settings,
    )
    val recordNotificationPermission get() = RecordNotificationPermissionUseCase(
        repos.settings,
        time,
    )
    val resolveReminderLink get() = ResolveReminderLinkUseCase(repos.tasks, repos.appointments, repos.matters)

    // Inbox
    val captureInboxItem get() = CaptureInboxItemUseCase(repos.inbox, ids, time)
    val convertInboxItem get() = ConvertInboxItemUseCase(repos.inbox, ids, time, repos.settings)
    val discardInboxItem get() = DiscardInboxItemUseCase(repos.inbox)
    val editInboxItem get() = EditInboxItemUseCase(repos.inbox, time)
    val observeInbox get() = ObserveInboxUseCase(repos.inbox)
    val observeInboxCount get() = ObserveInboxCountUseCase(repos.inbox)
    val restoreInboxItem get() = RestoreInboxItemUseCase(repos.inbox)

    // Tasks
    val completeTask get() = CompleteTaskUseCase(repos.tasks, repos.matters, ids, time)
    val createTask get() = CreateTaskUseCase(repos.tasks, repos.matters, ids, time)
    val deleteTask get() = DeleteTaskUseCase(repos.tasks)
    val dismissTask get() = DismissTaskUseCase(repos.tasks, repos.matters, ids, time)
    val observeTask get() = ObserveTaskUseCase(repos.tasks)
    val observeTaskList get() = ObserveTaskListUseCase(repos.tasks, time)
    val postponeTask get() = PostponeTaskUseCase(repos.tasks, repos.matters, ids, time)
    val reopenTask get() = ReopenTaskUseCase(repos.tasks, repos.matters, ids, time)
    val restoreTask get() = RestoreTaskUseCase(repos.tasks, repos.matters)
    val setTaskDone get() = SetTaskDoneUseCase(completeTask, reopenTask)
    val updateTask get() = UpdateTaskUseCase(repos.tasks, repos.matters, ids, time)

    // Appointments and agenda
    val createAppointment get() = CreateAppointmentUseCase(repos.appointments, repos.matters, ids, time, repos.settings)
    val deleteAppointment get() = DeleteAppointmentUseCase(repos.appointments)
    val observeAgenda get() = ObserveAgendaUseCase(observeAppointments, repos.tasks, time)
    val observeAppointment get() = ObserveAppointmentUseCase(repos.appointments)
    val observeAppointments get() = ObserveAppointmentsUseCase(repos.appointments, time)
    val restoreAppointment get() = RestoreAppointmentUseCase(repos.appointments, repos.matters)
    val updateAppointment get() = UpdateAppointmentUseCase(repos.appointments, repos.matters, time, ids)

    // Matters
    val addMatterNote get() = AddMatterNoteUseCase(repos.matters, ids, time)
    val createMatter get() = CreateMatterUseCase(repos.matters, ids, time)
    val deleteMatter get() = DeleteMatterUseCase(repos.matters, repos.tasks)
    val deleteMatterNote get() = DeleteMatterNoteUseCase(repos.matters, ids, time)
    val editMatterNote get() = EditMatterNoteUseCase(repos.matters, ids, time)
    val observeMatterDetail get() = ObserveMatterDetailUseCase(repos.matters, repos.tasks, time)
    val observeMatters get() = ObserveMattersUseCase(repos.matters, repos.tasks, time)
    val reopenMatter get() = ReopenMatterUseCase(repos.matters, ids, time)
    val resolveMatter get() = ResolveMatterUseCase(repos.matters, repos.tasks, ids, time)
    val restoreMatterNote get() = RestoreMatterNoteUseCase(repos.matters, ids, time)
    val restoreMatter get() = RestoreMatterUseCase(repos.matters, repos.tasks)
    val resumeMatter get() = ResumeMatterUseCase(repos.matters, ids, time)
    val setMatterFollowUp get() = SetMatterFollowUpUseCase(repos.matters, ids, time)
    val updateMatter get() = UpdateMatterUseCase(repos.matters, time)
    val waitOnMatter get() = WaitOnMatterUseCase(repos.matters, ids, time)

    // Today, search, settings
    val getDayPeriod get() = GetDayPeriodUseCase(time)
    val observeCurrentDate get() = ObserveCurrentDateUseCase(time)
    val observeToday get() = ObserveTodayUseCase(repos.tasks, repos.appointments, repos.matters, repos.inbox, time)
    val search get() = SearchUseCase(repos.search)
    val observeSettings get() = ObserveSettingsUseCase(repos.settings)
    val updateSetting get() = UpdateSettingUseCase(repos.settings, time)
}
