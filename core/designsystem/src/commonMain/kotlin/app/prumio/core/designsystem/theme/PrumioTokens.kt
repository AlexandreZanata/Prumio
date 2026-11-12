package app.prumio.core.designsystem.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Corner shapes (docs/design-system/tokens.md#shape). */
object PrumioShape {
    val small: CornerBasedShape = RoundedCornerShape(8.dp)
    val medium: CornerBasedShape = RoundedCornerShape(12.dp)
    val large: CornerBasedShape = RoundedCornerShape(16.dp)
    val extraLarge: CornerBasedShape = RoundedCornerShape(28.dp)
    val pill: CornerBasedShape = CircleShape
}

/** Elevation: Prumio is mostly flat; depth only for things that float above content. */
object PrumioElevation {
    val flat: Dp = 0.dp
    val raised: Dp = 1.dp
    val floating: Dp = 3.dp
}

/** Motion durations in milliseconds: short and calm; respect the system "remove animations" setting. */
object PrumioMotion {
    const val SHORT_MILLIS = 150
    const val MEDIUM_MILLIS = 250
    const val LONG_MILLIS = 400
}
