package app.prumio.feature.today

import androidx.navigation3.runtime.NavKey
import app.prumio.core.model.DayPeriod
import kotlinx.datetime.LocalDate

// The complete contract of the Today screen: what it shows, what the user can do, what happens once.
// Reference implementation of docs/architecture/presentation-pattern.md; copy this shape for new screens.

internal data class TodayUiState(val dayPeriod: DayPeriod = DayPeriod.Morning, val date: LocalDate? = null)

internal sealed interface TodayAction {
    data object Refresh : TodayAction

    data object OpenSearch : TodayAction

    data object OpenSettings : TodayAction
}

internal sealed interface TodayEffect {
    data class Navigate(val key: NavKey) : TodayEffect
}
