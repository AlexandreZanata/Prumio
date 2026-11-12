package app.prumio.core.domain.appointment

import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.MatterId
import app.prumio.core.testing.FakeIdGenerator
import app.prumio.core.testing.FakeRepositories
import app.prumio.core.testing.FakeTimeProvider
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.assertNotFound
import app.prumio.core.testing.assertSuccess
import app.prumio.core.testing.assertValidation
import app.prumio.core.testing.timedAt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AppointmentUseCasesTest {
    private val saoPaulo = TimeZone.of("America/Sao_Paulo")
    private val lisbon = TimeZone.of("Europe/Lisbon")
    private val repos = FakeRepositories()
    private val ids = FakeIdGenerator()
    private val time = FakeTimeProvider(LocalDateTime(2026, 1, 15, 9, 0), saoPaulo)
    private val create = CreateAppointmentUseCase(repos.appointments, repos.matters, ids, time, repos.settings)
    private val update = UpdateAppointmentUseCase(repos.appointments, repos.matters, time, ids)
    private val observeDays = ObserveAppointmentsUseCase(repos.appointments, time)

    private val doctor = AppointmentDraft("Doctor", AppointmentSchedule.Timed(LocalDateTime(2026, 1, 15, 16, 0)))

    @Test
    fun `BR-APPT-003 BR-TIME-002 timed appointments are anchored to the zone they were created in`() = runTest {
        val appointment = create(doctor).assertSuccess()

        assertEquals(saoPaulo, assertIs<AppointmentTiming.Timed>(appointment.timing).timeZone)
        assertEquals(appointment, repos.appointments.get(appointment.id))
        create(doctor.copy(title = "")).assertValidation("BR-APPT-001")
        create(doctor.copy(matterId = MatterId("missing"))).assertNotFound()
    }

    @Test
    fun `BR-APPT-003 editing the schedule re-anchors, other edits keep the zone`() = runTest {
        val appointment = create(doctor).assertSuccess()
        time.moveTo(lisbon)

        val renamed = update(appointment.id, doctor.copy(title = "Dentist", location = "Clinic")).assertSuccess()
        assertEquals(saoPaulo, (renamed.timing as AppointmentTiming.Timed).timeZone)
        assertEquals("Clinic", renamed.location)

        val moved = update(
            appointment.id,
            doctor.copy(schedule = AppointmentSchedule.Timed(LocalDateTime(2026, 1, 16, 10, 0))),
        )
            .assertSuccess()
        assertEquals(lisbon, (moved.timing as AppointmentTiming.Timed).timeZone)
        update(AppointmentId("missing"), doctor).assertNotFound()
        update(appointment.id, doctor.copy(title = " ")).assertValidation("BR-APPT-001")
    }

    @Test
    fun `BR-APPT-002 ranges are validated when saving`() = runTest {
        val backwards =
            AppointmentDraft("Trip", AppointmentSchedule.AllDay(LocalDate(2026, 1, 20), LocalDate(2026, 1, 18)))

        create(backwards).assertValidation("BR-APPT-002")
    }

    @Test
    fun `BR-APPT-005 an appointment occurs on every local day it overlaps`() {
        val zone = TimeZone.UTC
        val overnight =
            anAppointment(timing = timedAt(LocalDateTime(2026, 1, 15, 22, 0), LocalDateTime(2026, 1, 16, 2, 0)))
        val endsAtMidnight =
            anAppointment(timing = timedAt(LocalDateTime(2026, 1, 15, 22, 0), LocalDateTime(2026, 1, 16, 0, 0)))
        val pointInTime = anAppointment(timing = timedAt(LocalDateTime(2026, 1, 15, 23, 59)))
        val trip = anAppointment(timing = allDay(LocalDate(2026, 1, 14), LocalDate(2026, 1, 16)))

        assertTrue(AppointmentTimePolicy.occursOn(overnight, LocalDate(2026, 1, 16), zone))
        assertFalse(AppointmentTimePolicy.occursOn(endsAtMidnight, LocalDate(2026, 1, 16), zone))
        assertTrue(AppointmentTimePolicy.occursOn(pointInTime, LocalDate(2026, 1, 15), zone))
        assertFalse(AppointmentTimePolicy.occursOn(pointInTime, LocalDate(2026, 1, 16), zone))
        assertTrue(AppointmentTimePolicy.occursOn(trip, LocalDate(2026, 1, 16), zone))
        assertFalse(AppointmentTimePolicy.occursOn(trip, LocalDate(2026, 1, 17), zone))
    }

    @Test
    fun `BR-APPT-005 BR-TIME-002 a timed appointment moves day when seen from another zone`() = runTest {
        repos.appointments.save(
            anAppointment(id = "late", timing = timedAt(LocalDateTime(2026, 1, 15, 23, 0), zone = saoPaulo)),
        )
        time.moveTo(lisbon) // 23:00 in São Paulo is 02:00 the next day in Lisbon

        val days = observeDays(LocalDate(2026, 1, 15), LocalDate(2026, 1, 16)).first()

        assertTrue(days[0].appointments.isEmpty())
        assertEquals(listOf("late"), days[1].appointments.map { it.id.value })
        assertEquals(
            LocalDateTime(2026, 1, 16, 2, 0),
            AppointmentTimePolicy.localStart(days[1].appointments.single(), lisbon),
        )
    }

    @Test
    fun `BR-APPT-006 BR-APPT-007 past appointments stay visible and days list all-day first, then by time`() = runTest {
        repos.appointments.save(
            anAppointment(id = "afternoon", timing = timedAt(LocalDateTime(2026, 1, 14, 15, 0), zone = saoPaulo)),
        )
        repos.appointments.save(
            anAppointment(id = "morning", timing = timedAt(LocalDateTime(2026, 1, 14, 8, 0), zone = saoPaulo)),
        )
        repos.appointments.save(anAppointment(id = "holiday", timing = allDay(LocalDate(2026, 1, 14))))

        val day = observeDays(LocalDate(2026, 1, 14), LocalDate(2026, 1, 14)).first().single()

        assertEquals(LocalDate(2026, 1, 14), day.date)
        assertEquals(listOf("holiday", "morning", "afternoon"), day.appointments.map { it.id.value })
    }

    @Test
    fun `BR-GEN-005 a deleted appointment can be restored`() = runTest {
        repos.matters.save(aMatter(id = "m1"))
        val appointment = create(doctor.copy(matterId = MatterId("m1"))).assertSuccess()
        val delete = DeleteAppointmentUseCase(repos.appointments)
        val restore = RestoreAppointmentUseCase(repos.appointments, repos.matters)

        val deleted = delete(appointment.id).assertSuccess()
        assertNull(ObserveAppointmentUseCase(repos.appointments)(appointment.id).first())
        assertEquals(appointment, restore(deleted).assertSuccess())

        delete(appointment.id).assertSuccess()
        repos.matters.delete(MatterId("m1"))
        assertNull(restore(deleted).assertSuccess().matterId)
        delete(appointment.id).assertSuccess()
        delete(appointment.id).assertNotFound()
    }
}
