package app.prumio.feature.settings

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.prumio.feature.settings.reliability.ReliabilityTarget

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

            override fun openReminderSettings(target: ReliabilityTarget) {
                val packageUri = Uri.parse("package:${context.packageName}")
                val intent = when (target) {
                    ReliabilityTarget.Notifications -> Intent(
                        Settings.ACTION_APP_NOTIFICATION_SETTINGS,
                    ).putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)

                    ReliabilityTarget.ExactAlarms -> if (Build.VERSION.SDK_INT >=
                        31
                    ) {
                        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM, packageUri)
                    } else {
                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri)
                    }

                    ReliabilityTarget.Battery -> Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri)
                }
                runCatching {
                    context.startActivity(intent)
                }.onFailure { context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageUri)) }
            }
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
