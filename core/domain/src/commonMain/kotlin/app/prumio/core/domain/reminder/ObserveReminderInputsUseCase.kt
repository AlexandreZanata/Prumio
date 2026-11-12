package app.prumio.core.domain.reminder

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.domain.appointment.AppointmentRepository
import app.prumio.core.domain.matter.MatterRepository
import app.prumio.core.domain.settings.SettingKeys
import app.prumio.core.domain.settings.SettingsRepository
import app.prumio.core.domain.task.TaskRepository
import kotlinx.coroutines.flow.combine
import kotlin.time.Duration.Companion.days

/** Cold live input; each subscription reads fresh time and delivery/snooze state. Rules: BR-REM-001, BR-REM-002, BR-SET-002. */
class ObserveReminderInputsUseCase(
    private val tasks: TaskRepository,
    private val appointments: AppointmentRepository,
    private val matters: MatterRepository,
    private val settings: SettingsRepository,
    private val snoozes: SnoozeStore,
    private val delivered: DeliveredAlarmStore,
    private val time: TimeProvider,
) {
    operator fun invoke() =
        combine(tasks.observeOpen(), appointments.observeAll(), matters.observeUnresolved(), settings.observeValues()) {
                t,
                a,
                m,
                s,
            ->
            PlanningInput(
                time.now(),
                time.timeZone(),
                SettingKeys.read(s),
                t,
                a,
                m,
                snoozes.all(),
                delivered.recent(time.now() - 30.days),
            )
        }
}
