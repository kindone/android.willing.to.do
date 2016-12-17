package org.kindone.willingtodo.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import org.kindone.willingtodo.data.Task;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by kindone on 2016. 12. 13..
 */

public class TaskDbPrimitives {
    final static String TAG = "TaskDbPrimitives";
    final String tableName;

    public TaskDbPrimitives(String tableName) {
        this.tableName = tableName;
    }

    public void createTableIfNotExists(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName + "(_id INTEGER PRIMARY KEY AUTOINCREMENT," + " title TEXT NOT NULL, context_id INTEGER, priority INTEGER NOT NULL, willingness INTEGER NOT NULL, deadline TEXT, category TEXT)");
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "PRIORITY_IDX ON " + tableName + "(context_id, priority)");
        db.execSQL("CREATE UNIQUE INDEX " + tableName + "WILLINGNESS_IDX ON " + tableName + "(context_id, willingness)");
    }

    public void insertDummyEntries(SQLiteDatabase db) {
        SQLiteStatement stmt = db.compileStatement("INSERT INTO " + tableName + " (title, context_id, category, deadline, priority, willingness) " + "VALUES (?, ?, ?, ?, (SELECT IFNULL(MAX(priority),0) FROM " + tableName + ")+1, " + "(SELECT IFNULL(MAX(willingness),0) FROM " + tableName + ")+1)");

        stmt.bindAllArgsAsStrings(new String[]{"existing task", "1", "", ""});
        stmt.executeInsert();
    }

    public Task getTaskFromCurrentStarCursor(Cursor cursor) {
        long id = cursor.getLong(0);
        String title = cursor.getString(1);
        int contextId = cursor.getInt(2);
        int priority = cursor.getInt(3);
        int willingness = cursor.getInt(4);
        String deadline = cursor.getString(5);
        String category = cursor.getString(6);

        return new Task(id, title, contextId, category, deadline, priority, willingness);
    }

    public ContentValues getContentValuesForTask(Task task) {
        ContentValues values = new ContentValues();
        values.put("title", task.title);
        values.put("context_id", task.contextId);
        values.put("priority", task.priority);
        values.put("willingness", task.willingness);
        values.put("category", task.category);
        values.put("deadline", task.deadline);
        values.put("category", task.category);

        return values;
    }

//    public int copyColumnValue(SQLiteDatabase db, String columnName, long targetId, long srcId) {
//        // id1's priority = id2's priority
//        Log.v(TAG, "copy value of id1=" + srcId + " to id2=" + targetId + " for column=" + columnName);
//        SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = (SELECT " + columnName + " FROM " + tableName + " WHERE _id = ?)" + "WHERE _id = ?");
//        stmt.bindAllArgsAsStrings(new String[]{String.valueOf(srcId), String.valueOf(targetId)});
//        int numRowsAffected = stmt.executeUpdateDelete();
//        return numRowsAffected;
//    }

    public int setIntColumnValue(SQLiteDatabase db, String columnName, long targetId, int value) {
        SQLiteStatement stmt = db.compileStatement("UPDATE " + tableName + " SET " + columnName + " = ?" + "WHERE _id = ?");
        stmt.bindAllArgsAsStrings(new String[]{String.valueOf(value), String.valueOf(targetId)});
        int numRowsAffected = stmt.executeUpdateDelete();
        return numRowsAffected;
    }

    public int getIntColumnForTaskId(SQLiteDatabase db, String columnName, long taskId) {
        int value = 0;
        Cursor cursor = db.rawQuery("select " + columnName + " from " + tableName + " where _id = " + String.valueOf(taskId), null);
        while (cursor.moveToNext()) {
            value = cursor.getInt(0);
        }
        return value;
    }

    public Cursor selectAllTasks(SQLiteDatabase db) {
        return db.rawQuery("select * from " + tableName, null);
    }

    public Cursor selectAllTasksOrdered(SQLiteDatabase db, String columnName, long contextId) {
        Cursor cursor = db.rawQuery("select * from " + tableName + " where context_id = " + String.valueOf(contextId) + " order by " + columnName + " desc, _id desc", null);
        return cursor;
    }

    public int getTaskCount(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select count(*) from " + tableName, null);

        if (cursor.moveToNext()) return cursor.getInt(0);
        else return -1;
    }

    public void checkNumTasks(SQLiteDatabase db) {
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = selectAllTasks(db);
        while (cursor.moveToNext()) {
            Task task = getTaskFromCurrentStarCursor(cursor);
            tasks.add(task);
        }

        Log.v(TAG, "count(task)=" + getTaskCount(db));
        Log.v(TAG, "number of tasks=" + tasks.size());

    }

    public void checkTasks(SQLiteDatabase db) {
        List<Task> tasks = new LinkedList<>();

        Cursor cursor = selectAllTasks(db);
        while (cursor.moveToNext()) {
            Task task = getTaskFromCurrentStarCursor(cursor);
            tasks.add(task);
            Log.v(TAG, "Tasks=" + task.toString());
        }

        db.close();
    }
}
