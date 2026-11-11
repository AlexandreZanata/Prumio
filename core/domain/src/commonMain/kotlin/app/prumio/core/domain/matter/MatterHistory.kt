package app.prumio.core.domain.matter

import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.today
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task

/**
 * Creates history entries and touches activity: the single place that writes a matter's history
 * (BR-MATTER-003, BR-MATTER-012). Entries are dated today unless stated otherwise.
 */
internal class MatterHistory(private val ids: IdGenerator, private val time: TimeProvider) {
    fun entry(
        matterId: MatterId,
        kind: MatterEntryKind,
        text: String? = null,
        from: MatterStatus? = null,
        to: MatterStatus? = null,
        task: Task? = null,
    ): MatterEntry {
        val now = time.now()
        return MatterEntry(
            id = MatterEntryId(ids.newId()),
            matterId = matterId,
            kind = kind,
            text = text ?: task?.title,
            fromStatus = from,
            toStatus = to,
            taskId = task?.id,
            occurredOn = time.today(),
            createdAt = now,
            updatedAt = now,
        )
    }

    fun statusChanged(matter: Matter, to: MatterStatus): MatterEntry =
        entry(matter.id, MatterEntryKind.StatusChanged, from = matter.status, to = to)

    /** The follow-up is recorded as ISO text, e.g. "2026-11-14" or "2026-11-14T09:00". */
    fun followUpSet(matterId: MatterId, followUp: FollowUp): MatterEntry {
        val text = followUp.time?.let { "${followUp.date}T$it" } ?: followUp.date.toString()
        return entry(matterId, MatterEntryKind.FollowUpSet, text = text)
    }

    /** [matter] with its activity time set to now (BR-MATTER-012). */
    fun touched(matter: Matter): Matter = matter.copy(lastActivityAt = time.now())
}
