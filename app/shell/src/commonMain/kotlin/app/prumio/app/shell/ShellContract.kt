package app.prumio.app.shell

import androidx.navigation3.runtime.NavKey
import app.prumio.core.ui.message.UiMessage

internal sealed interface ShellAction {
    data class OpenLink(val uri: String) : ShellAction
}
internal sealed interface ShellEffect {
    data class OpenLink(val key: NavKey) : ShellEffect
    data class ShowMessage(val message: UiMessage) : ShellEffect
}
