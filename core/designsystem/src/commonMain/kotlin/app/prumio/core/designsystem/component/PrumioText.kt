package app.prumio.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.toTextStyle

/** All text in the app. Pick a semantic [style]; never pass raw font sizes. */
@Composable
fun PrumioText(
    text: String,
    modifier: Modifier = Modifier,
    style: PrumioTextStyle = PrumioTextStyle.Body,
    color: Color = Color.Unspecified,
    textAlign: TextAlign? = null,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = if (style == PrumioTextStyle.Overline) text.uppercase() else text,
        modifier = modifier,
        style = style.toTextStyle(MaterialTheme.typography),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
