package app.prumio.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import org.koin.compose.viewmodel.koinViewModel

/** Applies platform effects; preferences remain in the domain. */
@Composable
internal fun SettingsRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val platform = rememberSettingsPlatform()
    val uriHandler = LocalUriHandler.current
    val showMessage = rememberMessageSender()
    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            SettingsEffect.Close -> navigator.goBack()
            SettingsEffect.OpenLanguage -> platform.openAppLanguage()
            SettingsEffect.OpenSource -> uriHandler.openUri("https://github.com/AlexandreZanata/Prumio")
            is SettingsEffect.ShowMessage -> showMessage(effect.message)
        }
    }
    SettingsScreen(
        state = state,
        version = platform.version,
        supportsAppLanguage = platform.supportsAppLanguage,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
