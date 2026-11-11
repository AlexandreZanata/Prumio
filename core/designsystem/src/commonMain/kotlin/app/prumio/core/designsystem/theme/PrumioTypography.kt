package app.prumio.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Semantic text styles. Components and screens pick one of these; raw font sizes are never
 * written outside this file (docs/design-system/tokens.md#typography).
 */
enum class PrumioTextStyle {
    /** Big numbers in summaries (e.g. "3" tasks today). */
    Display,

    /** Screen greetings and hero titles. */
    Headline,

    /** Top bar titles and card titles. */
    Title,

    /** Section headers. */
    Subtitle,

    /** Default reading text. */
    Body,

    /** Emphasized body text (list item titles). */
    BodyStrong,

    /** Buttons, chips, tabs. */
    Label,

    /** Secondary information (times, metadata). */
    Caption,

    /** Small all-caps context line (e.g. "WEDNESDAY, 11 NOV"). */
    Overline,
}

internal val PrumioMaterialTypography = Typography()

internal fun PrumioTextStyle.toTextStyle(typography: Typography): TextStyle = when (this) {
    PrumioTextStyle.Display -> typography.displaySmall.copy(fontWeight = FontWeight.SemiBold)
    PrumioTextStyle.Headline -> typography.headlineMedium.copy(fontWeight = FontWeight.SemiBold)
    PrumioTextStyle.Title -> typography.titleLarge
    PrumioTextStyle.Subtitle -> typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
    PrumioTextStyle.Body -> typography.bodyLarge
    PrumioTextStyle.BodyStrong -> typography.bodyLarge.copy(fontWeight = FontWeight.Medium)
    PrumioTextStyle.Label -> typography.labelLarge
    PrumioTextStyle.Caption -> typography.bodySmall
    PrumioTextStyle.Overline -> typography.labelMedium.copy(fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
}
