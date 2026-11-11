package app.prumio.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task
import app.prumio.core.ui.format.dateTimeText
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.matter_follow_up
import app.prumio.core.ui.resources.matter_next_action
import app.prumio.core.ui.resources.matter_no_next_action
import app.prumio.core.ui.resources.matter_waiting_for
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/** How a matter looks in lists: status, title, next action (BR-MATTER-005), who it waits for and the follow-up. */
@Composable
fun MatterCard(
    matter: Matter,
    nextAction: Task?,
    today: LocalDate,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioCard(modifier = modifier.fillMaxWidth(), onClick = onClick) {
        Column(
            modifier = Modifier.padding(PrumioTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xs),
        ) {
            MatterStatusChip(status = matter.status)
            PrumioText(
                text = matter.title,
                style = PrumioTextStyle.Subtitle,
                color = PrumioTheme.colors.onSurface,
                maxLines = 2,
            )
            if (matter.status != MatterStatus.Resolved) {
                PrumioText(
                    text = nextAction?.let { stringResource(Res.string.matter_next_action, it.title) }
                        ?: stringResource(Res.string.matter_no_next_action),
                    style = PrumioTextStyle.Body,
                    color = if (nextAction ==
                        null
                    ) {
                        PrumioTheme.colors.onSurfaceVariant
                    } else {
                        PrumioTheme.colors.onSurface
                    },
                    maxLines = 2,
                )
            }
            matter.waitingFor?.let { Caption(stringResource(Res.string.matter_waiting_for, it)) }
            matter.followUpDate?.let { date ->
                Caption(stringResource(Res.string.matter_follow_up, dateTimeText(date, matter.followUpTime, today)))
            }
        }
    }
}

@Composable
private fun Caption(text: String) {
    PrumioText(text = text, style = PrumioTextStyle.Caption, color = PrumioTheme.colors.onSurfaceVariant, maxLines = 1)
}
