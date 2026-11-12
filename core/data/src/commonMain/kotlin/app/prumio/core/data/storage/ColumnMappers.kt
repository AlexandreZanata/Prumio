package app.prumio.core.data.storage

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlin.time.Instant

// Column formats of docs/domain/time-semantics.md#storage-formats-sqlite.

internal fun Instant.toColumn(): Long = toEpochMilliseconds()

internal fun Long.toInstant(): Instant = Instant.fromEpochMilliseconds(this)

/** ISO `yyyy-MM-dd`: sorts correctly as text. */
internal fun LocalDate.toColumn(): String = toString()

internal fun String.toLocalDate(): LocalDate = LocalDate.parse(this)

/** `HH:mm`: seconds are never stored. */
internal fun LocalTime.toColumn(): String = LocalTime(hour, minute).toString()

internal fun String.toLocalTime(): LocalTime = LocalTime.parse(this)

/** Enum values are stored in UPPER_SNAKE_CASE and mapped explicitly (renaming a Kotlin constant never corrupts data). */
internal fun <E : Enum<E>> String.toEnum(entries: List<E>, column: String): E =
    entries.firstOrNull { it.name.toUpperSnakeCase() == this } ?: error("Unknown $column value '$this'")

internal fun Enum<*>.toColumn(): String = name.toUpperSnakeCase()

private fun String.toUpperSnakeCase(): String = replace(Regex("([a-z])([A-Z])"), "$1_$2").uppercase()
