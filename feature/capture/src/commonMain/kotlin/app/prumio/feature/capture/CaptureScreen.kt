package app.prumio.feature.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.component.PrumioTextField
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.domain.text.TextLimits
import app.prumio.core.ui.resources.action_cancel
import app.prumio.core.ui.resources.action_save
import app.prumio.feature.capture.resources.Res
import app.prumio.feature.capture.resources.capture_placeholder
import app.prumio.feature.capture.resources.capture_title
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** The quick capture dialog: focused field, keyboard open, Save (BR-INBOX-002). */
@Composable
internal fun CaptureScreen(state: CaptureUiState, onAction: (CaptureAction) -> Unit, modifier: Modifier = Modifier) {
    val focus = remember { FocusRequester() }
    LaunchedEffect(Unit) { focus.requestFocus() }
    PrumioCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(PrumioTheme.spacing.lg),
            verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
        ) {
            PrumioText(text = stringResource(Res.string.capture_title), style = PrumioTextStyle.Title)
            PrumioTextField(
                value = state.text,
                onValueChange = { onAction(CaptureAction.ChangeText(it)) },
                label = stringResource(Res.string.capture_title),
                placeholder = stringResource(Res.string.capture_placeholder),
                errorText = state.error?.let { stringResource(it) },
                maxLength = TextLimits.INBOX_TEXT,
                minLines = 3,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onAction(CaptureAction.Save) }),
                focusRequester = focus,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm, Alignment.End),
            ) {
                PrumioButton(
                    text = stringResource(CoreUiRes.string.action_cancel),
                    onClick = { onAction(CaptureAction.Close) },
                    variant = PrumioButtonVariant.Text,
                )
                PrumioButton(
                    text = stringResource(CoreUiRes.string.action_save),
                    onClick = { onAction(CaptureAction.Save) },
                    enabled = state.canSave,
                )
            }
        }
    }
}

@Preview
@Composable
private fun CaptureScreenPreview() {
    PrumioTheme { CaptureScreen(state = CaptureUiState(text = "Call the plumber"), onAction = {}) }
}
