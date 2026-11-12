package app.prumio.feature.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun rememberSettingsPlatform(): SettingsPlatform {
    val context = LocalContext.current
    return remember(context) {
        object : SettingsPlatform {
            override val version: String = context.packageManager.getPackageInfo(
                context.packageName,
                0,
            ).versionName.orEmpty()
            override val supportsAppLanguage: Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

            override fun openAppLanguage() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.startActivity(
                        Intent(Settings.ACTION_APP_LOCALE_SETTINGS, Uri.parse("package:${context.packageName}")),
                    )
                }
            }
        }
    }
}
