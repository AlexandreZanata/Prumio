package app.prumio.feature.matters.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task
import app.prumio.core.ui.component.DateField
import app.prumio.core.ui.component.MatterStatusChip
import app.prumio.core.ui.component.ReminderPermissionDialog
import app.prumio.core.ui.component.TaskRow
import app.prumio.core.ui.component.TimeField
import app.prumio.core.ui.component.TimelineEntryRow
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.action_delete
import app.prumio.core.ui.resources.action_edit
import app.prumio.feature.matters.resources.Res
import app.prumio.feature.matters.resources.matter_add_next_action
import app.prumio.feature.matters.resources.matter_add_note
import app.prumio.feature.matters.resources.matter_detail
import app.prumio.feature.matters.resources.matter_follow_up_label
import app.prumio.feature.matters.resources.matter_follow_up_time
import app.prumio.feature.matters.resources.matter_history
import app.prumio.feature.matters.resources.matter_mark_waiting
import app.prumio.feature.matters.resources.matter_next_action_title
import app.prumio.feature.matters.resources.matter_other_actions
import app.prumio.feature.matters.resources.matter_reopen
import app.prumio.feature.matters.resources.matter_resolve
import app.prumio.feature.matters.resources.matter_resume
import app.prumio.feature.matters.resources.matter_waiting_for_label
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless matter detail: domain results decide status, ordering and the next action. */
@Composable
internal fun MatterDetailScreen(
    state: MatterDetailUiState,
    onAction: (MatterDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PrumioScaffold(
            topBar = {
                PrumioTopBar(
                    title = stringResource(Res.string.matter_detail),
                    onNavigateBack = { onAction(MatterDetailAction.Close) },
                    navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
                    actions = {
                        if (state.detail != null) {
                            PrumioIconButton(
                                icon = PrumioIcons.Edit,
                                contentDescription = stringResource(CoreUiRes.string.action_edit),
                                onClick = { onAction(MatterDetailAction.Edit) },
                            )
                            PrumioIconButton(
                                icon = PrumioIcons.Delete,
                                contentDescription = stringResource(CoreUiRes.string.action_delete),
                                onClick = { onAction(MatterDetailAction.Delete) },
                            )
                        }
                    },
                )
            },
        ) { padding ->
            state.detail?.let { detail ->
                val matter = detail.matter
                Column(
                    modifier = Modifier.fillMaxSize().padding(padding)
                        .verticalScroll(rememberScrollState()).padding(PrumioTheme.spacing.screenHorizontal),
                    verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
                ) {
                    PrumioText(text = matter.title, style = PrumioTextStyle.Headline)
                    MatterStatusChip(status = matter.status)
                    matter.description?.let { PrumioText(text = it) }
                    matter.waitingFor?.let {
                        PrumioListItem(headline = it, overline = stringResource(Res.string.matter_waiting_for_label))
                    }
                    StatusActions(status = matter.status, onAction = onAction)
                    if (!matter.isResolved) {
                        PrumioSectionHeader(title = stringResource(Res.string.matter_next_action_title))
                        detail.nextAction?.let { MatterActionRow(task = it, state = state, onAction = onAction) }
                        PrumioButton(
                            text = stringResource(Res.string.matter_add_next_action),
                            variant = PrumioButtonVariant.Secondary,
                            onClick = { onAction(MatterDetailAction.AddNextAction) },
                        )
                        val other = detail.openActions.filter { it.id != detail.nextAction?.id }
                        if (other.isNotEmpty()) {
                            PrumioButton(
                                text = stringResource(Res.string.matter_other_actions, other.size),
                                variant = PrumioButtonVariant.Text,
                                onClick = { onAction(MatterDetailAction.ToggleOtherActions) },
                            )
                            if (state.showOtherActions) {
                                other.forEach { MatterActionRow(task = it, state = state, onAction = onAction) }
                            }
                        }
                        DateField(
                            label = stringResource(Res.string.matter_follow_up_label),
                            date = matter.followUpDate,
                            today = state.today,
                            onDateChange = { onAction(MatterDetailAction.SetFollowUp(it, matter.followUpTime)) },
                        )
                        if (matter.followUpDate != null) {
                            TimeField(
                                label = stringResource(Res.string.matter_follow_up_time),
                                time = matter.followUpTime,
                                onTimeChange = { onAction(MatterDetailAction.SetFollowUp(matter.followUpDate, it)) },
                            )
                        }
                    }
                    PrumioSectionHeader(
                        title = stringResource(Res.string.matter_history),
                        action = {
                            PrumioButton(
                                text = stringResource(Res.string.matter_add_note),
                                variant = PrumioButtonVariant.Text,
                                onClick = { onAction(MatterDetailAction.StartNote) },
                            )
                        },
                    )
                    detail.history.forEach { entry ->
                        TimelineEntryRow(
                            entry = entry,
                            today = state.today,
                            modifier = if (entry.isEditable) {
                                Modifier.clickable(onClickLabel = stringResource(CoreUiRes.string.action_edit)) {
                                    onAction(MatterDetailAction.EditNote(entry))
                                }
                            } else {
                                Modifier
                            },
                        )
                    }
                }
            }
        }
        MatterDetailSheets(state = state, onAction = onAction)
        if (state.showPermissionExplanation) {
            ReminderPermissionDialog(
                onConfirm = { onAction(MatterDetailAction.ConfirmPermission) },
                onDismiss = { onAction(MatterDetailAction.PermissionResult(false)) },
            )
        }
    }
}

@Composable
private fun StatusActions(status: MatterStatus, onAction: (MatterDetailAction) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm)) {
        if (status == MatterStatus.Resolved) {
            PrumioButton(text = stringResource(Res.string.matter_reopen), onClick = {
                onAction(MatterDetailAction.Reopen)
            })
        } else {
            PrumioButton(
                text = stringResource(
                    if (status ==
                        MatterStatus.Waiting
                    ) {
                        Res.string.matter_resume
                    } else {
                        Res.string.matter_mark_waiting
                    },
                ),
                variant = PrumioButtonVariant.Secondary,
                onClick = {
                    onAction(
                        if (status ==
                            MatterStatus.Waiting
                        ) {
                            MatterDetailAction.Resume
                        } else {
                            MatterDetailAction.StartWaiting
                        },
                    )
                },
            )
            PrumioButton(text = stringResource(Res.string.matter_resolve), onClick = {
                onAction(MatterDetailAction.Resolve)
            })
        }
    }
}

@Composable
private fun MatterActionRow(task: Task, state: MatterDetailUiState, onAction: (MatterDetailAction) -> Unit) {
    TaskRow(
        task = task,
        today = state.today,
        isOverdue = task.id in state.overdueTaskIds,
        onToggle = { onAction(MatterDetailAction.SetTaskDone(task.id, it)) },
        onClick = { onAction(MatterDetailAction.OpenTask(task.id)) },
    )
}

@Preview
@Composable
private fun MatterDetailEmptyPreview() {
    PrumioTheme { MatterDetailScreen(state = MatterDetailUiState(), onAction = {}) }
}
