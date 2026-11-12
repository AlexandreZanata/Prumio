package app.prumio.feature.inbox

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Public API of this feature: its DI definitions. Registered in :app:shell (PrumioModules.kt). */
val inboxFeatureModule = module {
    viewModelOf(::InboxViewModel)
}
