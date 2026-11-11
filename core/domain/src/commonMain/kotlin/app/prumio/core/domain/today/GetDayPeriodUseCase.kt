package app.prumio.core.domain.today

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import app.prumio.core.model.DayPeriod
import kotlinx.datetime.LocalTime

/**
 * Returns the current [DayPeriod] for the Today greeting.
 *
 * Rule: BR-TODAY-001 (docs/domain/business-rules.md).
 */
class GetDayPeriodUseCase(private val timeProvider: TimeProvider) {
    operator fun invoke(): DayPeriod = dayPeriodAt(timeProvider.localNow().time)
}

/** BR-TODAY-001: 05:00–11:59 morning, 12:00–17:59 afternoon, 18:00–04:59 evening. */
internal fun dayPeriodAt(time: LocalTime): DayPeriod = when (time.hour) {
    in 5..11 -> DayPeriod.Morning
    in 12..17 -> DayPeriod.Afternoon
    else -> DayPeriod.Evening
}
