package app.prumio.core.ui.message

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import app.prumio.core.designsystem.component.PrumioMessenger
import app.prumio.core.domain.error.DomainError
import app.prumio.core.domain.text.ValidationReasons
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.error_generic
import app.prumio.core.ui.resources.error_not_found
import app.prumio.core.ui.resources.error_storage
import app.prumio.core.ui.resources.validation_blank
import app.prumio.core.ui.resources.validation_end_before_start
import app.prumio.core.ui.resources.validation_future_date
import app.prumio.core.ui.resources.validation_not_editable
import app.prumio.core.ui.resources.validation_reminder_offsets
import app.prumio.core.ui.resources.validation_too_long
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString

/**
 * A short message for the user, emitted by ViewModels as an effect (docs/architecture/presentation-pattern.md).
 * [onAction] runs when the user taps [actionLabel] (e.g. Undo, BR-GEN-005), in the app's scope: call
 * use cases directly there, not ViewModel actions (the ViewModel may be gone).
 */
data class UiMessage(
    val text: StringResource,
    val args: List<Any> = emptyList(),
    val actionLabel: StringResource? = null,
    val onAction: (suspend () -> Unit)? = null,
)

/** The app's messenger, provided once by the app shell. */
val LocalMessenger: ProvidableCompositionLocal<PrumioMessenger?> = staticCompositionLocalOf { null }

/**
 * Returns a function that shows a [UiMessage] and runs its action when tapped. Use it in Routes.
 * It returns at once; the message and its action run in the app's scope, so they survive the
 * screen that sent them (Undo after a detail screen closed).
 */
@Composable
fun rememberMessageSender(): (UiMessage) -> Unit {
    val messenger = LocalMessenger.current
    return remember(messenger) {
        { message ->
            messenger?.post {
                val text = getString(message.text, *message.args.toTypedArray())
                val action = message.actionLabel?.let { getString(it) }
                if (show(text, action)) message.onAction?.invoke()
            }
        }
    }
}

/** The one mapping from expected failures to what the user reads (docs/architecture/error-handling.md). */
fun DomainError.toUiMessage(): UiMessage = when (this) {
    is DomainError.Storage -> UiMessage(Res.string.error_storage)
    is DomainError.NotFound -> UiMessage(Res.string.error_not_found)
    is DomainError.Validation -> UiMessage(fieldMessage())
    is DomainError.InvalidTransition -> UiMessage(Res.string.error_generic)
}

/** Text shown under a form field for a validation failure. */
fun DomainError.Validation.fieldMessage(): StringResource = when (reason) {
    ValidationReasons.BLANK -> Res.string.validation_blank
    ValidationReasons.TOO_LONG -> Res.string.validation_too_long
    ValidationReasons.END_BEFORE_START -> Res.string.validation_end_before_start
    ValidationReasons.FUTURE_DATE -> Res.string.validation_future_date
    ValidationReasons.NOT_EDITABLE -> Res.string.validation_not_editable
    "reminder_offsets" -> Res.string.validation_reminder_offsets
    else -> Res.string.error_generic
}
