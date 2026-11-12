package app.prumio.feature.matters

import androidx.navigation3.runtime.NavKey
import app.prumio.core.domain.matter.MattersOverview
import app.prumio.core.model.MatterId
import kotlinx.datetime.LocalDate

// Matters tab (docs/product/screens.md#matters): groups of BR-MATTER-013.

internal data class MattersUiState(
    val overview: MattersOverview? = null,
    val today: LocalDate = LocalDate(1970, 1, 1),
    val showResolved: Boolean = false,
) {
    val isEmpty: Boolean
        get() = overview != null &&
            listOf(overview.needsAttention, overview.waiting, overview.open, overview.resolved).all { it.isEmpty() }
}

internal sealed interface MattersAction {
    data class Open(val id: MatterId) : MattersAction

    data object New : MattersAction

    data object ToggleResolved : MattersAction
}

internal sealed interface MattersEffect {
    data class Navigate(val key: NavKey) : MattersEffect
}
