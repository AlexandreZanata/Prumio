package app.prumio.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.LocalTime

/** Clock dialog to pick a time; [is24Hour] follows the device setting (`rememberIs24HourFormat`, :core:ui). */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrumioTimePicker(
    initialTime: LocalTime,
    is24Hour: Boolean,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: (LocalTime) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state =
        rememberTimePickerState(initialHour = initialTime.hour, initialMinute = initialTime.minute, is24Hour = is24Hour)
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        confirmButton = {
            PrumioButton(
                text = confirmLabel,
                onClick = { onConfirm(LocalTime(state.hour, state.minute)) },
                variant = PrumioButtonVariant.Text,
            )
        },
        dismissButton = { PrumioButton(text = dismissLabel, onClick = onDismiss, variant = PrumioButtonVariant.Text) },
        text = { TimePicker(state = state) },
    )
}
