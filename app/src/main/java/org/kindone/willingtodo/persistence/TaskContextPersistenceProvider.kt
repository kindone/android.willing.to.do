package org.kindone.willingtodo.persistence

import org.kindone.willingtodo.data.TaskContext

/**
 * Created by kindone on 2016. 12. 22..
 */

interface TaskContextPersistenceProvider {

    val taskContexts: List<TaskContext>

    fun getModeOfTaskContext(contextId: Long): Int

    fun setModeOfTaskContext(contextId: Long, mode: Int)

    fun createTaskContext(context: TaskContext): TaskContext

    fun updateTaskContext(context: TaskContext)

    fun swapPositionOfTaskContexts(id1: Long, id2: Long)

    fun deleteTaskContext(id: Long)

    val version: Int
}
