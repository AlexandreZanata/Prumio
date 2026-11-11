package app.prumio.core.domain.matter

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/** Everything the matter detail screen shows. */
data class MatterDetail(
    val matter: Matter,
    val history: List<MatterEntry>,
    val nextAction: Task?,
    val openActions: List<Task>,
    val closedActions: List<Task>,
    val needsAttention: Boolean,
)

/** One matter with its history and actions, live; null once deleted. Rules: BR-MATTER-005, BR-MATTER-010. */
class ObserveMatterDetailUseCase(
    private val matters: MatterRepository,
    private val tasks: TaskRepository,
    private val time: TimeProvider,
) {
    operator fun invoke(id: MatterId): Flow<MatterDetail?> = combine(
        matters.observe(id),
        matters.observeHistory(id),
        tasks.observeByMatter(id),
    ) { matter, history, linked ->
        matter?.let {
            val open = NextActionPolicy.openActions(linked)
            MatterDetail(
                matter = it,
                history = history,
                nextAction = open.firstOrNull(),
                openActions = open,
                closedActions = linked.filterNot { task -> task.isOpen },
                needsAttention = MatterAttentionPolicy.needsAttention(it, open, time.today()),
            )
        }
    }
}
