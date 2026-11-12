package app.prumio.core.ui.component

import androidx.compose.runtime.Composable
import app.prumio.core.designsystem.component.PrumioConfirmDialog
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.reminder_permission_allow
import app.prumio.core.ui.resources.reminder_permission_later
import app.prumio.core.ui.resources.reminder_permission_text
import app.prumio.core.ui.resources.reminder_permission_title
import org.jetbrains.compose.resources.stringResource

/** Explanation shown in the editor before Android's permission dialog (BR-REM-014). */
@Composable
fun ReminderPermissionDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    PrumioConfirmDialog(
        title = stringResource(Res.string.reminder_permission_title),
        text = stringResource(Res.string.reminder_permission_text),
        confirmLabel = stringResource(Res.string.reminder_permission_allow),
        dismissLabel = stringResource(Res.string.reminder_permission_later),
        onConfirm = onConfirm,
        onDismiss = onDismiss,
    )
}
