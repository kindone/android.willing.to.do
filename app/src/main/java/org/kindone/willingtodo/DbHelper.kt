package org.kindone.willingtodo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.util.*

/**
 * Created by kindone on 2015. 11. 7..
 */
class DbHelper(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) : SQLiteOpenHelper(context, name, factory, version) {
    val tasksTableName = "TASKS"
    val timersTableName = "TIMERS"
    var version: Int = 0
        private set //state propagation TODO: confusing name

    init {
        this.version = 0
    }

    override fun onCreate(db: SQLiteDatabase) {

        db.execSQL("CREATE TABLE $tasksTableName(_id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, priority INTEGER NOT NULL, willingness INTEGER NOT NULL, deadline TEXT, category TEXT)")

        val stmt = db.compileStatement("INSERT INTO $tasksTableName (title, category, deadline, priority, willingness) VALUES (?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM $tasksTableName)+1, (SELECT IFNULL(MAX(willingness),0) FROM $tasksTableName)+1)")

        stmt.bindAllArgsAsStrings(arrayOf("existing task", "", ""))
        stmt.executeInsert()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion == 1) {
            db.execSQL("CREATE TABLE $timersTableName(_id INTEGER PRIMARY KEY AUTOINCREMENT, task_id INTEGER NOT NULL, title TEXT, duration_min INTEGER NOT NULL, started_ms INTEGER NOT NULL,is_paused BOOLEAN NOT NULL, elapsed_ms INTEGER NOT NULL,is_active BOOLEAN NOT NULL)")
            db.execSQL("CREATE INDEX " + timersTableName + "_IDX ON " + timersTableName + "(is_active)")
        }
    }

    //
    // for exceptional cases - expire all else
    val activeTimerEntry: TimerEntry?
        get() {
            val db = readableDatabase
            val timers = LinkedList<TimerEntry>()
            val cursor = db.rawQuery("select * from $timersTableName where is_active != 0 order by started_ms DESC", null)
            if (cursor.moveToNext()) {
                val id = cursor.getLong(0)
                val taskId = cursor.getLong(1)
                val title = cursor.getString(2)
                val durationMin = cursor.getInt(3)
                val startedMs = cursor.getLong(4)
                val isPaused = cursor.getInt(5) != 0
                val elapsedMs = cursor.getLong(6)

                timers.add(TimerEntry(id, taskId, title, durationMin, startedMs, isPaused, elapsedMs))

                if (cursor.moveToNext()) {
                    cleanupTimers(id)
                }
            }

            Log.v(TAG, "getActiveTimerEntry: " + timers.size)

            return if (timers.size > 0) timers[0] else null
        }

    fun insertTimerEntry(timer: TimerEntry) {
        val db = writableDatabase

        try {
            val stmt = db.compileStatement("INSERT INTO $timersTableName (task_id, title, duration_min, started_ms, is_paused, elapsed_ms, is_active) VALUES (?, ?, ?, ?, ?, ? ,?)")
            stmt.bindAllArgsAsStrings(arrayOf(timer.taskId.toString(), timer.title, timer.durationMin.toString(), timer.startedMs.toString(), (if (timer.isPaused) 1 else 0).toString(), timer.elapsedMs.toString(), 1.toString()))

            val row = stmt.executeInsert()
            Log.e(TAG, "insert result: " + row)
        } catch (e: Exception) {
            Log.e(TAG, "insertTimerEntry error: " + e.message)
        } finally {
            db.close()
        }

        checkNumTasks()
    }

    fun pauseTimerEntry(timer: TimerEntry): TimerEntry {
        Log.d(TAG, "pauseTimerEntry()")
        val db = writableDatabase

        val values = ContentValues()
        val newElapsedMs = System.currentTimeMillis() - timer.startedMs + timer.elapsedMs
        values.put("is_paused", 1)
        values.put("elapsed_ms", newElapsedMs)

        val selectionArgs = arrayOf(timer.id.toString())

        try {
            db.update(timersTableName, values, "_id = ?", selectionArgs)
        } finally {
            db.close()
        }

        return TimerEntry(timer.id, timer.taskId, timer.title,
                timer.durationMin, timer.startedMs, true, newElapsedMs)
    }

    fun resumeTimerEntry(timer: TimerEntry): TimerEntry {
        Log.d(TAG, "resumeTimerEntry()")
        val db = writableDatabase

        val values = ContentValues()
        val newStartedMs = System.currentTimeMillis()
        values.put("is_paused", 0)
        values.put("started_ms", newStartedMs)

        val selectionArgs = arrayOf(timer.id.toString())

        try {
            db.update(timersTableName, values, "_id = ?", selectionArgs)
        } finally {
            db.close()
        }

        return TimerEntry(timer.id, timer.taskId, timer.title,
                timer.durationMin, newStartedMs, false, timer.elapsedMs)
    }

    fun expireTimerEntry(timer: TimerEntry) {
        Log.d(TAG, "expireTimerEntry()")
        val db = writableDatabase

        val values = ContentValues()
        values.put("is_active", 0)

        val selectionArgs = arrayOf(timer.id.toString())

        try {
            db.update(timersTableName, values, "_id = ?", selectionArgs)
        } finally {
            db.close()
        }
    }

    // deactivate all timers except current one
    fun cleanupTimers(currentId: Long) {
        val db = writableDatabase

        val values = ContentValues()
        values.put("is_active", false)

        val selectionArgs = arrayOf(currentId.toString())

        try {
            db.update(timersTableName, values, "_id != ?", selectionArgs)
        } finally {
            db.close()
            version++
        }
    }

    fun cleanupTimers() {
        val db = writableDatabase

        val values = ContentValues()
        values.put("is_active", false)

        val selectionArgs = arrayOf<String>()

        try {
            db.update(timersTableName, values, "", selectionArgs)
        } finally {
            db.close()
            version++
        }
    }


    val priorityOrderedTasks: List<Task>
        get() = getOrderedTasks("priority")

    val willingnessOrderedTasks: List<Task>
        get() = getOrderedTasks("willingness")

    fun getOrderedTasks(columnName: String): List<Task> {
        val db = readableDatabase
        val tasks = LinkedList<Task>()

        val cursor = db.rawQuery("select * from $tasksTableName order by $columnName desc, _id desc", null)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val priority = cursor.getInt(2)
            val willingness = cursor.getInt(3)
            val deadline = cursor.getString(4)
            val category = cursor.getString(5)
            tasks.add(Task(id, title, category, deadline, priority, willingness))
        }

        Log.v(TAG, "getOrderedTasks: " + tasks.size)

        return tasks
    }

    fun insertTask(task: Task) {
        val db = writableDatabase

        try {
            val stmt = db.compileStatement("INSERT INTO $tasksTableName (title, category, deadline, priority, willingness) VALUES (?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM $tasksTableName)+1, (SELECT IFNULL(MAX(willingness),0) FROM $tasksTableName)+1)")
            stmt.bindAllArgsAsStrings(arrayOf(task.title, task.category, task.deadline))

            val row = stmt.executeInsert()
            Log.e(TAG, "insert result: " + row)
        } catch (e: Exception) {
            Log.e(TAG, "insertTask error: " + e.message)
        } finally {
            db.close()
            version++
        }

        checkNumTasks()
    }

    fun swapTaskPriority(id1: Long, id2: Long) {
        swapTask("priority", id1, id2)
    }

    fun swapTaskWillingness(id1: Long, id2: Long) {
        swapTask("willingness", id1, id2)
    }

    fun swapTask(columnName: String, id1: Long, id2: Long) {
        val db = writableDatabase

        db.beginTransaction()

        try {
            // save id1's priority
            var id1_value = 0
            val cursor = db.rawQuery("select " + columnName + " from " + tasksTableName + " where _id = " + id1.toString(), null)
            while (cursor.moveToNext()) {
                id1_value = cursor.getInt(0)
            }

            Log.v(TAG, "swapTask val1:" + id1_value)

            // id1's priority = id2's priority
            var stmt = db.compileStatement("UPDATE $tasksTableName SET $columnName = (SELECT $columnName FROM $tasksTableName WHERE _id = ?)WHERE _id = ?")
            stmt.bindAllArgsAsStrings(arrayOf(id2.toString(), id1.toString()))
            var numRowsAffected = stmt.executeUpdateDelete()

            // set id2's priority
            stmt = db.compileStatement("UPDATE $tasksTableName SET $columnName = ?WHERE _id = ?")
            stmt.bindAllArgsAsStrings(arrayOf(id1_value.toString(), id2.toString()))
            numRowsAffected += stmt.executeUpdateDelete()
            if (numRowsAffected == 2)
                db.setTransactionSuccessful()
        } catch (e: Exception) {
            Log.v(TAG, "swapTask error:" + e.toString())
            throw e
        } finally {
            db.endTransaction()
            db.close()
            version++
        }

        checkTasks()
        Log.v(TAG, "swapTask")

    }

    // update
    fun updateTask(id: Long, task: Task) {
        val db = writableDatabase

        val values = ContentValues()
        values.put("title", task.title)
        values.put("category", task.category)
        values.put("deadline", task.deadline)

        val selectionArgs = arrayOf(id.toString())
        Log.w("TEMP", "id:" + id + ", task.id:" + task.id)

        try {
            db.update(tasksTableName, values, "_id = ?", selectionArgs)
        } finally {
            db.close()
            version++
        }
    }

    // delete
    fun deleteTask(id: Long) {
        val db = writableDatabase

        val selectionArgs = arrayOf(id.toString())

        try {
            db.delete(tasksTableName, "_id = ?", selectionArgs)
        } finally {
            db.close()
            version++
        }

        Log.v(TAG, "deleteTask")
    }

    private fun checkNumTasks() {
        val db = readableDatabase
        val tasks = LinkedList<Task>()

        var cursor = db.rawQuery("select * from " + tasksTableName, null)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val priority = cursor.getInt(2)
            val willingness = cursor.getInt(3)
            val deadline = cursor.getString(4)
            val category = cursor.getString(5)
            tasks.add(Task(id, title, category, deadline, priority, willingness))
        }

        cursor = db.rawQuery("select count(*) from " + tasksTableName, null)
        if (cursor.moveToNext())
            Log.v(TAG, "count(task): " + cursor.getInt(0))

        Log.v(TAG, "number of tasks: " + tasks.size)

    }

    private fun checkTasks() {
        val db = readableDatabase
        val tasks = LinkedList<Task>()

        val cursor = db.rawQuery("select * from " + tasksTableName, null)
        while (cursor.moveToNext()) {
            val id = cursor.getLong(0)
            val title = cursor.getString(1)
            val priority = cursor.getInt(2)
            val willingness = cursor.getInt(3)
            val deadline = cursor.getString(4)
            val category = cursor.getString(5)
            val task = Task(id, title, category, deadline, priority, willingness)
            tasks.add(task)
            Log.v(TAG, "Tasks: " + task.toString())
        }

        db.close()

    }

    companion object {

        val LatestVersion = 2
        private val TAG = "DbHelper"
    }

}
