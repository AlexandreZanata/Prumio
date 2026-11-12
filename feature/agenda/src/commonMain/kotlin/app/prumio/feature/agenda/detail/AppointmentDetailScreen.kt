package app.prumio.feature.agenda.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.ui.format.dateTimeText
import app.prumio.core.ui.format.timeText
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.action_delete
import app.prumio.core.ui.resources.action_edit
import app.prumio.core.ui.resources.all_day
import app.prumio.core.ui.resources.time_range
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.appointment_detail
import app.prumio.feature.agenda.resources.appointment_notes
import app.prumio.feature.agenda.resources.appointment_until
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless appointment detail. */
@Composable
internal fun AppointmentDetailScreen(
    state: AppointmentDetailUiState,
    onAction: (AppointmentDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(Res.string.appointment_detail),
                onNavigateBack = { onAction(AppointmentDetailAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                actions = {
                    if (state.appointment != null) {
                        PrumioIconButton(
                            icon = PrumioIcons.Edit,
                            contentDescription = stringResource(CoreUiRes.string.action_edit),
                            onClick = { onAction(AppointmentDetailAction.Edit) },
                        )
                        PrumioIconButton(
                            icon = PrumioIcons.Delete,
                            contentDescription = stringResource(CoreUiRes.string.action_delete),
                            onClick = { onAction(AppointmentDetailAction.Delete) },
                        )
                    }
                },
            )
        },
    ) { padding ->
        state.appointment?.let { appointment ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(PrumioTheme.spacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
            ) {
                PrumioText(text = appointment.title, style = PrumioTextStyle.Headline)
                PrumioListItem(
                    headline = whenText(appointment, state),
                    leading = { PrumioIcon(icon = PrumioIcons.Schedule, contentDescription = null) },
                )
                appointment.location?.let {
                    PrumioListItem(
                        headline = it,
                        leading = { PrumioIcon(icon = PrumioIcons.Location, contentDescription = null) },
                    )
                }
                appointment.notes?.let { notes ->
                    PrumioText(
                        text = stringResource(Res.string.appointment_notes),
                        style = PrumioTextStyle.Label,
                        color = PrumioTheme.colors.onSurfaceVariant,
                    )
                    PrumioText(text = notes)
                }
            }
        }
    }
}

/** "Tomorrow, 15:00 – 16:00", "Mon, 26 Jan · All day", or a range across days. */
@Composable
private fun whenText(appointment: Appointment, state: AppointmentDetailUiState): String {
    val start = state.localStart
    val end = state.localEnd
    return when (val timing = appointment.timing) {
        is AppointmentTiming.AllDay -> {
            val first = dateTimeText(timing.startDate, null, state.today)
            val range = if (timing.endDate == timing.startDate) {
                first
            } else {
                stringResource(CoreUiRes.string.time_range, first, dateTimeText(timing.endDate, null, state.today))
            }
            "$range · ${stringResource(CoreUiRes.string.all_day)}"
        }

        is AppointmentTiming.Timed -> when {
            start == null -> ""

            end == null -> dateTimeText(start.date, start.time, state.today)

            end.date == start.date -> stringResource(
                CoreUiRes.string.time_range,
                dateTimeText(start.date, start.time, state.today),
                end.time.timeText(),
            )

            else -> dateTimeText(start.date, start.time, state.today) + " · " +
                stringResource(Res.string.appointment_until, dateTimeText(end.date, end.time, state.today))
        }
    }
}

@Preview
@Composable
private fun AppointmentDetailScreenPreview() {
    val now = Instant.parse("2026-11-11T12:00:00Z")
    val start = LocalDateTime(2026, 11, 12, 15, 0)
    PrumioTheme {
        AppointmentDetailScreen(
            state = AppointmentDetailUiState(
                appointment = Appointment(
                    id = AppointmentId("1"),
                    title = "Dentist",
                    location = "Rua Augusta, 1200",
                    timing = AppointmentTiming.Timed(start, null, TimeZone.UTC),
                    createdAt = now,
                    updatedAt = now,
                ),
                localStart = start,
                today = LocalDate(2026, 11, 11),
            ),
            onAction = {},
        )
    }
}
