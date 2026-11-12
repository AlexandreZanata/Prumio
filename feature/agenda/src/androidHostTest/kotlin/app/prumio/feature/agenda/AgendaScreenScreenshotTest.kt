package app.prumio.feature.agenda

import app.prumio.core.domain.agenda.AgendaDay
import app.prumio.core.domain.agenda.AgendaItem
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.allDay
import app.prumio.core.testing.anAppointment
import app.prumio.feature.agenda.detail.AppointmentDetailScreen
import app.prumio.feature.agenda.detail.AppointmentDetailUiState
import app.prumio.feature.agenda.editor.AppointmentEditorScreen
import app.prumio.feature.agenda.editor.AppointmentEditorUiState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.junit.Test

class AgendaScreenScreenshotTest : ScreenshotTest() {
    private val today = LocalDate(2026, 1, 15)

    @Test fun empty() = screenshot("empty") {
        AgendaScreen(
            AgendaUiState(
                today = today,
                selectedDate = today,
                days = listOf(AgendaDay(today, emptyList())),
                isLoading = false,
            ),
            {
            },
        )
    }

    @Test fun typical() = screenshot("typical") {
        AgendaScreen(
            AgendaUiState(
                today = today,
                selectedDate = today,
                days = listOf(
                    AgendaDay(
                        today,
                        listOf(
                            AgendaItem.AppointmentItem(
                                anAppointment(title = "Dentist", location = "Rua Augusta, 1200"),
                                LocalTime(16, 0),
                                null,
                            ),
                        ),
                    ),
                ),
                isLoading = false,
            ),
            {
            },
        )
    }

    @Test fun editorEmpty() = screenshot("editor_empty") {
        AppointmentEditorScreen(AppointmentEditorUiState(isLoading = false, today = today, startDate = today), {})
    }

    @Test fun editorAllDay() = screenshot("editor_all_day") {
        AppointmentEditorScreen(
            AppointmentEditorUiState(
                isLoading = false,
                title = "Maria’s birthday",
                isAllDay = true,
                today = today,
                startDate = today,
            ),
            {
            },
        )
    }

    @Test fun detailTimed() = screenshot("detail_timed") {
        AppointmentDetailScreen(
            AppointmentDetailUiState(
                appointment = anAppointment(title = "Dentist", location = "Rua Augusta, 1200"),
                today = today,
                localStart = LocalDateTime(2026, 1, 15, 16, 0),
            ),
            {
            },
        )
    }

    @Test fun detailAllDay() = screenshot("detail_all_day") {
        AppointmentDetailScreen(
            AppointmentDetailUiState(
                appointment = anAppointment(title = "A very long appointment title ".repeat(8), timing = allDay(today)),
                today = today,
            ),
            {
            },
        )
    }
}
