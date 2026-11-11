package app.prumio.core.designsystem.theme

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Spacing scale (4dp grid). Use these instead of literal dp values in screens. */
object PrumioSpacing {
    val xxs: Dp = 2.dp
    val xs: Dp = 4.dp
    val sm: Dp = 8.dp
    val md: Dp = 12.dp
    val lg: Dp = 16.dp
    val xl: Dp = 24.dp
    val xxl: Dp = 32.dp
    val xxxl: Dp = 48.dp

    /** Minimum touch target (accessibility). */
    val minTouchTarget: Dp = 48.dp

    /** Horizontal padding of screen content. */
    val screenHorizontal: Dp = lg
}
