package app.prumio.feature.agenda

import app.prumio.feature.agenda.detail.AppointmentDetailViewModel
import app.prumio.feature.agenda.editor.AppointmentEditorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Public API of this feature: its DI definitions. Registered in :app:shell (PrumioModules.kt). */
val agendaFeatureModule = module {
    viewModelOf(::AgendaViewModel)
    viewModelOf(::AppointmentEditorViewModel)
    viewModelOf(::AppointmentDetailViewModel)
}
