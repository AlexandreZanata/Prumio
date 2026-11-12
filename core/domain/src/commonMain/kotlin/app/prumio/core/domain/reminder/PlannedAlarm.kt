package app.prumio.core.domain.reminder

import kotlin.time.Instant

data class PlannedAlarm(
    val key: AlarmKey,
    val triggerAt: Instant,
    val kind: AlarmKind,
    val itemId: String,
    val anchor: Instant,
    val priority: AlarmPriority,
) {
    val itemKey: String get() = "${kind.name.lowercase()}:$itemId"
}
