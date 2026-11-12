package app.prumio.core.model

import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate

/**
 * A repeat rule (docs/domain/recurrence.md). Data only: parsing, formatting and occurrence
 * generation arrive in phase 5 (`:core:domain/recurrence`).
 */
data class RecurrenceRule(
    val frequency: Frequency,
    val interval: Int = 1,
    /** Weekly only; empty means "the weekday of the anchor". */
    val weekdays: Set<DayOfWeek> = emptySet(),
    /** Monthly only. */
    val monthly: MonthlyPattern? = null,
    val end: RecurrenceEnd = RecurrenceEnd.Never,
) {
    init {
        require(interval >= 1) { "interval must be >= 1" }
    }
}

enum class Frequency {
    Daily,
    Weekly,
    Monthly,
    Yearly,
}

sealed interface MonthlyPattern {
    /** On this day of the month; 29–31 fall on the last day of shorter months (BR-REC-003). */
    data class DayOfMonth(val day: Int) : MonthlyPattern {
        init {
            require(day in 1..31) { "day must be in 1..31" }
        }
    }

    /** On the nth (1–4) or last (-1) [weekday] of the month. */
    data class NthWeekday(val ordinal: Int, val weekday: DayOfWeek) : MonthlyPattern {
        init {
            require(ordinal in 1..4 || ordinal == -1) { "ordinal must be 1..4 or -1" }
        }
    }
}

sealed interface RecurrenceEnd {
    data object Never : RecurrenceEnd

    data class AfterCount(val count: Int) : RecurrenceEnd {
        init {
            require(count >= 1) { "count must be >= 1" }
        }
    }

    /** Inclusive end date. */
    data class Until(val date: LocalDate) : RecurrenceEnd
}
