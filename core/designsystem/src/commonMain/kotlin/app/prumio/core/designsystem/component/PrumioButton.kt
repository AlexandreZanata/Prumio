package app.prumio.core.designsystem.component

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.prumio.core.designsystem.theme.PrumioTheme

enum class PrumioButtonVariant {
    /** The main action of a screen or dialog. At most one per screen. */
    Primary,

    /** Alternative actions next to a primary one. */
    Secondary,

    /** Low-emphasis actions (Cancel, Not now). */
    Text,

    /** Irreversible actions (Delete). Always confirm first. */
    Destructive,
}

@Composable
fun PrumioButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: PrumioButtonVariant = PrumioButtonVariant.Primary,
    enabled: Boolean = true,
) {
    when (variant) {
        PrumioButtonVariant.Primary -> Button(onClick = onClick, modifier = modifier, enabled = enabled) {
            Text(text)
        }

        PrumioButtonVariant.Secondary -> OutlinedButton(onClick = onClick, modifier = modifier, enabled = enabled) {
            Text(text)
        }

        PrumioButtonVariant.Text -> TextButton(onClick = onClick, modifier = modifier, enabled = enabled) {
            Text(text)
        }

        PrumioButtonVariant.Destructive -> Button(
            onClick = onClick,
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = PrumioTheme.colors.error,
                contentColor = PrumioTheme.colors.onError,
            ),
        ) {
            Text(text)
        }
    }
}
