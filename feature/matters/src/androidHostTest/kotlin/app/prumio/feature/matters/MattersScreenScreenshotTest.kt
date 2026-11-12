package app.prumio.feature.matters

import app.prumio.core.domain.matter.MatterDetail
import app.prumio.core.domain.matter.MatterSummary
import app.prumio.core.domain.matter.MattersOverview
import app.prumio.core.model.MatterStatus
import app.prumio.core.screenshottesting.ScreenshotTest
import app.prumio.core.testing.aMatter
import app.prumio.core.testing.aMatterEntry
import app.prumio.core.testing.aTask
import app.prumio.feature.matters.detail.MatterDetailScreen
import app.prumio.feature.matters.detail.MatterDetailUiState
import app.prumio.feature.matters.editor.MatterEditorScreen
import app.prumio.feature.matters.editor.MatterEditorUiState
import kotlinx.datetime.LocalDate
import org.junit.Test

class MattersScreenScreenshotTest : ScreenshotTest() {
    private val today = LocalDate(2026, 1, 15)

    @Test fun empty() = screenshot("empty") {
        MattersScreen(
            MattersUiState(
                overview = MattersOverview(emptyList(), emptyList(), emptyList(), emptyList()),
                today = today,
            ),
            {
            },
        )
    }

    @Test fun typical() = screenshot("typical") {
        MattersScreen(
            MattersUiState(
                overview = MattersOverview(
                    listOf(MatterSummary(aMatter(title = "Car documents"), aTask(title = "Call the office"), true)),
                    listOf(
                        MatterSummary(
                            aMatter("m2", "Insurance refund", MatterStatus.Waiting, waitingFor = "the insurer"),
                            null,
                            false,
                        ),
                    ),
                    emptyList(),
                    emptyList(),
                ),
                today = today,
            ),
            {
            },
        )
    }

    @Test fun editorEmpty() = screenshot("editor_empty") {
        MatterEditorScreen(MatterEditorUiState(isLoading = false), {})
    }

    @Test fun editorLong() = screenshot("editor_long") {
        MatterEditorScreen(
            MatterEditorUiState(
                isLoading = false,
                title = "Sort out the car documents ".repeat(10),
                description = "Copies and receipts ".repeat(10),
            ),
            {
            },
        )
    }

    @Test fun detailOpen() = screenshot("detail_open") {
        MatterDetailScreen(
            MatterDetailUiState(
                detail = MatterDetail(
                    aMatter(title = "Car documents"),
                    listOf(aMatterEntry(text = "Called the office")),
                    aTask(title = "Send the copies"),
                    listOf(aTask(title = "Send the copies")),
                    emptyList(),
                    true,
                ),
                today = today,
            ),
            {
            },
        )
    }

    @Test fun detailWaiting() = screenshot("detail_waiting") {
        MatterDetailScreen(
            MatterDetailUiState(
                detail = MatterDetail(
                    aMatter(
                        title = "Insurance refund",
                        status = MatterStatus.Waiting,
                        waitingFor = "the insurer",
                        followUpDate = today,
                    ),
                    emptyList(),
                    null,
                    emptyList(),
                    emptyList(),
                    true,
                ),
                today = today,
            ),
            {
            },
        )
    }

    @Test fun detailResolved() = screenshot("detail_resolved") {
        MatterDetailScreen(
            MatterDetailUiState(
                detail = MatterDetail(
                    aMatter(title = "Passport renewal", status = MatterStatus.Resolved),
                    listOf(aMatterEntry(text = "Collected the passport")),
                    null,
                    emptyList(),
                    emptyList(),
                    false,
                ),
                today = today,
            ),
            {
            },
        )
    }
}
