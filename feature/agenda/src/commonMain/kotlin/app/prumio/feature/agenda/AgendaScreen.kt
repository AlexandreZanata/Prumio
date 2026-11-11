package app.prumio.feature.agenda

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
import app.prumio.core.ui.resources.coming_soon_title
import app.prumio.core.ui.resources.destination_agenda
import app.prumio.feature.agenda.resources.Res
import app.prumio.feature.agenda.resources.agenda_placeholder_description
import org.jetbrains.compose.resources.stringResource
import app.prumio.core.ui.resources.Res as CoreUiRes

/** Placeholder screen. The real implementation is specified in docs/product/screens.md. */
@Composable
internal fun AgendaScreen(modifier: Modifier = Modifier) {
    PrumioScaffold(
        modifier = modifier,
        topBar = {
            PrumioTopBar(title = stringResource(CoreUiRes.string.destination_agenda))
        },
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            PrumioEmptyState(
                title = stringResource(CoreUiRes.string.coming_soon_title),
                description = stringResource(Res.string.agenda_placeholder_description),
                icon = PrumioIcons.Agenda,
            )
        }
    }
}

@Preview
@Composable
private fun AgendaScreenPreview() {
    PrumioTheme { AgendaScreen() }
}
