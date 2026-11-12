package app.prumio.feature.matters.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioBottomSheet
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioChip
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.matter.OpenActionsChoice
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.ui.component.DateField
import app.prumio.core.ui.resources.action_done
import app.prumio.core.ui.resources.action_save
import app.prumio.feature.matters.resources.Res
import app.prumio.feature.matters.resources.matter_add_next_action
import app.prumio.feature.matters.resources.matter_follow_up_label
import app.prumio.feature.matters.resources.matter_in_days
import app.prumio.feature.matters.resources.matter_mark_waiting
import app.prumio.feature.matters.resources.matter_next_action_prompt
import app.prumio.feature.matters.resources.matter_note
import app.prumio.feature.matters.resources.matter_note_date
import app.prumio.feature.matters.resources.matter_note_delete
import app.prumio.feature.matters.resources.matter_resolve
import app.prumio.feature.matters.resources.matter_resolve_dismiss
import app.prumio.feature.matters.resources.matter_resolve_keep
import app.prumio.feature.matters.resources.matter_resolve_text
import app.prumio.feature.matters.resources.matter_resolve_title
import app.prumio.feature.matters.resources.matter_waiting_for_label
import app.prumio.feature.matters.resources.matter_waiting_for_placeholder
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.plus
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Contextual waiting, resolution and dated-note editors. */
@Composable
internal fun MatterDetailSheets(
    state: MatterDetailUiState,
    onAction: (MatterDetailAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        state.waiting?.let { draft ->
            PrumioBottomSheet(onDismissRequest = { onAction(MatterDetailAction.Dismiss) }) {
                PrumioText(text = stringResource(Res.string.matter_mark_waiting), style = PrumioTextStyle.Title)
                PrumioTextField(
                    value = draft.waitingFor,
                    onValueChange = { onAction(MatterDetailAction.ChangeWaiting(draft.copy(waitingFor = it))) },
                    label = stringResource(Res.string.matter_waiting_for_label),
                    placeholder = stringResource(Res.string.matter_waiting_for_placeholder),
                    maxLength = TextLimits.WAITING_FOR,
                    modifier = Modifier.fillMaxWidth(),
                )
                listOf(3, 7, 14).forEach { days ->
                    PrumioChip(
                        label = stringResource(Res.string.matter_in_days, days),
                        selected = draft.followUp == state.today.plus(DatePeriod(days = days)),
                        onClick = {
                            onAction(
                                MatterDetailAction.ChangeWaiting(
                                    draft.copy(followUp = state.today.plus(DatePeriod(days = days))),
                                ),
                            )
                        },
                    )
                }
                DateField(
                    label = stringResource(Res.string.matter_follow_up_label),
                    date = draft.followUp,
                    today = state.today,
                    onDateChange = { onAction(MatterDetailAction.ChangeWaiting(draft.copy(followUp = it))) },
                )
                PrumioButton(text = stringResource(CoreUiRes.string.action_done), onClick = {
                    onAction(MatterDetailAction.ConfirmWaiting)
                })
            }
        }
        if (state.isConfirmingResolve) {
            PrumioBottomSheet(onDismissRequest = { onAction(MatterDetailAction.Dismiss) }) {
                PrumioText(text = stringResource(Res.string.matter_resolve_title), style = PrumioTextStyle.Title)
                PrumioText(
                    text = pluralStringResource(
                        Res.plurals.matter_resolve_text,
                        state.detail?.openActions.orEmpty().size,
                        state.detail?.openActions.orEmpty().size,
                    ),
                )
                PrumioButton(
                    text = stringResource(Res.string.matter_resolve_dismiss),
                    onClick = { onAction(MatterDetailAction.ConfirmResolve(OpenActionsChoice.Dismiss)) },
                )
                PrumioButton(
                    text = stringResource(Res.string.matter_resolve_keep),
                    variant = PrumioButtonVariant.Secondary,
                    onClick = { onAction(MatterDetailAction.ConfirmResolve(OpenActionsChoice.Unlink)) },
                )
            }
        }
        if (state.promptNextAction) {
            PrumioBottomSheet(onDismissRequest = { onAction(MatterDetailAction.Dismiss) }) {
                PrumioText(text = stringResource(Res.string.matter_next_action_prompt), style = PrumioTextStyle.Title)
                PrumioButton(text = stringResource(Res.string.matter_add_next_action), onClick = {
                    onAction(MatterDetailAction.AddNextAction)
                })
                PrumioButton(text = stringResource(Res.string.matter_mark_waiting), onClick = {
                    onAction(MatterDetailAction.StartWaiting)
                })
                PrumioButton(text = stringResource(Res.string.matter_resolve), onClick = {
                    onAction(MatterDetailAction.Resolve)
                })
            }
        }
        state.note?.let { note ->
            PrumioBottomSheet(onDismissRequest = { onAction(MatterDetailAction.Dismiss) }) {
                Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md)) {
                    PrumioTextField(
                        value = note.text,
                        onValueChange = { onAction(MatterDetailAction.ChangeNote(it, note.date)) },
                        label = stringResource(Res.string.matter_note),
                        minLines = 3,
                        maxLength = TextLimits.MATTER_NOTE,
                        errorText = note.error?.let { stringResource(it) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    DateField(
                        label = stringResource(Res.string.matter_note_date),
                        date = note.date,
                        today = state.today,
                        clearable = false,
                        onDateChange = { date -> date?.let { onAction(MatterDetailAction.ChangeNote(note.text, it)) } },
                    )
                    PrumioButton(
                        text = stringResource(CoreUiRes.string.action_save),
                        enabled = note.text.isNotBlank(),
                        onClick = { onAction(MatterDetailAction.SaveNote) },
                    )
                    if (note.entryId != null) {
                        PrumioButton(
                            text = stringResource(Res.string.matter_note_delete),
                            variant = PrumioButtonVariant.Destructive,
                            onClick = { onAction(MatterDetailAction.DeleteNote) },
                        )
                    }
                }
            }
        }
    }
}
