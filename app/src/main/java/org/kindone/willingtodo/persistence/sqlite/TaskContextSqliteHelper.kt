package org.kindone.willingtodo.persistence.sqlite

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteStatement
import android.util.Log

import org.kindone.willingtodo.data.Task
import org.kindone.willingtodo.data.TaskContext

import java.util.LinkedList

/**
 * Created by kindone on 2016. 12. 14..
 */

class TaskContextSqliteHelper(internal val tableName: String) {

    fun createTableIfNotExists(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $tableName(_id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL, position INTEGER, mode INTEGER)")
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "_POSITION_IDX ON " + tableName + "(position)")
    }

    fun insertDefaultEntries(db: SQLiteDatabase) {
        val stmt = db.compileStatement("INSERT INTO $tableName (name, position, mode) VALUES (?, ?, ?)")
        stmt.bindAllArgsAsStrings(arrayOf("Work", "0", "0"))
        stmt.executeInsert()
    }

    fun insertTaskContext(db: SQLiteDatabase, context: TaskContext): Long {

        val stmt = db.compileStatement("INSERT INTO " + tableName + " (name, position) " +
                "VALUES (?, (SELECT IFNULL(MAX(position),0) FROM " + tableName + ")+1)")
        stmt.bindAllArgsAsStrings(arrayOf(context.name))

        return stmt.executeInsert()
    }

    fun getTaskContexts(db: SQLiteDatabase): List<TaskContext> {
        val contexts = LinkedList<TaskContext>()
        val cursor = selectAllContextsOrderedByPosition(db)
        while (cursor.moveToNext()) {
            val context = getTaskContextFromCurrentStarCursor(cursor)
            contexts.add(context)
        }
        return contexts
    }

    fun getTaskContextByRowId(db: SQLiteDatabase, id: Long): TaskContext? {
        val cursor = selectContextByRowId(db, id)
        if (cursor.moveToNext()) {
            val context = getTaskContextFromCurrentStarCursor(cursor)
            return context
        }
        return null
    }

    fun updateTaskContext(db: SQLiteDatabase, taskContext: TaskContext) {
        val selectionArgs = arrayOf(taskContext.id.toString())
        db.update(tableName, getContentValuesForContext(taskContext), "_id = ?", selectionArgs)
    }

    fun deleteTaskContext(db: SQLiteDatabase, id: Long) {
        val selectionArgs = arrayOf(id.toString())
        db.delete(tableName, "_id = ?", selectionArgs)
    }

    fun swapPositionOfTaskContexts(db: SQLiteDatabase, id1: Long, id2: Long) {
        db.beginTransaction()
        // save id1's value
        val id1_value = getIntColumnValue(db, "position", id1)
        Log.v(TAG, "swapContext val1=" + id1_value)
        val id2_value = getIntColumnValue(db, "position", id2)
        Log.v(TAG, "swapContext val2=" + id2_value)
        var numRowsAffected = setIntColumnValue(db, "position", id2, -1)

        // set id1's value to id2's value
        numRowsAffected += setIntColumnValue(db, "position", id1, id2_value)
        // set id2's value
        numRowsAffected += setIntColumnValue(db, "position", id2, id1_value)
        if (numRowsAffected == 3 &&
                getIntColumnValue(db, "position", id1) == id2_value &&
                getIntColumnValue(db, "position", id2) == id1_value) {
            db.setTransactionSuccessful()
        } else
            Log.e(TAG, "swapContext was not done properly. Affected rows=" + numRowsAffected)
        db.endTransaction()
    }

    fun getModeOfTaskContext(db: SQLiteDatabase, id: Long): Int {
        val cursor = db.rawQuery("select mode from " + tableName + " where _id = " + id.toString(), null)
        if (cursor.moveToNext()) {
            return cursor.getInt(0)
        }
        return 0 // default
    }

    fun setModeOfTaskContext(db: SQLiteDatabase, id: Long, mode: Int) {
        val selectionArgs = arrayOf(id.toString())

        val values = ContentValues()
        values.put("mode", mode)
        db.update(tableName, values, "_id = ?", selectionArgs)
    }


    private fun getTaskContextFromCurrentStarCursor(cursor: Cursor): TaskContext {
        val id = cursor.getLong(0)
        val name = cursor.getString(1)
        val position = cursor.getInt(2)
        val mode = cursor.getInt(3)
        return TaskContext(id, name, position, mode)
    }

    private fun selectAllContextsOrderedByPosition(db: SQLiteDatabase): Cursor {
        val cursor = db.rawQuery("select * from $tableName order by position asc, _id desc", null)
        return cursor
    }

    private fun selectContextByRowId(db: SQLiteDatabase, id: Long): Cursor {
        val cursor = db.rawQuery("select * from " + tableName + " where rowid = "
                + id.toString(), null)
        return cursor
    }

    private fun getContentValuesForContext(context: TaskContext): ContentValues {
        val values = ContentValues()
        values.put("name", context.name)
        values.put("position", context.position)
        values.put("mode", context.mode)

        return values
    }


    private fun setIntColumnValue(db: SQLiteDatabase, columnName: String, targetId: Long, value: Int): Int {
        val stmt = db.compileStatement("UPDATE $tableName SET $columnName = ?WHERE _id = ?")
        stmt.bindAllArgsAsStrings(arrayOf(value.toString(), targetId.toString()))
        val numRowsAffected = stmt.executeUpdateDelete()
        return numRowsAffected
    }

    private fun getIntColumnValue(db: SQLiteDatabase, columnName: String, taskId: Long): Int {
        var value = 0
        val cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + taskId.toString(), null)
        if (cursor.moveToNext()) {
            value = cursor.getInt(0)
        }
        return value
    }

    companion object {
        internal val TAG = "TaskContextSqliteHelper"
    }
}
