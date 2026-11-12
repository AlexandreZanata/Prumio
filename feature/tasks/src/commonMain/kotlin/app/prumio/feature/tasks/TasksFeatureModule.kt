package app.prumio.feature.tasks

import app.prumio.feature.tasks.detail.TaskDetailViewModel
import app.prumio.feature.tasks.editor.TaskEditorViewModel
import app.prumio.feature.tasks.list.AllTasksViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/**
 * Public API of this feature: its DI definitions. Registered in :app:shell (PrumioModules.kt).
 * Each ViewModel receives its navigation key as a parameter (`koinViewModel { parametersOf(key) }`).
 */
val tasksFeatureModule = module {
    viewModelOf(::TaskEditorViewModel)
    viewModelOf(::TaskDetailViewModel)
    viewModelOf(::AllTasksViewModel)
}
