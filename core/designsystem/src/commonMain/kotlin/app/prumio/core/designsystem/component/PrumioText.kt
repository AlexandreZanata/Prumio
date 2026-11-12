package app.prumio.core.designsystem.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
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
    textDecoration: TextDecoration? = null,
) {
    Text(
        text = if (style == PrumioTextStyle.Overline) text.uppercase() else text,
        modifier = modifier,
        style = style.toTextStyle(MaterialTheme.typography),
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        textDecoration = textDecoration,
        overflow = TextOverflow.Ellipsis,
    )
}

/** Text with styled ranges (e.g. search highlights). Same rules as the plain overload. */
@Composable
fun PrumioText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: PrumioTextStyle = PrumioTextStyle.Body,
    color: Color = Color.Unspecified,
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        style = style.toTextStyle(MaterialTheme.typography),
        color = color,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
