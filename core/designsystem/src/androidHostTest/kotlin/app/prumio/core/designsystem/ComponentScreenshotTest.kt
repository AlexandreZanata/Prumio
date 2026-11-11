package app.prumio.core.designsystem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioBadge
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioCheckbox
import app.prumio.core.designsystem.component.PrumioChip
import app.prumio.core.designsystem.component.PrumioDivider
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioFloatingActionButton
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioNavigationBar
import app.prumio.core.designsystem.component.PrumioNavigationBarItem
import app.prumio.core.designsystem.component.PrumioQuickDateChips
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioSwitch
import app.prumio.core.designsystem.component.PrumioTag
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.component.QuickDate
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.screenshottesting.ScreenshotTest
import kotlinx.datetime.LocalDate
import org.junit.Test

// Every generic component in its states (P2.01). Dialogs and sheets open their own window and are
// checked on the device instead (docs/architecture/testing-strategy.md#screenshot-tests).
class ComponentScreenshotTest : ScreenshotTest() {
    @Test
    fun textFields() = screenshot("text_fields") {
        Spaced {
            PrumioTextField(value = "", onValueChange = {}, label = "Title", placeholder = "What needs doing?")
            PrumioTextField(value = "Call the accountant", onValueChange = {}, label = "Title", maxLength = 200)
            PrumioTextField(value = "", onValueChange = {}, label = "Title", errorText = "Fill in this field")
        }
    }

    @Test
    fun selectionControls() = screenshot("selection_controls") {
        Spaced {
            Row(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                PrumioCheckbox(checked = false, onCheckedChange = {}, contentDescription = "Complete")
                PrumioCheckbox(checked = true, onCheckedChange = {}, contentDescription = "Reopen")
                PrumioSwitch(checked = false, onCheckedChange = {})
                PrumioSwitch(checked = true, onCheckedChange = {})
            }
            Row(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                PrumioChip(label = "Open", selected = true, onClick = {})
                PrumioChip(label = "Waiting", selected = false, onClick = {}, leadingIcon = PrumioIcons.Waiting)
            }
            PrumioQuickDateChips(
                options = listOf(QuickDate("Today", Today), QuickDate("Tomorrow", Today.plusDays(1))),
                selected = Today,
                onSelect = {},
                pickLabel = "Pick a date…",
                onPick = {},
            )
        }
    }

    @Test
    fun listsAndLabels() = screenshot("lists_and_labels") {
        Spaced {
            PrumioSectionHeader(title = "Overdue") { PrumioBadge(count = 3) }
            PrumioListItem(
                headline = "Buy printer ink",
                supporting = "2 days ago",
                overline = "Inbox",
                onClick = {},
                leading = { PrumioIcon(icon = PrumioIcons.Inbox, contentDescription = null) },
                trailing = { PrumioBadge(count = 120) },
            )
            PrumioDivider()
            Row(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                PrumioTag(label = "Waiting", color = PrumioTheme.colors.statusWaiting, icon = PrumioIcons.Waiting)
                PrumioTag(label = "Resolved", color = PrumioTheme.colors.statusResolved)
            }
            PrumioEmptyState(
                title = "Nothing here",
                description = "Captured items show up here.",
                icon = PrumioIcons.Inbox,
            )
        }
    }

    @Test
    fun chrome() = screenshot("chrome") {
        Spaced {
            PrumioTopBar(title = "Prumio", showBrandMark = true) {
                PrumioIconButton(icon = PrumioIcons.Search, contentDescription = "Search", onClick = {})
            }
            PrumioTopBar(title = "Matter", onNavigateBack = {}, navigateBackContentDescription = "Back")
            PrumioCard(onClick = {
            }) {
                PrumioText(
                    text = "Card content",
                    modifier = Modifier.fillMaxWidth().padding(PrumioTheme.spacing.lg),
                )
            }
            PrumioFloatingActionButton(icon = PrumioIcons.Add, contentDescription = "Add", onClick = {})
            PrumioNavigationBar {
                PrumioNavigationBarItem(true, {}, PrumioIcons.Today, PrumioIcons.TodaySelected, "Today")
                PrumioNavigationBarItem(false, {}, PrumioIcons.Agenda, PrumioIcons.AgendaSelected, "Agenda")
                PrumioNavigationBarItem(false, {}, PrumioIcons.Matters, PrumioIcons.MattersSelected, "Matters")
                PrumioNavigationBarItem(false, {}, PrumioIcons.Inbox, PrumioIcons.InboxSelected, "Inbox")
            }
        }
    }

    @Test
    fun icons() = screenshot("icons") {
        val icons = with(PrumioIcons) {
            listOf(
                Add, Back, Check, CheckCircle, ChevronRight, Close, Delete, Edit, Event, ExpandLess, ExpandMore,
                History, Info, Label, Location, Mic, More, Notes, Notifications, Repeat, Schedule, Search,
                Settings, Share, TaskDone, TaskOpen, Undo, Waiting, Today, TodaySelected, Agenda,
                AgendaSelected, Matters, MattersSelected, Inbox, InboxSelected,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
            icons.chunked(9).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
                    row.forEach { PrumioIcon(icon = it, contentDescription = null) }
                }
            }
        }
    }

    @Composable
    private fun Spaced(content: @Composable () -> Unit) {
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md)) { content() }
    }

    private companion object {
        val Today = LocalDate(2026, 1, 15)

        fun LocalDate.plusDays(days: Int) = LocalDate.fromEpochDays(toEpochDays() + days)
    }
}
