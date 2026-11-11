package app.prumio.app.android

import android.app.Application
import app.prumio.app.shell.di.prumioModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class PrumioApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@PrumioApplication)
            modules(prumioModules)
        }
    }
}
