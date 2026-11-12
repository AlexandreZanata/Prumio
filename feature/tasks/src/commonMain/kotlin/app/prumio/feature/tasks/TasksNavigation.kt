package app.prumio.feature.tasks

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import app.prumio.core.navigation.AllTasksKey
import app.prumio.core.navigation.Navigator
import app.prumio.core.navigation.TaskDetailKey
import app.prumio.core.navigation.TaskEditorKey
import app.prumio.feature.tasks.detail.TaskDetailRoute
import app.prumio.feature.tasks.editor.TaskEditorRoute
import app.prumio.feature.tasks.list.AllTasksRoute

/** Public API of this feature: the navigation entries it provides. Registered in :app:shell. */
fun EntryProviderScope<NavKey>.tasksEntries(navigator: Navigator) {
    entry<TaskEditorKey> { key -> TaskEditorRoute(key = key, navigator = navigator) }
    entry<TaskDetailKey> { key -> TaskDetailRoute(key = key, navigator = navigator) }
    entry<AllTasksKey> { key -> AllTasksRoute(key = key, navigator = navigator) }
}
