package app.prumio.core.domain.reminder

import app.prumio.core.model.Appointment
import app.prumio.core.model.Matter
import app.prumio.core.model.Settings
import app.prumio.core.model.Task
import kotlinx.datetime.TimeZone
import kotlin.time.Instant

/** Immutable input for the pure reminder projection (BR-REM-001). */
data class PlanningInput(
    val now: Instant,
    val zone: TimeZone,
    val settings: Settings,
    val tasks: List<Task>,
    val appointments: List<Appointment>,
    val matters: List<Matter>,
    val snoozes: Map<AlarmKey, Instant> = emptyMap(),
    val delivered: Set<AlarmKey> = emptySet(),
)
