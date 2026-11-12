package app.prumio.core.domain.task

import app.prumio.core.model.Task
import app.prumio.core.model.TaskId
import kotlinx.coroutines.flow.Flow

/** One task, live; null once deleted. */
class ObserveTaskUseCase(private val tasks: TaskRepository) {
    operator fun invoke(id: TaskId): Flow<Task?> = tasks.observe(id)
}
