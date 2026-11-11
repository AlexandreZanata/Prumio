package app.prumio.feature.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import app.prumio.core.designsystem.component.PrumioEmptyState
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioTopBar
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.ui.resources.action_back
import app.prumio.core.ui.resources.coming_soon_title
import app.prumio.core.ui.resources.destination_settings
import app.prumio.feature.settings.resources.Res
import app.prumio.feature.settings.resources.settings_placeholder_description
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Placeholder screen. The real implementation is specified in docs/product/screens.md. */
@Composable
internal fun SettingsScreen(onNavigateBack: () -> Unit, modifier: Modifier = Modifier) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(
                title = stringResource(CoreUiRes.string.destination_settings),
                onNavigateBack = onNavigateBack,
                navigateBackContentDescription = stringResource(CoreUiRes.string.action_back),
            )
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            PrumioEmptyState(
                title = stringResource(CoreUiRes.string.coming_soon_title),
                description = stringResource(Res.string.settings_placeholder_description),
                icon = PrumioIcons.Settings,
            )
        }
    }
}

@Preview
@Composable
private fun SettingsScreenPreview() {
    PrumioTheme { SettingsScreen(onNavigateBack = {}) }
}
