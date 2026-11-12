package app.prumio.core.ui.mvi

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberUpdatedState

@Composable
actual fun rememberNotificationPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit {
    val latest = rememberUpdatedState(onResult)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { latest.value(it) }
    return {
        if (Build.VERSION.SDK_INT >=
            33
        ) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            latest.value(true)
        }
    }
}
