package app.prumio.feature.today

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Public API of this feature: its DI definitions. Registered in :app:shell (PrumioModules.kt). */
val todayFeatureModule = module {
    viewModelOf(::TodayViewModel)
}
