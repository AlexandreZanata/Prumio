package app.prumio.feature.agenda

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioDatePicker
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.agenda.AgendaDay
import app.prumio.core.domain.agenda.AgendaItem
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.ui.component.AppointmentRow
import app.prumio.core.ui.component.TaskRow
import app.prumio.core.ui.format.RelativeDay
import app.prumio.core.ui.format.dayHeaderText
import app.prumio.core.ui.format.monthYearText
import app.prumio.core.ui.format.relativeDateText
import app.prumio.core.ui.format.relativeDayOf
import app.prumio.core.ui.format.shortWeekdayText
import app.prumio.core.ui.resources.action_cancel
import app.prumio.core.ui.resources.action_done
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.agenda_add_appointment
import app.prumio.feature.agenda.resources.agenda_has_items
import app.prumio.feature.agenda.resources.agenda_jump_to_date
import app.prumio.feature.agenda.resources.agenda_next_week
import app.prumio.feature.agenda.resources.agenda_nothing_this_week
import app.prumio.feature.agenda.resources.agenda_nothing_today
import app.prumio.feature.agenda.resources.agenda_previous_week
import app.prumio.feature.agenda.resources.agenda_today
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless Agenda tab. */
@Composable
internal fun AgendaScreen(state: AgendaUiState, onAction: (AgendaAction) -> Unit, modifier: Modifier = Modifier) {
    var pickingDate by rememberSaveable { mutableStateOf(false) }
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = state.selectedDate.monthYearText(),
                actions = {
                    if (state.selectedDate != state.today) {
                        PrumioIconButton(
                            icon = PrumioIcons.Today,
                            contentDescription = stringResource(Res.string.agenda_today),
                            onClick = { onAction(AgendaAction.GoToToday) },
                        )
                    }
                    PrumioIconButton(
                        icon = PrumioIcons.Event,
                        contentDescription = stringResource(Res.string.agenda_jump_to_date),
                        onClick = { pickingDate = true },
                    )
                    PrumioIconButton(
                        icon = PrumioIcons.Add,
                        contentDescription = stringResource(Res.string.agenda_add_appointment),
                        onClick = { onAction(AgendaAction.AddAppointment) },
                    )
                },
            )
        },
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            WeekStrip(state, onAction)
            if (!state.isLoading) DayList(state, onAction)
        }
    }
    if (pickingDate) {
        PrumioDatePicker(
            initialDate = state.selectedDate,
            confirmLabel = stringResource(CoreUiRes.string.action_done),
            dismissLabel = stringResource(CoreUiRes.string.action_cancel),
            onConfirm = {
                pickingDate = false
                onAction(AgendaAction.SelectDate(it))
            },
            onDismiss = { pickingDate = false },
        )
    }
}

/** The week of the selected day (Monday first), with a dot on days that have items. */
@Composable
private fun WeekStrip(state: AgendaUiState, onAction: (AgendaAction) -> Unit) {
    val selected = state.selectedDate
    val monday = selected.minus(DatePeriod(days = selected.dayOfWeek.ordinal - DayOfWeek.MONDAY.ordinal))
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            PrumioIconButton(
                icon = PrumioIcons.Back,
                contentDescription = stringResource(Res.string.agenda_previous_week),
                onClick = { onAction(AgendaAction.SelectDate(selected.minus(DatePeriod(days = 7)))) },
            )
            PrumioIconButton(
                icon = PrumioIcons.ChevronRight,
                contentDescription = stringResource(Res.string.agenda_next_week),
                onClick = { onAction(AgendaAction.SelectDate(selected.plus(DatePeriod(days = 7)))) },
            )
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = PrumioTheme.spacing.xs)) {
            (0 until AgendaUiState.DAYS_IN_WEEK).forEach { offset ->
                val date = monday.plus(DatePeriod(days = offset))
                WeekDay(
                    date = date,
                    isSelected = date == selected,
                    isToday = date == state.today,
                    hasItems = state.hasItems(date),
                    modifier = Modifier.weight(1f),
                    onClick = { onAction(AgendaAction.SelectDate(date)) },
                )
            }
        }
    }
}

