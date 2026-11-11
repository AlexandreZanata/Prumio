package app.prumio.app.shell.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import app.prumio.core.designsystem.component.PrumioNavigationBar
import app.prumio.core.designsystem.component.PrumioNavigationBarItem
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.navigation.AgendaKey
import app.prumio.core.navigation.InboxKey
import app.prumio.core.navigation.MattersKey
import app.prumio.core.navigation.TodayKey
import app.prumio.core.navigation.TopLevelNavKey
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.destination_agenda
import app.prumio.core.ui.resources.destination_inbox
import app.prumio.core.ui.resources.destination_matters
import app.prumio.core.ui.resources.destination_today
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/** The four top-level sections, in bottom bar order (docs/product/screens.md#navigation-map). */
internal enum class TopLevelDestination(
    val key: TopLevelNavKey,
    val label: StringResource,
    val icon: () -> ImageVector,
    val selectedIcon: () -> ImageVector,
) {
    Today(TodayKey, Res.string.destination_today, { PrumioIcons.Today }, { PrumioIcons.TodaySelected }),
    Agenda(AgendaKey, Res.string.destination_agenda, { PrumioIcons.Agenda }, { PrumioIcons.AgendaSelected }),
    Matters(MattersKey, Res.string.destination_matters, { PrumioIcons.Matters }, { PrumioIcons.MattersSelected }),
    Inbox(InboxKey, Res.string.destination_inbox, { PrumioIcons.Inbox }, { PrumioIcons.InboxSelected }),
}

@Composable
internal fun PrumioBottomBar(
    selected: TopLevelNavKey,
    onSelect: (TopLevelNavKey) -> Unit,
    modifier: Modifier = Modifier,
) {
    PrumioNavigationBar(modifier = modifier) {
        TopLevelDestination.entries.forEach { destination ->
            PrumioNavigationBarItem(
                selected = destination.key == selected,
                onClick = { onSelect(destination.key) },
                icon = destination.icon(),
                selectedIcon = destination.selectedIcon(),
                label = stringResource(destination.label),
            )
        }
    }
}
