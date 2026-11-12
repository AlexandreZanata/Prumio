package app.prumio.core.ui.mvi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun OnTimeChange(onChange: () -> Unit) {
    val context = LocalContext.current
    val latestOnChange by rememberUpdatedState(onChange)
    DisposableEffect(context) {
        val stop = observeTimeChanges(context) { latestOnChange() }
        onDispose(stop)
    }
}

/** Only device-time events; no data or platform types are passed into a screen's domain logic. */
internal fun observeTimeChanges(context: Context, onChange: () -> Unit): () -> Unit {
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) = onChange()
    }
    val filter = IntentFilter().apply {
        addAction(Intent.ACTION_DATE_CHANGED)
        addAction(Intent.ACTION_TIME_CHANGED)
        addAction(Intent.ACTION_TIMEZONE_CHANGED)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        context.registerReceiver(receiver, filter, Context.RECEIVER_NOT_EXPORTED)
    } else {
        context.registerReceiver(receiver, filter)
    }
    return { context.unregisterReceiver(receiver) }
}
