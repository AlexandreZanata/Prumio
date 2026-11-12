package app.prumio.feature.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.today.TodaySnapshot
import app.prumio.core.ui.component.AppointmentRow
import app.prumio.core.ui.component.MatterCard
import app.prumio.core.ui.component.SummaryTile
import app.prumio.core.ui.component.TaskRow
import app.prumio.core.ui.format.dayHeaderText
import app.prumio.core.ui.format.relativeDateText
import app.prumio.core.ui.label.greeting
import app.prumio.core.ui.resources.action_see_all
import app.prumio.core.ui.resources.destination_matters
import app.prumio.core.ui.resources.destination_search
import app.prumio.core.ui.resources.destination_settings
import app.prumio.core.ui.resources.destination_today
import app.prumio.feature.today.resources.Res
import app.prumio.feature.today.resources.today_all_tasks
import app.prumio.feature.today.resources.today_appointments
import app.prumio.feature.today.resources.today_attention
import app.prumio.feature.today.resources.today_done
import app.prumio.feature.today.resources.today_empty_description
import app.prumio.feature.today.resources.today_empty_title
import app.prumio.feature.today.resources.today_inbox_nudge
import app.prumio.feature.today.resources.today_next_appointment
import app.prumio.feature.today.resources.today_section_summary
import app.prumio.feature.today.resources.today_tasks
import app.prumio.feature.today.resources.today_what_to_do
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Today's domain projection, with in-place task completion and links to full lists. */
@Composable
internal fun TodayScreen(state: TodayUiState, onAction: (TodayAction) -> Unit, modifier: Modifier = Modifier) {
    val tasks = remember { BringIntoViewRequester() }
    val appointments = remember { BringIntoViewRequester() }
    val scope = rememberCoroutineScope()
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(CoreUiRes.string.destination_today),
                showBrandMark = true,
                actions = {
                    PrumioIconButton(
                        icon = PrumioIcons.Search,
                        contentDescription = stringResource(CoreUiRes.string.destination_search),
                        onClick = { onAction(TodayAction.OpenSearch) },
                    )
                    PrumioIconButton(
                        icon = PrumioIcons.Settings,
                        contentDescription = stringResource(CoreUiRes.string.destination_settings),
                        onClick = { onAction(TodayAction.OpenSettings) },
                    )
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
                .padding(horizontal = PrumioTheme.spacing.screenHorizontal),
            verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
        ) {
            state.date?.let {
                PrumioText(
                    text = it.dayHeaderText(),
                    style = PrumioTextStyle.Overline,
                    color = PrumioTheme.colors.onSurfaceVariant,
                )
            }
            PrumioText(text = state.dayPeriod.greeting(), style = PrumioTextStyle.Headline)
            state.snapshot?.let { day ->
                PrumioSectionHeader(title = stringResource(Res.string.today_section_summary))
                TodaySummary(
                    day = day,
                    onTasks = { scope.launch { tasks.bringIntoView() } },
                    onAppointments = { scope.launch { appointments.bringIntoView() } },
                    onMatters = { onAction(TodayAction.OpenMatters) },
                )
                if (day.inboxCount > 0) {
                    PrumioListItem(
                        headline = pluralStringResource(Res.plurals.today_inbox_nudge, day.inboxCount, day.inboxCount),
                        onClick = { onAction(TodayAction.OpenInbox) },
                    )
                }
                if (day.taskCount == 0 && day.appointmentCountToday == 0 && day.attentionMatterCount == 0) {
                    PrumioEmptyState(
                        title = stringResource(Res.string.today_empty_title),
                        description = stringResource(Res.string.today_empty_description),
                        icon = PrumioIcons.Check,
                    )
                }
                PrumioSectionHeader(
                    title = stringResource(Res.string.today_what_to_do),
                    modifier = Modifier.bringIntoViewRequester(tasks),
                    action = {
                        PrumioButton(
                            text = stringResource(Res.string.today_all_tasks),
                            variant = PrumioButtonVariant.Text,
                            onClick = { onAction(TodayAction.OpenAllTasks) },
                        )
                    },
                )
                (day.overdueTasks + day.dueTodayTasks).forEach { task ->
                    TaskRow(
                        task = task,
                        today = day.date,
                        isOverdue = task in day.overdueTasks,
                        onToggle = { onAction(TodayAction.SetTaskDone(task.id, it)) },
                        onClick = { onAction(TodayAction.OpenTask(task.id)) },
                    )
                }
                PrumioButton(
                    text = stringResource(Res.string.today_done, day.doneTodayCount),
                    variant = PrumioButtonVariant.Text,
                    onClick = { onAction(TodayAction.OpenDoneToday) },
                )
                if (day.nextAppointment != null || day.allDayToday.isNotEmpty()) {
                    PrumioSectionHeader(
                        title = stringResource(Res.string.today_next_appointment),
                        modifier = Modifier.bringIntoViewRequester(appointments),
                    )
                    day.allDayToday.forEach { appointment ->
                        AppointmentRow(
                            appointment = appointment,
                            start = null,
                            end = null,
                            onClick = { onAction(TodayAction.OpenAppointment(appointment.id)) },
                        )
                    }
                    day.nextAppointment?.let { next ->
                        PrumioText(text = next.start.date.relativeDateText(day.date), style = PrumioTextStyle.Label)
                        AppointmentRow(
                            appointment = next.appointment,
                            start = if (next.appointment.isAllDay) null else next.start.time,
                            end = null,
                            onClick = { onAction(TodayAction.OpenAppointment(next.appointment.id)) },
                        )
                    }
                }
                if (day.attentionMatters.isNotEmpty()) {
                    PrumioSectionHeader(
                        title = stringResource(Res.string.today_attention),
                        action = {
                            PrumioButton(
                                text = stringResource(CoreUiRes.string.action_see_all),
                                variant = PrumioButtonVariant.Text,
                                onClick = { onAction(TodayAction.OpenMatters) },
                            )
                        },
                    )
                    day.attentionMatters.forEach { summary ->
                        MatterCard(
                            matter = summary.matter,
                            nextAction = summary.nextAction,
                            today = day.date,
                            onClick = { onAction(TodayAction.OpenMatter(summary.matter.id)) },
                        )
                    }
                }
            }
        }
    }
}

/** Larger text gets full-width tiles, keeping labels readable without breaking words. */
@Composable
private fun TodaySummary(day: TodaySnapshot, onTasks: () -> Unit, onAppointments: () -> Unit, onMatters: () -> Unit) {
    val tiles = listOf(
        Triple(day.taskCount, stringResource(Res.string.today_tasks), onTasks),
        Triple(day.appointmentCountToday, stringResource(Res.string.today_appointments), onAppointments),
        Triple(day.attentionMatterCount, stringResource(CoreUiRes.string.destination_matters), onMatters),
    )
    if (LocalDensity.current.fontScale > 1.3f) {
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
            tiles.forEach { (count, label, onClick) ->
                SummaryTile(count = count, label = label, onClick = onClick, modifier = Modifier.fillMaxWidth())
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
            tiles.forEach { (count, label, onClick) ->
                SummaryTile(count = count, label = label, onClick = onClick, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Preview
@Composable
private fun TodayScreenPreview() {
    PrumioTheme { TodayScreen(state = TodayUiState(date = LocalDate(2026, 11, 11)), onAction = {}) }
}
