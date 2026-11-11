package app.prumio.core.domain.inbox

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.id.IdGenerator
import app.prumio.core.common.map
import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.notFound
import app.prumio.core.domain.error.storageCall
import app.prumio.core.domain.matter.MatterHistory
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.InboxItemId
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours

/** What an inbox item can become. */
enum class ConversionType {
    Task,
    Appointment,
    Matter,
}

/**
 * Turns an inbox item into a task, an appointment or a matter.
 *
 * Rules: BR-INBOX-004 (first line → title, rest → notes; one transaction), BR-APPT-001 (a new
 * appointment starts today at the next full hour), BR-MATTER-001 (a matter starts with a Created entry).
 */
class ConvertInboxItemUseCase(
    private val inbox: InboxRepository,
    private val ids: IdGenerator,
    private val time: TimeProvider,
) {
    private val history = MatterHistory(ids, time)

    suspend operator fun invoke(id: InboxItemId, into: ConversionType): Outcome<ConversionTarget, DomainError> {
        val item =
            storageCall { inbox.get(id) }.valueOr { return it.asFailure() } ?: return notFound("InboxItem", id.value)
        val lines = item.text.lines()
        val title = lines.first().trim().take(TextLimits.TITLE)
        val rest = lines.drop(1).joinToString("\n").trim().takeIf { it.isNotEmpty() }
        val now = time.now()
        val target = when (into) {
            ConversionType.Task -> ConversionTarget.ToTask(
                Task(id = TaskId(ids.newId()), title = title, notes = rest, createdAt = now, updatedAt = now),
            )

            ConversionType.Appointment -> ConversionTarget.ToAppointment(
                Appointment(
                    id = AppointmentId(ids.newId()),
                    title = title,
                    notes = rest,
                    timing = AppointmentTiming.Timed(start = nextFullHour(), end = null, timeZone = time.timeZone()),
                    createdAt = now,
                    updatedAt = now,
                ),
            )

            ConversionType.Matter -> {
                val matter = Matter(
                    id = MatterId(ids.newId()),
                    title = title,
                    description = rest,
                    lastActivityAt = now,
                    createdAt = now,
                    updatedAt = now,
                )
                ConversionTarget.ToMatter(matter, history.entry(matter.id, MatterEntryKind.Created))
            }
        }
        return storageCall { inbox.convert(id, target) }.map { target }
    }

    private fun nextFullHour(): LocalDateTime {
        val zone = time.timeZone()
        val local = time.localNow()
        val startOfHour = LocalDateTime(local.date, LocalTime(local.hour, 0)).toInstant(zone)
        return (startOfHour + 1.hours).toLocalDateTime(zone)
    }
}
