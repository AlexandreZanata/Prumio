package app.prumio.app.android

import android.app.Application
import app.prumio.app.shell.di.prumioModules
import app.prumio.core.notifications.ReminderRuntime
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PrumioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val application = startKoin {
            androidContext(this@PrumioApplication)
            modules(prumioModules)
        }
        application.koin.get<ReminderRuntime>().start()
    }
}
