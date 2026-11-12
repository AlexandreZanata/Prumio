package app.prumio.feature.agenda

import app.cash.turbine.test
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.runViewModelTest
import app.prumio.core.testing.timedAt
import app.prumio.core.ui.resources.validation_end_before_start
import app.prumio.core.ui.resources.validation_too_long
import app.prumio.feature.agenda.editor.AppointmentEditorAction
import app.prumio.feature.agenda.editor.AppointmentEditorEffect
import app.prumio.feature.agenda.editor.AppointmentEditorViewModel
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import app.prumio.core.ui.resources.Res as CoreUiRes

class AppointmentEditorViewModelTest {
    private val domain = TestUseCases()

    private fun createViewModel(key: AppointmentEditorKey = AppointmentEditorKey()) = AppointmentEditorViewModel(
        key,
        domain.observeAppointment,
        domain.createAppointment,
        domain.updateAppointment,
        domain.time,
        domain.checkReminderPermission,
        domain.recordNotificationPermission,
        domain.observeSettings,
    )

    private suspend fun saved() = domain.repos.appointments.observeOverlapping(
        LocalDate(2026, 1, 1),
        LocalDate(2026, 12, 31),
    ).first()

    @Test
    fun `BR-APPT-001 a new appointment starts at the next full hour of the given day`() = runViewModelTest {
        val viewModel = createViewModel(AppointmentEditorKey(date = "2026-01-20"))
        val state = viewModel.state.value
        assertTrue(state.isNew)
        assertEquals(LocalDate(2026, 1, 20), state.startDate)
        assertEquals(LocalTime(10, 0), state.startTime)

        viewModel.effects.test {
            viewModel.onAction(AppointmentEditorAction.ChangeTitle("Dentist"))
            viewModel.onAction(AppointmentEditorAction.ChangeLocation("Clinic"))
            viewModel.onAction(AppointmentEditorAction.ChangeNotes("Bring the X-rays"))
            viewModel.onAction(AppointmentEditorAction.ChangeEndTime(LocalTime(11, 0)))
            viewModel.onAction(AppointmentEditorAction.Save)
            assertIs<AppointmentEditorEffect.ShowMessage>(awaitItem())
            assertEquals(AppointmentEditorEffect.Close, awaitItem())
        }
        val timing = assertIs<AppointmentTiming.Timed>(saved().single().timing)
        assertEquals(LocalDateTime(2026, 1, 20, 10, 0), timing.start)
        assertEquals(LocalDateTime(2026, 1, 20, 11, 0), timing.end)
    }

    @Test
    fun `BR-APPT-002 an end before the start is rejected on the end field`() = runViewModelTest {
        val viewModel = createViewModel()
        viewModel.onAction(AppointmentEditorAction.ChangeTitle("Dentist"))
        viewModel.onAction(AppointmentEditorAction.ChangeStartTime(LocalTime(15, 0)))
        viewModel.onAction(AppointmentEditorAction.ChangeEndTime(LocalTime(14, 0)))

        viewModel.onAction(AppointmentEditorAction.Save)

        assertEquals(CoreUiRes.string.validation_end_before_start, viewModel.state.value.endError)
        viewModel.onAction(AppointmentEditorAction.ChangeEndDate(LocalDate(2026, 1, 16)))
        assertEquals(null, viewModel.state.value.endError)
    }

    @Test
    fun `all-day appointments keep their date range`() = runViewModelTest {
        val viewModel = createViewModel()
        viewModel.onAction(AppointmentEditorAction.ChangeTitle("Holiday"))
        viewModel.onAction(AppointmentEditorAction.ChangeAllDay(true))
        viewModel.onAction(AppointmentEditorAction.ChangeStartDate(LocalDate(2026, 2, 1)))
        viewModel.onAction(AppointmentEditorAction.ChangeEndDate(LocalDate(2026, 2, 3)))

        viewModel.onAction(AppointmentEditorAction.Save)

        assertEquals(allDay(LocalDate(2026, 2, 1), LocalDate(2026, 2, 3)), saved().single().timing)
    }

    @Test
    fun `editing loads the appointment in the device zone and keeps its matter`() = runViewModelTest {
        domain.repos.store.putMatter(aMatter("m1"))
        val existing = anAppointment(
            "a1",
            "Dentist",
            timedAt(LocalDateTime(2026, 1, 16, 10, 0), LocalDateTime(2026, 1, 17, 9, 0)),
            matterId = "m1",
        )
        domain.repos.store.putAppointment(existing)
        val viewModel = createViewModel(AppointmentEditorKey(appointmentId = "a1"))
        assertEquals(LocalDate(2026, 1, 17), viewModel.state.value.endDate)

        viewModel.onAction(AppointmentEditorAction.ChangeTitle("x".repeat(501)))
        viewModel.onAction(AppointmentEditorAction.Save)
        assertEquals(CoreUiRes.string.validation_too_long, viewModel.state.value.titleError)

        viewModel.onAction(AppointmentEditorAction.ChangeTitle("Orthodontist"))
        viewModel.onAction(AppointmentEditorAction.Save)
        val updated = domain.repos.appointments.get(AppointmentId("a1"))!!
        assertEquals("Orthodontist", updated.title)
        assertEquals(existing.matterId, updated.matterId)
    }

    @Test
    fun `all-day appointments load as all-day`() = runViewModelTest {
        domain.repos.store.putAppointment(
            anAppointment("a1", "Trip", allDay(LocalDate(2026, 3, 1), LocalDate(2026, 3, 2))),
        )
        val viewModel = createViewModel(AppointmentEditorKey(appointmentId = "a1"))

        assertTrue(viewModel.state.value.isAllDay)
        assertEquals(LocalDate(2026, 3, 2), viewModel.state.value.endDate)
        viewModel.effects.test {
            viewModel.onAction(AppointmentEditorAction.Close)
            assertEquals(AppointmentEditorEffect.Close, awaitItem())
        }
    }
}
