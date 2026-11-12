package app.prumio.feature.agenda

import app.cash.turbine.test
import app.prumio.core.model.AppointmentId
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.AppointmentEditorKey
import app.prumio.core.testing.TestUseCases
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.runViewModelTest
import app.prumio.core.testing.timedAt
import app.prumio.feature.agenda.detail.AppointmentDetailAction
import app.prumio.feature.agenda.detail.AppointmentDetailEffect
import app.prumio.feature.agenda.detail.AppointmentDetailViewModel
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.appointment_deleted
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AppointmentDetailViewModelTest {
    private val domain = TestUseCases()

    private fun createViewModel(): AppointmentDetailViewModel {
        val timing = timedAt(
            LocalDateTime(2026, 1, 16, 10, 0),
            LocalDateTime(2026, 1, 16, 11, 0),
            zone = TimeZone.of("Europe/Lisbon"),
        )
        domain.repos.store.putAppointment(anAppointment("a1", "Dentist", timing))
        return AppointmentDetailViewModel(
            AppointmentDetailKey("a1"),
            domain.observeAppointment,
            domain.deleteAppointment,
            domain.restoreAppointment,
            domain.time,
        )
    }

    @Test
    fun `BR-TIME-002 times are shown in the device zone`() = runViewModelTest {
        val state = createViewModel().state.value

        assertEquals("Dentist", state.appointment?.title)
        assertEquals(LocalDateTime(2026, 1, 16, 10, 0), state.localStart)
        assertEquals(LocalDateTime(2026, 1, 16, 11, 0), state.localEnd)
    }

    @Test
    fun `BR-GEN-005 deleting closes the screen and Undo restores it`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(AppointmentDetailAction.Delete)
            val message = assertIs<AppointmentDetailEffect.ShowMessage>(awaitItem()).message
            assertEquals(Res.string.appointment_deleted, message.text)
            assertEquals(AppointmentDetailEffect.Close, awaitItem())
            assertNull(domain.repos.appointments.get(AppointmentId("a1")))
            message.onAction?.invoke()
            assertNotNull(domain.repos.appointments.get(AppointmentId("a1")))
        }
    }

    @Test
    fun `edit and close navigate, a missing item reports an error`() = runViewModelTest {
        val viewModel = createViewModel()

        viewModel.effects.test {
            viewModel.onAction(AppointmentDetailAction.Edit)
            assertEquals(AppointmentDetailEffect.Navigate(AppointmentEditorKey(appointmentId = "a1")), awaitItem())
            viewModel.onAction(AppointmentDetailAction.Close)
            assertEquals(AppointmentDetailEffect.Close, awaitItem())
            domain.repos.appointments.delete(AppointmentId("a1"))
            viewModel.onAction(AppointmentDetailAction.Delete)
            assertIs<AppointmentDetailEffect.ShowMessage>(awaitItem())
        }
    }
}
