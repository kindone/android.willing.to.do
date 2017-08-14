package org.kindone.willingtodo.persistence.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log

import org.kindone.willingtodo.data.Task

import java.util.LinkedList

/**
 * Created by kindone on 2016. 12. 13..
 */

class TaskSqliteHelper(internal val tableName: String) {

    fun createTableIfNotExists(db: SQLiteDatabase) {
        createTable(db)
        createIndexes(db)
    }

    fun insertDummyEntries(db: SQLiteDatabase) {
        val dummyTask = Task("dummy task", 1)
        insertTask(db, dummyTask)
    }

    fun insertTask(db: SQLiteDatabase, task: Task): Long {
        val stmt = db.compileStatement("INSERT INTO " + tableName + " (title, context_id, category, deadline, priority, willingness) " +
                "VALUES (?, ?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + tableName + ")+1, " +
                "(SELECT IFNULL(MAX(willingness),0) FROM " + tableName + ")+1)")
        stmt.bindAllArgsAsStrings(arrayOf(task.title, task.contextId.toString(), task.category, task.deadline))

        return stmt.executeInsert()
    }

    fun getPriorityOrderedTasks(db: SQLiteDatabase, contextId: Long): List<Task> {
        return getOrderedTasks(db, "priority", contextId)
    }

    fun getWillingnessOrderedTasks(db: SQLiteDatabase, contextId: Long): List<Task> {
        return getOrderedTasks(db, "priority", contextId)
    }

    private fun getOrderedTasks(db: SQLiteDatabase, orderedColumnName: String, contextId: Long): List<Task> {
        val tasks = LinkedList<Task>()

        val cursor = selectAllTasksOrdered(db, orderedColumnName, contextId)
        while (cursor.moveToNext()) {
            val task = getTaskFromCurrentStarCursor(cursor)
            tasks.add(task)
        }
        Log.v(TAG, "getOrderedTasks: size=" + tasks.size)
        return tasks
    }

    fun updateTask(db: SQLiteDatabase, task: Task) {
        val selectionArgs = arrayOf(task.id.toString())
        db.update(tableName, getContentValuesForTask(task), "_id = ?", selectionArgs)
    }

    fun deleteTask(db: SQLiteDatabase, id: Long) {
        val selectionArgs = arrayOf(id.toString())
        db.delete(tableName, "_id = ?", selectionArgs)
    }

    fun swapPriorityOfTasks(db: SQLiteDatabase, id1: Long, id2: Long) {
        swapTask(db, "priority", id1, id2)
    }

    fun swapWillingnessOfTasks(db: SQLiteDatabase, id1: Long, id2: Long) {
        swapTask(db, "willingness", id1, id2)
    }

    private fun swapTask(db: SQLiteDatabase, columnName: String, id1: Long, id2: Long) {
        db.beginTransaction()
        // save id1's value
        val id1_value = getIntColumnForTaskId(db, columnName, id1)
        Log.v(TAG, "swapTask val1=" + id1_value)
        val id2_value = getIntColumnForTaskId(db, columnName, id2)
        Log.v(TAG, "swapTask val1=" + id2_value)
        var numRowsAffected = setIntColumnValue(db, columnName, id2, -1)

        // set id1's value to id2's value
        numRowsAffected += setIntColumnValue(db, columnName, id1, id2_value)
        // set id2's value
        numRowsAffected += setIntColumnValue(db, columnName, id2, id1_value)
        if (numRowsAffected == 3 &&
                getIntColumnForTaskId(db, columnName, id1) == id2_value &&
                getIntColumnForTaskId(db, columnName, id2) == id1_value) {
            db.setTransactionSuccessful()
        } else
            Log.e(TAG, "swapTask was not done properly. Affected rows=" + numRowsAffected)

        db.endTransaction()
    }

    private fun getTaskFromCurrentStarCursor(cursor: Cursor): Task {
        val id = cursor.getLong(0)
        val title = cursor.getString(1)
        val contextId = cursor.getInt(2)
        val priority = cursor.getInt(3)
        val willingness = cursor.getInt(4)
        val deadline = cursor.getString(5)
        val category = cursor.getString(6)

        return Task(id, title, contextId.toLong(), category, deadline, priority, willingness)
    }

    private fun getContentValuesForTask(task: Task): ContentValues {
        val values = ContentValues()
        values.put("title", task.title)
        values.put("context_id", task.contextId)
        values.put("priority", task.priority)
        values.put("willingness", task.willingness)
        values.put("category", task.category)
        values.put("deadline", task.deadline)
        values.put("category", task.category)

        return values
    }

    private fun setIntColumnValue(db: SQLiteDatabase, columnName: String, targetId: Long, value: Int): Int {
        val stmt = db.compileStatement("UPDATE $tableName SET $columnName = ?WHERE _id = ?")
        stmt.bindAllArgsAsStrings(arrayOf(value.toString(), targetId.toString()))
        val numRowsAffected = stmt.executeUpdateDelete()
        return numRowsAffected
    }

    private fun getIntColumnForTaskId(db: SQLiteDatabase, columnName: String, taskId: Long): Int {
        var value = 0
        val cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + taskId.toString(), null)
        if (cursor.moveToNext()) {
            value = cursor.getInt(0)
        }
        return value
    }

    private fun selectAllTasks(db: SQLiteDatabase): Cursor {
        return db.rawQuery("select * from " + tableName, null)
    }

    private fun selectAllTasksOrdered(db: SQLiteDatabase, columnName: String, contextId: Long): Cursor {
        val cursor = db.rawQuery("select * from " + tableName + " where context_id = " + contextId.toString() + " order by " + columnName + " desc, _id desc", null)
        return cursor
    }

    fun getTaskCount(db: SQLiteDatabase): Int {
        val cursor = db.rawQuery("select count(*) from " + tableName, null)

        if (cursor.moveToNext())
            return cursor.getInt(0)
        else
            return -1
    }

    fun checkNumTasks(db: SQLiteDatabase) {
        val tasks = LinkedList<Task>()

        val cursor = selectAllTasks(db)
        while (cursor.moveToNext()) {
            val task = getTaskFromCurrentStarCursor(cursor)
            tasks.add(task)
        }

        Log.v(TAG, "count(task)=" + getTaskCount(db))
        Log.v(TAG, "number of tasks=" + tasks.size)

    }

    fun checkTasks(db: SQLiteDatabase) {
        val tasks = LinkedList<Task>()

        val cursor = selectAllTasks(db)
        while (cursor.moveToNext()) {
            val task = getTaskFromCurrentStarCursor(cursor)
            tasks.add(task)
            Log.v(TAG, "Tasks=" + task.toString())
        }
    }

    private fun createTable(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " title TEXT NOT NULL," +
                " context_id INTEGER," +
                " priority INTEGER NOT NULL," +
                " willingness INTEGER NOT NULL," +
                " deadline TEXT, category TEXT)")
    }

    private fun createIndexes(db: SQLiteDatabase) {
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "PRIORITY_IDX ON " + tableName + "(context_id, priority)")
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "WILLINGNESS_IDX ON " + tableName + "(context_id, willingness)")
    }

    companion object {
        internal val TAG = "TaskSqliteHelper"
    }
}
