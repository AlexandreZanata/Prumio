package app.prumio.core.ui.label

import androidx.compose.runtime.Composable
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.TaskStatus
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.matter_status_open
import app.prumio.core.ui.resources.matter_status_resolved
import app.prumio.core.ui.resources.matter_status_waiting
import app.prumio.core.ui.resources.task_status_completed
import app.prumio.core.ui.resources.task_status_dismissed
import app.prumio.core.ui.resources.task_status_open
import org.jetbrains.compose.resources.stringResource

/** "Open" / "Waiting" / "Resolved" (Em aberto · Aguardando · Resolvida). */
@Composable
fun MatterStatus.label(): String = stringResource(
    when (this) {
        MatterStatus.Open -> Res.string.matter_status_open
        MatterStatus.Waiting -> Res.string.matter_status_waiting
        MatterStatus.Resolved -> Res.string.matter_status_resolved
    },
)

@Composable
fun TaskStatus.label(): String = stringResource(
    when (this) {
        TaskStatus.Open -> Res.string.task_status_open
        TaskStatus.Completed -> Res.string.task_status_completed
        TaskStatus.Dismissed -> Res.string.task_status_dismissed
    },
)
