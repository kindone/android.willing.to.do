package org.kindone.willingtodo.persistence

import org.kindone.willingtodo.data.Task

/**
 * Created by kindone on 2016. 12. 13..
 */
interface TaskPersistenceProvider {
    val version: Int

    fun getTasksOfContextOrderedByPriority(contextId: Long): List<Task>

    fun getTasksOfContextOrderedByWillingness(contextId: Long): List<Task>

    fun createTask(task: Task): Task

    fun updateTask(task: Task)

    fun swapPriorityOfTasks(id1: Long, id2: Long)

    fun swapWillingnessOfTasks(id1: Long, id2: Long)

    fun deleteTask(id: Long)
}
