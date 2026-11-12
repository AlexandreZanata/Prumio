package app.prumio.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import app.prumio.core.designsystem.component.PrumioIcon
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.ui.format.dateTimeText
import app.prumio.core.ui.format.relativeDateText
import app.prumio.core.ui.label.label
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.entry_action_completed
import app.prumio.core.ui.resources.entry_action_dismissed
import app.prumio.core.ui.resources.entry_action_linked
import app.prumio.core.ui.resources.entry_action_unlinked
import app.prumio.core.ui.resources.entry_created
import app.prumio.core.ui.resources.entry_follow_up_cleared
import app.prumio.core.ui.resources.entry_follow_up_set
import app.prumio.core.ui.resources.entry_status_changed
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import org.jetbrains.compose.resources.stringResource

/** One line of a matter's history (BR-MATTER-003): icon, what happened, when. */
@Composable
fun TimelineEntryRow(entry: MatterEntry, today: LocalDate, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = PrumioTheme.spacing.xs),
        horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
    ) {
        PrumioIcon(
            icon = entry.icon(),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = PrumioTheme.colors.onSurfaceVariant,
        )
        Column(verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xxs)) {
            PrumioText(text = entry.description(today), style = PrumioTextStyle.Body)
            PrumioText(
                text = entry.occurredOn.relativeDateText(today),
                style = PrumioTextStyle.Caption,
                color = PrumioTheme.colors.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun MatterEntry.icon(): ImageVector = when (kind) {
    MatterEntryKind.Created -> PrumioIcons.Add
    MatterEntryKind.Note -> PrumioIcons.Notes
    MatterEntryKind.StatusChanged -> PrumioIcons.History
    MatterEntryKind.ActionLinked -> PrumioIcons.TaskOpen
    MatterEntryKind.ActionCompleted -> PrumioIcons.TaskDone
    MatterEntryKind.ActionDismissed, MatterEntryKind.ActionUnlinked -> PrumioIcons.Close
    MatterEntryKind.FollowUpSet, MatterEntryKind.FollowUpCleared -> PrumioIcons.Event
}

@Composable
private fun MatterEntry.description(today: LocalDate): String {
    val subject = text.orEmpty()
    return when (kind) {
        MatterEntryKind.Note -> subject
        MatterEntryKind.Created -> stringResource(Res.string.entry_created)
        MatterEntryKind.StatusChanged -> stringResource(Res.string.entry_status_changed, toStatus?.label().orEmpty())
        MatterEntryKind.ActionLinked -> stringResource(Res.string.entry_action_linked, subject)
        MatterEntryKind.ActionCompleted -> stringResource(Res.string.entry_action_completed, subject)
        MatterEntryKind.ActionDismissed -> stringResource(Res.string.entry_action_dismissed, subject)
        MatterEntryKind.ActionUnlinked -> stringResource(Res.string.entry_action_unlinked, subject)
        MatterEntryKind.FollowUpSet -> stringResource(Res.string.entry_follow_up_set, followUpText(subject, today))
        MatterEntryKind.FollowUpCleared -> stringResource(Res.string.entry_follow_up_cleared)
    }
}

/** FollowUpSet entries store the follow-up as ISO text (docs/domain/model.md#matterentry). */
@Composable
private fun followUpText(iso: String, today: LocalDate): String =
    runCatching { LocalDateTime.parse(iso) }.getOrNull()?.let { dateTimeText(it.date, it.time, today) }
        ?: runCatching { LocalDate.parse(iso) }.getOrNull()?.let { dateTimeText(it, null, today) }
        ?: iso
