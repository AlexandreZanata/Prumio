package app.prumio.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskStatus
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import app.prumio.core.testing.anInboxItem
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import org.junit.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

// How each domain object is drawn everywhere (P2.04–P2.06), in light, dark and 200% font.
class DomainComponentScreenshotTest : ScreenshotTest() {
    @Test
    fun dateAndTimeFields() = screenshot("date_time_fields") {
        Column {
            DateField(label = "Due date", date = null, today = Today, onDateChange = {})
            DateField(label = "Due date", date = Today, today = Today, onDateChange = {})
            TimeField(label = "Time", time = null, onTimeChange = {})
            TimeField(label = "Time", time = LocalTime(15, 30), onTimeChange = {})
        }
    }

    @Test
    fun tasks() = screenshot("tasks") {
        Column {
            TaskRow(aTask(title = "Call the accountant"), Today, isOverdue = false, onToggle = {}, onClick = {})
            TaskRow(
                aTask(title = "Send the signed contract", dueDate = Today, dueTime = LocalTime(15, 30)),
                Today,
                isOverdue = false,
                onToggle = {},
                onClick = {},
                matterTitle = "Apartment rental",
            )
            TaskRow(
                aTask(title = "Renew the car insurance", dueDate = LocalDate(2026, 1, 12)),
                Today,
                isOverdue = true,
                onToggle = {},
                onClick = {},
            )
            TaskRow(aTask(title = "Buy printer ink", status = TaskStatus.Completed), Today, false, {}, {})
        }
    }

    @Test
    fun appointments() = screenshot("appointments") {
        Column {
            AppointmentRow(
                anAppointment(title = "Dentist", location = "Rua Augusta, 1200"),
                start = LocalTime(9, 0),
                end = LocalTime(10, 0),
                onClick = {},
            )
            AppointmentRow(anAppointment(title = "Call with the bank"), LocalTime(14, 0), null, onClick = {})
            AppointmentRow(anAppointment(title = "Maria's birthday"), null, null, onClick = {})
        }
    }

    @Test
    fun matters() = screenshot("matters") {
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
            MatterCard(aMatter(title = "Apartment rental"), aTask(title = "Send the signed contract"), Today, {})
            MatterCard(
                aMatter(
                    title = "Insurance refund",
                    status = MatterStatus.Waiting,
                    waitingFor = "the insurer",
                    followUpDate = LocalDate(2026, 1, 16),
                ),
                nextAction = null,
                today = Today,
                onClick = {},
            )
            MatterCard(aMatter(title = "Passport renewal", status = MatterStatus.Resolved), null, Today, {})
        }
    }

    @Test
    fun labels() = screenshot("labels") {
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
            Row(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                MatterStatus.entries.forEach { MatterStatusChip(it) }
            }
            DueLabel(date = Today, time = LocalTime(9, 0), today = Today, isOverdue = false)
            DueLabel(date = LocalDate(2026, 1, 13), time = null, today = Today, isOverdue = true)
            ReminderSummary(offsetsMinutes = listOf(0, 60, 1440))
            Row(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                SummaryTile(count = 3, label = "Tasks", modifier = Modifier.weight(1f))
                SummaryTile(count = 12, label = "Inbox", modifier = Modifier.weight(1f), onClick = {})
            }
        }
    }

    @Test
    fun inboxAndHistory() = screenshot("inbox_and_history") {
        Column {
            InboxItemRow(anInboxItem(text = "Ask Ana about the invoice", createdAt = Now - 2.days), Now, {})
            TimelineEntryRow(aMatterEntry(kind = MatterEntryKind.Created, text = null), Today)
            TimelineEntryRow(aMatterEntry(kind = MatterEntryKind.Note, text = "Called them, no answer"), Today)
            TimelineEntryRow(
                aMatterEntry(kind = MatterEntryKind.StatusChanged, text = null).copy(toStatus = MatterStatus.Waiting),
                Today,
            )
            TimelineEntryRow(aMatterEntry(kind = MatterEntryKind.ActionCompleted, text = "Send the documents"), Today)
            TimelineEntryRow(
                aMatterEntry(
                    kind = MatterEntryKind.FollowUpSet,
                    text = "2026-01-20T10:00",
                    occurredOn = LocalDate(2026, 1, 14),
                ),
                Today,
            )
        }
    }

    private companion object {
        val Today = LocalDate(2026, 1, 15)
        val Now = Instant.parse("2026-01-15T12:00:00Z")
    }
}
