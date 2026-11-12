package app.prumio.feature.matters

import app.prumio.feature.matters.detail.MatterDetailViewModel
import app.prumio.feature.matters.editor.MatterEditorViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** Public API: feature ViewModels, registered in :app:shell. */
val mattersFeatureModule = module {
    viewModelOf(::MattersViewModel)
    viewModelOf(::MatterEditorViewModel)
    viewModelOf(::MatterDetailViewModel)
}
