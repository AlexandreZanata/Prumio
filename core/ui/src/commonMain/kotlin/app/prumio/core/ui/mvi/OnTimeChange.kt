package app.prumio.core.ui.mvi

import androidx.compose.runtime.Composable

/**
 * Refreshes a visible screen after a device clock, date or time-zone change, without waiting for
 * its midnight timer or the next resume. The callback re-subscribes to domain observations.
 * Rules: BR-TODAY-007, BR-TIME-003.
 */
@Composable
expect fun OnTimeChange(onChange: () -> Unit)
