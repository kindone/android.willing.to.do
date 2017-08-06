package org.kindone.willingtodo.persistence.sqlite

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.data.TaskContext

/**
 * Created by kindone on 2015. 11. 7..
 */
class SqliteHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?) : SQLiteOpenHelper(context, name, factory, LatestVersion) {

    private val taskContextSqliteHelper = TaskContextSqliteHelper(contextTableName)
    private val taskPrimitives = TaskSqliteHelper(taskTableName)
    private val timerSqliteHelper = TimerSqliteHelper(timerTableName)
    private val configSqliteHelper = ConfigSqliteHelper(configTableName)
    var version: Int = 0
        private set //state propagation

    init {
        version = 0
    }

    override fun onCreate(db: SQLiteDatabase) {
        initializeTaskContextTable(db)
        initializeTaskTable(db)
    }

    private fun initializeTaskContextTable(db: SQLiteDatabase) {
        taskContextSqliteHelper.createTableIfNotExists(db)
        taskContextSqliteHelper.insertDefaultEntries(db)
    }

    private fun initializeTaskTable(db: SQLiteDatabase) {
        taskPrimitives.createTableIfNotExists(db)
        taskPrimitives.insertDummyEntries(db)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldDbVersion: Int, newDbVersion: Int) {
        if (oldDbVersion == 1)
            configSqliteHelper.createTableIfNotExists(db)
        else if (oldDbVersion == 2)
            timerSqliteHelper.createTableIfNotExists(db)
    }

    private fun increaseVersion() {
        version++
    }

    val taskContexts: List<TaskContext>
        get() {
            val db = readableDatabase
            return taskContextSqliteHelper.getTaskContexts(db)
        }

    fun insertTaskContext(context: TaskContext): TaskContext {
        val db = writableDatabase
        var rowId = 0L
        try {
            rowId = taskContextSqliteHelper.insertTaskContext(db, context)
            Log.e(TAG, "insert result: rowId=" + rowId)

        } catch (e: Exception) {
            Log.e(TAG, "insertTaskContext error: message=" + e.message)
        } finally {
            db.close()
            increaseVersion()

            /** check  */
            val db2 = readableDatabase
            val newContext = taskContextSqliteHelper.getTaskContextByRowId(db2, rowId) ?: throw RuntimeException("task context not properly inserted")

            return newContext

        }
    }

    fun deleteTaskContext(id: Long) {
        val db = writableDatabase

        try {
            taskContextSqliteHelper.deleteTaskContext(db, id)
        } finally {
            db.close()
            increaseVersion()
        }

        Log.v(TAG, "deleteTaskContext")
    }

    fun updateTaskContext(context: TaskContext) {
        val db = writableDatabase

        try {
            taskContextSqliteHelper.updateTaskContext(db, context)
        } finally {
            db.close()
            increaseVersion()
        }
    }

    fun getContextMode(contextId: Long): Int {
        val db = readableDatabase
        return taskContextSqliteHelper.getModeOfTaskContext(db, contextId)
    }

    fun setContextMode(contextId: Long, mode: Int) {
        val db = writableDatabase
        try {
            taskContextSqliteHelper.setModeOfTaskContext(db, contextId, mode)
        } finally {
            db.close()
            increaseVersion()
        }
    }

    fun swapPositionOfTaskContexts(id1: Long, id2: Long) {
        val db = writableDatabase

        try {
            taskContextSqliteHelper.swapPositionOfTaskContexts(db, id1, id2)
        } catch (e: Exception) {
            Log.v(TAG, "swapContext error: Exception=" + e.toString())
            throw e
        } finally {
            db.close()
            increaseVersion()
        }
    }

    fun getPriorityOrderedTasks(contextId: Long): List<Task> {
        val db = readableDatabase
        return taskPrimitives.getPriorityOrderedTasks(db, contextId)
    }

    fun getWillingnessOrderedTasks(contextId: Long): List<Task> {
        val db = readableDatabase
        return taskPrimitives.getWillingnessOrderedTasks(db, contextId)
    }

    fun insertTask(task: Task) {
        val db = writableDatabase

        try {
            val row = taskPrimitives.insertTask(db, task)
            Log.e(TAG, "insert result: rowId=" + row)
        } catch (e: Exception) {
            Log.e(TAG, "insertTask error: message=" + e.message)
        } finally {
            db.close()
            increaseVersion()
        }

        taskPrimitives.checkNumTasks(readableDatabase)
    }

    fun swapPriorityOfTasks(id1: Long, id2: Long) {
        val db = writableDatabase

        try {
            taskPrimitives.swapPriorityOfTasks(db, id1, id2)
        } catch (e: Exception) {
            Log.v(TAG, "swapTask error: Exception=" + e.toString())
            throw e
        } finally {
            db.close()
            increaseVersion()
        }

        taskPrimitives.checkTasks(readableDatabase)
        Log.v(TAG, "swapTask")
    }

    fun swapWillingnessOfTasks(id1: Long, id2: Long) {
        val db = writableDatabase

        try {
            taskPrimitives.swapWillingnessOfTasks(db, id1, id2)
        } catch (e: Exception) {
            Log.v(TAG, "swapTask error: Exception=" + e.toString())
            throw e
        } finally {
            db.close()
            increaseVersion()
        }

        taskPrimitives.checkTasks(readableDatabase)
        Log.v(TAG, "swapTask")
    }


    // updateTask
    fun updateTask(id: Long, task: Task) {
        val db = writableDatabase

        try {
            taskPrimitives.updateTask(db, task)
        } finally {
            db.close()
            increaseVersion()
        }
    }

    // deleteTask
    fun deleteTask(id: Long) {
        val db = writableDatabase

        try {
            taskPrimitives.deleteTask(db, id)
        } finally {
            db.close()
            increaseVersion()
        }

        Log.v(TAG, "deleteTask: id=" + id)
    }

    var currentTabIndex: Int
        get() {
            val db = readableDatabase
            val tabIndex = configSqliteHelper.getCurrentTabIndex(db)
            db.close()
            return tabIndex
        }
        set(index) {
            val db = writableDatabase
            configSqliteHelper.setCurrentTabIndex(db, index)
            db.close()
        }

    companion object {

        private val TAG = "SqliteHelper"
        val MODE_PRIORITY = 0
        val MODE_WILLINGNESS = 1
        val contextTableName = "CONTEXTS"
        val taskTableName = "TASKS"
        val timerTableName = "TIMERS"
        val configTableName = "CONFIGS"

        private val LatestVersion = 2
    }
}
