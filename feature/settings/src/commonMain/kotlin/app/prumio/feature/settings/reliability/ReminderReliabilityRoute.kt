package app.prumio.feature.settings.reliability

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.prumio.core.navigation.Navigator
import app.prumio.core.ui.message.rememberMessageSender
import app.prumio.core.ui.mvi.CollectEffects
import app.prumio.feature.settings.rememberSettingsPlatform
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun ReminderReliabilityRoute(
    navigator: Navigator,
    modifier: Modifier = Modifier,
    viewModel: ReminderReliabilityViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val platform = rememberSettingsPlatform()
    val showMessage = rememberMessageSender()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.onAction(ReminderReliabilityAction.Refresh) }
    CollectEffects(viewModel.effects) {
        when (it) {
            ReminderReliabilityEffect.Close -> navigator.goBack()
            is ReminderReliabilityEffect.OpenSettings -> platform.openReminderSettings(it.target)
            is ReminderReliabilityEffect.ShowMessage -> showMessage(it.message)
        }
    }
    ReminderReliabilityScreen(state, viewModel::onAction, modifier)
}
