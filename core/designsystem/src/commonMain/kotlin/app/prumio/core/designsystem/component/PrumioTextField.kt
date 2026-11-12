package app.prumio.core.designsystem.component

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester

/**
 * Text input. [errorText] shows a validation message and the error state; [maxLength] shows a
 * "12/500" counter (input beyond it is still typed, so the user sees why saving fails).
 * Pass a [focusRequester] to focus it programmatically (Quick capture opens with the keyboard up).
 */
@Composable
fun PrumioTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    errorText: String? = null,
    maxLength: Int? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    focusRequester: FocusRequester? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = if (focusRequester != null) modifier.focusRequester(focusRequester) else modifier,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } },
        supportingText = when {
            errorText != null -> {
                { Text(errorText) }
            }

            maxLength != null -> {
                { Text("${value.length}/$maxLength") }
            }

            else -> null
        },
        isError = errorText != null || (maxLength != null && value.length > maxLength),
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
    )
}
