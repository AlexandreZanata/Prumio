package app.prumio.core.domain.reminder

import app.prumio.core.common.Outcome
import app.prumio.core.domain.appointment.AppointmentDraft
import app.prumio.core.domain.appointment.AppointmentSchedule
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.task.TaskDraft
import app.prumio.core.testing.ReminderTestEnvironment
import app.prumio.core.testing.aMatter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ReminderAccessAndLinksTest {
    private val env = ReminderTestEnvironment()
    private val domain = env.domain
    private fun <T> value(result: Outcome<T, *>) = (result as Outcome.Success).value

    @Test fun `BR-REM-014 permission is contextual and never requested after two refusals`() = runTest {
        assertFalse(value(domain.checkReminderPermission(true)))
        domain.notificationAccess.current = NotificationAccessState(false, true, false, true)
        assertFalse(value(domain.checkReminderPermission(false)))
        repeat(2) {
            assertTrue(value(domain.checkReminderPermission(true)))
            domain.recordNotificationPermission(false)
        }
        assertFalse(value(domain.checkReminderPermission(true)))
        domain.recordNotificationPermission(false)
        domain.recordNotificationPermission(true)
        assertEquals(2, domain.observeSettings().first().notificationPermissionDenials)
    }

    @Test fun `BR-REM-013 BR-REM-014 denied banner can be dismissed and includes follow-ups`() = runTest {
        domain.notificationAccess.current = NotificationAccessState(false, false, false, true)
        assertFalse(value(env.reliability()).showDeniedBanner)
        domain.repos.store.putMatter(aMatter("m1", followUpDate = LocalDate(2026, 1, 15)))
        assertTrue(value(env.reliability()).showDeniedBanner)
        domain.updateSetting(SettingKeys.NotificationBannerDismissed, true)
        assertFalse(value(env.reliability()).showDeniedBanner)
        domain.recordNotificationPermission(false)
        assertTrue(value(env.reliability()).showDeniedBanner)
    }

    @Test fun `BR-REM-017 deep links validate each target and safely handle missing or malformed IDs`() = runTest {
        val task = value(domain.createTask(TaskDraft("Call")))
        val appointment =
            value(
                domain.createAppointment(
                    AppointmentDraft("Dentist", AppointmentSchedule.Timed(LocalDateTime(2026, 1, 15, 10, 0))),
                ),
            )
        domain.repos.store.putMatter(aMatter("m1"))
        assertEquals(ReminderLinkKind.Task, value(domain.resolveReminderLink("prumio://task/${task.id.value}")).kind)
        assertEquals(
            ReminderLinkKind.Appointment,
            value(domain.resolveReminderLink("prumio://appointment/${appointment.id.value}")).kind,
        )
        assertEquals(ReminderLinkKind.Matter, value(domain.resolveReminderLink("prumio://matter/m1")).kind)
        assertEquals(ReminderLinkKind.Capture, value(domain.resolveReminderLink("prumio://capture")).kind)
        assertEquals(
            ReminderLinkKind.Snooze,
            value(domain.resolveReminderLink("prumio://snooze/task:${task.id.value}:123:0")).kind,
        )
        for (uri in listOf(
            "prumio://task/missing",
            "prumio://appointment/missing",
            "prumio://matter/missing",
            "prumio://task/",
            "prumio://task/bad?id",
            "prumio://task/a/b",
            "prumio://snooze/bad",
            "prumio://snooze/task:missing:123:0",
        )) {
            assertEquals(ReminderLink(ReminderLinkKind.Today, missing = true), value(domain.resolveReminderLink(uri)))
        }
        assertEquals(ReminderLinkKind.Today, value(domain.resolveReminderLink("https://task/id")).kind)
        assertEquals(ReminderLinkKind.Today, value(domain.resolveReminderLink("prumio://unknown")).kind)
    }

    @Test fun `BR-TASK-006 BR-APPT-004 defaults preserve IDs and explicit reminder removal`() = runTest {
        var task = value(domain.createTask(TaskDraft("Call", dueDate = LocalDate(2026, 1, 15))))
        assertTrue(task.reminders.isEmpty())
        task = value(domain.updateTask(task.id, TaskDraft("Call", dueDate = task.dueDate, dueTime = LocalTime(10, 0))))
        val reminder = task.reminders.single()
        assertEquals(0, reminder.offsetMinutes)
        task =
            value(domain.updateTask(task.id, TaskDraft("Call again", dueDate = task.dueDate, dueTime = task.dueTime)))
        assertEquals(reminder, task.reminders.single())
        task =
            value(
                domain.updateTask(
                    task.id,
                    TaskDraft("Call", dueDate = task.dueDate, dueTime = task.dueTime, reminderOffsets = emptyList()),
                ),
            )
        assertTrue(task.reminders.isEmpty())
        domain.updateSetting(SettingKeys.DefaultAppointmentNoticeMinutes, 60)
        val appointment =
            value(
                domain.createAppointment(
                    AppointmentDraft("Dentist", AppointmentSchedule.Timed(LocalDateTime(2026, 1, 15, 10, 0))),
                ),
            )
        assertEquals(60, appointment.reminders.single().offsetMinutes)
        val updated =
            value(
                domain.updateAppointment(
                    appointment.id,
                    AppointmentDraft(
                        "Dentist",
                        AppointmentSchedule.AllDay(LocalDate(2026, 1, 15)),
                        reminderOffsets = emptyList(),
                    ),
                ),
            )
        assertTrue(updated.reminders.isEmpty())
        val allDay =
            value(
                domain.createAppointment(
                    AppointmentDraft("Holiday", AppointmentSchedule.AllDay(LocalDate(2026, 1, 15))),
                ),
            )
        assertEquals(0, allDay.reminders.single().offsetMinutes)
    }

    @Test fun `BR-SET-001 invalid reminder preferences fall back to defaults`() {
        val settings = SettingKeys.read(
            mapOf(
                "default_reminder_time" to "25:90",
                "default_appointment_notice_minutes" to "-1",
                "notification_permission_denials" to "3",
                "notification_banner_dismissed" to "bad",
            ),
        )
        assertEquals(LocalTime(9, 0), settings.defaultReminderTime)
        assertEquals(30, settings.defaultAppointmentNoticeMinutes)
        assertEquals(0, settings.notificationPermissionDenials)
        assertFalse(settings.notificationBannerDismissed)
    }
}
