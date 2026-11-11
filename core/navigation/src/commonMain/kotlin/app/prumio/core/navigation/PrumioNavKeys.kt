package app.prumio.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// Every destination of the app. Rules (docs/architecture/navigation.md):
// - Keys carry only primitive arguments (ids as String), never domain objects.
// - Every key must be registered in navKeySerializersModule (NavKeySerialization.kt).
// - Exactly one feature module provides the entry for each key.

/** Top-level destinations shown in the bottom bar. */
sealed interface TopLevelNavKey : NavKey

@Serializable
data object TodayKey : TopLevelNavKey

@Serializable
data object AgendaKey : TopLevelNavKey

@Serializable
data object MattersKey : TopLevelNavKey

@Serializable
data object InboxKey : TopLevelNavKey

/** Destinations shown as a dialog over the current screen; the underlying screen stays visible. */
sealed interface OverlayNavKey : NavKey

/** "Add anything" quick capture. */
@Serializable
data object CaptureKey : OverlayNavKey

@Serializable
data object SearchKey : NavKey

@Serializable
data object SettingsKey : NavKey
