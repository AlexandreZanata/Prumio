package app.prumio.feature.inbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioBottomSheet
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioDivider
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioIconButton
import app.prumio.core.designsystem.component.PrumioListItem
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.inbox.ConversionType
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import app.prumio.core.ui.component.InboxItemRow
import app.prumio.core.ui.resources.action_save
import app.prumio.core.ui.resources.destination_inbox
import app.prumio.feature.inbox.resources.Res
import app.prumio.feature.inbox.resources.inbox_as_appointment
import app.prumio.feature.inbox.resources.inbox_as_matter
import app.prumio.feature.inbox.resources.inbox_as_task
import app.prumio.feature.inbox.resources.inbox_discard
import app.prumio.feature.inbox.resources.inbox_edit
import app.prumio.feature.inbox.resources.inbox_empty_description
import app.prumio.feature.inbox.resources.inbox_empty_title
import app.prumio.feature.inbox.resources.inbox_item_actions
import app.prumio.feature.inbox.resources.inbox_organize_title
import org.jetbrains.compose.resources.stringResource
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Stateless Inbox tab (docs/product/screens.md#inbox). */
@Composable
internal fun InboxScreen(state: InboxUiState, onAction: (InboxAction) -> Unit, modifier: Modifier = Modifier) {
    PrumioScaffold(
        modifier = modifier,
        topBar = { PrumioTopBar(title = stringResource(CoreUiRes.string.destination_inbox)) },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                state.isLoading -> Unit

                state.items.isEmpty() -> PrumioEmptyState(
                    title = stringResource(Res.string.inbox_empty_title),
                    description = stringResource(Res.string.inbox_empty_description),
                    icon = PrumioIcons.Inbox,
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(state.items, key = { it.id.value }) { item ->
                        InboxItemRow(
                            item = item,
                            now = state.now,
                            onClick = { onAction(InboxAction.Select(item.id)) },
                            trailing = {
                                PrumioIconButton(
                                    icon = PrumioIcons.More,
                                    contentDescription = stringResource(Res.string.inbox_item_actions, item.text),
                                    onClick = { onAction(InboxAction.Select(item.id)) },
                                )
                            },
                        )
                    }
                }
            }
        }
    }
    state.selected?.let { item ->
        PrumioBottomSheet(onDismissRequest = { onAction(InboxAction.DismissSheet) }) {
            val editText = state.editText
            if (editText == null) ItemActions(item, onAction) else EditItem(editText, state, onAction)
        }
    }
}

@Composable
private fun ItemActions(item: InboxItem, onAction: (InboxAction) -> Unit) {
    Column {
        PrumioText(
            text = item.text,
            style = PrumioTextStyle.BodyStrong,
            maxLines = 3,
            modifier = Modifier.padding(horizontal = PrumioTheme.spacing.lg),
        )
        PrumioSectionHeader(
            title = stringResource(Res.string.inbox_organize_title),
            modifier = Modifier.padding(horizontal = PrumioTheme.spacing.lg),
        )
        SheetItem(stringResource(Res.string.inbox_as_task), PrumioIcons.TaskOpen) {
            onAction(InboxAction.Organize(ConversionType.Task))
        }
        SheetItem(stringResource(Res.string.inbox_as_appointment), PrumioIcons.Event) {
            onAction(InboxAction.Organize(ConversionType.Appointment))
        }
        SheetItem(stringResource(Res.string.inbox_as_matter), PrumioIcons.Matters) {
            onAction(InboxAction.Organize(ConversionType.Matter))
        }
        PrumioDivider()
        SheetItem(stringResource(Res.string.inbox_edit), PrumioIcons.Edit) { onAction(InboxAction.StartEdit) }
        SheetItem(stringResource(Res.string.inbox_discard), PrumioIcons.Delete) { onAction(InboxAction.Discard) }
    }
}

@Composable
private fun SheetItem(label: String, icon: ImageVector, onClick: () -> Unit) {
    PrumioListItem(
        headline = label,
        onClick = onClick,
        leading = { PrumioIcon(icon = icon, contentDescription = null) },
    )
}

@Composable
private fun EditItem(text: String, state: InboxUiState, onAction: (InboxAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(PrumioTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
    ) {
        PrumioTextField(
            value = text,
            onValueChange = { onAction(InboxAction.ChangeEditText(it)) },
            label = stringResource(Res.string.inbox_edit),
            errorText = state.editError?.let { stringResource(it) },
            maxLength = TextLimits.INBOX_TEXT,
            minLines = 3,
            modifier = Modifier.fillMaxWidth(),
        )
        PrumioButton(
            text = stringResource(CoreUiRes.string.action_save),
            onClick = { onAction(InboxAction.SaveEdit) },
            enabled = text.isNotBlank(),
            modifier = Modifier.align(Alignment.End),
        )
    }
}

@Preview
@Composable
private fun InboxScreenPreview() {
    val now = Instant.parse("2026-11-11T12:00:00Z")
    PrumioTheme {
        InboxScreen(
            state = InboxUiState(
                items = listOf(
                    InboxItem(InboxItemId("1"), "Ask Ana about the invoice", now - 2.days, now - 2.days),
                    InboxItem(InboxItemId("2"), "Book the car service", now - 5.days, now - 5.days),
                ),
                now = now,
                isLoading = false,
            ),
            onAction = {},
        )
    }
}
