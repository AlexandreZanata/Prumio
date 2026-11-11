package app.prumio.core.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Navigation contract used by feature Routes. Implemented once, in :app:shell.
 *
 * Features never manipulate the back stack directly: ViewModels emit navigation effects and the
 * Route translates them into calls on this interface.
 */
interface Navigator {
    /** Opens [key]. Top-level keys switch tabs instead of stacking. */
    fun navigate(key: NavKey)

    /** Closes the current screen or dialog. Does nothing on the root screen. */
    fun goBack()
}
