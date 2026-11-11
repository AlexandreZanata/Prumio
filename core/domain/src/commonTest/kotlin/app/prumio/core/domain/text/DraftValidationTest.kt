package app.prumio.core.domain.text

import app.prumio.core.common.Outcome
import app.prumio.core.domain.appointment.AppointmentDraft
import app.prumio.core.domain.appointment.AppointmentSchedule
import app.prumio.core.domain.appointment.normalized
import app.prumio.core.domain.appointment.toSchedule
import app.prumio.core.domain.appointment.toTiming
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.matter.MatterDraft
import app.prumio.core.domain.matter.normalized
import app.prumio.core.domain.task.TaskDraft
import app.prumio.core.domain.task.normalized
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.Frequency
import app.prumio.core.model.RecurrenceRule
import app.prumio.core.model.TaskRecurrence
import app.prumio.core.testing.assertFailure
import app.prumio.core.testing.assertSuccess
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class DraftValidationTest {
    private fun <T> Outcome<T, DomainError>.value(): T = assertSuccess()

    private fun Outcome<*, DomainError>.validation(): DomainError.Validation =
        assertIs<DomainError.Validation>(assertFailure())

    @Test
    fun `BR-GEN-001 a title alone is enough for every entity`() {
        assertEquals("Pay rent", TaskDraft("Pay rent").normalized().value().title)
        assertEquals("Car documents", MatterDraft("Car documents").normalized().value().title)
        val schedule = AppointmentSchedule.AllDay(LocalDate(2026, 11, 12))
        assertEquals("Doctor", AppointmentDraft("Doctor", schedule).normalized().value().title)
    }

    @Test
    fun `BR-GEN-002 text is trimmed, blank optionals become null and limits are enforced`() {
        val draft = TaskDraft(title = "  Pay rent  ", notes = "   ").normalized().value()

        assertEquals("Pay rent", draft.title)
        assertEquals(null, draft.notes)
        val tooLong = TaskDraft(title = "x".repeat(TextLimits.TITLE + 1)).normalized().validation()
        assertEquals("BR-GEN-002", tooLong.ruleId)
        assertEquals(ValidationReasons.TOO_LONG, tooLong.reason)
        assertEquals(
            "BR-GEN-002",
            TaskDraft("ok", notes = "n".repeat(TextLimits.NOTES + 1)).normalized().validation().ruleId,
        )
        assertEquals("x".repeat(TextLimits.TITLE), TaskDraft("x".repeat(TextLimits.TITLE)).normalized().value().title)
    }

    @Test
    fun `BR-TASK-001 a blank task title is rejected`() {
        val error = TaskDraft(title = "   ").normalized().validation()

        assertEquals("BR-TASK-001", error.ruleId)
        assertEquals("title", error.field)
        assertEquals(ValidationReasons.BLANK, error.reason)
    }

    @Test
    fun `BR-TASK-002 removing the due date removes time and recurrence`() {
        val draft = TaskDraft(
            title = "Water plants",
            dueDate = null,
            dueTime = LocalTime(9, 0),
            recurrence = TaskRecurrence(RecurrenceRule(Frequency.Daily)),
        ).normalized().value()

        assertEquals(null, draft.dueTime)
        assertEquals(null, draft.recurrence)
        val dated = TaskDraft("Water plants", dueDate = LocalDate(2026, 11, 12), dueTime = LocalTime(9, 0))
        assertEquals(LocalTime(9, 0), dated.normalized().value().dueTime)
    }

    @Test
    fun `BR-APPT-001 a blank appointment title is rejected`() {
        val error = AppointmentDraft(" ", AppointmentSchedule.AllDay(LocalDate(2026, 11, 12))).normalized().validation()

        assertEquals("BR-APPT-001", error.ruleId)
    }

    @Test
    fun `BR-APPT-002 the end must not be before the start`() {
        val start = LocalDateTime(2026, 11, 12, 16, 0)
        val timed = AppointmentDraft("Doctor", AppointmentSchedule.Timed(start, LocalDateTime(2026, 11, 12, 15, 0)))
        val allDay =
            AppointmentDraft("Trip", AppointmentSchedule.AllDay(LocalDate(2026, 11, 12), LocalDate(2026, 11, 11)))

        assertEquals("BR-APPT-002", timed.normalized().validation().ruleId)
        assertEquals("BR-APPT-002", allDay.normalized().validation().ruleId)
        assertEquals(
            start,
            (
                AppointmentDraft(
                    "Call",
                    AppointmentSchedule.Timed(start),
                ).normalized().value().schedule as AppointmentSchedule.Timed
                ).start,
        )
    }

    @Test
    fun `BR-APPT-002 optional fields are normalized`() {
        val draft = AppointmentDraft(
            title = " Doctor ",
            schedule = AppointmentSchedule.AllDay(LocalDate(2026, 11, 12)),
            notes = " ",
            location = " Clinic ",
        ).normalized().value()

        assertEquals("Doctor", draft.title)
        assertEquals(null, draft.notes)
        assertEquals("Clinic", draft.location)
        val longLocation = draft.copy(location = "l".repeat(TextLimits.LOCATION + 1)).normalized().validation()
        assertEquals("location", longLocation.field)
    }

    @Test
    fun `BR-TIME-002 timed schedules are anchored to a zone and all-day ones float`() {
        val zone = TimeZone.of("America/Sao_Paulo")
        val timed = AppointmentSchedule.Timed(LocalDateTime(2026, 11, 12, 16, 0))
        val allDay = AppointmentSchedule.AllDay(LocalDate(2026, 11, 12))

        assertEquals(AppointmentTiming.Timed(timed.start, null, zone), timed.toTiming(zone))
        assertEquals(AppointmentTiming.AllDay(allDay.startDate, allDay.startDate), allDay.toTiming(zone))
        assertEquals(timed, timed.toTiming(zone).toSchedule())
        assertEquals(allDay, allDay.toTiming(zone).toSchedule())
    }

    @Test
    fun `BR-MATTER-001 a blank matter title is rejected`() {
        val error = MatterDraft(title = "", description = "x").normalized().validation()

        assertEquals("BR-MATTER-001", error.ruleId)
        assertEquals(null, MatterDraft("Car", description = " ").normalized().value().description)
        assertEquals(
            "BR-GEN-002",
            MatterDraft("Car", "d".repeat(TextLimits.NOTES + 1)).normalized().validation().ruleId,
        )
    }
}
