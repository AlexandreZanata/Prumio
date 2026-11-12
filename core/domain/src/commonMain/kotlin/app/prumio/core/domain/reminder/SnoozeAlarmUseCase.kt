package app.prumio.core.domain.reminder

import app.prumio.core.common.Outcome
import app.prumio.core.common.asFailure
import app.prumio.core.common.valueOr
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.error.storageCall
import kotlinx.coroutines.flow.first
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/** Reschedules only this identity; matters permit Tomorrow only. Rules: BR-REM-007, BR-REM-008. */
class SnoozeAlarmUseCase(
    private val observeInputs: ObserveReminderInputsUseCase,
    private val planner: ReminderPlanner,
    private val snoozes: SnoozeStore,
    private val presenter: NotificationPresenter,
    private val trigger: ReminderReconciliationTrigger,
) {
    suspend operator fun invoke(key: AlarmKey, option: SnoozeOption): Outcome<Unit, DomainError> {
        val input = storageCall { observeInputs().first() }.valueOr { return it.asFailure() }
        if (!planner.isCurrent(key, input)) return DomainError.NotFound("Reminder", key.value).asFailure()
        if (option !in
            snoozeOptions(key)
        ) {
            return DomainError.Validation("BR-REM-008", "snooze", "tomorrow_only").asFailure()
        }
        val until = when (option) {
            SnoozeOption.TenMinutes -> input.now + 10.minutes

            SnoozeOption.OneHour -> input.now + 1.hours

            SnoozeOption.Tomorrow -> LocalDateTime(
                input.now.toLocalDateTime(input.zone).date.plus(DatePeriod(days = 1)),
                input.settings.defaultReminderTime,
            ).toInstant(input.zone)
        }
        return storageCall {
            snoozes.put(key, until)
            presenter.cancelFor(key.value.substringBeforeLast(':').substringBeforeLast(':'))
            trigger.request()
        }
    }
}
