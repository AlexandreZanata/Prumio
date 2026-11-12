package app.prumio.feature.agenda.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSwitch
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.ui.component.DateField
import app.prumio.core.ui.component.ReminderField
import app.prumio.core.ui.component.ReminderPermissionDialog
import app.prumio.core.ui.component.TimeField
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.action_save
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.appointment_all_day
import app.prumio.feature.agenda.resources.appointment_edit
import app.prumio.feature.agenda.resources.appointment_end_date
import app.prumio.feature.agenda.resources.appointment_end_time
import app.prumio.feature.agenda.resources.appointment_location
import app.prumio.feature.agenda.resources.appointment_new
import app.prumio.feature.agenda.resources.appointment_notes
import app.prumio.feature.agenda.resources.appointment_start_date
import app.prumio.feature.agenda.resources.appointment_start_time
import app.prumio.feature.agenda.resources.appointment_title
import app.prumio.feature.agenda.resources.appointment_title_placeholder
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless appointment editor. */
@Composable
internal fun AppointmentEditorScreen(
    state: AppointmentEditorUiState,
    onAction: (AppointmentEditorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(if (state.isNew) Res.string.appointment_new else Res.string.appointment_edit),
                onNavigateBack = { onAction(AppointmentEditorAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                actions = {
                    PrumioButton(
                        text = stringResource(CoreUiRes.string.action_save),
                        onClick = { onAction(AppointmentEditorAction.Save) },
                        enabled = state.canSave,
                    )
                },
            )
        },
    ) { padding ->
        if (!state.isLoading) AppointmentForm(state, onAction, Modifier.fillMaxSize().padding(padding))
    }
    if (state.showPermissionExplanation) {
        ReminderPermissionDialog(
            onConfirm = { onAction(AppointmentEditorAction.ConfirmPermission) },
            onDismiss = { onAction(AppointmentEditorAction.PermissionResult(false)) },
        )
    }
}

@Composable
private fun AppointmentForm(
    state: AppointmentEditorUiState,
    onAction: (AppointmentEditorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { if (state.isNew) focus.requestFocus() }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(vertical = PrumioTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs),
    ) {
        val horizontal = Modifier.fillMaxWidth().padding(horizontal = PrumioTheme.spacing.screenHorizontal)
        PrumioTextField(
            value = state.title,
            onValueChange = { onAction(AppointmentEditorAction.ChangeTitle(it)) },
            label = stringResource(Res.string.appointment_title),
            placeholder = stringResource(Res.string.appointment_title_placeholder),
            errorText = state.titleError?.let { stringResource(it) },
            maxLength = TextLimits.TITLE,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            focusRequester = focus,
            modifier = horizontal,
        )
        PrumioListItem(
            headline = stringResource(Res.string.appointment_all_day),
            onClick = { onAction(AppointmentEditorAction.ChangeAllDay(!state.isAllDay)) },
            leading = { PrumioIcon(icon = PrumioIcons.Today, contentDescription = null) },
            trailing = {
                PrumioSwitch(
                    checked = state.isAllDay,
                    onCheckedChange = { onAction(AppointmentEditorAction.ChangeAllDay(it)) },
                )
            },
        )
        DateField(
            label = stringResource(Res.string.appointment_start_date),
            date = state.startDate,
            today = state.today,
            onDateChange = { date -> date?.let { onAction(AppointmentEditorAction.ChangeStartDate(it)) } },
            clearable = false,
        )
        if (!state.isAllDay) {
            TimeField(
                label = stringResource(Res.string.appointment_start_time),
                time = state.startTime,
                onTimeChange = { time -> time?.let { onAction(AppointmentEditorAction.ChangeStartTime(it)) } },
                clearable = false,
            )
        }
        DateField(
            label = stringResource(Res.string.appointment_end_date),
            date = state.endDate,
            today = state.today,
            onDateChange = { onAction(AppointmentEditorAction.ChangeEndDate(it)) },
        )
        if (!state.isAllDay) {
            TimeField(
                label = stringResource(Res.string.appointment_end_time),
                time = state.endTime,
                onTimeChange = { onAction(AppointmentEditorAction.ChangeEndTime(it)) },
                suggestedTime = state.startTime,
            )
        }
        state.endError?.let {
            PrumioText(
                text = stringResource(it),
                style = PrumioTextStyle.Caption,
                color = PrumioTheme.colors.error,
                modifier = horizontal,
            )
        }
        ReminderField(state.reminderOffsets, { onAction(AppointmentEditorAction.ChangeReminders(it)) }, horizontal)
        PrumioTextField(
            value = state.location,
            onValueChange = { onAction(AppointmentEditorAction.ChangeLocation(it)) },
            label = stringResource(Res.string.appointment_location),
            maxLength = TextLimits.LOCATION,
            singleLine = true,
            modifier = horizontal,
        )
        PrumioTextField(
            value = state.notes,
            onValueChange = { onAction(AppointmentEditorAction.ChangeNotes(it)) },
            label = stringResource(Res.string.appointment_notes),
            maxLength = TextLimits.NOTES,
            minLines = 3,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = horizontal,
        )
    }
}

@Preview
@Composable
private fun AppointmentEditorScreenPreview() {
    val today = LocalDate(2026, 11, 11)
    PrumioTheme {
        AppointmentEditorScreen(
            state = AppointmentEditorUiState(isLoading = false, title = "Dentist", startDate = today, today = today),
            onAction = {},
        )
    }
}
