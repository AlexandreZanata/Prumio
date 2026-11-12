package app.prumio.app.shell.navigation

import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.OverlayNavKey
import app.prumio.core.navigation.TodayKey
import app.prumio.core.navigation.TopLevelNavKey

/**
 * The only implementation of [Navigator]. Back stack rules (docs/architecture/navigation.md#back-stack-rules):
 * 1. The stack always starts with [TodayKey] and is never empty.
 * 2. Navigating to a top-level key resets the stack to `[Today]` or `[Today, key]`.
 * 3. Any other key is pushed, unless it is already on top.
 * 4. Going back pops the top entry, except the root.
 */
internal class PrumioNavigator(private val backStack: MutableList<NavKey>) : Navigator {
    /** Top-most entry that is a full screen (dialogs are skipped). */
    val currentScreenKey: NavKey
        get() = backStack.lastOrNull { it !is OverlayNavKey } ?: TodayKey

    /** Tab to highlight in the bottom bar. */
    val currentTopLevelKey: TopLevelNavKey
        get() = backStack.lastOrNull { it is TopLevelNavKey } as? TopLevelNavKey ?: TodayKey

    /** Whether the bottom bar and the "Add anything" button are shown. */
    val isShowingTopLevel: Boolean
        get() = currentScreenKey is TopLevelNavKey

    override fun navigate(key: NavKey) {
        when {
            key is TopLevelNavKey -> resetTo(key)
            backStack.lastOrNull() == key -> Unit
            else -> backStack.add(key)
        }
    }

    /** Notification links always have Today immediately underneath (BR-REM-017). */
    fun openLink(key: NavKey) {
        resetTo(TodayKey)
        if (key != TodayKey) backStack.add(key)
    }

    override fun goBack() {
        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
    }

    private fun resetTo(key: TopLevelNavKey) {
        // Mutate in place without ever leaving the stack empty (NavDisplay requires an entry).
        while (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
        if (backStack.isEmpty()) backStack.add(TodayKey) else backStack[0] = TodayKey
        if (key != TodayKey) backStack.add(key)
    }
}
