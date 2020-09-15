package org.fouryouandme.tasks

import arrow.core.None
import arrow.core.Option
import org.fouryouandme.core.arch.navigation.NavigationAction
import org.fouryouandme.core.entity.configuration.Configuration
import org.fouryouandme.researchkit.task.Task

data class TaskState(
    val configuration: Option<Configuration> = None,
    val task: Option<Task> = None,
    val isCancelled: Boolean = false
)

sealed class TaskStateUpdate {

    data class Initialization(
        val configuration: Configuration,
        val task: Task
    ) : TaskStateUpdate()

    data class Cancelled(val isCancelled: Boolean) : TaskStateUpdate()

}

sealed class TaskLoading {

    object Initialization : TaskLoading()
}

sealed class TaskError {

    object Initialization : TaskError()

}

/* --- navigation --- */

data class StepToStep(val index: Int) : NavigationAction

