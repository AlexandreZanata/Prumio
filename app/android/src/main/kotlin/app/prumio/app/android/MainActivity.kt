package app.prumio.app.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.prumio.app.shell.PrumioApp
import app.prumio.core.domain.reminder.ReminderReconciliationTrigger
import org.koin.core.context.GlobalContext

/** Single activity. All UI lives in shared code (PrumioApp in :app:shell). */
class MainActivity : ComponentActivity() {
    private var incomingLink by mutableStateOf<String?>(null)
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        incomingLink = intent.dataString
    }

    override fun onResume() {
        super.onResume()
        GlobalContext.get().get<ReminderReconciliationTrigger>().request(forceReschedule = true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        incomingLink = intent?.dataString
        setContent {
            PrumioApp(initialDeepLink = incomingLink, onConsumeDeepLink = {
                incomingLink = null
                intent.data =
                    null
            })
        }
    }
}
