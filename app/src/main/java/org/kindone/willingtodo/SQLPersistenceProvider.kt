package org.kindone.willingtodo

import android.content.Context

import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.data.TaskContext
import org.kindone.willingtodo.persistence.ConfigPersistenceProvider
import org.kindone.willingtodo.persistence.sqlite.SqliteHelper
import org.kindone.willingtodo.persistence.PersistenceProvider
import org.kindone.willingtodo.persistence.TaskContextPersistenceProvider
import org.kindone.willingtodo.persistence.TaskPersistenceProvider

/**
 * Created by kindone on 2016. 12. 20..
 */
class SQLPersistenceProvider internal constructor(context: Context) : PersistenceProvider {

    override val taskPersistenceProvider: TaskPersistenceProvider
    override val taskContextPersistenceProvider: TaskContextPersistenceProvider
    internal val mDbHelper: SqliteHelper = SqliteHelperSingleton.get(context)
    override val configPersistenceProvider: ConfigPersistenceProvider

    init {

        taskPersistenceProvider = object : TaskPersistenceProvider {
            override val version: Int
                get() = mDbHelper.version

            override fun getTasksOfContextOrderedByPriority(contextId: Long): List<Task> {
                return mDbHelper.getPriorityOrderedTasks(contextId)
            }

            override fun getTasksOfContextOrderedByWillingness(contextId: Long): List<Task> {
                return mDbHelper.getWillingnessOrderedTasks(contextId)
            }

            override fun createTask(task: Task): Task {
                mDbHelper.insertTask(task)
                return task
            }

            override fun updateTask(task: Task) {
                mDbHelper.updateTask(task.id, task)
            }

            override fun swapPriorityOfTasks(id1: Long, id2: Long) {
                mDbHelper.swapPriorityOfTasks(id1, id2)
            }

            override fun swapWillingnessOfTasks(id1: Long, id2: Long) {
                mDbHelper.swapWillingnessOfTasks(id1, id2)
            }

            override fun deleteTask(id: Long) {
                mDbHelper.deleteTask(id)
            }
        }

        taskContextPersistenceProvider = object : TaskContextPersistenceProvider {
            override val taskContexts: List<TaskContext>
                get() = mDbHelper.taskContexts

            override fun getModeOfTaskContext(contextId: Long): Int {
                return mDbHelper.getContextMode(contextId)
            }

            override fun setModeOfTaskContext(contextId: Long, mode: Int) {
                mDbHelper.setContextMode(contextId, mode)
            }

            override fun createTaskContext(context: TaskContext): TaskContext {
                return mDbHelper.insertTaskContext(context)
            }

            override fun updateTaskContext(context: TaskContext) {
                mDbHelper.updateTaskContext(context)
            }

            override fun swapPositionOfTaskContexts(id1: Long, id2: Long) {
                mDbHelper.swapPositionOfTaskContexts(id1, id2)
            }

            override fun deleteTaskContext(id: Long) {
                mDbHelper.deleteTaskContext(id)
            }

            override val version: Int
                get() = mDbHelper.version
        }

        configPersistenceProvider = object : ConfigPersistenceProvider {
            override val tabIndex: Int
                get() = mDbHelper.currentTabIndex

            override fun saveTabIndex(index: Int) {
                mDbHelper.currentTabIndex = index
            }

            override val version: Int
                get() = mDbHelper.version
        }
    }

    override fun getVersion(): Int {
        return mDbHelper.version
    }

}
