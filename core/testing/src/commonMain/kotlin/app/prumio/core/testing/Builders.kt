package app.prumio.core.testing

import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentId
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.InboxItem
import app.prumio.core.model.InboxItemId
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.MatterEntryId
import app.prumio.core.model.MatterEntryKind
import app.prumio.core.model.MatterId
import app.prumio.core.model.MatterStatus
import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import app.prumio.core.model.TaskStatus
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

// Builders with sensible defaults, so tests state only what matters for them.

/** Default instant used by builders: the default start of [FakeTimeProvider] (2026-01-15 09:00 UTC). */
val TestInstant: Instant = Instant.parse("2026-01-15T09:00:00Z")

fun anInboxItem(id: String = "inbox-1", text: String = "Something to remember", createdAt: Instant = TestInstant) =
    InboxItem(id = InboxItemId(id), text = text, createdAt = createdAt, updatedAt = createdAt)

fun aTask(
    id: String = "task-1",
    title: String = "Task $id",
    notes: String? = null,
    status: TaskStatus = TaskStatus.Open,
    dueDate: LocalDate? = null,
    dueTime: LocalTime? = null,
    matterId: String? = null,
    createdAt: Instant = TestInstant,
    completedAt: Instant? = if (status == TaskStatus.Completed) createdAt else null,
    dismissedAt: Instant? = if (status == TaskStatus.Dismissed) createdAt else null,
) = Task(
    id = TaskId(id),
    title = title,
    notes = notes,
    status = status,
    dueDate = dueDate,
    dueTime = dueTime,
    matterId = matterId?.let(::MatterId),
    completedAt = completedAt,
    dismissedAt = dismissedAt,
    createdAt = createdAt,
    updatedAt = createdAt,
)

fun anAppointment(
    id: String = "appointment-1",
    title: String = "Appointment $id",
    timing: AppointmentTiming = timedAt(LocalDateTime(2026, 1, 15, 16, 0)),
    location: String? = null,
    matterId: String? = null,
    createdAt: Instant = TestInstant,
) = Appointment(
    id = AppointmentId(id),
    title = title,
    location = location,
    timing = timing,
    matterId = matterId?.let(::MatterId),
    createdAt = createdAt,
    updatedAt = createdAt,
)

fun timedAt(start: LocalDateTime, end: LocalDateTime? = null, zone: TimeZone = TimeZone.UTC) =
    AppointmentTiming.Timed(start = start, end = end, timeZone = zone)

fun allDay(startDate: LocalDate, endDate: LocalDate = startDate) = AppointmentTiming.AllDay(startDate, endDate)

fun aMatter(
    id: String = "matter-1",
    title: String = "Matter $id",
    status: MatterStatus = MatterStatus.Open,
    waitingFor: String? = null,
    followUpDate: LocalDate? = null,
    lastActivityAt: Instant = TestInstant,
    createdAt: Instant = TestInstant,
) = Matter(
    id = MatterId(id),
    title = title,
    status = status,
    waitingFor = waitingFor,
    followUpDate = followUpDate,
    resolvedAt = if (status == MatterStatus.Resolved) lastActivityAt else null,
    lastActivityAt = lastActivityAt,
    createdAt = createdAt,
    updatedAt = createdAt,
)

fun aMatterEntry(
    id: String = "entry-1",
    matterId: String = "matter-1",
    kind: MatterEntryKind = MatterEntryKind.Note,
    text: String? = "A note",
    occurredOn: LocalDate = LocalDate(2026, 1, 15),
    createdAt: Instant = TestInstant,
) = MatterEntry(
    id = MatterEntryId(id),
    matterId = MatterId(matterId),
    kind = kind,
    text = text,
    occurredOn = occurredOn,
    createdAt = createdAt,
    updatedAt = createdAt,
)
