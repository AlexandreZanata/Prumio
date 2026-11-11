package app.prumio.core.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

/** Lifecycle of a matter (docs/domain/state-machines.md#matter). */
enum class MatterStatus {
    Open,
    Waiting,
    Resolved,
}

/** Life's unfinished business: "what do I need to resolve?" (docs/domain/model.md#matter). */
data class Matter(
    val id: MatterId,
    val title: String,
    val description: String? = null,
    val status: MatterStatus = MatterStatus.Open,
    val waitingFor: String? = null,
    val followUpDate: LocalDate? = null,
    val followUpTime: LocalTime? = null,
    val resolvedAt: Instant? = null,
    val lastActivityAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isResolved: Boolean get() = status == MatterStatus.Resolved
}

/** Kinds of history entries. Only [Note] entries are written, edited and deleted by the user. */
enum class MatterEntryKind {
    Created,
    Note,
    StatusChanged,
    ActionLinked,
    ActionCompleted,
    ActionDismissed,
    ActionUnlinked,
    FollowUpSet,
    FollowUpCleared,
}

/** One line of a matter's history (docs/domain/model.md#matterentry). */
data class MatterEntry(
    val id: MatterEntryId,
    val matterId: MatterId,
    val kind: MatterEntryKind,
    val text: String? = null,
    val fromStatus: MatterStatus? = null,
    val toStatus: MatterStatus? = null,
    val taskId: TaskId? = null,
    val occurredOn: LocalDate,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    val isEditable: Boolean get() = kind == MatterEntryKind.Note
}
