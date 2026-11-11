package app.prumio.core.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import app.prumio.core.designsystem.component.PrumioCheckbox
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.model.Task
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.task_complete
import app.prumio.core.ui.resources.task_matter
import app.prumio.core.ui.resources.task_reopen
import kotlinx.datetime.LocalDate
import org.jetbrains.compose.resources.stringResource

/**
 * How a task looks everywhere (Today, lists, search, matter detail): completion control, title,
 * due label and the matter it moves forward. Overdue is decided by the domain and passed in.
 */
@Composable
fun TaskRow(
    task: Task,
    today: LocalDate,
    isOverdue: Boolean,
    onToggle: (Boolean) -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    matterTitle: String? = null,
) {
    val done = !task.isOpen
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(end = PrumioTheme.spacing.lg, top = PrumioTheme.spacing.xs, bottom = PrumioTheme.spacing.xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PrumioCheckbox(
            checked = done,
            onCheckedChange = onToggle,
            contentDescription = stringResource(
                if (done) Res.string.task_reopen else Res.string.task_complete,
                task.title,
            ),
        )
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.xxs)) {
            PrumioText(
                text = task.title,
                style = PrumioTextStyle.BodyStrong,
                color = if (done) PrumioTheme.colors.onSurfaceVariant else PrumioTheme.colors.onBackground,
                textDecoration = if (done) TextDecoration.LineThrough else null,
                maxLines = 2,
            )
            task.dueDate?.let {
                DueLabel(date = it, time = task.dueTime, today = today, isOverdue = isOverdue && !done)
            }
            matterTitle?.let {
                PrumioText(
                    text = stringResource(Res.string.task_matter, it),
                    style = PrumioTextStyle.Caption,
                    color = PrumioTheme.colors.primary,
                    maxLines = 1,
                )
            }
        }
    }
}
