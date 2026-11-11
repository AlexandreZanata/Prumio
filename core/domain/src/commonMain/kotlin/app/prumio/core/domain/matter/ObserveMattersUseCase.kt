package app.prumio.core.domain.matter

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.domain.task.TaskRepository
import app.prumio.core.model.MatterStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlin.time.Duration.Companion.days

/** The Matters tab, each matter in exactly one group. Rule: BR-MATTER-013. */
data class MattersOverview(
    val needsAttention: List<MatterSummary>,
    val waiting: List<MatterSummary>,
    val open: List<MatterSummary>,
    val resolved: List<MatterSummary>,
)

/** Rules: BR-MATTER-005, BR-MATTER-010, BR-MATTER-013. */
class ObserveMattersUseCase(
    private val matters: MatterRepository,
    private val tasks: TaskRepository,
    private val time: TimeProvider,
) {
    operator fun invoke(): Flow<MattersOverview> = combine(
        matters.observeUnresolved(),
        matters.observeResolvedSince(time.now() - 90.days),
        tasks.observeOpen(),
    ) { unresolved, resolved, openTasks ->
        val today = time.today()
        val actionsByMatter = openTasks.groupBy { it.matterId }
        val summaries = unresolved.map { matter ->
            val actions = actionsByMatter[matter.id].orEmpty()
            MatterSummary(
                matter = matter,
                nextAction = NextActionPolicy.nextAction(actions),
                needsAttention = MatterAttentionPolicy.needsAttention(matter, actions, today),
            )
        }
        val (attention, calm) = summaries.partition { it.needsAttention }
        MattersOverview(
            needsAttention = attention.sortedWith(compareBy(MatterAttentionPolicy.order) { it.matter }),
            waiting = calm.filter { it.matter.status == MatterStatus.Waiting }
                .sortedWith(
                    compareBy({
                        it.matter.followUpDate == null
                    }, { it.matter.followUpDate }, { it.matter.id.value }),
                ),
            open = calm.filter { it.matter.status == MatterStatus.Open }
                .sortedWith(
                    compareByDescending<MatterSummary> {
                        it.matter.lastActivityAt
                    }.thenBy { it.matter.id.value },
                ),
            resolved = resolved.map { MatterSummary(it, nextAction = null, needsAttention = false) },
        )
    }
}
