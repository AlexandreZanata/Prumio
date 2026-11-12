package app.prumio.app.shell

import app.prumio.core.model.ThemePreference

/** Shared application chrome state, independent of the currently visible screen. */
internal data class ShellUiState(val inboxCount: Int = 0, val theme: ThemePreference = ThemePreference.System)
