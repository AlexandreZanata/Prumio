package app.prumio.feature.tasks.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioBottomSheet
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioDatePicker
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.task.PostponeOption
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.ui.component.DueLabel
import app.prumio.core.ui.label.label
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.action_cancel
import app.prumio.core.ui.resources.action_delete
import app.prumio.core.ui.resources.action_done
import app.prumio.core.ui.resources.action_edit
import app.prumio.feature.tasks.resources.Res
import app.prumio.feature.tasks.resources.task_complete
import app.prumio.feature.tasks.resources.task_detail
import app.prumio.feature.tasks.resources.task_dismiss
import app.prumio.feature.tasks.resources.task_matter
import app.prumio.feature.tasks.resources.task_no_due_date
import app.prumio.feature.tasks.resources.task_notes
import app.prumio.feature.tasks.resources.task_postpone
import app.prumio.feature.tasks.resources.task_postpone_next_week
import app.prumio.feature.tasks.resources.task_postpone_pick
import app.prumio.feature.tasks.resources.task_postpone_tomorrow
import app.prumio.feature.tasks.resources.task_reopen
import app.prumio.feature.tasks.resources.task_status_line
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Instant
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless task detail. Dismiss and postpone come before Delete (BR-GEN-005). */
@Composable
internal fun TaskDetailScreen(
    state: TaskDetailUiState,
    onAction: (TaskDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(Res.string.task_detail),
                onNavigateBack = { onAction(TaskDetailAction.Close) },
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                actions = {
                    if (state.task != null) {
                        PrumioIconButton(
                            icon = PrumioIcons.Edit,
                            contentDescription = stringResource(CoreUiRes.string.action_edit),
                            onClick = { onAction(TaskDetailAction.Edit) },
                        )
                        PrumioIconButton(
                            icon = PrumioIcons.Delete,
                            contentDescription = stringResource(CoreUiRes.string.action_delete),
                            onClick = { onAction(TaskDetailAction.Delete) },
                        )
                    }
                },
            )
        },
    ) { padding ->
        state.task?.let { task -> TaskDetails(task, state, onAction, Modifier.fillMaxSize().padding(padding)) }
    }
    if (state.isPostponing) PostponeSheet(state.today, onAction)
}

@Composable
private fun TaskDetails(
    task: Task,
    state: TaskDetailUiState,
    onAction: (TaskDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(PrumioTheme.spacing.screenHorizontal),
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
    ) {
        PrumioText(text = task.title, style = PrumioTextStyle.Headline)
        PrumioText(
            text = stringResource(Res.string.task_status_line, task.status.label()),
            style = PrumioTextStyle.Label,
            color = PrumioTheme.colors.onSurfaceVariant,
        )
        val dueDate = task.dueDate
        if (dueDate != null) {
            DueLabel(date = dueDate, time = task.dueTime, today = state.today, isOverdue = state.isOverdue)
        } else {
            PrumioText(text = stringResource(Res.string.task_no_due_date), color = PrumioTheme.colors.onSurfaceVariant)
        }
        FlowRow(horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
            if (task.isOpen) {
                PrumioButton(
                    text = stringResource(Res.string.task_complete),
                    onClick = { onAction(TaskDetailAction.SetDone(true)) },
                )
                PrumioButton(
                    text = stringResource(Res.string.task_postpone),
                    onClick = { onAction(TaskDetailAction.ShowPostpone(true)) },
                    variant = PrumioButtonVariant.Secondary,
                )
                PrumioButton(
                    text = stringResource(Res.string.task_dismiss),
                    onClick = { onAction(TaskDetailAction.Dismiss) },
                    variant = PrumioButtonVariant.Text,
                )
            } else {
                PrumioButton(
                    text = stringResource(Res.string.task_reopen),
                    onClick = { onAction(TaskDetailAction.SetDone(false)) },
                    variant = PrumioButtonVariant.Secondary,
                )
            }
        }
        state.matterTitle?.let { title ->
            PrumioListItem(
                headline = title,
                overline = stringResource(Res.string.task_matter),
                onClick = { onAction(TaskDetailAction.OpenMatter) },
                leading = { PrumioIcon(icon = PrumioIcons.Matters, contentDescription = null) },
                trailing = { PrumioIcon(icon = PrumioIcons.ChevronRight, contentDescription = null) },
            )
        }
        task.notes?.let { notes ->
            PrumioText(
                text = stringResource(Res.string.task_notes),
                style = PrumioTextStyle.Label,
                color = PrumioTheme.colors.onSurfaceVariant,
            )
            PrumioText(text = notes)
        }
    }
}

/** Postpone choices of BR-TASK-007. */
@Composable
private fun PostponeSheet(today: LocalDate, onAction: (TaskDetailAction) -> Unit) {
    var picking by rememberSaveable { mutableStateOf(false) }
    PrumioBottomSheet(onDismissRequest = { onAction(TaskDetailAction.ShowPostpone(false)) }) {
        Column(modifier = Modifier.fillMaxWidth()) {
            PostponeItem(stringResource(Res.string.task_postpone_tomorrow)) {
                onAction(TaskDetailAction.Postpone(PostponeOption.Tomorrow))
            }
            PostponeItem(stringResource(Res.string.task_postpone_next_week)) {
                onAction(TaskDetailAction.Postpone(PostponeOption.NextWeek))
            }
            PostponeItem(stringResource(Res.string.task_postpone_pick)) { picking = true }
        }
    }
    if (picking) {
        PrumioDatePicker(
            initialDate = today,
            confirmLabel = stringResource(CoreUiRes.string.action_done),
            dismissLabel = stringResource(CoreUiRes.string.action_cancel),
            onConfirm = {
                picking = false
                onAction(TaskDetailAction.Postpone(PostponeOption.To(it)))
            },
            onDismiss = { picking = false },
        )
    }
}

@Composable
private fun PostponeItem(label: String, onClick: () -> Unit) {
    PrumioListItem(
        headline = label,
        onClick = onClick,
        leading = { PrumioIcon(icon = PrumioIcons.Event, contentDescription = null) },
    )
}

@Preview
@Composable
private fun TaskDetailScreenPreview() {
    val now = Instant.parse("2026-11-11T12:00:00Z")
    PrumioTheme {
        TaskDetailScreen(
            state = TaskDetailUiState(
                task = Task(
                    id = TaskId("1"),
                    title = "Send the signed contract",
                    notes = "Scan both copies first.",
                    dueDate = LocalDate(2026, 11, 12),
                    createdAt = now,
                    updatedAt = now,
                ),
                matterTitle = "Apartment rental",
                today = LocalDate(2026, 11, 11),
            ),
            onAction = {},
        )
    }
}
