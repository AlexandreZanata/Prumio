package app.prumio.core.common.time

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * The only source of "now" in Prumio.
 *
 * Never call `Clock.System.now()` or `TimeZone.currentSystemDefault()` directly in app code:
 * inject a [TimeProvider] so rules are testable with `FakeTimeProvider` (:core:testing).
 * See docs/domain/time-semantics.md.
 */
interface TimeProvider {
    fun now(): Instant

    fun timeZone(): TimeZone
}

fun TimeProvider.localNow(): LocalDateTime = now().toLocalDateTime(timeZone())

fun TimeProvider.today(): LocalDate = localNow().date

class SystemTimeProvider : TimeProvider {
    override fun now(): Instant = Clock.System.now()

    override fun timeZone(): TimeZone = TimeZone.currentSystemDefault()
}
