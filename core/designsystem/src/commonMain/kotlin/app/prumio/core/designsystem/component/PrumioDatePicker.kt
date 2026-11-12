package app.prumio.core.designsystem.component

import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

/** Calendar dialog to pick a date. Prefer [PrumioQuickDateChips] first; this is the "Pick…" fallback. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrumioDatePicker(
    initialDate: LocalDate?,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // The Material picker works with UTC midnight milliseconds.
    val state =
        rememberDatePickerState(
            initialSelectedDateMillis = initialDate?.atStartOfDayIn(TimeZone.UTC)?.toEpochMilliseconds(),
        )
    DatePickerDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            PrumioButton(
                text = confirmLabel,
                onClick = {
                    state.selectedDateMillis?.let { millis ->
                        onConfirm(Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date)
                    }
                },
                variant = PrumioButtonVariant.Text,
                enabled = state.selectedDateMillis != null,
            )
        },
        dismissButton = { PrumioButton(text = dismissLabel, onClick = onDismiss, variant = PrumioButtonVariant.Text) },
    ) {
        DatePicker(state = state)
    }
}
