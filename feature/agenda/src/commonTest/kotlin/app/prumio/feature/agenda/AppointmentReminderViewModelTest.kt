package app.prumio.feature.agenda

import app.cash.turbine.test
import app.prumio.core.domain.reminder.NotificationAccessState
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.runViewModelTest
import app.prumio.feature.agenda.editor.AppointmentEditorAction
import app.prumio.feature.agenda.editor.AppointmentEditorEffect
import app.prumio.feature.agenda.editor.AppointmentEditorViewModel
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertTrue

class AppointmentReminderViewModelTest {
    private val domain = TestUseCases()
    private fun editor() = AppointmentEditorViewModel(
        AppointmentEditorKey(),
        domain.observeAppointment,
        domain.createAppointment,
        domain.updateAppointment,
        domain.time,
        domain.checkReminderPermission,
        domain.recordNotificationPermission,
        domain.observeSettings,
    )

    @Test fun `BR-REM-014 permission follows explanation and refusal preserves wishes`() = runViewModelTest {
        domain.notificationAccess.current = NotificationAccessState(false, true, false, true)
        val viewModel = editor()
        viewModel.onAction(AppointmentEditorAction.ChangeTitle("Reminder"))

        viewModel.onAction(AppointmentEditorAction.ChangeReminders(listOf(0, 30)))
        viewModel.onAction(AppointmentEditorAction.Save)
        assertTrue(viewModel.state.value.showPermissionExplanation)
        assertTrue(domain.repos.appointments.observeAll().first().isEmpty())
        viewModel.effects.test {
            viewModel.onAction(AppointmentEditorAction.ConfirmPermission)
            assertEquals(AppointmentEditorEffect.RequestPermission, awaitItem())
            viewModel.onAction(AppointmentEditorAction.PermissionResult(false))
            assertIs<AppointmentEditorEffect.ShowMessage>(awaitItem())
            assertEquals(AppointmentEditorEffect.Close, awaitItem())
        }
        assertEquals(
            listOf(0, 30),
            domain.repos.appointments.observeAll().first().single().reminders.map {
                it.offsetMinutes
            },
        )
        assertEquals(1, domain.observeSettings().first().notificationPermissionDenials)
        assertFalse(viewModel.state.value.showPermissionExplanation)
    }

    @Test fun `BR-REM-018 invalid sixth offset keeps the previous selection`() = runViewModelTest {
        val viewModel = editor()
        viewModel.onAction(AppointmentEditorAction.ChangeReminders(listOf(0)))
        viewModel.effects.test {
            viewModel.onAction(AppointmentEditorAction.ChangeReminders(listOf(0, 5, 10, 15, 30, 60)))
            assertIs<AppointmentEditorEffect.ShowMessage>(awaitItem())
        }
        assertEquals(listOf(0), viewModel.state.value.reminderOffsets)
    }

    @Test fun `BR-REM-014 permission granted continues saving and explicit removal is respected`() = runViewModelTest {
        val viewModel = editor()
        viewModel.onAction(AppointmentEditorAction.ChangeTitle("Silent"))

        viewModel.onAction(AppointmentEditorAction.ChangeReminders(emptyList()))
        viewModel.onAction(AppointmentEditorAction.PermissionResult(true))
        assertTrue(domain.repos.appointments.observeAll().first().single().reminders.isEmpty())
    }
}
