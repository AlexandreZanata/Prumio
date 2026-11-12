package app.prumio.core.domain.today

import app.prumio.core.common.time.TimeProvider
import app.prumio.core.common.time.localNow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlin.time.Duration.Companion.seconds

/**
 * Today's local date, now and again after each local midnight, so screens roll over without user
 * action. Screens also re-subscribe on resume to catch time zone or clock changes.
 * Rules: BR-TODAY-007, BR-TIME-003.
 */
class ObserveCurrentDateUseCase(private val time: TimeProvider) {
    operator fun invoke(): Flow<LocalDate> = flow {
        while (true) {
            val today = time.localNow().date
            emit(today)
            val before = time.now()
            val nextMidnight = today.plus(DatePeriod(days = 1)).atStartOfDayIn(time.timeZone())
            delay((nextMidnight - before).coerceAtLeast(1.seconds))
            // A clock that did not move while we waited is a frozen test clock: stop instead of
            // spinning forever in virtual time. A real clock always moves.
            if (time.now() <= before) break
        }
    }.distinctUntilChanged()
}
