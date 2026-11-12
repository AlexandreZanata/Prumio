package app.prumio.core.model

import kotlin.time.Instant

/** A user's reminder wish, relative to its owner's anchor. Rules: BR-TASK-006, BR-APPT-004, BR-REM-018. */
data class Reminder(val id: ReminderId, val owner: ReminderOwner, val offsetMinutes: Int, val createdAt: Instant)
