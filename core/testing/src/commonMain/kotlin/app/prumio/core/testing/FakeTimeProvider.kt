package app.prumio.core.testing

import app.prumio.core.common.time.TimeProvider
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration
import kotlin.time.Instant

/**
 * Controllable clock for tests. Starts at [start] in [zone] (UTC by default).
 * Use [advanceBy], [setLocal] and [moveTo] to simulate time passing or travelling.
 */
class FakeTimeProvider(start: LocalDateTime = LocalDateTime(2026, 1, 15, 9, 0), zone: TimeZone = TimeZone.UTC) :
    TimeProvider {
    private var currentZone: TimeZone = zone
    private var currentInstant: Instant = start.toInstant(zone)

    override fun now(): Instant = currentInstant

    override fun timeZone(): TimeZone = currentZone

    fun advanceBy(duration: Duration) {
        currentInstant += duration
    }

    fun setLocal(dateTime: LocalDateTime) {
        currentInstant = dateTime.toInstant(currentZone)
    }

    /** Simulates the device changing time zone; the instant stays the same. */
    fun moveTo(zone: TimeZone) {
        currentZone = zone
    }
}
