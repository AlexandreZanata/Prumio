package app.prumio.feature.capture

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Public API of this feature: its DI definitions. Registered in :app:shell (PrumioModules.kt). */
val captureFeatureModule = module {
    viewModelOf(::CaptureViewModel)
}
