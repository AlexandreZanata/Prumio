package app.prumio.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme

/**
 * Asks before an important or destructive action (BR-GEN-005). [destructive] colors the confirm
 * button as a warning. Labels come from the caller (e.g. shared `action_delete` / `action_cancel`).
 */
@Composable
fun PrumioConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String,
    dismissLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = { Text(title) },
        text = { Text(text) },
        confirmButton = {
            PrumioButton(
                text = confirmLabel,
                onClick = onConfirm,
                variant = if (destructive) PrumioButtonVariant.Destructive else PrumioButtonVariant.Primary,
            )
        },
        dismissButton = { PrumioButton(text = dismissLabel, onClick = onDismiss, variant = PrumioButtonVariant.Text) },
        containerColor = PrumioTheme.colors.surfaceContainerHigh,
    )
}
