package app.prumio.feature.capture

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.ui.resources.action_close
import app.prumio.feature.capture.resources.Res
import app.prumio.feature.capture.resources.capture_placeholder_description
import app.prumio.feature.capture.resources.capture_title
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Placeholder dialog. The real quick capture is specified in docs/product/screens.md#quick-capture. */
@Composable
internal fun CaptureScreen(onClose: () -> Unit, modifier: Modifier = Modifier) {
    PrumioCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(PrumioTheme.spacing.xl),
            verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.md),
        ) {
            PrumioText(text = stringResource(Res.string.capture_title), style = PrumioTextStyle.Title)
            PrumioText(text = stringResource(Res.string.capture_placeholder_description))
            PrumioButton(
                text = stringResource(CoreUiRes.string.action_close),
                onClick = onClose,
                variant = PrumioButtonVariant.Text,
                modifier = Modifier.align(Alignment.End),
            )
        }
    }
}

@Preview
@Composable
private fun CaptureScreenPreview() {
    PrumioTheme { CaptureScreen(onClose = {}) }
}
