package app.prumio.app.shell

import androidx.lifecycle.viewModelScope
import app.prumio.core.common.onFailure
import app.prumio.core.common.onSuccess
import app.prumio.core.domain.inbox.ObserveInboxCountUseCase
import app.prumio.core.domain.reminder.ReminderLinkKind
import app.prumio.core.domain.reminder.ResolveReminderLinkUseCase
import app.prumio.core.domain.settings.ObserveSettingsUseCase
import app.prumio.core.navigation.AppointmentDetailKey
import app.prumio.core.navigation.CaptureKey
import app.prumio.core.navigation.MatterDetailKey
import app.prumio.core.navigation.SnoozeKey
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TodayKey
import app.prumio.core.ui.message.UiMessage
import app.prumio.core.ui.message.toUiMessage
import app.prumio.core.ui.mvi.PrumioViewModel
import app.prumio.core.ui.resources.Res
import app.prumio.core.ui.resources.error_not_found
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

/** App-wide appearance and Inbox badge (BR-SET-001, BR-INBOX-005); the sole shell ViewModel (ADR-0021). */
internal class ShellViewModel(
    observeInboxCount: ObserveInboxCountUseCase,
    observeSettings: ObserveSettingsUseCase,
    private val resolveLink: ResolveReminderLinkUseCase,
) : PrumioViewModel<ShellUiState, ShellAction, ShellEffect>(ShellUiState()) {
    init {
        combine(observeInboxCount(), observeSettings()) { count, settings -> ShellUiState(count, settings.theme) }
            .onEach { state -> updateState { state } }
            .launchIn(viewModelScope)
    }

    override fun onAction(action: ShellAction) {
        when (action) {
            is ShellAction.OpenLink -> viewModelScope.launch {
                resolveLink(action.uri).onSuccess { target ->
                    val key = when (target.kind) {
                        ReminderLinkKind.Today -> TodayKey
                        ReminderLinkKind.Task -> TaskDetailKey(requireNotNull(target.id))
                        ReminderLinkKind.Appointment -> AppointmentDetailKey(requireNotNull(target.id))
                        ReminderLinkKind.Matter -> MatterDetailKey(requireNotNull(target.id))
                        ReminderLinkKind.Capture -> CaptureKey
                        ReminderLinkKind.Snooze -> SnoozeKey(requireNotNull(target.id))
                    }
                    sendEffect(ShellEffect.OpenLink(key))
                    if (target.missing) sendEffect(ShellEffect.ShowMessage(UiMessage(Res.string.error_not_found)))
                }.onFailure {
                    sendEffect(ShellEffect.OpenLink(TodayKey))
                    sendEffect(ShellEffect.ShowMessage(it.toUiMessage()))
                }
            }
        }
    }
}
