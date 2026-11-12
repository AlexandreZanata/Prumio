package app.prumio.core.domain.di

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
import app.prumio.core.domain.search.SearchUseCase
import app.prumio.core.domain.settings.ObserveSettingsUseCase
import app.prumio.core.domain.settings.UpdateSettingUseCase
import app.prumio.core.domain.task.CompleteTaskUseCase
import app.prumio.core.domain.task.CreateTaskUseCase
import app.prumio.core.domain.task.DeleteTaskUseCase
import app.prumio.core.domain.task.DismissTaskUseCase
import app.prumio.core.domain.task.LinkTaskToMatterUseCase
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
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/** Registers every use case. Use cases are stateless: always `factoryOf`. Keep alphabetical. */
val coreDomainModule = module {
    factoryOf(::AddMatterNoteUseCase)
    factoryOf(::CaptureInboxItemUseCase)
    factoryOf(::CompleteTaskUseCase)
    factoryOf(::ConvertInboxItemUseCase)
    factoryOf(::CreateAppointmentUseCase)
    factoryOf(::CreateMatterUseCase)
    factoryOf(::CreateTaskUseCase)
    factoryOf(::DeleteAppointmentUseCase)
    factoryOf(::DeleteMatterNoteUseCase)
    factoryOf(::DeleteMatterUseCase)
    factoryOf(::DeleteTaskUseCase)
    factoryOf(::DiscardInboxItemUseCase)
    factoryOf(::DismissTaskUseCase)
    factoryOf(::EditInboxItemUseCase)
    factoryOf(::EditMatterNoteUseCase)
    factoryOf(::GetDayPeriodUseCase)
    factoryOf(::LinkTaskToMatterUseCase)
    factoryOf(::ObserveAgendaUseCase)
    factoryOf(::ObserveAppointmentUseCase)
    factoryOf(::ObserveAppointmentsUseCase)
    factoryOf(::ObserveCurrentDateUseCase)
    factoryOf(::ObserveInboxCountUseCase)
    factoryOf(::ObserveInboxUseCase)
    factoryOf(::ObserveMatterDetailUseCase)
    factoryOf(::ObserveMattersUseCase)
    factoryOf(::ObserveSettingsUseCase)
    factoryOf(::ObserveTaskListUseCase)
    factoryOf(::ObserveTaskUseCase)
    factoryOf(::ObserveTodayUseCase)
    factoryOf(::PostponeTaskUseCase)
    factoryOf(::ReopenMatterUseCase)
    factoryOf(::ReopenTaskUseCase)
    factoryOf(::ResolveMatterUseCase)
    factoryOf(::RestoreAppointmentUseCase)
    factoryOf(::RestoreInboxItemUseCase)
    factoryOf(::RestoreMatterNoteUseCase)
    factoryOf(::RestoreMatterUseCase)
    factoryOf(::RestoreTaskUseCase)
    factoryOf(::ResumeMatterUseCase)
    factoryOf(::SearchUseCase)
    factoryOf(::SetMatterFollowUpUseCase)
    factoryOf(::SetTaskDoneUseCase)
    factoryOf(::UpdateAppointmentUseCase)
    factoryOf(::UpdateMatterUseCase)
    factoryOf(::UpdateSettingUseCase)
    factoryOf(::UpdateTaskUseCase)
    factoryOf(::WaitOnMatterUseCase)
}
