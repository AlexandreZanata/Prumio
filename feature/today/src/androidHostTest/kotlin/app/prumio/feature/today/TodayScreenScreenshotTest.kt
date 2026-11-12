package app.prumio.feature.today

import app.prumio.core.domain.matter.MatterSummary
import app.prumio.core.domain.today.TodaySnapshot
import app.prumio.core.domain.today.UpcomingAppointment
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aTask
import app.prumio.core.testing.anAppointment
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.junit.Test

class TodayScreenScreenshotTest : ScreenshotTest() {
    private val today = LocalDate(2026, 1, 15)
    private val empty = TodaySnapshot(today, emptyList(), emptyList(), 0, emptyList(), 0, null, emptyList(), 0, 0)

    @Test fun empty() = screenshot("empty") { TodayScreen(TodayUiState(date = today, snapshot = empty), {}) }

    @Test fun typical() = screenshot("typical") {
        TodayScreen(
            TodayUiState(
                date = today,
                snapshot = empty.copy(
                    dueTodayTasks = listOf(aTask(title = "Call the accountant", dueDate = today)),
                    inboxCount = 2,
                    appointmentCountToday = 1,
                    nextAppointment = UpcomingAppointment(
                        anAppointment(title = "Dentist"),
                        LocalDateTime(2026, 1, 15, 16, 0),
                    ),
                    attentionMatters = listOf(MatterSummary(aMatter(title = "Car documents"), null, true)),
                    attentionMatterCount = 1,
                ),
            ),
            {
            },
        )
    }

    @Test fun longText() = screenshot("long_text") {
        TodayScreen(
            TodayUiState(
                date = today,
                snapshot = empty.copy(
                    overdueTasks = listOf(
                        aTask(title = "Follow up on the documents ".repeat(12), dueDate = LocalDate(2026, 1, 14)),
                    ),
                ),
            ),
            {
            },
        )
    }
}
