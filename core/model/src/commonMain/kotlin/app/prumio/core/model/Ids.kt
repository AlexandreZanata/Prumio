package app.prumio.core.model

import kotlin.jvm.JvmInline

// Typed identifiers (ADR-0011). Never pass a raw String where one of these is expected.
// Values are UUID strings created by IdGenerator (:core:common).

@JvmInline
value class InboxItemId(val value: String)

@JvmInline
value class TaskId(val value: String)

@JvmInline
value class AppointmentId(val value: String)

@JvmInline
value class MatterId(val value: String)

@JvmInline
value class MatterEntryId(val value: String)

@JvmInline
value class ReminderId(val value: String)
