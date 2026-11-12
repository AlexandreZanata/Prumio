package app.prumio.core.data.storage

import app.prumio.core.database.PrumioDatabase
import app.prumio.core.domain.search.searchableText
import app.prumio.core.model.Appointment
import app.prumio.core.model.AppointmentTiming
import app.prumio.core.model.InboxItem
import app.prumio.core.model.Matter
import app.prumio.core.model.MatterEntry
import app.prumio.core.model.ReminderOwner
import app.prumio.core.model.Task

// Synchronous upserts shared by every repository. Call them inside `database.transaction { }` on the
// IO dispatcher (docs/architecture/data-layer.md). Each one also maintains `search_text` (BR-SEARCH-002).

internal fun PrumioDatabase.upsertInboxItem(item: InboxItem) {
    val exists = inbox_itemQueries.selectById(item.id.value).executeAsOneOrNull() != null
    if (exists) {
        inbox_itemQueries.update(
            text = item.text,
            searchText = item.searchableText(),
            updatedAt = item.updatedAt.toColumn(),
            id = item.id.value,
        )
    } else {
        inbox_itemQueries.insert(
            id = item.id.value,
            text = item.text,
            search_text = item.searchableText(),
            created_at = item.createdAt.toColumn(),
            updated_at = item.updatedAt.toColumn(),
        )
    }
}

internal fun PrumioDatabase.upsertTask(task: Task) {
    replaceReminders(ReminderOwner.Task(task.id), task.reminders)
    // Recurrence is stored once its codec exists (task P5.01); no flow can create it before.
    check(task.recurrence == null) { "Recurring tasks are stored from phase 5 (P5.01)" }
    if (taskQueries.countById(task.id.value).executeAsOne() > 0) {
        taskQueries.update(
            title = task.title,
            notes = task.notes,
            status = task.status.toColumn(),
            dueDate = task.dueDate?.toColumn(),
            dueTime = task.dueTime?.toColumn(),
            recurrence = null,
            recurrenceBasis = null,
            matterId = task.matterId?.value,
            completedAt = task.completedAt?.toColumn(),
            dismissedAt = task.dismissedAt?.toColumn(),
            searchText = task.searchableText(),
            updatedAt = task.updatedAt.toColumn(),
            id = task.id.value,
        )
    } else {
        taskQueries.insert(
            id = task.id.value,
            title = task.title,
            notes = task.notes,
            status = task.status.toColumn(),
            due_date = task.dueDate?.toColumn(),
            due_time = task.dueTime?.toColumn(),
            recurrence = null,
            recurrence_basis = null,
            matter_id = task.matterId?.value,
            completed_at = task.completedAt?.toColumn(),
            dismissed_at = task.dismissedAt?.toColumn(),
            search_text = task.searchableText(),
            created_at = task.createdAt.toColumn(),
            updated_at = task.updatedAt.toColumn(),
        )
    }
}

internal fun PrumioDatabase.upsertAppointment(appointment: Appointment) {
    replaceReminders(ReminderOwner.Appointment(appointment.id), appointment.reminders)
    check(appointment.recurrence == null) { "Recurring appointments are stored from phase 5 (P5.05)" }
    val columns = TimingColumns.of(appointment.timing)
    if (appointmentQueries.countById(appointment.id.value).executeAsOne() > 0) {
        appointmentQueries.update(
            title = appointment.title,
            notes = appointment.notes,
            location = appointment.location,
            allDay = columns.allDay,
            startDate = columns.startDate,
            startTime = columns.startTime,
            endDate = columns.endDate,
            endTime = columns.endTime,
            timeZone = columns.timeZone,
            recurrence = null,
            matterId = appointment.matterId?.value,
            searchText = appointment.searchableText(),
            updatedAt = appointment.updatedAt.toColumn(),
            id = appointment.id.value,
        )
    } else {
        appointmentQueries.insert(
            id = appointment.id.value,
            title = appointment.title,
            notes = appointment.notes,
            location = appointment.location,
            all_day = columns.allDay,
            start_date = columns.startDate,
            start_time = columns.startTime,
            end_date = columns.endDate,
            end_time = columns.endTime,
            time_zone = columns.timeZone,
            recurrence = null,
            matter_id = appointment.matterId?.value,
            search_text = appointment.searchableText(),
            created_at = appointment.createdAt.toColumn(),
            updated_at = appointment.updatedAt.toColumn(),
        )
    }
}

internal fun PrumioDatabase.upsertMatter(matter: Matter) {
    if (matterQueries.countById(matter.id.value).executeAsOne() > 0) {
        matterQueries.update(
            title = matter.title,
            description = matter.description,
            status = matter.status.toColumn(),
            waitingFor = matter.waitingFor,
            followUpDate = matter.followUpDate?.toColumn(),
            followUpTime = matter.followUpTime?.toColumn(),
            resolvedAt = matter.resolvedAt?.toColumn(),
            lastActivityAt = matter.lastActivityAt.toColumn(),
            searchText = matter.searchableText(),
            updatedAt = matter.updatedAt.toColumn(),
            id = matter.id.value,
        )
    } else {
        matterQueries.insert(
            id = matter.id.value,
            title = matter.title,
            description = matter.description,
            status = matter.status.toColumn(),
            waiting_for = matter.waitingFor,
            follow_up_date = matter.followUpDate?.toColumn(),
            follow_up_time = matter.followUpTime?.toColumn(),
            resolved_at = matter.resolvedAt?.toColumn(),
            last_activity_at = matter.lastActivityAt.toColumn(),
            search_text = matter.searchableText(),
            created_at = matter.createdAt.toColumn(),
            updated_at = matter.updatedAt.toColumn(),
        )
    }
}

internal fun PrumioDatabase.upsertMatterEntry(entry: MatterEntry) {
    if (matter_entryQueries.countById(entry.id.value).executeAsOne() > 0) {
        matter_entryQueries.update(
            text = entry.text,
            occurredOn = entry.occurredOn.toColumn(),
            searchText = entry.searchableText(),
            updatedAt = entry.updatedAt.toColumn(),
            id = entry.id.value,
        )
    } else {
        matter_entryQueries.insert(
            id = entry.id.value,
            matter_id = entry.matterId.value,
            kind = entry.kind.toColumn(),
            text = entry.text,
            from_status = entry.fromStatus?.toColumn(),
            to_status = entry.toStatus?.toColumn(),
            task_id = entry.taskId?.value,
            occurred_on = entry.occurredOn.toColumn(),
            search_text = entry.searchableText(),
            created_at = entry.createdAt.toColumn(),
            updated_at = entry.updatedAt.toColumn(),
        )
    }
}

/** Column representation of an appointment timing (docs/architecture/database-schema.md). */
internal data class TimingColumns(
    val allDay: Long,
    val startDate: String,
    val startTime: String?,
    val endDate: String,
    val endTime: String?,
    val timeZone: String?,
) {
    companion object {
        fun of(timing: AppointmentTiming): TimingColumns = when (timing) {
            is AppointmentTiming.Timed -> TimingColumns(
                allDay = 0,
                startDate = timing.start.date.toColumn(),
                startTime = timing.start.time.toColumn(),
                endDate = (timing.end ?: timing.start).date.toColumn(),
                endTime = timing.end?.time?.toColumn(),
                timeZone = timing.timeZone.id,
            )

            is AppointmentTiming.AllDay -> TimingColumns(
                allDay = 1,
                startDate = timing.startDate.toColumn(),
                startTime = null,
                endDate = timing.endDate.toColumn(),
                endTime = null,
                timeZone = null,
            )
        }
    }
}
