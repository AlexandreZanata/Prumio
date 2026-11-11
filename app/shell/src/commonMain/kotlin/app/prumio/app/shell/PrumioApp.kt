package app.prumio.app.shell

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.rememberNavBackStack
import app.prumio.app.shell.navigation.PrumioBottomBar
import app.prumio.app.shell.navigation.PrumioNavDisplay
import app.prumio.app.shell.navigation.PrumioNavigator
import app.prumio.app.shell.navigation.navSavedStateConfiguration
import app.prumio.app.shell.resources.Res
import app.prumio.app.shell.resources.add_anything
import app.prumio.core.designsystem.component.PrumioFloatingActionButton
import app.prumio.core.designsystem.component.PrumioScaffold
import app.prumio.core.designsystem.component.PrumioSnackbarHost
import app.prumio.core.designsystem.component.rememberPrumioMessenger
import app.prumio.core.designsystem.icon.PrumioIcons
import app.prumio.core.designsystem.theme.PrumioTheme
import app.prumio.core.navigation.CaptureKey
import app.prumio.core.navigation.TodayKey
import app.prumio.core.ui.message.LocalMessenger
import org.jetbrains.compose.resources.stringResource

/**
 * Root composable shared by every platform: theme, messenger (snackbars), bottom bar, "Add anything" button and the
 * navigation host. Platform entry points (MainActivity) only call this.
 */
@Composable
fun PrumioApp(modifier: Modifier = Modifier) {
    PrumioTheme {
        val backStack = rememberNavBackStack(navSavedStateConfiguration, TodayKey)
        val navigator = remember(backStack) { PrumioNavigator(backStack) }
        val showChrome = navigator.isShowingTopLevel
        val messenger = rememberPrumioMessenger()

        CompositionLocalProvider(LocalMessenger provides messenger) {
            PrumioScaffold(
                modifier = modifier,
                snackbarHost = { PrumioSnackbarHost(messenger) },
                bottomBar = {
                    if (showChrome) {
                        PrumioBottomBar(selected = navigator.currentTopLevelKey, onSelect = navigator::navigate)
                    }
                },
                floatingActionButton = {
                    if (showChrome) {
                        PrumioFloatingActionButton(
                            icon = PrumioIcons.Add,
                            contentDescription = stringResource(Res.string.add_anything),
                            onClick = { navigator.navigate(CaptureKey) },
                        )
                    }
                },
            ) { padding ->
                PrumioNavDisplay(
                    backStack = backStack,
                    navigator = navigator,
                    modifier = Modifier.padding(padding).consumeWindowInsets(padding),
                )
            }
        }
    }
}
