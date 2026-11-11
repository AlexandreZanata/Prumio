package app.prumio.core.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioButton
import app.prumio.core.designsystem.component.PrumioButtonVariant
import app.prumio.core.designsystem.component.PrumioCard
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioSectionHeader
import app.prumio.core.designsystem.component.PrumioText
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTextStyle
import app.prumio.core.designsystem.theme.PrumioTheme

// Visual catalog of the design system. Add every new component here (docs/ai/recipes/add-design-system-component.md).

@Preview
@Composable
private fun ComponentCatalogLightPreview() {
    PrumioTheme(darkTheme = false) { ComponentCatalog() }
}

@Preview
@Composable
private fun ComponentCatalogDarkPreview() {
    PrumioTheme(darkTheme = true) { ComponentCatalog() }
}

@Composable
private fun ComponentCatalog() {
    Column(
        modifier = Modifier.background(PrumioTheme.colors.background).padding(PrumioTheme.spacing.lg),
        verticalArrangement = Arrangement.spacedBy(PrumioTheme.spacing.sm),
    ) {
        PrumioTextStyle.entries.forEach { style -> PrumioText(text = style.name, style = style) }
        PrumioSectionHeader(title = "Buttons")
        PrumioButtonVariant.entries.forEach { variant ->
            PrumioButton(text = variant.name, onClick = {}, variant = variant)
        }
        PrumioCard {
            PrumioText(text = "Card content", modifier = Modifier.padding(PrumioTheme.spacing.lg))
        }
        PrumioEmptyState(
            title = "Nothing here yet",
            description = "Calm, helpful empty state.",
            icon = PrumioIcons.Info,
        )
    }
}
