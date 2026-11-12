package app.prumio.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Every destination of the app. Rules (docs/architecture/navigation.md):
// - Keys carry only primitive arguments (ids as String), never domain objects.
// - Every key must be registered in navKeySerializersModule (NavKeySerialization.kt).
// - Exactly one feature module provides the entry for each key.

/** Top-level destinations shown in the bottom bar. */
sealed interface TopLevelNavKey : NavKey

@Serializable
data object TodayKey : TopLevelNavKey

@Serializable
data object AgendaKey : TopLevelNavKey

@Serializable
data object MattersKey : TopLevelNavKey

@Serializable
data object InboxKey : TopLevelNavKey

/** Destinations shown as a dialog over the current screen; the underlying screen stays visible. */
sealed interface OverlayNavKey : NavKey

/** "Add anything" quick capture. */
@Serializable
data object CaptureKey : OverlayNavKey

@Serializable
data object SearchKey : NavKey

@Serializable
data object SettingsKey : NavKey

// Tasks (:feature:tasks)

/** New task when [taskId] is null; [matterId] pre-links it (BR-MATTER-005 "Add next action"). */
@Serializable
data class TaskEditorKey(val taskId: String? = null, val matterId: String? = null) : NavKey

@Serializable
data class TaskDetailKey(val taskId: String) : NavKey

/** [filter] is a `TaskListFilter` name; null opens the default segment. */
@Serializable
data class AllTasksKey(val filter: String? = null) : NavKey

// Appointments (:feature:agenda)

/** New appointment when [appointmentId] is null, starting on [date] (ISO) when given. */
@Serializable
data class AppointmentEditorKey(val appointmentId: String? = null, val date: String? = null) : NavKey

@Serializable
data class AppointmentDetailKey(val appointmentId: String) : NavKey

// Matters (:feature:matters)

/** New matter when [matterId] is null. */
@Serializable
data class MatterEditorKey(val matterId: String? = null) : NavKey

/** [promptNextAction] opens the optional BR-MATTER-006 question after closing the last action. */
@Serializable
data class MatterDetailKey(val matterId: String, val promptNextAction: Boolean = false) : NavKey

@Serializable
data object ReminderReliabilityKey : NavKey

@Serializable
data class SnoozeKey(val alarmKey: String) : OverlayNavKey
