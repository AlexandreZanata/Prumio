package app.prumio.feature.tasks.editor

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioBottomSheet
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioDatePicker
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioQuickDateChips
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.component.QuickDate
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.ui.component.TimeField
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.action_cancel
import app.prumio.core.ui.resources.action_done
import app.prumio.core.ui.resources.action_pick_date
import app.prumio.core.ui.resources.action_save
import app.prumio.core.ui.resources.date_today
import app.prumio.core.ui.resources.date_tomorrow
import app.prumio.feature.tasks.resources.Res
import app.prumio.feature.tasks.resources.task_date_next_week
import app.prumio.feature.tasks.resources.task_due_date
import app.prumio.feature.tasks.resources.task_due_time
import app.prumio.feature.tasks.resources.task_edit
import app.prumio.feature.tasks.resources.task_matter
import app.prumio.feature.tasks.resources.task_new
import app.prumio.feature.tasks.resources.task_no_matter
import app.prumio.feature.tasks.resources.task_notes
import app.prumio.feature.tasks.resources.task_title
import app.prumio.feature.tasks.resources.task_title_placeholder
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless task editor. */
@Composable
internal fun TaskEditorScreen(
    state: TaskEditorUiState,
    onAction: (TaskEditorAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(if (state.isNew) Res.string.task_new else Res.string.task_edit),
                onNavigateBack = { onAction(TaskEditorAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                actions = {
                    PrumioButton(
                        text = stringResource(CoreUiRes.string.action_save),
                        onClick = { onAction(TaskEditorAction.Save) },
                        enabled = state.canSave,
                    )
                },
            )
        },
    ) { padding ->
        if (!state.isLoading) {
            TaskForm(state, onAction, Modifier.fillMaxSize().padding(padding))
        }
    }
    if (state.isPickingMatter) MatterPicker(state, onAction)
}

@Composable
private fun TaskForm(state: TaskEditorUiState, onAction: (TaskEditorAction) -> Unit, modifier: Modifier = Modifier) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { if (state.isNew) focus.requestFocus() }
    var pickingDate by rememberSaveable { mutableStateOf(false) }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(vertical = PrumioTheme.spacing.md),
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm),
    ) {
        val horizontal = Modifier.fillMaxWidth().padding(horizontal = PrumioTheme.spacing.screenHorizontal)
        PrumioTextField(
            value = state.title,
            onValueChange = { onAction(TaskEditorAction.ChangeTitle(it)) },
            label = stringResource(Res.string.task_title),
            placeholder = stringResource(Res.string.task_title_placeholder),
            errorText = state.titleError?.let { stringResource(it) },
            maxLength = TextLimits.TITLE,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            focusRequester = focus,
            modifier = horizontal,
        )
        PrumioText(
            text = stringResource(Res.string.task_due_date),
            style = PrumioTextStyle.Label,
            color = PrumioTheme.colors.onSurfaceVariant,
            modifier = horizontal,
        )
        PrumioQuickDateChips(
            options = quickDates(state.today),
            selected = state.dueDate,
            onSelect = { onAction(TaskEditorAction.ChangeDueDate(if (it == state.dueDate) null else it)) },
            pickLabel = stringResource(CoreUiRes.string.action_pick_date),
            onPick = { pickingDate = true },
            modifier = horizontal,
        )
        if (state.dueDate != null) {
            TimeField(
                label = stringResource(Res.string.task_due_time),
                time = state.dueTime,
                onTimeChange = { onAction(TaskEditorAction.ChangeDueTime(it)) },
            )
        }
        PrumioListItem(
            headline = state.matterTitle ?: stringResource(Res.string.task_no_matter),
            overline = stringResource(Res.string.task_matter),
            onClick = { onAction(TaskEditorAction.PickMatter(open = true)) },
            leading = { PrumioIcon(icon = PrumioIcons.Matters, contentDescription = null) },
        )
        PrumioTextField(
            value = state.notes,
            onValueChange = { onAction(TaskEditorAction.ChangeNotes(it)) },
            label = stringResource(Res.string.task_notes),
            maxLength = TextLimits.NOTES,
            minLines = 3,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            modifier = horizontal,
        )
    }
    if (pickingDate) {
        PrumioDatePicker(
            initialDate = state.dueDate ?: state.today,
            confirmLabel = stringResource(CoreUiRes.string.action_done),
            dismissLabel = stringResource(CoreUiRes.string.action_cancel),
            onConfirm = {
                pickingDate = false
                onAction(TaskEditorAction.ChangeDueDate(it))
            },
            onDismiss = { pickingDate = false },
        )
    }
}

@Composable
private fun quickDates(today: LocalDate): List<QuickDate> = listOf(
    QuickDate(stringResource(CoreUiRes.string.date_today), today),
    QuickDate(stringResource(CoreUiRes.string.date_tomorrow), today.plus(DatePeriod(days = 1))),
    QuickDate(stringResource(Res.string.task_date_next_week), today.plus(DatePeriod(days = 7))),
)

@Composable
private fun MatterPicker(state: TaskEditorUiState, onAction: (TaskEditorAction) -> Unit) {
    PrumioBottomSheet(onDismissRequest = { onAction(TaskEditorAction.PickMatter(open = false)) }) {
        PrumioListItem(
            headline = stringResource(Res.string.task_no_matter),
            onClick = { onAction(TaskEditorAction.SelectMatter(null)) },
            leading = { PrumioIcon(icon = PrumioIcons.Close, contentDescription = null) },
        )
        state.matters.forEach { matter ->
            PrumioListItem(
                headline = matter.title,
                onClick = { onAction(TaskEditorAction.SelectMatter(matter.id)) },
                leading = {
                    PrumioIcon(
                        icon = if (matter.id == state.matterId) PrumioIcons.CheckCircle else PrumioIcons.Matters,
                        contentDescription = null,
                    )
                },
            )
        }
    }
}

@Preview
@Composable
private fun TaskEditorScreenPreview() {
    PrumioTheme {
        TaskEditorScreen(
            state = TaskEditorUiState(
                isLoading = false,
                title = "Send the signed contract",
                today = LocalDate(2026, 11, 11),
                dueDate = LocalDate(2026, 11, 12),
            ),
            onAction = {},
        )
    }
}
