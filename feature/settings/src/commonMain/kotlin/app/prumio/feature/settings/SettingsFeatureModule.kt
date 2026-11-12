package app.prumio.feature.settings

import app.prumio.feature.settings.reliability.ReminderReliabilityViewModel
import app.prumio.feature.settings.snooze.SnoozeViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Public API of this feature: its DI definitions. Registered in :app:shell (PrumioModules.kt). */
val settingsFeatureModule = module {
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ReminderReliabilityViewModel)
    viewModelOf(::SnoozeViewModel)
}