@Composable
private fun WeekDay(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    hasItems: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = PrumioTheme.colors
    val header = date.dayHeaderText()
    val description = if (hasItems) stringResource(Res.string.agenda_has_items, header) else header
    Column(
        modifier = modifier
            .heightIn(min = PrumioTheme.spacing.minTouchTarget)
            .clickable(onClick = onClick)
            .background(if (isSelected) colors.primaryContainer else colors.background, PrumioTheme.shape.medium)
            .padding(horizontal = PrumioTheme.spacing.xxs, vertical = PrumioTheme.spacing.xs)
            .semantics {
                this.selected = isSelected
                contentDescription = description
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val textColor = if (isSelected) colors.onPrimaryContainer else colors.onSurfaceVariant
        PrumioText(
            text = if (LocalDensity.current.fontScale >
                1.3f
            ) {
                date.shortWeekdayText().take(1)
            } else {
                date.shortWeekdayText()
            },
            style = PrumioTextStyle.Caption,
            color = textColor,
            maxLines = 1,
        )
        PrumioText(
            text = date.day.toString(),
            style = PrumioTextStyle.BodyStrong,
            color = if (isToday && !isSelected) colors.primary else textColor,
        )
        Box(
            modifier = Modifier
                .size(PrumioTheme.spacing.xs)
                .background(if (hasItems) textColor else colors.background, PrumioTheme.shape.pill),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DayList(state: AgendaUiState, onAction: (AgendaAction) -> Unit) {
    val listState = rememberLazyListState()
    val days = state.visibleDays
    LaunchedEffect(state.selectedDate, days.size) {
        // Every day is one header plus its rows (or one "nothing scheduled" row).
        val index = days.takeWhile { it.date < state.selectedDate }.sumOf { 1 + maxOf(it.items.size, 1) }
        listState.scrollToItem(index + if (state.isWeekEmpty) 1 else 0)
    }
    LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
        if (state.isWeekEmpty) {
            item(key = "week-empty") {
                PrumioText(
                    text = stringResource(Res.string.agenda_nothing_this_week),
                    color = PrumioTheme.colors.onSurfaceVariant,
                    modifier = Modifier.padding(PrumioTheme.spacing.screenHorizontal),
                )
            }
        }
        days.forEach { day -> agendaDay(day, state.today, onAction) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.agendaDay(day: AgendaDay, today: LocalDate, onAction: (AgendaAction) -> Unit) {
    stickyHeader(key = "day-${day.date}") {
        Box(modifier = Modifier.fillMaxWidth().background(PrumioTheme.colors.background)) {
            PrumioSectionHeader(
                title = day.date.headerText(today),
                modifier = Modifier.padding(horizontal = PrumioTheme.spacing.screenHorizontal),
            )
        }
    }
    if (day.items.isEmpty()) {
        item(key = "empty-${day.date}") {
            PrumioText(
                text = stringResource(Res.string.agenda_nothing_today),
                style = PrumioTextStyle.Caption,
                color = PrumioTheme.colors.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = PrumioTheme.spacing.screenHorizontal),
            )
        }
    }
    items(day.items, key = { "${day.date}-${it.key}" }) { item ->
        when (item) {
            is AgendaItem.AppointmentItem -> AppointmentRow(
                appointment = item.appointment,
                start = item.startTime,
                end = item.endTime,
                onClick = { onAction(AgendaAction.OpenAppointment(item.appointment.id)) },
            )

            is AgendaItem.TaskItem -> TaskRow(
                task = item.task,
                today = today,
                isOverdue = false,
                onToggle = { onAction(AgendaAction.SetTaskDone(item.task.id, it)) },
                onClick = { onAction(AgendaAction.OpenTask(item.task.id)) },
            )
        }
    }
}

private val AgendaItem.key: String
    get() = when (this) {
        is AgendaItem.AppointmentItem -> "a-${appointment.id.value}"
        is AgendaItem.TaskItem -> "t-${task.id.value}"
    }

/** "Today", "Tomorrow", "Yesterday", else "Friday, 14 Nov". */
@Composable
private fun LocalDate.headerText(today: LocalDate): String = when (relativeDayOf(this, today)) {
    RelativeDay.Today, RelativeDay.Tomorrow, RelativeDay.Yesterday -> relativeDateText(today)
    else -> dayHeaderText()
}

@Preview
@Composable
private fun AgendaScreenPreview() {
    val today = LocalDate(2026, 11, 11)
    val now = Instant.parse("2026-11-11T12:00:00Z")
    val dentist = Appointment(
        id = AppointmentId("1"),
        title = "Dentist",
        location = "Rua Augusta, 1200",
        timing = AppointmentTiming.Timed(LocalDateTime(2026, 11, 11, 15, 0), null, TimeZone.UTC),
        createdAt = now,
        updatedAt = now,
    )
    PrumioTheme {
        AgendaScreen(
            state = AgendaUiState(
                today = today,
                selectedDate = today,
                days = listOf(AgendaDay(today, listOf(AgendaItem.AppointmentItem(dentist, LocalTime(15, 0), null)))),
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
